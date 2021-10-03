package com.example.giveall;

public class Listing {
    private String title;
    private String description;
    private String date;
    private String firstName;
    private String dbKey;
    private String userID;

    public Listing(String title, String description, String date, String firstName, String dbKey, String userID) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.firstName = firstName;
        this.dbKey = dbKey;
        this.userID = userID;
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

    public String getKey() { return dbKey;}

    public String getUserID() {return userID; }

}