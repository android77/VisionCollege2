package com.example.seung.visioncollege;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;


public class Post {


    private String title, desc, image, date;

    public Post(){

    }

    public Post(String title, String desc, String image, String date) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDate(String date){
        this.date = date;
    }


}