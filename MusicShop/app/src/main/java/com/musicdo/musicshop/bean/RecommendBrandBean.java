package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述:产品分类-品牌馆-推荐品牌
 * 作者：haiming on 2017/7/24 16:48
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class RecommendBrandBean implements Parcelable {

    public static final Creator<RecommendBrandBean> CREATOR = new Creator<RecommendBrandBean>() {
        @Override
        public RecommendBrandBean createFromParcel(Parcel in) {
            RecommendBrandBean rb=new RecommendBrandBean();
            rb.ID = in.readInt();
            rb.OrderID = in.readInt();
            rb.IsShow = in.readInt();
            rb.IsRecom = in.readInt();
            rb.CategoryID = in.readInt();
            rb.NetType = in.readInt();
            rb.IsMenuShow = in.readInt();
            rb.IsBid = in.readInt();
            rb.BidOrderID = in.readInt();
            rb.SaleWeekCount = in.readInt();
            rb.Name = in.readString();
            rb.Url = in.readString();
            rb.Logo = in.readString();
            rb.SrcDetail = in.readString();
            rb.Keywords = in.readString();
            rb.Description = in.readString();
            rb.Content = in.readString();
            rb.UNO = in.readString();
            rb.EngName = in.readString();
            rb.Place = in.readString();
            rb.Product = in.readString();
            rb.CategoryFamily = in.readString();
            rb.Letter = in.readString();
            rb.Rang = in.readString();
            rb.Activity = in.readString();
            rb.shopid = in.readString();
            return  rb;
        }

        @Override
        public RecommendBrandBean[] newArray(int size) {
            return new RecommendBrandBean[size];
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
