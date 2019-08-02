package com.musicdo.musicshop.bean;

import java.util.List;

/**
 * 店铺商品品牌和分类
 * Created by Yuedu on 2017/11/17.
 */

public class ShopCatetoryAndBrandBean {
    public List<CategoryLevelOneBean> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryLevelOneBean> category) {
        this.category = category;
    }

    public List<CategoryLevelOneBean> getBrand() {
        return brand;
    }

    public void setBrand(List<CategoryLevelOneBean> brand) {
        this.brand = brand;
    }

    List<CategoryLevelOneBean> category;
    List<CategoryLevelOneBean> brand;
}
