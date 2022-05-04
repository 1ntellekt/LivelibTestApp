package com.example.livelibtestapp;

import java.io.Serializable;

public class Book implements Serializable {
    private int id;
    private String name_book;
    private String description;
    private String img_res;
    private String genre;
    private int id_author;
    private String about_book;
    private String post;
    private String int_fact;

    public Book(){}

    public Book(int id, String name_book, String description, String img_res, String genre, int id_author, String about_book, String post, String int_fact) {
        this.id = id;
        this.name_book = name_book;
        this.description = description;
        this.img_res = img_res;
        this.genre = genre;
        this.id_author = id_author;
        this.about_book = about_book;
        this.post = post;
        this.int_fact = int_fact;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_book() {
        return name_book;
    }

    public void setName_book(String name_book) {
        this.name_book = name_book;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_res() {
        return img_res;
    }

    public void setImg_res(String img_res) {
        this.img_res = img_res;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getId_author() {
        return id_author;
    }

    public void setId_author(int id_author) {
        this.id_author = id_author;
    }

    public String getAbout_book() {
        return about_book;
    }

    public void setAbout_book(String about_book) {
        this.about_book = about_book;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getInt_fact() {
        return int_fact;
    }

    public void setInt_fact(String int_fact) {
        this.int_fact = int_fact;
    }
}
