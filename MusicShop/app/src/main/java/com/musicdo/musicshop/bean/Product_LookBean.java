package com.musicdo.musicshop.bean;

import java.util.List;

/**
 * Created by Yuedu on 2017/11/23.
 */

public class Product_LookBean {
    String Date;

    public List<DataListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataListBean> dataList) {
        this.dataList = dataList;
    }

    List<DataListBean>dataList;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
