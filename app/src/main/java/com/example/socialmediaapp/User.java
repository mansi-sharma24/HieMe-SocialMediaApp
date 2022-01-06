package com.example.socialmediaapp;

public class User {
    private String name;

    public User(String name, String profession, String email, String password, String bio) {
        this.name = name;
        this.profession = profession;
        this.email = email;
        this.password = password;
        this.bio = bio;
    }

    private String profession;
    private String email;
    private String password;
    private String bio;
    private String cover_img;
    private String userID;


    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }



    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    private int followerCount;

    private int commentCount;

    public User(int commentCount, int postLike) {
        this.commentCount = commentCount;
        this.postLike = postLike;
    }

    private int postLike;


    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }
    private String profile_img;


    public User(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public User() {
    }

    public String getCover_img() {
        return cover_img;
    }

    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
