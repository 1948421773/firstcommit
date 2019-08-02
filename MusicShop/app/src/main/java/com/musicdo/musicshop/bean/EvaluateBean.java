package com.musicdo.musicshop.bean;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Administrator on 2017/9/6.
 */

public class EvaluateBean implements Parcelable{


    public static final Creator<EvaluateBean> CREATOR = new Creator<EvaluateBean>() {
        @Override
        public EvaluateBean createFromParcel(Parcel in) {
            EvaluateBean eb=new EvaluateBean();
            eb.ShopID = in.readInt();
            eb.ShopName = in.readString();
            eb.OrderNoAndProID = in.readString();
            eb.CommentLevel = in.readString();
            eb.CommentContent = in.readString();
            eb.ProductName = in.readString();
            eb.Pro_Des = in.readString();
            eb.Pro_Service = in.readString();
            eb.Pro_Logistics = in.readString();
            eb.IsAdditional = in.readString();
            eb.StartNumber = in.readInt();
            eb.src = in.readString();
            eb.ProductId = in.readString();
            eb.orderNumber = in.readString();
            eb.orderNumber = in.readString();
            eb.Name = in.readString();
            return eb;
        }

        @Override
        public EvaluateBean[] newArray(int size) {
            return new EvaluateBean[size];
        }
    };

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getOrderNoAndProID() {
        return OrderNoAndProID;
    }

    public void setOrderNoAndProID(String orderNoAndProID) {
        OrderNoAndProID = orderNoAndProID;
    }

    public String getCommentLevel() {
        return CommentLevel;
    }

    public void setCommentLevel(String commentLevel) {
        CommentLevel = commentLevel;
    }

    public String getCommentContent() {
        return CommentContent;
    }

    public void setCommentContent(String commentContent) {
        CommentContent = commentContent;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getPro_Des() {
        return Pro_Des;
    }

    public void setPro_Des(String pro_Des) {
        Pro_Des = pro_Des;
    }

    public String getPro_Service() {
        return Pro_Service;
    }

    public void setPro_Service(String pro_Service) {
        Pro_Service = pro_Service;
    }

    public String getPro_Logistics() {
        return Pro_Logistics;
    }

    public void setPro_Logistics(String pro_Logistics) {
        Pro_Logistics = pro_Logistics;
    }

    public String getIsAdditional() {
        return IsAdditional;
    }

    public void setIsAdditional(String isAdditional) {
        IsAdditional = isAdditional;
    }

    int ShopID;
    String ShopName;
    String OrderNoAndProID;
    String CommentLevel;
    String CommentContent;
    String ProductName;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    String Name;
    String ProductId;
    String orderNumber;
    String Pro_Des;
    String Pro_Service;
    String Pro_Logistics;
    String IsAdditional;
    String src;
    String Contents;
    String Contents_state;
    String Contents_time;
    String Date;
    Drawable drawable;
    List<String> imgpath;
    String evaluateStringLength;
    boolean IsFocusable;
    int StartNumber;

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        this.Contents = contents;
    }

    public String getContents_state() {
        return Contents_state;
    }

    public void setContents_state(String contents_state) {
        this.Contents_state = contents_state;
    }

    public String getContents_time() {
        return Contents_time;
    }

    public void setContents_time(String contents_time) {
        this.Contents_time = contents_time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public boolean isFocusable() {
        return IsFocusable;
    }

    public void setFocusable(boolean focusable) {
        IsFocusable = focusable;
    }

    public String getEvaluateStringLength() {
        return evaluateStringLength;
    }

    public void setEvaluateStringLength(String evaluateStringLength) {
        this.evaluateStringLength = evaluateStringLength;
    }

    public List<String> getImgpath() {
        return imgpath;
    }

    public void setImgpath(List<String> imgpath) {
        this.imgpath = imgpath;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getStartNumber() {
        return StartNumber;
    }

    public void setStartNumber(int startNumber) {
        StartNumber = startNumber;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ShopID);
        dest.writeString(ShopName);
        dest.writeString(OrderNoAndProID);
        dest.writeString(CommentLevel);
        dest.writeString(CommentContent);
        dest.writeString(ProductName);
        dest.writeString(Pro_Des);
        dest.writeString(Pro_Service);
        dest.writeString(Pro_Logistics);
        dest.writeString(IsAdditional);
        dest.writeString(ProductId);
        dest.writeString(orderNumber);
        dest.writeString(Name);
        dest.writeInt(StartNumber);
        dest.writeString(src);
    }
}
