package com.nnkwachi.firebase_test.models;

public class User {
    private String username;
    private  String profile_photo;
    private  String email;

    public User(String username, String profile_photo, String email) {
        this.username = username;
        this.profile_photo = profile_photo;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
