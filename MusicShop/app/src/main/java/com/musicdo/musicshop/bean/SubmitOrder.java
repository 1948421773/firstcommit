package com.musicdo.musicshop.bean;

/**
 * 描述:
 * 作者：haiming on 2017/8/17 11:53
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class SubmitOrder {
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public double getMarketPrice() {
        return MarketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        MarketPrice = marketPrice;
    }

    public double getMemberPrice() {
        return MemberPrice;
    }

    public void setMemberPrice(double memberPrice) {
        MemberPrice = memberPrice;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getPropertys() {
        return Propertys;
    }

    public void setPropertys(String propertys) {
        Propertys = propertys;
    }

    public String getPropertyIDs() {
        return PropertyIDs;
    }

    public void setPropertyIDs(String propertyIDs) {
        PropertyIDs = propertyIDs;
    }

    int ID;
    int Count;
    double MarketPrice;
    double MemberPrice;
    String Name;
    String ImgUrl;
    String Propertys;
    String PropertyIDs;
}
