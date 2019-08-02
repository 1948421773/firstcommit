package com.musicdo.musicshop.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 描述:
 * 作者：haiming on 2017/8/25 14:55
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class OrderDetailBean {
    int ID;
    String OrderNumber;
    int ProductID;
    String UserName;
    int Count;
    int CategoryID;
    String Name;
    double  MarketPrice;
    double  MemberPrice;
    int Weight;
    String PropertysText;
    String PropertysID;
    String Src;
    int StatusID;
    int ShopID;

    public int getCommentStatus() {
        return CommentStatus;
    }

    public void setCommentStatus(int commentStatus) {
        CommentStatus = commentStatus;
    }

    int CommentStatus;
    String ShopName;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public int getWeight() {
        return Weight;
    }

    public void setWeight(int weight) {
        Weight = weight;
    }

    public String getPropertysText() {
        return PropertysText;
    }

    public void setPropertysText(String propertysText) {
        PropertysText = propertysText;
    }

    public String getPropertysID() {
        return PropertysID;
    }

    public void setPropertysID(String propertysID) {
        PropertysID = propertysID;
    }

    public String getSrc() {
        return Src;
    }

    public void setSrc(String Src) {
        try {
            this.Src = URLEncoder.encode(Src,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public int getStatusID() {
        return StatusID;
    }

    public void setStatusID(int statusID) {
        StatusID = statusID;
    }

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
}
