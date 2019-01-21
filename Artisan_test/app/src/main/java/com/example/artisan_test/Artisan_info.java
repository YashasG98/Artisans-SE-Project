package com.example.artisan_test;

public class Artisan_info
{
    String artisan_id;
    String name;
    String contact_no;
    //String UID;
    String postal_address;
    String username;
    String password;
    String[] skillset;


    public Artisan_info(String artisan_id, String name, String contact_no, String postal_address, String username, String password, String[] skillset) {
        this.artisan_id = artisan_id;
        this.name = name;
        this.contact_no = contact_no;
        //this.UID = UID;
        this.postal_address = postal_address;
        this.username = username;
        this.password = password;
        this.skillset = skillset;
    }

    public String getArtisan_id() {
        return artisan_id;
    }

    public String getName() {
        return name;
    }

    public String getContact_no() {
        return contact_no;
    }

//    public String getUID() {
//        return UID;
//    }

    public String getPostal_address() {
        return postal_address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String[] getSkillset() {
        return skillset;
    }
}
