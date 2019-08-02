package com.musicdo.musicshop.bean;

import java.util.List;

/**
 * 描述:
 * 作者：haiming on 2017/8/25 14:54
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class AllOrderBean {

    int ShopID;

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    String ProductName;

    public int getStatusID() {
        return StatusID;
    }

    public void setStatusID(int statusID) {
        StatusID = statusID;
    }

    int StatusID;
    String ShopName;

    public List<OrderProObjBean> getOrderProObj() {
        return OrderProObj;
    }

    public void setOrderProObj(List<OrderProObjBean> orderProObj) {
        OrderProObj = orderProObj;
    }

    List<OrderProObjBean> OrderProObj;
    List<OrderDetailBean> OrderDetail;
    double TotalPrice;
    int Count;
    String OrderNumber;

    public String getReceiveName() {
        return ReceiveName;
    }

    public void setReceiveName(String receiveName) {
        ReceiveName = receiveName;
    }

    public String getReceiveMobile() {
        return ReceiveMobile;
    }

    public void setReceiveMobile(String receiveMobile) {
        ReceiveMobile = receiveMobile;
    }

    public String getReceiveAddress() {
        return ReceiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        ReceiveAddress = receiveAddress;
    }

    String ReceiveName;
    String ReceiveMobile;
    String ReceiveAddress;

    public String getPayTime() {
        return PayTime;
    }

    public void setPayTime(String payTime) {
        PayTime = payTime;
    }

    String PayTime;

    public String getCompleteTime() {
        return CompleteTime;
    }

    public void setCompleteTime(String completeTime) {
        CompleteTime = completeTime;
    }

    String CompleteTime;

    public String getDeliverTime() {
        return DeliverTime;
    }

    public void setDeliverTime(String deliverTime) {
        DeliverTime = deliverTime;
    }

    String DeliverTime;

    int Status;

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    String CreateTime;
    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public List<OrderDetailBean> getOrderDetail() {
        return OrderDetail;
    }

    public void setOrderDetail(List<OrderDetailBean> orderDetail) {
        OrderDetail = orderDetail;
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        TotalPrice = totalPrice;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getLogisticsName() {
        return LogisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        LogisticsName = logisticsName;
    }

    public String getLogisticsCode() {
        return LogisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        LogisticsCode = logisticsCode;
    }

    String LogisticsName;
    String LogisticsCode;

}
