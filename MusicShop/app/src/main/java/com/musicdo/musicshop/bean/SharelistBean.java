package com.musicdo.musicshop.bean;

/**
 * Created by Yuedu on 2019/2/27.
 */

public class SharelistBean {
    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getShareProID() {
        return ShareProID;
    }

    public void setShareProID(int shareProID) {
        ShareProID = shareProID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getShareDate() {
        return ShareDate;
    }

    public void setShareDate(String shareDate) {
        ShareDate = shareDate;
    }

    public String getProname() {
        return proname;
    }

    public void setProname(String proname) {
        this.proname = proname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int rowId;
    public int ID;
    public int UserID;
    public int ShareProID;
    public String UserName;
    public String ShareDate;
    public String proname;
    public String date;

    public String getSrcDetail() {
        return SrcDetail;
    }

    public void setSrcDetail(String srcDetail) {
        SrcDetail = srcDetail;
    }

    public String SrcDetail;

}
