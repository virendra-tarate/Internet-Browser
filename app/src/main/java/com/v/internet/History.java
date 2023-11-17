package com.v.internet;

public class History {


    private String title;
    private String url;

    //    Empty Constructor for firebase
    public History() {
    }

    //Getters and Setters


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //Constructor


    public History(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
