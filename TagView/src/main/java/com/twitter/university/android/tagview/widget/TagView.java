/* $Id: $
   Copyright 2013, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.twitter.university.android.tagview.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.twitter.university.android.tagview.R;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class TagView extends View {
    static final String TAG = "TAGVIEW";

    static final String DEFAULT_FONT = "fonts/DroidSansFallback.ttf";

    private static class Tag {
        final PointF tl = new PointF();
        final int level;
        final String text;
        String shortTag;
        float w;
        public Tag(String tag, int level) {
            this.level = level;
            this.text = tag;
            this.shortTag = tag;
        }
    }

    private final Config config;
    private final Rect tagRect = new Rect();
    private final RectF tagRectF = new RectF();
    private final PointF tagTL = new PointF();
    private final List<Tag> tags = new ArrayList<Tag>();

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public TagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Config.Builder b = new Config.Builder(context);
        b.setMargin(4);
        b.setPaddingH(4);
        b.setPaddingV(4);
        b.setTextColor(Color.BLACK);
        b.setTextSize(18);
        b.setTextStyle(0);
        b.setTextFace(4);

        TypedArray atts = context.getTheme()
            .obtainStyledAttributes(attrs, R.styleable.tag_view, defStyle, 0);
        try { config = b.build(atts); }
        finally { atts.recycle(); }
    }

    /**
     * @param context
     * @param attrs
     */
    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     */
    public TagView(Context context) {
        this(context, null);
    }

    // the requestLayout is necessary; not sure about the invalidate.
    /**
     * @param tg
     * @param level
     */
    public void addTag(String tg, int level) {
        tags.add(new Tag(tg, level));
        invalidate();
        requestLayout();
    }

    /**
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int wSpec, int hSpec) {
        int w = View.getDefaultSize(getSuggestedMinimumWidth(), wSpec);
        int h = View.getDefaultSize(getSuggestedMinimumHeight(), hSpec);

        if (MeasureSpec.EXACTLY != MeasureSpec.getMode(hSpec)) {
            Log.d("TAG", "w: " + w);
            int maxW = w - (getPaddingLeft() + getPaddingRight());

            float x = 0;
            int lines = 1;
            for (Tag tag : tags) {
                float tw = config.textPaint.measureText(tag.text) + config.tagBorderH;

                if (x + tw > maxW) {
                    lines++;
                    x = 0;
                }

                x += tw;
            }

            h = Math.round(lines * config.tagBorderedV);
        }

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) { return; }

        int padL = getPaddingLeft();
        int padT = getPaddingTop();
        int maxW = right - (left + padL + getPaddingRight());
        PointF tagBorderTL = new PointF(padL, padT);
        for (Tag tag : tags) {
            tag.shortTag = TextUtils
                .ellipsize(tag.text, config.textPaint, maxW - config.tagBorderH, TruncateAt.END)
                .toString();

            tag.w = config.textPaint.measureText(tag.shortTag);
            if (tagBorderTL.x + tag.w + config.tagBorderH > maxW) {
                tagBorderTL.set(padL, tagBorderTL.y + config.tagBorderedV);
            }
            tag.tl.set(tagBorderTL);

            tagBorderTL.x += tag.w + config.tagBorderH;
        }
    }

    /**
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Tag tag: tags) {
            tagTL.set(tag.tl);
            tagTL.offset(config.margin, config.margin);

            tagRectF.set(
                    tagTL.x,
                    tagTL.y,
                    tagTL.x + tag.w + (2 * config.paddingH) + 1,
                    tagTL.y + config.tagHeight);

            tagRectF.round(tagRect);
            config.background.setBounds(tagRect);
            config.background.setLevel(tag.level);
            config.background.draw(canvas);

            canvas.drawText(
                    tag.shortTag,
                    tagTL.x + config.paddingH,
                    tagTL.y + config.textBaseline,
                    config.textPaint);
        }
    }
}
