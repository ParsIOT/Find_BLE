package com.parsin.bletool.Model;

/**
 * Created by root on 7/19/17.
 */
public class TodayNews {
    private int id;
    private String title;
    private String text;
    private String image_url;

    public TodayNews(int id, String title, String text, String image_url) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getImage_url() {
        return image_url;
    }
}
