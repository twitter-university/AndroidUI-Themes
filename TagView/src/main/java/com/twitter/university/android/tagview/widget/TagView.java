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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.LevelListDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
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
    private static final String TAG = "TAGVIEW";

    private static class Tag {
        final PointF tl = new PointF();
        final int level;
        final String text;
        String shortTag;
        float w;
        public Tag(String tag, int level) {
            this.level = level;
            this.text = tag;
         }
    }

    private final float textBaseline;
    private final float tagHeight;
    private final float tagBorderedV;
    private final float tagBorderH;
    private final TextPaint textPaint = new TextPaint();

    private final Rect tagRect = new Rect();
    private final RectF tagRectF = new RectF();
    private final PointF tagTL = new PointF();
    private final List<Tag> tags = new ArrayList<Tag>();

    private int margin = 7;
    private int paddingV = 7;
    private int paddingH = 1;
    private int textSize = 64;
    private int textColor = Color.BLUE;
    private LevelListDrawable background;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public TagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray atts
            = context.getTheme().obtainStyledAttributes(attrs, R.styleable.tag_view, defStyle, 0);
        try { parseAttrs(atts); }
        finally { atts.recycle(); }

        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textPaint.setColor(textColor);

        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        textBaseline = metrics.leading - metrics.ascent;
        float textHeight = metrics.descent + textBaseline;
        tagHeight = textHeight + (2 * paddingV);
        tagBorderedV = tagHeight + (2 * margin);
        tagBorderH = 2 * (paddingH + margin);

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
                float tw = textPaint.measureText(tag.text) + tagBorderH;

                if (x + tw > maxW) {
                    lines++;
                    x = 0;
                }

                x += tw;
            }

            h = Math.round(lines * tagBorderedV);
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
                .ellipsize(tag.text, textPaint, maxW - tagBorderH, TextUtils.TruncateAt.END)
                .toString();

            tag.w = textPaint.measureText(tag.shortTag);
            if (tagBorderTL.x + tag.w + tagBorderH > maxW) {
                tagBorderTL.set(padL, tagBorderTL.y + tagBorderedV);
            }
            tag.tl.set(tagBorderTL);

            tagBorderTL.x += tag.w + tagBorderH;
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
            tagTL.offset(margin, margin);

            tagRectF.set(
                    tagTL.x,
                    tagTL.y,
                    tagTL.x + tag.w + (2 * paddingH) + 1,
                    tagTL.y + tagHeight);

            tagRectF.round(tagRect);
            background.setBounds(tagRect);
            background.setLevel(tag.level);
            background.draw(canvas);

            canvas.drawText(
                    tag.shortTag,
                    tagTL.x + paddingH,
                    tagTL.y + textBaseline,
                    textPaint);
        }
    }

    private void parseAttrs(TypedArray atts) {
        final int n = atts.getIndexCount();
        for (int i = 0; i < n; i++) {
            try {
                int attr = atts.getIndex(i);
                switch (attr) {
                    case R.styleable.tag_view_tag_view_tag_drawable:
                        background = (LevelListDrawable) atts.getDrawable(attr);
                        break;

                    case R.styleable.tag_view_tag_view_tag_margin:
                        margin = atts.getDimensionPixelSize(attr, margin);
                        break;

                    case R.styleable.tag_view_tag_view_tag_padding_horizontal:
                        paddingH = atts.getDimensionPixelSize(attr, paddingH);
                        break;

                    case R.styleable.tag_view_tag_view_tag_padding_vertical:
                        paddingV = atts.getDimensionPixelSize(attr, paddingV);
                        break;

                    case R.styleable.tag_view_tag_view_text_color:
                        textColor = atts.getColor(attr, textColor);
                        break;

                    case R.styleable.tag_view_tag_view_text_size:
                        textSize = atts.getDimensionPixelSize(attr, textSize);
                        break;
                }
            }
            catch (UnsupportedOperationException e) {
                Log.w(TagView.TAG, "Failed parsing attribute: " + atts.getString(i), e);
            }
            catch (Resources.NotFoundException e) {
                Log.w(TagView.TAG, "Failed parsing attribute: " + atts.getString(i), e);
            }
        }
    }
}

