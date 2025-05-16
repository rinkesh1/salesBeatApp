package com.newsalesbeatApp.pojo;

import java.io.Serializable;

/**
 * Created by MTC on 04-08-2017.
 */

public class MyProduct implements Serializable {
    String mySkus, brand, price,weight, unit, product_id, quantity, conversion,imageStr;

    public String getMySkus() {
        return this.mySkus;
    }

    public void setMySkus(String mySkus) {
        this.mySkus = mySkus;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProductId() {
        return this.product_id;
    }

    public void setProductId(String productId) {
        this.product_id = productId;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String qty) {
        this.quantity = qty;
    }

    public String getConversion() {
        return this.conversion;
    }

    public void setConversion(String conversion) {
        this.conversion = conversion;
    }

    public String getImageStr() {
        return imageStr;
    }

    public void setImageStr(String imageStr) {
        this.imageStr = imageStr;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
