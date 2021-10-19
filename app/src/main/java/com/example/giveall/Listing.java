package com.example.giveall;

public class Listing {
    private String title;
    private String date;
    private String firstName;
    private String profileImage;
    private String dbKey;
    private String userID;
    private String listingImage;

    public Listing(String title, String date, String firstName, String profileImage, String dbKey, String userID, String listingImage) {
        this.title = title;
        this.date = date;
        this.firstName = firstName;
        this.profileImage = profileImage;
        this.dbKey = dbKey;
        this.userID = userID;
        this.listingImage = listingImage;
    }
    public Listing() {}

    public String getTitle(){ return title; }

    public void setTitle(String title) { this.title = title; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getDate(){
        return date;
    }

    public void setDate(String date) { this.date = date; }

    public String getKey() { return dbKey;}

    public void setKey(String dbKey) { this.dbKey = dbKey; }

    public String getUserID() { return userID; }

    public void setUserID(String userID) { this.userID = userID; }

    public String getImage() {
        return listingImage;
    }

    public void setImage(String listingImage) {
        this.listingImage = listingImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}