package com.rt.mylibrary.viewbean;

import com.rt.mylibrary.utils.ApiConfig;

public class TextBean {
    private String text;

    private String originalWidth;
    private String originalHeight;

    private TextStytle style;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getOriginalWidth() {
        return Float.parseFloat(originalWidth);
    }

    public float getOriginalHeight() {
        return Float.parseFloat(originalHeight);
    }

    public void setOriginalWidth(String originalWidth) {
        this.originalWidth = originalWidth;
    }

    public void setOriginalHeight(String originalHeight) {
        this.originalHeight = originalHeight;
    }

    public TextStytle getStyle() {
        return style;
    }

    public void setStyle(TextStytle style) {
        this.style = style;
    }

    public class TextStytle {
        private String letterSpacing;
        private String color;
        private String textDecoration;
        private String textAlign;
        private String fontSize;
        private String lineHeight;
        private String fontWeight;
        private String fontStyle;

        public void setLetterSpacing(String letterSpacing) {
            this.letterSpacing = letterSpacing;
        }

        public void setFontSize(String fontSize) {
            this.fontSize = fontSize;
        }

        public void setLineHeight(String lineHeight) {
            this.lineHeight = lineHeight;
        }

        public void setFontWeight(String fontWeight) {
            this.fontWeight = fontWeight;
        }

        public void setFontStyle(String fontStyle) {
            this.fontStyle = fontStyle;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getTextDecoration() {
            return textDecoration;
        }

        public void setTextDecoration(String textDecoration) {
            this.textDecoration = textDecoration;
        }

        public String getTextAlign() {
            return textAlign;
        }

        public void setTextAlign(String textAlign) {
            this.textAlign = textAlign;
        }

        public float getLetterSpacing() {
            return Float.parseFloat(letterSpacing) * ApiConfig.mScale;
        }

        public float getFontSize() {
            return Float.parseFloat(fontSize)* ApiConfig.mScale;
        }

        public float getLineHeight() {
            return Float.parseFloat(lineHeight)* ApiConfig.mScaleY;
        }

        public float getFontWeight() {
            return Float.parseFloat(fontWeight)* ApiConfig.mScale;
        }

        public String getFontStyle() {
            return fontStyle;
        }
    }
}
