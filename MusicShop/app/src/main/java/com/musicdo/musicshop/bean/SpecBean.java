package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 描述:商品详情-商品规格（颜色尺寸材质，最多三层，可空）
 * 作者：haiming on 2017/8/2 15:27
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SpecBean implements Parcelable{
    List<SpecItemBean> ReturnData;

    public static final Creator<SpecBean> CREATOR = new Creator<SpecBean>() {
        @Override
        public SpecBean createFromParcel(Parcel in) {
            SpecBean sb=new SpecBean();
            sb.ReturnData = in.createTypedArrayList(SpecItemBean.CREATOR);
            return sb;
        }

        @Override
        public SpecBean[] newArray(int size) {
            return new SpecBean[size];
        }
    };

    public List<SpecItemBean> getData() {
        return ReturnData;
    }

    public void setData(List<SpecItemBean> data) {
        ReturnData = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(ReturnData);
    }
}
