package com.parsin.bletool.Model;

public class EmkanatRefahi {
    private int id;
    private int imageRes;
    private String title;
    private String xy;



    public EmkanatRefahi(int id, int imageRes, String title, String xy) {
        this.id = id;
        this.imageRes = imageRes;
        this.title = title;
        this.xy = xy;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getTitle() {
        return title;
    }

    public String getXy() {
        return xy;
    }
}
