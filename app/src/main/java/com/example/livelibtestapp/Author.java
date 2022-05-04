package com.example.livelibtestapp;

public class Author {
    private int id;
    private String full_Name;
    private String biography;
    private String img_res;

    public Author(){}

    public Author(int id, String full_Name, String biography, String img_res) {
        this.id = id;
        this.full_Name = full_Name;
        this.biography = biography;
        this.img_res = img_res;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFull_Name() {
        return full_Name;
    }

    public void setFull_Name(String full_Name) {
        this.full_Name = full_Name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getImg_res() {
        return img_res;
    }

    public void setImg_res(String img_res) {
        this.img_res = img_res;
    }
}
