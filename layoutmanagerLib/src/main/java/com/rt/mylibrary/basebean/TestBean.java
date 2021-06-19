package com.rt.mylibrary.basebean;


import org.json.JSONObject;

public class TestBean {
    private String text;

    private String originalWidth;
    private String originalHeight;

    private String style;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOriginalWidth() {
        return originalWidth;
    }

    public void setOriginalWidth(String originalWidth) {
        this.originalWidth = originalWidth;
    }

    public String getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalHeight(String originalHeight) {
        this.originalHeight = originalHeight;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}

