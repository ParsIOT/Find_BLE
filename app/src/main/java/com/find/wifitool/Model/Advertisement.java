package com.find.wifitool.Model;

import java.util.ArrayList;

/**
 * Created by root on 4/18/17.
 */

public class Advertisement {
    private int id;
    private String title;
    private String text;
    private ArrayList<Section> sections;

    public Advertisement(int id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.sections = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void setSections(ArrayList<Section> sections) {
        this.sections = sections;
    }



}
