package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 描述:
 * 作者：haiming on 2017/7/31 17:12
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class ProductDetailBean implements Parcelable{
    int ID;
    double MarketPrice;
    double MemberPrice;
    int Score;
    int CategoryID;
    int IsScore;

    public int getIsCollect() {
        return IsCollect;
    }

    public void setIsCollect(int isCollect) {
        IsCollect = isCollect;
    }

    int IsCollect;
    int ShopID;
    int Weight;
    int CommentCount;

    public int getSaleCount() {
        return SaleCount;
    }

    public void setSaleCount(int saleCount) {
        SaleCount = saleCount;
    }

    int SaleCount;


    public static final Creator<ProductDetailBean> CREATOR = new Creator<ProductDetailBean>() {
        @Override
        public ProductDetailBean createFromParcel(Parcel in) {
            ProductDetailBean pdb=new ProductDetailBean();
            pdb.ID = in.readInt();
            pdb.MarketPrice = in.readDouble();
            pdb.MemberPrice = in.readDouble();
            pdb.Score = in.readInt();
            pdb.CategoryID = in.readInt();
            pdb.IsScore = in.readInt();
            pdb.IsCollect = in.readInt();
            pdb.ShopID = in.readInt();
            pdb.Weight = in.readInt();
            pdb.CommentCount = in.readInt();
            pdb.SpecNum = in.readInt();
            pdb.Name = in.readString();
            pdb.ShortName = in.readString();
            pdb.ImgUrl = in.readString();
            pdb.CategoryName = in.readString();
            pdb.ShopName = in.readString();
            pdb.ShopIco = in.readString();
            pdb.ImgList = in.readString();
            pdb.Number = in.readString();
            return pdb;
        }

        @Override
        public ProductDetailBean[] newArray(int size) {
            return new ProductDetailBean[size];
        }
    };

    public int getSpecNum() {
        return SpecNum;
    }

    public void setSpecNum(int specNum) {
        SpecNum = specNum;
    }

    int SpecNum;
    String Name;
    String ShortName;
    String ImgUrl;
    String CategoryName;
    String ShopName;
    String ShopIco;
    String ImgList;
    String Number;

    public ProductDetailBean.Data getData() {
        return Data;
    }

    public void setData(ProductDetailBean.Data data) {
        Data = data;
    }

    Data Data;
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getMarketPrice() {
        return MarketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        MarketPrice = marketPrice;
    }

    public double getMemberPrice() {
        return MemberPrice;
    }

    public void setMemberPrice(double memberPrice) {
        MemberPrice = memberPrice;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public int getIsScore() {
        return IsScore;
    }

    public void setIsScore(int isScore) {
        IsScore = isScore;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    public int getWeight() {
        return Weight;
    }

    public void setWeight(int weight) {
        Weight = weight;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public int getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(int commentCount) {
        CommentCount = commentCount;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String shortName) {
        ShortName = shortName;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getShopIco() {
        return ShopIco;
    }

    public void setShopIco(String shopIco) {
        ShopIco = shopIco;
    }



    public String getImgList() {
        return ImgList;
    }

    public void setImgList(String imgList) {
        ImgList = imgList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeDouble(MarketPrice);
        dest.writeDouble(MemberPrice);
        dest.writeInt(Score);
        dest.writeInt(CategoryID);
        dest.writeInt(IsScore);
        dest.writeInt(IsCollect);
        dest.writeInt(ShopID);
        dest.writeInt(Weight);
        dest.writeInt(CommentCount);
        dest.writeInt(SpecNum);
        dest.writeString(Name);
        dest.writeString(ShortName);
        dest.writeString(ImgUrl);
        dest.writeString(CategoryName);
        dest.writeString(ShopName);
        dest.writeString(ShopIco);
        dest.writeString(ImgList);
        dest.writeString(Number);
    }

    public static class Data{
        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getStarNum() {
            return StarNum;
        }

        public void setStarNum(int starNum) {
            StarNum = starNum;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public String getIco() {
            return Ico;
        }

        public void setIco(String ico) {
            Ico = ico;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }

        int ID;
        int StarNum;
        String UserName;
        String Ico;
        String Content;
        List<CommentImgList>ImgList;
        public List<CommentImgList> getImgList() {
            return ImgList;
        }
        public void setImgList(List<CommentImgList> imgList) {
            ImgList = imgList;
        }



    }
}
