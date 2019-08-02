package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述:
 * 作者：haiming on 2017/8/2 16:55
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SpecitemDataBean implements Parcelable{
    int SpecValueID;
    int ProductImgID;
    String SpecValue;
    String Src;

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    boolean ischeck;

    protected SpecitemDataBean(Parcel in) {
        SpecValueID = in.readInt();
        ProductImgID = in.readInt();
        SpecValue = in.readString();
        Src = in.readString();
        ischeck = in.readByte() != 0;
    }

    public static final Creator<SpecitemDataBean> CREATOR = new Creator<SpecitemDataBean>() {
        @Override
        public SpecitemDataBean createFromParcel(Parcel in) {
            return new SpecitemDataBean(in);
        }

        @Override
        public SpecitemDataBean[] newArray(int size) {
            return new SpecitemDataBean[size];
        }
    };

    public int getSpecValueID() {
        return SpecValueID;
    }

    public void setSpecValueID(int specValueID) {
        SpecValueID = specValueID;
    }

    public int getProductImgID() {
        return ProductImgID;
    }

    public void setProductImgID(int productImgID) {
        ProductImgID = productImgID;
    }

    public String getSpecValue() {
        return SpecValue;
    }

    public void setSpecValue(String specValue) {
        SpecValue = specValue;
    }

    public String getSrc() {
        return Src;
    }

    public void setSrc(String src) {
        Src = src;
    }

    @Override
    public String toString() {
        return SpecValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(SpecValueID);
        dest.writeInt(ProductImgID);
        dest.writeString(SpecValue);
        dest.writeString(Src);
        dest.writeByte((byte) (ischeck ? 1 : 0));
    }
}
