package com.newsalesbeatApp.pojo;

/*
 * Created by Dhirendra Thakur on 23-12-2017.
 */

import java.io.Serializable;

public class SkuItem implements Serializable {

    private String sku;
    private String opening;
    private String closing;
    private String secondary;
    private String conversionFactor;
    private String price;
    private String unit;
    private String weight;

    public String getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(String conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSku() {
        return this.sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getOpening() {
        return this.opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getClosing() {
        return this.closing;
    }

    public void setClosing(String closing) {
        this.closing = closing;
    }

    public String getSecondary() {
        return this.secondary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }
    public String getweight() {
        return this.weight;
    }
    public void setweight(String weight) {
        this.weight = weight;
    }

}
