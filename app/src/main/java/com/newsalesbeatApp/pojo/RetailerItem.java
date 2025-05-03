package com.newsalesbeatApp.pojo;

import java.io.Serializable;

/**
 * Created by MTC on 25-07-2017.
 */

public class RetailerItem implements Serializable {

    private String retailer_pin;
    private String retailerbeat_unic_id;
    private String retailer_gstin;
    private String retailer_fssai;
    private String BeatName;
    private String retailer_owner_name;
    private String retailer_unic_id;
    private String retailer_email;
    private String retailer_state;
    private String retailer_name;
    private String retailer_id;
    private String retailer_address;
    private String retailer_phone;
    private String retailer_grade;
    private String locality;
    private String retailer_image;
    private String retailer_city;
    private String whatsAppNo;
    private String orderType;
    private String latitude;
    private String longtitude;
    private String timeStamp;
    public boolean expanded = false;

    public String getServerStatusCode() {
        return serverStatusCode;
    }

    public void setServerStatusCode(String serverStatusCode) {
        this.serverStatusCode = serverStatusCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private String serverStatusCode;
    private String errorMsg;
    private String serverStatus;
    private String reatialerTarget;
    private String reatialerTC;

    public String getReatialerTC() {
        return reatialerTC;
    }

    public void setReatialerTC(String reatialerTC) {
        this.reatialerTC = reatialerTC;
    }

    public String getReatialerPC() {
        return reatialerPC;
    }

    public void setReatialerPC(String reatialerPC) {
        this.reatialerPC = reatialerPC;
    }

    private String reatialerPC;

    public String getReatialerTarget() {
        return reatialerTarget;
    }

    public void setReatialerTarget(String reatialerTarget) {
        this.reatialerTarget = reatialerTarget;
    }

    public String getRetailerName() {
        return this.retailer_name;
    }

    public void setRetailerName(String retailerName) {
        this.retailer_name = retailerName;
    }

    public String getRetailerId() {
        return this.retailer_id;
    }

    public void setRetailerId(String retailerId) {
        this.retailer_id = retailerId;
    }

    public String getRetailerAddress() {
        return retailer_address;
    }

    public void setRetailerAddress(String retailerAddress) {
        this.retailer_address = retailerAddress;
    }

    public String getRetailerPhone() {
        return retailer_phone;
    }

    public void setRetailerPhone(String retailerPhone) {
        this.retailer_phone = retailerPhone;
    }

    public String getBeatName() {
        return BeatName;
    }

    public void setBeatName(String beatName) {
        BeatName = beatName;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getRetailer_pin() {
        return retailer_pin;
    }

    public void setRetailer_pin(String pin) {
        this.retailer_pin = pin;
    }

    public String getRetailerbeat_unic_id() {
        return this.retailerbeat_unic_id;
    }

    public void setRetailerbeat_unic_id(String buid) {
        this.retailerbeat_unic_id = buid;
    }

    public String getRetailer_gstin() {
        return this.retailer_gstin;
    }

    public void setRetailer_gstin(String gstin) {
        this.retailer_gstin = gstin;
    }

    public String getRetailer_fssai() {
        return this.retailer_fssai;
    }

    public void setRetailer_fssai(String fssai) {
        this.retailer_fssai = fssai;
    }

    public String getRetailer_owner_name() {
        return this.retailer_owner_name;
    }

    public void setRetailer_owner_name(String owneerName) {
        this.retailer_owner_name = owneerName;
    }

    public String getRetailer_unic_id() {
        return this.retailer_unic_id;
    }

    public void setRetailer_unic_id(String ruid) {
        this.retailer_unic_id = ruid;
    }

    public String getRetailer_email() {
        return this.retailer_email;
    }

    public void setRetailer_email(String email) {
        this.retailer_email = email;
    }

    public String getRetailer_state() {
        return this.retailer_state;
    }

    public void setRetailer_state(String state) {
        this.retailer_state = state;
    }

    public String getReatilerWhatsAppNo() {
        return this.whatsAppNo;
    }

    public void setReatilerWhatsAppNo(String whatsAppNo) {
        this.whatsAppNo = whatsAppNo;
    }

    public String getRetailer_grade() {
        return this.retailer_grade;
    }

    public void setRetailer_grade(String grade) {
        this.retailer_grade = grade;
    }

    public String getRetailerLocality() {
        return this.locality;
    }

    public void setRetailerLocality(String locality) {
        this.locality = locality;
    }

    public String getRetailer_image() {
        return this.retailer_image;
    }

    public void setRetailer_image(String image) {
        this.retailer_image = image;
    }

    public String getRetailer_city() {
        return this.retailer_city;
    }

    public void setRetailer_city(String city) {
        this.retailer_city = city;
    }

    public String getOrderType() {
        return this.orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getServerStatus() {
        return this.serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }
}
