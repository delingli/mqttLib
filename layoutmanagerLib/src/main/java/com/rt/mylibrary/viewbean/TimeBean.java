package com.rt.mylibrary.viewbean;

public class TimeBean {

    private int originalWidth;
    private int originalHeight;
    private String type;
    private TimeStyle style;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TimeStyle getStyle() {
        return style;
    }

    public void setStyle(TimeStyle style) {
        this.style = style;
    }

    public class TimeStyle {
        private String color;
        private int fontSize;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }
    }
}
