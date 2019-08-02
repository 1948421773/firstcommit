package com.musicdo.musicshop.bean;

import java.util.List;

/**
 * 店铺三个商品列表
 * Created by Yuedu on 2017/11/16.
 */

public class ShopListBean {

    public List<ProductOfShopBean> getList1() {
        return list1;
    }

    public void setList1(List<ProductOfShopBean> list1) {
        this.list1 = list1;
    }

    public List<ProductOfShopBean> getList2() {
        return list2;
    }

    public void setList2(List<ProductOfShopBean> list2) {
        this.list2 = list2;
    }

    public List<ProductOfShopBean> getList3() {
        return list3;
    }

    public void setList3(List<ProductOfShopBean> list3) {
        this.list3 = list3;
    }

    List<ProductOfShopBean> list1;
    List<ProductOfShopBean> list2;
    List<ProductOfShopBean> list3;
}
