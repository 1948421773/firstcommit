package com.musicdo.musicshop.bean;

/**
 * Created by Yuedu on 2017/10/19.
 */

public class PersonalRecommend {
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String shortName) {
        ShortName = shortName;
    }

    public String getSrcDetail() {
        return SrcDetail;
    }

    public void setSrcDetail(String srcDetail) {
        SrcDetail = srcDetail;
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

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public int getSaleCount() {
        return SaleCount;
    }

    public void setSaleCount(int saleCount) {
        SaleCount = saleCount;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    int ID;
    String Name;
    String ShortName;
    String SrcDetail;
    double MarketPrice;
    double MemberPrice;
    int Score;
    int SaleCount;
    int ShopID;
}
