package com.musicdo.musicshop.bean;

/**
 * 店铺中商品新品热销和推荐列表
 * Created by Yuedu on 2017/11/16.
 */

public class ProductOfShopBean {

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductImgUrl() {
        return ProductImgUrl;
    }

    public void setProductImgUrl(String productImgUrl) {
        ProductImgUrl = productImgUrl;
    }

    public int getMemberPrice() {
        return MemberPrice;
    }

    public void setMemberPrice(int memberPrice) {
        MemberPrice = memberPrice;
    }

    public int getMarketPrice() {
        return MarketPrice;
    }

    public void setMarketPrice(int marketPrice) {
        MarketPrice = marketPrice;
    }

    int ProductID;
    String ProductName;
    String ProductImgUrl;
    int MemberPrice;
    int MarketPrice;

}
