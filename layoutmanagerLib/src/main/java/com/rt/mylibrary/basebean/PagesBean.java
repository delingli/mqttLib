package com.rt.mylibrary.basebean;

import org.json.JSONObject;

import java.util.List;

public class PagesBean {
    private String id;
    private String name;
    private PageStyleBean style;
    private List<SimpleResp> layers;
    private JSONObject slider;

    public JSONObject getSlider() {
        return slider;
    }

    public void setSlider(JSONObject slider) {
        this.slider = slider;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PageStyleBean getStyle() {
        return style;
    }

    public void setStyle(PageStyleBean style) {
        this.style = style;
    }

    public List<SimpleResp> getLayers() {
        return layers;
    }

    public void setLayers(List<SimpleResp> layers) {
        this.layers = layers;
    }
}
