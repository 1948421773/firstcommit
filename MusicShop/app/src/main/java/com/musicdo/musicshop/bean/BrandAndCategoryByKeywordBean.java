package com.musicdo.musicshop.bean;

import java.util.List;

/**
 * 筛选模型
 * Created by Yuedu on 2017/12/4.
 */

public class BrandAndCategoryByKeywordBean {
    List<BrandAndCategoryByKeywordItemBean> Brand;
    List<BrandAndCategoryByKeywordItemBean> Category;

    public List<BrandAndCategoryByKeywordItemBean> getBrand() {
        return Brand;
    }

    public void setBrand(List<BrandAndCategoryByKeywordItemBean> brand) {
        Brand = brand;
    }

    public List<BrandAndCategoryByKeywordItemBean> getCategory() {
        return Category;
    }

    public void setCategory(List<BrandAndCategoryByKeywordItemBean> category) {
        Category = category;
    }
}
