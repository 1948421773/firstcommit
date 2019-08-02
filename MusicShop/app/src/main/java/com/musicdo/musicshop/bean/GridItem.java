package com.musicdo.musicshop.bean;

/**
 * 名 称 ：
 * 描 述 ：
 * 创建者：  张海明
 * 创建时间：2017/7/7.
 * 版 本 ：
 * 备 注 ：
 */

public class GridItem {
    /**
     * 图片的路径
     */
    private String path;
    /**
     * 图片加入手机中的时间，只取了年月日
     */
    private String time;



    private int prodid;
    /**
     * 每个Item对应的HeaderId
     */
    private int headerId;
   public GridItem(int prodid,String path,String time,int headerId){
       super();
       this.path = path;
       this.time = time;
       this.prodid = prodid;
       this.headerId = headerId;
   }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getHeaderId() {
        return headerId;
    }
    public void setHeaderId(int headerId) {
        this.headerId = headerId;
    }
    public int getProdid() {
        return prodid;
    }

    public void setProdid(int prodid) {
        this.prodid = prodid;
    }

}
