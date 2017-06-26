package com.find.wifitool.Model;

/**
 * Created by Hadi Shamgholi on 2/25/2017.
 */
public class Booth {
    private int id;
    private String name;
    private String owner;
    private String description;
    private String image_url;

    public Booth(int id, String name, String owner, String description, String image_url) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.image_url = image_url;
    }

    public Booth(String name, String owner, String description, String image_url){
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.image_url = image_url;
    }

    public String getImage_url() {
        return image_url;
    }
    public String getDescription() {
        return description;
    }
    public String getName() {
        return name;
    }
    public String getOwner() {
        return owner;
    }
    public int getId() {
        return id;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Booth{" +
                "name='" + name + '\'' +
                ", price='" + owner + '\'' +
                ", description='" + description + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
