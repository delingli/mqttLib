package com.rt.mylibrary.viewbean;

import com.rt.mylibrary.utils.ApiConfig;

public class RunTextBean {
    private String text;
    private String direction;
    private int originalWidth;
    private int originalHeight;
    private RunTextStyle style;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public void setOriginalWidth(int originalWidth) {
        this.originalWidth = originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalHeight(int originalHeight) {
        this.originalHeight = originalHeight;
    }

    public RunTextStyle getStyle() {
        return style;
    }

    public void setStyle(RunTextStyle style) {
        this.style = style;
    }

    public class RunTextStyle {
        private float letterSpacing;
        private String color;
        private float fontSize;
        private float lineHeight;
        private String fontWeight;
        private String fontStyle;

        public float getLetterSpacing() {
            return letterSpacing * ApiConfig.mScaleY;
        }

        public void setLetterSpacing(float letterSpacing) {
            this.letterSpacing = letterSpacing;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public float getFontSize() {
            return fontSize * ApiConfig.mScale;
        }

        public void setFontSize(float fontSize) {
            this.fontSize = fontSize;
        }

        public float getLineHeight() {
            return lineHeight * ApiConfig.mScaleY;
        }

        public void setLineHeight(float lineHeight) {
            this.lineHeight = lineHeight;
        }

        public String getFontWeight() {
            return fontWeight;
        }

        public void setFontWeight(String fontWeight) {
            this.fontWeight = fontWeight;
        }

        public String getFontStyle() {
            return fontStyle;
        }

        public void setFontStyle(String fontStyle) {
            this.fontStyle = fontStyle;
        }
    }
}
