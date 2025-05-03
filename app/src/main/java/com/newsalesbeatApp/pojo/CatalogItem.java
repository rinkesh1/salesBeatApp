package com.newsalesbeatApp.pojo;

import java.io.Serializable;

/**
 * Created by MTC on 28-09-2017.
 */

public class CatalogItem implements Serializable {

    private String catalogImageUrl, catalogThumbImageUrl, catalogDescription;

    public String getCatalogImageUrl() {
        return this.catalogImageUrl;
    }

    public void setCatalogImageUrl(String catalogImageUrl) {
        this.catalogImageUrl = catalogImageUrl;
    }

    public String getCatalogThumbImageUrl() {
        return this.catalogThumbImageUrl;
    }

    public void setCatalogThumbImageUrl(String catalogThumbImageUrl) {
        this.catalogThumbImageUrl = catalogThumbImageUrl;
    }

    public String getCatalogDescription() {
        return this.catalogDescription;
    }

    public void setCatalogDescription(String catalogDescription) {
        this.catalogDescription = catalogDescription;
    }
}
