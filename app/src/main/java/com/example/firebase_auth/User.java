package com.example.firebase_auth;

public class User {
    private String Email;
    private String FirstName;
    private String ImageURL;
    private String LastName;
    private String userID;

    public User() {
    }

    public User(String Email, String FirstName, String ImageURL, String LastName, String userID) {
        this.Email = Email;
        this.FirstName = FirstName;
        this.ImageURL = ImageURL;
        this.LastName = LastName;
        this.userID = userID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
