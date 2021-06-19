package com.rt.mylibrary.viewbean;

import java.util.ArrayList;

public class VideoBean {

  private int  playId;
  private String playName;
  private ArrayList<PlayItemBean> playItemList;
  private int originalWidth;
  private int originalHeight;
  private String backgroundImage;
  private int  border ;

    public int getPlayId() {
        return playId;
    }

    public void setPlayId(int playId) {
        this.playId = playId;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public ArrayList<PlayItemBean> getPlayItemList() {
        return playItemList;
    }

    public void setPlayItemList(ArrayList<PlayItemBean> playItemList) {
        this.playItemList = playItemList;
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

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }
}
