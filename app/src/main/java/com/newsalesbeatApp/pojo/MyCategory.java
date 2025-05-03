package com.newsalesbeatApp.pojo;

/*
 * Created by MTC on 18-07-2017.
 */
public class MyCategory {

    private String category_name, sub_category_name, catBackColor;
    private int icon;

    public String getCategoryBackColor() {
        return this.catBackColor;
    }

    public void setCategoryBackColor(String color) {
        this.catBackColor = color;
    }

    public String getSubCategoryName() {
        return this.sub_category_name;
    }

    public void setSubCategoryName(String subCategory) {
        this.sub_category_name = subCategory;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
