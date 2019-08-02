package com.musicdo.musicshop.bean;

/**
 * 订单详情列表item实体类
 * Created by Administrator on 2017/9/5.
 */

public class OrderDetailItemBean {
    int ID;
    String OrderNumber;
    int ProductID;
    String UserName;
    int Count;
    int CategoryID;
    String Name;
    double MarketPrice;
    double MemberPrice;
    double OrderPrice;
    int Weight;
    String PropertysText;
    String PropertysID;
    String Src;
    String Messages;
    int DeliveryId;
    double DeliveryPrice;
    int Discount;
    int BuyType;
    String DeepSalePrortionInfo;
    String CreateDate;
    int SpecId;
    int SourceIndex;
    int SourceID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

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

    public double getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        OrderPrice = orderPrice;
    }

    public int getWeight() {
        return Weight;
    }

    public void setWeight(int weight) {
        Weight = weight;
    }

    public String getPropertysText() {
        return PropertysText;
    }

    public void setPropertysText(String propertysText) {
        PropertysText = propertysText;
    }

    public String getPropertysID() {
        return PropertysID;
    }

    public void setPropertysID(String propertysID) {
        PropertysID = propertysID;
    }

    public String getSrc() {
        return Src;
    }

    public void setSrc(String src) {
        Src = src;
    }

    public String getMessages() {
        return Messages;
    }

    public void setMessages(String messages) {
        Messages = messages;
    }

    public int getDeliveryId() {
        return DeliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        DeliveryId = deliveryId;
    }

    public double getDeliveryPrice() {
        return DeliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        DeliveryPrice = deliveryPrice;
    }

    public int getDiscount() {
        return Discount;
    }

    public void setDiscount(int discount) {
        Discount = discount;
    }

    public int getBuyType() {
        return BuyType;
    }

    public void setBuyType(int buyType) {
        BuyType = buyType;
    }

    public String getDeepSalePrortionInfo() {
        return DeepSalePrortionInfo;
    }

    public void setDeepSalePrortionInfo(String deepSalePrortionInfo) {
        DeepSalePrortionInfo = deepSalePrortionInfo;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
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
}
