package com.musicdo.musicshop.bean;

/**
 * Created by Yuedu on 2017/10/19.
 */

public class CommentImgList {
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getOrderProEvaluationID() {
        return OrderProEvaluationID;
    }

    public void setOrderProEvaluationID(int orderProEvaluationID) {
        OrderProEvaluationID = orderProEvaluationID;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    int ID;
    int OrderProEvaluationID;
    String ImgUrl;
}
