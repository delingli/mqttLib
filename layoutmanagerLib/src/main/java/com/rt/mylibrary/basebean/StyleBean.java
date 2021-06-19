package com.rt.mylibrary.basebean;


import com.rt.mylibrary.utils.ApiConfig;

public class StyleBean {
    private String width;
    private String height;
    private String left;
    private String top;
    private String transform;

    public float getWidth() {
        return Integer.parseInt(width) * ApiConfig.mScaleX;
    }

    public float getHeight() {
        return Integer.parseInt(height) * ApiConfig.mScaleY;
    }

    public float getLeft() {
        return Integer.parseInt(left) * ApiConfig.mScaleX;
    }

    public float getTop() {
        return Integer.parseInt(top) * ApiConfig.mScaleY;
    }

    public float getTransform() {
        return Integer.parseInt(transform) * ApiConfig.mScale;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }
}
