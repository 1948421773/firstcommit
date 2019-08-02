package com.musicdo.musicshop.bean;

/**
 * Created by Yuedu on 2017/11/14.
 */

public class ShopIndexBaseBasic {
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

    public String getShopIco() {
        return ShopIco;
    }

    public void setShopIco(String shopIco) {
        ShopIco = shopIco;
    }

    public int getIsConcern() {
        return IsConcern;
    }

    public void setIsConcern(int isConcern) {
        IsConcern = isConcern;
    }

    int ShopID;
    String ShopName;
    String ShopIco;
    int IsConcern;
}
