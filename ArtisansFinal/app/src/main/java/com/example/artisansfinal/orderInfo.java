package com.example.artisansfinal;

public class orderInfo {
    String date,name,price;

    public orderInfo(String name, String price, String date) {
        this.date = date;
        this.name = name;
        this.price = price;
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
}
