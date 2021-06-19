package com.rt.mylibrary.basebean;

public class EstyleBean {
    private String borderRadius;
    private String borderStyle;
    private String opacity;

    public int getBorderRadius() {
        return Integer.parseInt(borderRadius);
    }

    public String getBorderStyle() {
        return borderStyle;
    }

    public int getOpacity() {
        return Integer.parseInt(opacity);
    }

    public void setBorderRadius(String borderRadius) {
        this.borderRadius = borderRadius;
    }

    public void setBorderStyle(String borderStyle) {
        this.borderStyle = borderStyle;
    }

    public void setOpacity(String opacity) {
        this.opacity = opacity;
    }
}
