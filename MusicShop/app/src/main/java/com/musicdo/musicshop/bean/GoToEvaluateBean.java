package com.musicdo.musicshop.bean;

/**
 * 描述:订单评价图片上传
 * 作者：haiming on 2017/9/1 16:03
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class GoToEvaluateBean {
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public boolean isUpLoading() {
        return isUpLoading;
    }

    public void setUpLoading(boolean upLoading) {
        isUpLoading = upLoading;
    }

    public boolean isDelate() {
        return isDelate;
    }

    public void setDelate(boolean delate) {
        isDelate = delate;
    }

    String imageName;
    int imageID;
    boolean isUpLoading;
    boolean isDelate;
}
