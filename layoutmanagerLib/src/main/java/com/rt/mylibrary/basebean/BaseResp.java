package com.rt.mylibrary.basebean;

import org.json.JSONObject;

import java.util.List;

public class BaseResp {
    private String img;
    private String desc;
    private String name;
    private String loading;
    private List<String> set;
    private String type;
    private JSONObject mp3;
    private JSONObject slider;
    private JSONObject style;
    private List<PagesBean> pages;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoading() {
        return loading;
    }

    public void setLoading(String loading) {
        this.loading = loading;
    }

    public List<String> getSet() {
        return set;
    }

    public void setSet(List<String> set) {
        this.set = set;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject getMp3() {
        return mp3;
    }

    public void setMp3(JSONObject mp3) {
        this.mp3 = mp3;
    }

    public JSONObject getSlider() {
        return slider;
    }

    public void setSlider(JSONObject slider) {
        this.slider = slider;
    }

    public JSONObject getStyle() {
        return style;
    }

    public void setStyle(JSONObject style) {
        this.style = style;
    }

    public List<PagesBean> getPages() {
        return pages;
    }

    public void setPages(List<PagesBean> pages) {
        this.pages = pages;
    }
}
