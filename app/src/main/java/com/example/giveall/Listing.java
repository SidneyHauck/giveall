package com.example.giveall;

public class Listing {
    private String title;
    private String description;
    private String date;
    private String firstName;

    public Listing(String title, String description, String date, String firstName) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.firstName = firstName;
    }
    public Listing() {}

    public String getTitle(){ return title; }

    public void setTitle(String title) { this.title = title; }

    public String getFirstName() { return firstName; }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) { this.description = description; }

    public String getDate(){
        return date;
    }

    public void setDate(String date) { this.date = date; }

}