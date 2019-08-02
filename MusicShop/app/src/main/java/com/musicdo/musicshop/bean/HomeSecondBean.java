package com.musicdo.musicshop.bean;

import java.util.List;

/**
 *主页品牌商品分类列表
 * Created by Yuedu on 2017/9/13.
 */

public class HomeSecondBean {
    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFamily() {
        return Family;
    }

    public void setFamily(String family) {
        Family = family;
    }

    public List<MusicalBean> get_List() {
        return _List;
    }

    public void set_List(List<MusicalBean> _List) {
        this._List = _List;
    }

    public List<MusicalBean_List1> get_List1() {
        return _List1;
    }

    public void set_List1(List<MusicalBean_List1> _List1) {
        this._List1 = _List1;
    }

    int CategoryID;
    String Name;
    String Family;
    List<MusicalBean> _List;
    List<MusicalBean_List1> _List1;
}
