package com.musicdo.musicshop.bean;

/**
 * Created by Yuedu on 2017/11/23.
 */

public class ShopCollectBean {
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

    public String getSrcLogo() {
        return SrcLogo;
    }

    public void setSrcLogo(String srcLogo) {
        SrcLogo = srcLogo;
    }

    public int getConcern() {
        return concern;
    }

    public void setConcern(int concern) {
        this.concern = concern;
    }

    int ShopID;
    String ShopName;
    String SrcLogo;
    int concern;
}
