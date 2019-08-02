package com.musicdo.musicshop.bean;

/**
 * 店铺商品分类
 * Created by Yuedu on 2017/11/17.
 */

public class ShopCatetoryBean {
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

    int ID;
    String Name;
}
