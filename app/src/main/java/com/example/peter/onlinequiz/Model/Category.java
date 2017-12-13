package com.example.peter.onlinequiz.Model;

import android.media.Image;

/**
 * Created by Peter on 01.12.2017.
 */

public class Category {
    private String Name;
    private String Image;

    public Category(){}

    public Category(String name,String image){
        Name=name;
        Image=image;


    }

    public String getImage() {
        return Image;
    }

    public String getName() {
        return Name;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setName(String name) {
        Name = name;
    }
}
