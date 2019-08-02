package com.musicdo.musicshop.bean;

/**
 * 商品详情评价选项卡--CommentImg列表
 * Created by Yuedu on 2017/11/6.
 */

public class CommentImg {
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
