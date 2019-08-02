package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 * 作者：haiming on 2017/8/1 09:33
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class CommentBean implements Parcelable {
    int ID;
    int StarNum;
    String UserName;
    String Ico;
    String Content;
    List<CommentImgList> ImgList;
    public List<CommentImgList> getImgList() {
        return ImgList;
    }
    public void setImgList(List<CommentImgList> imgList) {
        ImgList = imgList;
    }


    public static final Creator<CommentBean> CREATOR = new Creator<CommentBean>() {
        @Override
        public CommentBean createFromParcel(Parcel in) {
            CommentBean cb=new CommentBean();
            cb.ID = in.readInt();
            cb.StarNum = in.readInt();
            cb.UserName = in.readString();
            cb.Ico = in.readString();
            cb.Content = in.readString();
            return cb;
        }

        @Override
        public CommentBean[] newArray(int size) {
            return new CommentBean[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getStarNum() {
        return StarNum;
    }

    public void setStarNum(int starNum) {
        StarNum = starNum;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getIco() {
        return Ico;
    }

    public void setIco(String ico) {
        Ico = ico;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(StarNum);
        dest.writeString(UserName);
        dest.writeString(Ico);
        dest.writeString(Content);
    }
}
