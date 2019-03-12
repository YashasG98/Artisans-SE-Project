package com.example.artisansfinal;

public class ProductReview {

    private String userName;
    private String rating;
    private String review;

    public ProductReview(String userName, String rating, String review) {
        this.userName = userName;
        this.rating = rating;
        this.review = review;
    }
    public ProductReview(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
