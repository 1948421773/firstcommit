package com.musicdo.musicshop.bean;

/**
 * 描述:
 * 作者：haiming on 2017/7/22 09:51
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class MusicalBean {
    public int ID;
    public double MarketPrice;
    public int OrderID;
    public int BrandID;
    public int IsRecommond;
    public int IsHotSale;
    public int IsNew;
    public int IsSpecial;
    public int IsHot;
    public int CanSale;
    public int ShopID;
    public int CategoryID;
    public String Name;
    public String ShortName;
    public String MemberPrice;
    public String Number;
    public String CategoryName;
    public String Family;

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getFamily() {
        return Family;
    }

    public void setFamily(String family) {
        Family = family;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getMarketPrice() {
        return MarketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        MarketPrice = marketPrice;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public int getBrandID() {
        return BrandID;
    }

    public void setBrandID(int brandID) {
        BrandID = brandID;
    }

    public int getIsRecommond() {
        return IsRecommond;
    }

    public void setIsRecommond(int isRecommond) {
        IsRecommond = isRecommond;
    }

    public int getIsHotSale() {
        return IsHotSale;
    }

    public void setIsHotSale(int isHotSale) {
        IsHotSale = isHotSale;
    }

    public int getIsNew() {
        return IsNew;
    }

    public void setIsNew(int isNew) {
        IsNew = isNew;
    }

    public int getIsSpecial() {
        return IsSpecial;
    }

    public void setIsSpecial(int isSpecial) {
        IsSpecial = isSpecial;
    }

    public int getIsHot() {
        return IsHot;
    }

    public void setIsHot(int isHot) {
        IsHot = isHot;
    }

    public int getCanSale() {
        return CanSale;
    }

    public void setCanSale(int canSale) {
        CanSale = canSale;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
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

    public String getMemberPrice() {
        return MemberPrice;
    }

    public void setMemberPrice(String memberPrice) {
        MemberPrice = memberPrice;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    public String getSrcDetail() {
        return SrcDetail;
    }

    public void setSrcDetail(String srcDetail) {
        SrcDetail = srcDetail;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getSrcLogo() {
        return SrcLogo;
    }

    public void setSrcLogo(String srcLogo) {
        SrcLogo = srcLogo;
    }

    public String getShopAddress() {
        return ShopAddress;
    }

    public void setShopAddress(String shopAddress) {
        ShopAddress = shopAddress;
    }

    public String getShopIsClose() {
        return ShopIsClose;
    }

    public void setShopIsClose(String shopIsClose) {
        ShopIsClose = shopIsClose;
    }

    public String getShopIsAuthentication() {
        return ShopIsAuthentication;
    }

    public void setShopIsAuthentication(String shopIsAuthentication) {
        ShopIsAuthentication = shopIsAuthentication;
    }

    String BrandName;
    String SrcDetail;
    String ShopName;
    String SrcLogo;
    String ShopAddress;
    String ShopIsClose;
    String ShopIsAuthentication;
}
