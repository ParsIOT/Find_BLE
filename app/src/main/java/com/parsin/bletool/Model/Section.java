package com.parsin.bletool.Model;

/**
 * Created by root on 4/19/17.
 */

public class Section {
    private String section_name;
    private float t_x;
    private float t_y;
    private float b_x;
    private float b_y;

    public Section(String section_name, float t_x, float t_y, float b_x, float b_y) {
        this.section_name = section_name;
        this.t_x = t_x;
        this.t_y = t_y;
        this.b_x = b_x;
        this.b_y = b_y;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public float getT_x() {
        return t_x;
    }

    public void setT_x(float t_x) {
        this.t_x = t_x;
    }

    public float getT_y() {
        return t_y;
    }

    public void setT_y(float t_y) {
        this.t_y = t_y;
    }

    public float getB_x() {
        return b_x;
    }

    public void setB_x(float b_x) {
        this.b_x = b_x;
    }

    public float getB_y() {
        return b_y;
    }

    public void setB_y(float b_y) {
        this.b_y = b_y;
    }
}