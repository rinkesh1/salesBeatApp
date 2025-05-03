package com.newsalesbeatApp.pojo;

import java.util.ArrayList;

/*
 * Created by Dhirendra Thakur on 12-03-2018.
 */

public class PCOrderHistoryItem {


    private String rid, retName, retAddress, retPhone, did, disName, orderType, takenDate;
    private ArrayList<MyProduct> myOrderHistoryList = new ArrayList<>();

    public String getRid() {
        return this.rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRetName() {
        return this.retName;
    }

    public void setRetName(String retName) {
        this.retName = retName;
    }

    public String getRetAddress() {
        return this.retAddress;
    }

    public void setRetAddress(String retAddress) {
        this.retAddress = retAddress;
    }

    public String getRetPhone() {
        return this.retPhone;
    }

    public void setRetPhone(String retPhone) {
        this.retPhone = retPhone;
    }

    public String getDid() {
        return this.did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getDisName() {
        return this.disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getOrderType() {
        return this.orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getTakenDate() {
        return this.takenDate;
    }

    public void setTakenDate(String takenDate) {
        this.takenDate = takenDate;
    }

    public ArrayList<MyProduct> getMyOrderHistoryList() {
        return this.myOrderHistoryList;
    }

    public void setMyOrderHistoryList(ArrayList<MyProduct> myOrderHistoryList) {
        this.myOrderHistoryList = myOrderHistoryList;
    }

}
