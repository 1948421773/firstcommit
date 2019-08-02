package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 描述:
 * 作者：haiming on 2017/8/15 11:57
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class AreasBean implements Parcelable{
    public static final Creator<AreasBean> CREATOR = new Creator<AreasBean>() {
        @Override
        public AreasBean createFromParcel(Parcel in) {
            AreasBean ab=new AreasBean();
            ab.provinceId = in.readString();
            ab.provinceName = in.readString();
            ab.district = in.createTypedArrayList(AreasCityBean.CREATOR);
            return ab;
        }

        @Override
        public AreasBean[] newArray(int size) {
            return new AreasBean[size];
        }
    };

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public List<AreasCityBean> getDistrict() {
        return district;
    }

    public void setDistrict(List<AreasCityBean> district) {
        this.district = district;
    }

    String provinceId;
    String provinceName;
    List<AreasCityBean> district;

    @Override
    public String toString() {
        return provinceName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(provinceId);
        dest.writeString(provinceName);
        dest.writeTypedList(district);
    }
}
