package com.newsalesbeatApp.pojo;

public class AutosliderModel {
    String imgUrl;
    String campContent;

    public AutosliderModel(String imgUrl, String campContent) {
        this.imgUrl = imgUrl;
        this.campContent = campContent;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCampContent() {
        return campContent;
    }

    public void setCampContent(String campContent) {
        this.campContent = campContent;
    }
}
