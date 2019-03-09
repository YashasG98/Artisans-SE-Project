package com.example.artisansfinal;

public class orderInfo {
    String date, name, price, userUID;
    String c;

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
