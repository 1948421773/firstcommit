package com.musicdo.musicshop.bean;

/**
 * 描述:
 * 作者：haiming on 2017/8/1 15:07
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class ShopDetailBean {
    public int getProductCount() {
        return ProductCount;
    }

    public void setProductCount(int productCount) {
        ProductCount = productCount;
    }

    public int getProductNewCount() {
        return ProductNewCount;
    }

    public void setProductNewCount(int productNewCount) {
        ProductNewCount = productNewCount;
    }

    public int getShopCollectCount() {
        return ShopCollectCount;
    }

    public void setShopCollectCount(int shopCollectCount) {
        ShopCollectCount = shopCollectCount;
    }

    public double getProDes() {
        return ProDes;
    }

    public void setProDes(double proDes) {
        ProDes = proDes;
    }

    public double getService() {
        return Service;
    }

    public void setService(double service) {
        Service = service;
    }

    public double getLogistics() {
        return Logistics;
    }

    public void setLogistics(double logistics) {
        Logistics = logistics;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getShopIco() {
        return ShopIco;
    }

    public void setShopIco(String shopIco) {
        ShopIco = shopIco;
    }

    int ProductCount;
    int ProductNewCount;
    int ShopCollectCount;
    double ProDes;
    double Service;
    double Logistics;
    String ShopName;
    String ShopIco;

}
