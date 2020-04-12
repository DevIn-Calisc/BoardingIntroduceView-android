package com.calisc.boardingintroduceview.Models;

import java.io.Serializable;

public class BoardModel implements Serializable {
    private String titleText;
    private String descriptionText;
    private int bgColor;
    private int contentIconRes;
    private int bottomBarIconRes;

    public BoardModel() {

    }
    public BoardModel(String titleText, String descriptionText, int bgColor, int contentIconRes, int bottomBarIconRes) {
        this.titleText = titleText;
        this.descriptionText = descriptionText;
        this.bgColor = bgColor;
        this.contentIconRes = contentIconRes;
        this.bottomBarIconRes = bottomBarIconRes;
    }


    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getContentIconRes() {
        return contentIconRes;
    }

    public void setContentIconRes(int contentIconRes) {
        this.contentIconRes = contentIconRes;
    }

    public int getBottomBarIconRes() {
        return bottomBarIconRes;
    }

    public void setBottomBarIconRes(int bottomBarIconRes) {
        this.bottomBarIconRes = bottomBarIconRes;
    }
}
