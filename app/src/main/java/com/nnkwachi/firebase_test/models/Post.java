package com.nnkwachi.firebase_test.models;

public class Post {
    private String id;
    private  String time_of_post;
    private String like_count;
    private String description;
    private String postPhoto;
    private String user_id;
    private String profile_image;

    public Post() {
    }

    public Post(String timeOfPost, String likeCount, String description, String postPhotoUrl, String user_id) {
        this.time_of_post = timeOfPost;
        this.like_count = likeCount;
        this.description = description;
        this.postPhoto = postPhotoUrl;
        this.user_id = user_id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setTime_of_post(String time_of_post) {
        this.time_of_post = time_of_post;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPostPhoto(String postPhoto) {
        this.postPhoto = postPhoto;
    }

    public void setUser_id(String user_id){this.user_id = user_id;}






    public String getId() {
        return id;
    }


    public String getTime_of_post() {
        return time_of_post;
    }

    public String getLike_count() {
        return like_count;
    }

    public String getDescription() {
        return description;
    }

    public String getPostPhoto() {
        return postPhoto;
    }

    public String getProfile_image() {
        return profile_image;
    }


    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
