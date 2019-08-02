package com.musicdo.musicshop.bean;

import java.util.List;

/**
 * 商品详情评价选项卡
 * Created by Yuedu on 2017/11/6.
 */

public class ProCommentBean {

    public List<com.musicdo.musicshop.bean.CommentImg> getCommentImg() {
        return CommentImg;
    }

    public void setCommentImg(List<com.musicdo.musicshop.bean.CommentImg> commentImg) {
        CommentImg = commentImg;
    }

    public com.musicdo.musicshop.bean.CommentData getCommentData() {
        return CommentData;
    }

    public void setCommentData(com.musicdo.musicshop.bean.CommentData commentData) {
        CommentData = commentData;
    }

    CommentData CommentData;
    List<CommentImg> CommentImg;
}
