package com.musicdo.musicshop.bean;

/**
 * 82.店铺首页基本资料和Banner
 * Created by Yuedu on 2017/11/14.
 */

public class ShopIndexBase {
    public ShopIndexBaseBasic getBasic() {
        return basic;
    }

    public void setBasic(ShopIndexBaseBasic basic) {
        this.basic = basic;
    }

    public String getImgUrlArray() {
        return ImgUrlArray;
    }

    public void setImgUrlArray(String imgUrlArray) {
        ImgUrlArray = imgUrlArray;
    }

    ShopIndexBaseBasic basic;
    String  ImgUrlArray;
}
