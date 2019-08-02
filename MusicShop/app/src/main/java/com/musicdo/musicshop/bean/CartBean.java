package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 描述:购物车实体类
 * 作者：haiming on 2017/8/9 14:58
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class CartBean implements Parcelable{


    public static final Creator<CartBean> CREATOR = new Creator<CartBean>() {
        @Override
        public CartBean createFromParcel(Parcel in) {
            CartBean cb=new CartBean();
            cb.ShopName = in.readString();
            cb.ShopID = in.readInt();
            cb.ProductDetail = in.createTypedArrayList(CartItemBean.CREATOR);
            cb.isAllEdited=in.readByte() != 0;
            cb.isEdited=in.readByte() != 0;
            cb.isChoosed=in.readByte() != 0;
            return cb;
        }

        @Override
        public CartBean[] newArray(int size) {
            return new CartBean[size];
        }
    };

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    public List<CartItemBean> getProductDetail() {
        return ProductDetail;
    }

    public void setProductDetail(List<CartItemBean> productDetail) {
        ProductDetail = productDetail;
    }

    String ShopName;
    int ShopID;
    List<CartItemBean> ProductDetail;
    boolean isEdited;
    boolean isAllEdited;

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }

    boolean isChoosed;
    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public boolean isAllEdited() {
        return isAllEdited;
    }

    public void setAllEdited(boolean allEdited) {
        isAllEdited = allEdited;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ShopName);
        dest.writeInt(ShopID);
        dest.writeByte((byte)(isEdited?1:0));
        dest.writeByte((byte)(isAllEdited?1:0));
        dest.writeByte((byte)(isChoosed?1:0));
    }
}
