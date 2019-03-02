package com.example.artisansfinal;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductInfo implements Parcelable {

    private String productID;
    private String productName;
    private String productDescription;
    private String productCategory;
    private String productPrice;
    private String artisanName;
    private String artisanContactNumber;

    public ProductInfo(String productID, String productName, String productDescription, String productCategory, String productPrice, String artisanName, String artisanContactNumber) {
        this.productID = productID;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.artisanName = artisanName;
        this.artisanContactNumber = artisanContactNumber;
    }

    public ProductInfo(Parcel in) {

    }

    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getArtisanName() {
        return artisanName;
    }

    public String getArtisanContactNumber() {
        return artisanContactNumber;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setArtisanName(String artisanName) {
        this.artisanName = artisanName;
    }

    public void setArtisanContactNumber(String artisanContactNumber) {
        this.artisanContactNumber = artisanContactNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<ProductInfo> CREATOR = new Parcelable.Creator<ProductInfo>() {
        public ProductInfo createFromParcel(Parcel in) {
            return new ProductInfo(in);
        }
        public ProductInfo[] newArray(int size) {
            return new ProductInfo[size];
        }
    };

}