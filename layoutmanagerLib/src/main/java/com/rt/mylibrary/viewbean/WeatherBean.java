package com.rt.mylibrary.viewbean;

import java.util.List;

public class WeatherBean {
    private List<String> area;
    private float originalWidth;
    private float originalHeight;
    private WeatherStyle style;

    public WeatherStyle getStyle() {
        return style;
    }

    public void setStyle(WeatherStyle style) {
        this.style = style;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public float getOriginalWidth() {
        return originalWidth;
    }

    public void setOriginalWidth(float originalWidth) {
        this.originalWidth = originalWidth;
    }

    public float getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalHeight(float originalHeight) {
        this.originalHeight = originalHeight;
    }

    public class WeatherStyle {
        private String color;
        private float fontSize;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public float getFontSize() {
            return fontSize;
        }

        public void setFontSize(float fontSize) {
            this.fontSize = fontSize;
        }
    }
}
