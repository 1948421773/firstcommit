package com.musicdo.musicshop.bean;

import java.util.List;

/**
 * Created by Yuedu on 2017/11/29.
 */

public class MyCommentBean {
    public CommentData getData() {
        return Data;
    }

    public void setData(CommentData data) {
        Data = data;
    }

    public List<CommentImg> getImgList() {
        return ImgList;
    }

    public void setImgList(List<CommentImg> imgList) {
        ImgList = imgList;
    }

    CommentData Data;
    List<CommentImg> ImgList;
}
