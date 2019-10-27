package com.example.chatapp;

public class UserInfo {

    private String display_name;
    private String status;
    private String image;
    private String thumb_image;
    private String gender;

    public UserInfo(){}

    public UserInfo(String c_name,String c_status,String c_gender,String c_image,String c_thumb)
    {
        display_name = c_name;
        status = c_status;
        image = c_image;
        thumb_image =  c_thumb;
        gender = c_gender;
    }

    public String getImage() {
        return image;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getStatus() {
        return status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public String getGender() {
        return gender;
    }
}
