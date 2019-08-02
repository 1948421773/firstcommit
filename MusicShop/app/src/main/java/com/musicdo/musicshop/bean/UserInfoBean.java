package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yuedu on 2017/9/23.
 */

public class UserInfoBean implements Parcelable{
    int ID;
    String Name;
    String NickName;
    String ImageUrl;
    String GradeName;


    public static final Creator<UserInfoBean> CREATOR = new Creator<UserInfoBean>() {
        @Override
        public UserInfoBean createFromParcel(Parcel in) {
            UserInfoBean uf=new UserInfoBean();
            uf.ID = in.readInt();
            uf.Name = in.readString();
            uf.NickName = in.readString();
            uf.ImageUrl = in.readString();
            uf.GradeName = in.readString();
            return uf;
        }

        @Override
        public UserInfoBean[] newArray(int size) {
            return new UserInfoBean[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getGradeName() {
        return GradeName;
    }

    public void setGradeName(String gradeName) {
        GradeName = gradeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(Name);
        dest.writeString(NickName);
        dest.writeString(ImageUrl);
        dest.writeString(GradeName);
    }
}
