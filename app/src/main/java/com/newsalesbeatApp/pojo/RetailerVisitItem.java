package com.newsalesbeatApp.pojo;

import java.util.ArrayList;

/**
 * Created by Dhirendra Thakur on 26-12-2017.
 */

public class RetailerVisitItem {

    String checkIn, checkOut, comment, empName, empPhone, empEmail, empZone;
    ArrayList<SkuItem> orderList = new ArrayList<>();

    public String getCheckIn() {
        return this.checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return this.checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEmpName() {
        return this.empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpPhone() {
        return this.empPhone;
    }

    public void setEmpPhone(String phone) {
        this.empPhone = phone;
    }

    public String getEmpEmail() {
        return this.empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public String getEmpZone() {
        return this.empZone;
    }

    public void setEmpZone(String zone) {
        this.empZone = zone;
    }

    public ArrayList<SkuItem> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<SkuItem> orderList) {
        this.orderList = orderList;
    }
}
