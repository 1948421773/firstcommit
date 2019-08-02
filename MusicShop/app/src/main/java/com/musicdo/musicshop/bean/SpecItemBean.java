package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * 作者：haiming on 2017/8/2 16:53
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SpecItemBean implements Parcelable{

    public static final Creator<SpecItemBean> CREATOR = new Creator<SpecItemBean>() {
        @Override
        public SpecItemBean createFromParcel(Parcel in) {
            String Title = in.readString();
            List<SpecitemDataBean> parts = new ArrayList<SpecitemDataBean>();
            in.readTypedList(parts, SpecitemDataBean.CREATOR);

            SpecItemBean face = new SpecItemBean();
            face.Data = parts;
            face.Title=Title;
            return face;
        }

        @Override
        public SpecItemBean[] newArray(int size) {
            return new SpecItemBean[size];
        }
    };

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public List<SpecitemDataBean> getData() {
        return Data;
    }

    public void setData(List<SpecitemDataBean> data) {
        Data = data;
    }

    String Title;
    List<SpecitemDataBean> Data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Title);
        dest.writeTypedList(Data);
    }
}
