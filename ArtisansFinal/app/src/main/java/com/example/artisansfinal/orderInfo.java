package com.example.artisansfinal;

public class orderInfo {
    String date, name, price, userUID;
    String c;
    //added for userOrderHistory
    String productCategory, productID;
    String reviewExists,userEmail,fcmToken;
    String quantity;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public orderInfo(String name, String price, String date, String userUID) {
        this.date = date;
        this.name = name;
        this.price = price;
        this.userUID = userUID;
        this.c = "d";
        this.reviewExists="false";
    }

    public orderInfo(String date, String name, String price) {
        this.date = date;
        this.name = name;
        this.price = price;
    }

    public orderInfo(String name, String price, String date, String userUID, String productCategory, String productID, String userEmail,String FCMToken) {
        this.date = date;
        this.name = name;
        this.price = price;
        this.userUID = userUID;
        this.productCategory = productCategory;
        this.productID = productID;
        this.userEmail=userEmail;
        this.fcmToken=FCMToken;
        this.reviewExists="false";
        this.c = "d";
    }

//    public orderInfo(String date, String name, String price, String userUID, String c, String productCategory, String productID, String reviewExists, String userEmail, String fcmToken) {
//        this.date = date;
//        this.name = name;
//        this.price = price;
//        this.userUID = userUID;
//        this.c = c;
//        this.productCategory = productCategory;
//        this.productID = productID;
//        this.reviewExists = reviewExists;
//        this.userEmail = userEmail;
//        this.fcmToken = fcmToken;
//    }

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

    public String getFcmToken() {
        return fcmToken;
    }

    public String getReviewExists() {
        return reviewExists;
    }

    public String getUserEmail() {
        return userEmail;
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
