package com.example.chatapp;

public class ChatInfo {


      private String text;
      private String name;
      private String sender_id;
      private String photoUri;

    public ChatInfo() { }

    public ChatInfo(String sender_id, String sender_name, String text,String photoUri) {
        this.text = text;
        this.name = sender_name;
        this.sender_id = sender_id;
        this.photoUri = photoUri;
        }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getPhotoUri() {
        return photoUri;
    }
}
