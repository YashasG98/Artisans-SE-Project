package com.example.artisansfinal;

public class orderInfo {
    String date, name, price, userUID;
    String c;
    //added for userOrderHistory
    String productCategory, productID;

    public orderInfo(String name, String price, String date, String userUID) {
        this.date = date;
        this.name = name;
        this.price = price;
        this.userUID = userUID;
        this.c = "d";
    }

    public orderInfo(String date, String name, String price) {
        this.date = date;
        this.name = name;
        this.price = price;
    }

    public orderInfo(String name, String price, String date, String userUID, String productCategory, String productID) {
        this.date = date;
        this.name = name;
        this.price = price;
        this.userUID = userUID;
        this.productCategory = productCategory;
        this.productID = productID;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public orderInfo() {
        this.c = "d";
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getUserUID() {
        return userUID;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductID() {
        return productID;
    }

    @Override
    public String toString() {
        return "orderInfo{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", userUID='" + userUID + '\'' +
                '}';
    }
}
