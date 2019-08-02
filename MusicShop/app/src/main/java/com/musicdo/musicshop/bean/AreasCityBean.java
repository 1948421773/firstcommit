package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 描述:
 * 作者：haiming on 2017/8/15 11:58
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class AreasCityBean implements Parcelable{
    public static final Creator<AreasCityBean> CREATOR = new Creator<AreasCityBean>() {
        @Override
        public AreasCityBean createFromParcel(Parcel in) {
            AreasCityBean acb=new AreasCityBean();
            acb.districtId = in.readString();
            acb.districtName = in.readString();
            acb.county = in.createTypedArrayList(AreasAreaBean.CREATOR);
            return acb;
        }

        @Override
        public AreasCityBean[] newArray(int size) {
            return new AreasCityBean[size];
        }
    };

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public List<AreasAreaBean> getCounty() {
        return county;
    }

    public void setCounty(List<AreasAreaBean> county) {
        this.county = county;
    }

    String districtId;
    String districtName;
    List<AreasAreaBean> county;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(districtId);
        dest.writeString(districtName);
        dest.writeTypedList(county);
    }
}
