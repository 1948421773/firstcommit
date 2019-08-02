package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述:
 * 作者：haiming on 2017/8/15 13:31
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class AreasAreaBean implements Parcelable{

    public static final Creator<AreasAreaBean> CREATOR = new Creator<AreasAreaBean>() {
        @Override
        public AreasAreaBean createFromParcel(Parcel in) {
            AreasAreaBean aa=new AreasAreaBean();
            aa.countyId = in.readString();
            aa.countyName = in.readString();
            return aa;
        }

        @Override
        public AreasAreaBean[] newArray(int size) {
            return new AreasAreaBean[size];
        }
    };

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    String countyId;
    String countyName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(countyId);
        dest.writeString(countyName);
    }
}
