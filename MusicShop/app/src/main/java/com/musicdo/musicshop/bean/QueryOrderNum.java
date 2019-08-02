package com.musicdo.musicshop.bean;

/**
 * Created by Yuedu on 2019/2/25.
 * 获取不同状态的订单数量
 */

public class QueryOrderNum {
    public int getDaifukuan() {
        return daifukuan;
    }

    public void setDaifukuan(int daifukuan) {
        this.daifukuan = daifukuan;
    }

    public int getDaifahuo() {
        return daifahuo;
    }

    public void setDaifahuo(int daifahuo) {
        this.daifahuo = daifahuo;
    }

    public int getDaishouhuo() {
        return daishouhuo;
    }

    public void setDaishouhuo(int daishouhuo) {
        this.daishouhuo = daishouhuo;
    }

    public int getDaipingjia() {
        return daipingjia;
    }

    public void setDaipingjia(int daipingjia) {
        this.daipingjia = daipingjia;
    }

    public int getTuihuanhuo() {
        return tuihuanhuo;
    }

    public void setTuihuanhuo(int tuihuanhuo) {
        this.tuihuanhuo = tuihuanhuo;
    }

    public int daifukuan;
    public int daifahuo;
    public int daishouhuo;
    public int daipingjia;
    public int tuihuanhuo;
}
