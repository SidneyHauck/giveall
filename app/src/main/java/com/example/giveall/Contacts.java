package com.example.giveall;

public class Contacts {
    public String name, image;

    public Contacts(){ }


    public Contacts(String name, String image) {
        this.image = image;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}