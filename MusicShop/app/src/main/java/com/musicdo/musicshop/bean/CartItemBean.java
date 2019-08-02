package com.musicdo.musicshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述:
 * 作者：haiming on 2017/8/9 15:00
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class CartItemBean implements Parcelable{
    public static final Creator<CartItemBean> CREATOR = new Creator<CartItemBean>() {
        @Override
        public CartItemBean createFromParcel(Parcel in) {
            CartItemBean ci=new CartItemBean();
            ci.Propertys = in.readString();
            ci.ID = in.readInt();
            ci.ProductID = in.readInt();
            ci.CategoryID = in.readInt();
            ci.Count = in.readInt();
            ci.MarketPrice = in.readDouble();
            ci.MemberPrice = in.readDouble();
            ci.Weight = in.readInt();
            ci.UseScore = in.readInt();
            ci.Score = in.readInt();
            ci.ShopID = in.readInt();
            ci.SpecId = in.readInt();
            ci.SourceIndex = in.readInt();
            ci.SourceID = in.readInt();
            ci.OrderPrice = in.readDouble();
            ci.GUID = in.readString();
            ci.Number = in.readString();
            ci.UserName = in.readString();
            ci.Date = in.readString();
            ci.Name = in.readString();
            ci.PropertysText = in.readString();
            ci.IsScore = in.readString();
            ci.SrcDetail = in.readString();
            ci.ShopName = in.readString();
            ci.DeepSalePrortionInfo = in.readString();
            ci.DeliveryPrice = in.readDouble();
            ci.DeliveryId = in.readByte() != 0;
            ci.isEdited = in.readByte() != 0;
            ci.isChoosed = in.readByte() != 0;
            return ci;
        }

        @Override
        public CartItemBean[] newArray(int size) {
            return new CartItemBean[size];
        }
    };

    public String getPropertys() {
        return Propertys;
    }

    public void setPropertys(String propertys) {
        Propertys = propertys;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public Double getMarketPrice() {
        return MarketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        MarketPrice = marketPrice;
    }

    public Double getMemberPrice() {
        return MemberPrice;
    }

    public void setMemberPrice(Double memberPrice) {
        MemberPrice = memberPrice;
    }

    public int getWeight() {
        return Weight;
    }

    public void setWeight(int weight) {
        Weight = weight;
    }

    public int getUseScore() {
        return UseScore;
    }

    public void setUseScore(int useScore) {
        UseScore = useScore;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    public int getSpecId() {
        return SpecId;
    }

    public void setSpecId(int specId) {
        SpecId = specId;
    }

    public int getSourceIndex() {
        return SourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        SourceIndex = sourceIndex;
    }

    public int getSourceID() {
        return SourceID;
    }

    public void setSourceID(int sourceID) {
        SourceID = sourceID;
    }

    public double getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        OrderPrice = orderPrice;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPropertysText() {
        return PropertysText;
    }

    public void setPropertysText(String propertysText) {
        PropertysText = propertysText;
    }

    public String getIsScore() {
        return IsScore;
    }

    public void setIsScore(String isScore) {
        IsScore = isScore;
    }

    public String getSrcDetail() {
        return SrcDetail;
    }

    public void setSrcDetail(String srcDetail) {
        SrcDetail = srcDetail;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getDeepSalePrortionInfo() {
        return DeepSalePrortionInfo;
    }

    public void setDeepSalePrortionInfo(String deepSalePrortionInfo) {
        DeepSalePrortionInfo = deepSalePrortionInfo;
    }

    public double getDeliveryPrice() {
        return DeliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        DeliveryPrice = deliveryPrice;
    }

    public boolean isDeliveryId() {
        return DeliveryId;
    }

    public void setDeliveryId(boolean deliveryId) {
        DeliveryId = deliveryId;
    }

    String Propertys;
    int ID;
    int ProductID;
    int CategoryID;
    int Count;
    double MarketPrice;
    double MemberPrice;
    int Weight;
    int UseScore;
    int Score;
    int ShopID;
    int SpecId;
    int SourceIndex;
    int SourceID;
    double OrderPrice;
    String GUID;
    String Number;
    String UserName;
    String Date;
    String Name;
    String PropertysText;
    String IsScore;
    String SrcDetail;
    String ShopName;
    String DeepSalePrortionInfo;
    double DeliveryPrice;
    boolean DeliveryId;

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }

    boolean isChoosed;
    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    boolean isEdited;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Propertys);
        dest.writeInt(ID);
        dest.writeInt(ProductID);
        dest.writeInt(CategoryID);
        dest.writeInt(Count);
        dest.writeDouble(MarketPrice);
        dest.writeDouble(MemberPrice);
        dest.writeInt(Weight);
        dest.writeInt(UseScore);
        dest.writeInt(Score);
        dest.writeInt(ShopID);
        dest.writeInt(SpecId);
        dest.writeInt(SourceIndex);
        dest.writeInt(SourceID);
        dest.writeDouble(OrderPrice);
        dest.writeString(GUID);
        dest.writeString(Number);
        dest.writeString(UserName);
        dest.writeString(Date);
        dest.writeString(Name);
        dest.writeString(PropertysText);
        dest.writeString(IsScore);
        dest.writeString(SrcDetail);
        dest.writeString(ShopName);
        dest.writeString(DeepSalePrortionInfo);
        dest.writeDouble(DeliveryPrice);
        dest.writeByte((byte) (DeliveryId ? 1 : 0));
        dest.writeByte((byte) (isEdited ? 1 : 0));
        dest.writeByte((byte) (isChoosed ? 1 : 0));
    }
}
