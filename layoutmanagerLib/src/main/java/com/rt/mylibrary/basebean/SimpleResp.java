package com.rt.mylibrary.basebean;


public class SimpleResp<T> extends LayersBean {
    private T data;
    private StyleBean style;
    private EstyleBean estyle;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public StyleBean getStyle() {
        return style;
    }

    public void setStyle(StyleBean style) {
        this.style = style;
    }

    public EstyleBean getEstyle() {
        return estyle;
    }

    public void setEstyle(EstyleBean estyle) {
        this.estyle = estyle;
    }
}
