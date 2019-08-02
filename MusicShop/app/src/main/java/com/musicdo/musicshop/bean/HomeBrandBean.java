package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述:产品分类-品牌馆-国内名牌
 * 作者：haiming on 2017/7/24 16:56
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class HomeBrandBean implements Parcelable{
    int ID;
    int OrderID;
    int IsShow;
    int IsRecom;
    int CategoryID;
    int NetType;
    int IsMenuShow;
    int IsBid;
    int BidOrderID;
    int SaleWeekCount;
    String Name;
    String Url;
    String Logo;
    String SrcDetail;
    String Keywords;
    String Description;
    String Content;
    String UNO;
    String EngName;
    String Place;
    String Product;
    String CategoryFamily;
    String Letter;
    String Rang;
    String Activity;
    String shopid;


    public static final Creator<HomeBrandBean> CREATOR = new Creator<HomeBrandBean>() {
        @Override
        public HomeBrandBean createFromParcel(Parcel in) {
            HomeBrandBean hb=new HomeBrandBean();
            hb.ID = in.readInt();
            hb.OrderID = in.readInt();
            hb.IsShow = in.readInt();
            hb.IsRecom = in.readInt();
            hb.CategoryID = in.readInt();
            hb.NetType = in.readInt();
            hb.IsMenuShow = in.readInt();
            hb.IsBid = in.readInt();
            hb.BidOrderID = in.readInt();
            hb.SaleWeekCount = in.readInt();
            hb.Name = in.readString();
            hb.Url = in.readString();
            hb.Logo = in.readString();
            hb.SrcDetail = in.readString();
            hb.Keywords = in.readString();
            hb.Description = in.readString();
            hb.Content = in.readString();
            hb.UNO = in.readString();
            hb.EngName = in.readString();
            hb.Place = in.readString();
            hb.Product = in.readString();
            hb.CategoryFamily = in.readString();
            hb.Letter = in.readString();
            hb. Rang = in.readString();
            hb.Activity = in.readString();
            hb.shopid = in.readString();
            return hb;
        }

        @Override
        public HomeBrandBean[] newArray(int size) {
            return new HomeBrandBean[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public int getIsShow() {
        return IsShow;
    }

    public void setIsShow(int isShow) {
        IsShow = isShow;
    }

    public int getIsRecom() {
        return IsRecom;
    }

    public void setIsRecom(int isRecom) {
        IsRecom = isRecom;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public int getNetType() {
        return NetType;
    }

    public void setNetType(int netType) {
        NetType = netType;
    }

    public int getIsMenuShow() {
        return IsMenuShow;
    }

    public void setIsMenuShow(int isMenuShow) {
        IsMenuShow = isMenuShow;
    }

    public int getIsBid() {
        return IsBid;
    }

    public void setIsBid(int isBid) {
        IsBid = isBid;
    }

    public int getBidOrderID() {
        return BidOrderID;
    }

    public void setBidOrderID(int bidOrderID) {
        BidOrderID = bidOrderID;
    }

    public int getSaleWeekCount() {
        return SaleWeekCount;
    }

    public void setSaleWeekCount(int saleWeekCount) {
        SaleWeekCount = saleWeekCount;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getSrcDetail() {
        return SrcDetail;
    }

    public void setSrcDetail(String srcDetail) {
        SrcDetail = srcDetail;
    }

    public String getKeywords() {
        return Keywords;
    }

    public void setKeywords(String keywords) {
        Keywords = keywords;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getUNO() {
        return UNO;
    }

    public void setUNO(String UNO) {
        this.UNO = UNO;
    }

    public String getEngName() {
        return EngName;
    }

    public void setEngName(String engName) {
        EngName = engName;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getCategoryFamily() {
        return CategoryFamily;
    }

    public void setCategoryFamily(String categoryFamily) {
        CategoryFamily = categoryFamily;
    }

    public String getLetter() {
        return Letter;
    }

    public void setLetter(String letter) {
        Letter = letter;
    }

    public String getRang() {
        return Rang;
    }

    public void setRang(String rang) {
        Rang = rang;
    }

    public String getActivity() {
        return Activity;
    }

    public void setActivity(String activity) {
        Activity = activity;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(OrderID);
        dest.writeInt(IsShow);
        dest.writeInt(IsRecom);
        dest.writeInt(CategoryID);
        dest.writeInt(NetType);
        dest.writeInt(IsMenuShow);
        dest.writeInt(IsBid);
        dest.writeInt(BidOrderID);
        dest.writeInt(SaleWeekCount);
        dest.writeString(Name);
        dest.writeString(Url);
        dest.writeString(Logo);
        dest.writeString(SrcDetail);
        dest.writeString(Keywords);
        dest.writeString(Description);
        dest.writeString(Content);
        dest.writeString(UNO);
        dest.writeString(EngName);
        dest.writeString(Place);
        dest.writeString(Product);
        dest.writeString(CategoryFamily);
        dest.writeString(Letter);
        dest.writeString(Rang);
        dest.writeString(Activity);
        dest.writeString(shopid);
    }
}
