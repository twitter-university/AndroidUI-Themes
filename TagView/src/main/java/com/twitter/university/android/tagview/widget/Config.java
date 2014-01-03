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

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.TextPaint;
import android.util.Log;

import com.twitter.university.android.tagview.R;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
class Config {
    public static class Builder {
        private final Context ctxt;
        private Drawable drawable;
        private int margin;
        private int paddingH;
        private int paddingV;
        private int textColor;
        private int textSize;
        private int textStyle;
        private int textFace;

        public Builder(Context ctxt) { this.ctxt = ctxt; }
        public void setMargin(int margin) { this.margin = margin; }
        public void setPaddingH(int paddingH) { this.paddingH = paddingH; }
        public void setPaddingV(int paddingV) { this.paddingV = paddingV; }
        public void setTextColor(int textColor) { this.textColor = textColor; }
        public void setTextSize(int textSize) { this.textSize = textSize; }
        public void setTextStyle(int textStyle) { this.textStyle = textStyle; }
        public void setTextFace(int textFace) { this.textFace = textFace; }

        public Config build(TypedArray atts) {
            parseAttrs(atts);
            return new Config(
                    getBgDrawable(),
                    margin,
                    paddingH,
                    paddingV,
                    setTextAppearance(textColor, textSize, textStyle, textFace));
        }

        private LevelListDrawable getBgDrawable() {
            LevelListDrawable bg;
            if ((null != drawable) && (drawable instanceof LevelListDrawable)) {
                bg = (LevelListDrawable) drawable;
            }
            else {
                bg = new LevelListDrawable();
                bg.addLevel(0, 1, new ColorDrawable(Color.WHITE));
            }
            return bg;
        }

        // stolen pretty much directly from TextView
        private TextPaint setTextAppearance(int color, int size, int style, int face) {
            TextPaint paint = new TextPaint();

            Typeface tf = null;
            switch (face) {
                case 1:
                    tf = Typeface.SANS_SERIF;
                    break;
                case 2:
                    tf = Typeface.SERIF;
                    break;
                case 3:
                    tf = Typeface.MONOSPACE;
                    break;
                case 4:
                    try { tf = Typeface.createFromAsset(ctxt.getAssets(), TagView.DEFAULT_FONT); }
                    catch (Exception e) { Log.w(TagView.TAG, "Could not create default font"); }
                    break;
            }

            if (0 >= style) {
                paint.setFakeBoldText(false);
                paint.setTextSkewX(0);
                paint.setTypeface(tf);
            }
            else {
                tf = (tf != null)
                        ? Typeface.create(tf, style)
                                : Typeface.defaultFromStyle(style);
                        paint.setTypeface(tf);

                        // now compute what (if any) algorithmic styling is needed
                        int typefaceStyle = tf != null ? tf.getStyle() : 0;
                        int need = style & ~typefaceStyle;
                        paint.setFakeBoldText((need & Typeface.BOLD) != 0);
                        paint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
            }

            paint.setAntiAlias(true);
            paint.setTextSize(size);
            paint.setColor(color);

            return paint;
        }

        private void parseAttrs(TypedArray atts) {
            final int n = atts.getIndexCount();
            for (int i = 0; i < n; i++) {
                try {
                    int attr = atts.getIndex(i);
                    switch (attr) {
                        case R.styleable.tag_view_tag_view_tag_drawable:
                            drawable = atts.getDrawable(attr);
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

                        case R.styleable.tag_view_tag_view_text_style:
                            textStyle = atts.getInt(attr, textStyle);
                            break;

                        case R.styleable.tag_view_tag_view_text_face:
                            textFace = atts.getInt(attr, textFace);
                            break;
                    }
                }
                catch (UnsupportedOperationException e) {
                    Log.w(TagView.TAG, "Failed parsing attribute: " + atts.getString(i), e);
                }
                catch (NotFoundException e) {
                    Log.w(TagView.TAG, "Failed parsing attribute: " + atts.getString(i), e);
                }
            }
        }
    }

    public final LevelListDrawable background;
    public final int margin;
    public final int paddingH;
    public final TextPaint textPaint;
    public final float textBaseline;
    public final float tagHeight;
    public final int tagBorderH;
    public final float tagBorderedV;

    public Config(
            LevelListDrawable background,
            int margin,
            int paddingH,
            int paddingV,
            TextPaint textPaint)
    {
        this.background = background;
        this.margin = margin;
        this.paddingH = paddingH;
        this.textPaint = textPaint;

        FontMetrics metrics = textPaint.getFontMetrics();
        float baseline = metrics.leading - (metrics.ascent + 1);
        textBaseline = paddingV + baseline;

        float textHeight = metrics.descent + baseline;
        tagHeight = textHeight + (2 * paddingV);
        tagBorderedV = tagHeight + (2 * margin);
        tagBorderH = 2 * (paddingH + margin);
    }
}