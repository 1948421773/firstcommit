package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述:收货地址实体类
 * 作者：haiming on 2017/8/15 16:25
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class AddressBean implements Parcelable{


    public AddressBean() {

    }

    public String getCountyName() {
        return CountyName;
    }

    public void setCountyName(String countyName) {
        if (countyName==null){
            CountyName="";
        }else{
            CountyName = countyName;
        }
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {

        if (provinceName==null){
            ProvinceName="";
        }else{
            ProvinceName = provinceName;
        }
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {

        if (cityName==null){
            CityName="";
        }else{
            CityName = cityName;
        }
    }

    String CountyName="";
    String ProvinceName="";
    String CityName="";
    String ProvinceID="";
    String CountyID="";

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        if (cityID==null){
            CityID="";
        }else{
            CityID = cityID;

        }
    }

    String CityID="";
    int ID=0;
    int UserID=0;
    String Name="";
    String Sex="";
    String Email="";
    String Address="";
    String PostCode="";
    String Tel="";
    String Moblie="";
    String Building="";
    String BestTime="";
    String IsDefault="";
    String UserName="";
    String ReachDate="";
    String AreaId="";

    public final static  Creator<AddressBean> CREATOR = new Creator<AddressBean>() {
        @Override
        public AddressBean createFromParcel(Parcel in) {
            AddressBean ab=new AddressBean();
            ab.ProvinceID = in.readString();
            ab.CountyID = in.readString();
            ab.CityID = in.readString();
            ab.ID = in.readInt();
            ab.UserID = in.readInt();
            ab.Name = in.readString();
            ab.Sex = in.readString();
            ab.Email = in.readString();
            ab.Address = in.readString();
            ab.PostCode = in.readString();
            ab.Tel = in.readString();
            ab.Moblie = in.readString();
            ab.Building = in.readString();
            ab.BestTime = in.readString();
            ab.IsDefault = in.readString();
            ab.UserName = in.readString();
            ab.ReachDate = in.readString();
            ab.AreaId = in.readString();
            ab.CountyName= in.readString();
            ab.ProvinceName= in.readString();
            ab.CityName= in.readString();
            return ab;
        }

        @Override
        public AddressBean[] newArray(int size) {
            return new AddressBean[size];
        }
    };

    public String getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(String provinceID) {
        if (provinceID==null){
            ProvinceID="";
        }else{
            ProvinceID = provinceID;
        }
    }

    public String getCountyID() {
        return CountyID;
    }

    public void setCountyID(String countyID) {

        if (countyID==null){
            CountyID="";
        }else{
            CountyID = countyID;
        }
    }

    public int getID() {
        if (ID==0){
            return 0;
        }
        return ID;
    }

    public void setID(int ID) {

        if (ID==0){
            ID=0;
        }else{
            this.ID = ID;
        }
    }

    public int getUserID() {
        if (UserID==0){
            return 0;
        }
        return UserID;
    }

    public void setUserID(int userID) {

        if (userID==0){
            UserID=0;
        }else{
            UserID = userID;
        }
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        if (name==null){
            Name="";
        }else{
            Name = name;
        }

    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {

        if (sex==null){
            Sex="";
        }else{
            Sex = sex;
        }
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {

        if (email==null){
            Email="";
        }else{
            Email = email;
        }
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        if (address==null){
            Address="";
        }else{
            Address = address;
        }

    }

    public String getPostCode() {
        return PostCode;
    }

    public void setPostCode(String postCode) {

        if (postCode==null){
            PostCode="";
        }else{
            PostCode = postCode;
        }
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        if (tel==null){
            Tel="";
        }else{
            Tel = tel;
        }

    }

    public String getMoblie() {
        return Moblie;
    }

    public void setMoblie(String moblie) {
        if (moblie==null){
            Moblie="";
        }else{
            Moblie = moblie;
        }
    }

    public String getBuilding() {
        return Building;
    }

    public void setBuilding(String building) {

        if (building==null){
            Building="";
        }else {
            Building = building;
        }
    }

    public String getBestTime() {
        return BestTime;
    }

    public void setBestTime(String bestTime) {

        if (bestTime==null){
            BestTime="";
        }else {
            BestTime = bestTime;
        }
    }

    public String getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(String isDefault) {

        if (isDefault==null){
            IsDefault="";
        }else {
            IsDefault = isDefault;
        }
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {

        if (userName==null){
            UserName="";
        }else {
            UserName = userName;
        }
    }

    public String getReachDate() {
        return ReachDate;
    }

    public void setReachDate(String reachDate) {

        if (reachDate==null){
            ReachDate="";
        }else {
            ReachDate = reachDate;
        }
    }

    public String getAreaId() {
        if (AreaId==null){
            AreaId="";
        }
        return AreaId;
    }

    public void setAreaId(String areaId) {

        if (areaId==null){
            AreaId="";
        }else {
            AreaId = areaId;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ProvinceID);
        dest.writeString(CountyID);
        dest.writeString(CityID);
        dest.writeInt(ID);
        dest.writeInt(UserID);
        dest.writeString(Name);
        dest.writeString(Sex);
        dest.writeString(Email);
        dest.writeString(Address);
        dest.writeString(PostCode);
        dest.writeString(Tel);
        dest.writeString(Moblie);
        dest.writeString(Building);
        dest.writeString(BestTime);
        dest.writeString(IsDefault);
        dest.writeString(UserName);
        dest.writeString(ReachDate);
        dest.writeString(AreaId);
        dest.writeString(CountyName);
        dest.writeString(ProvinceName);
        dest.writeString(CityName);
    }





}
