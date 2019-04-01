package com.example.artisansfinal;

public class UserInfo {
    public String userName;
    public String userPcode;
    public String userPnumber;
    public String userEmail;
    public String userWallet;
    public String FCMToken;
    public String UID;

    public UserInfo(){

    }


    public UserInfo(String userName, String userPcode, String userPnumber,String userWallet, String userEmail, String UID) {
        this.userName = userName;
        this.userPcode = userPcode;
        this.userPnumber = userPnumber;
        this.userEmail = userEmail;
        this.userWallet= userWallet;
        this.UID=UID;
    }
}
