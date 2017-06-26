package com.find.wifitool.Model;

/**
 * Created by Hadi Shamgholi on 3/11/2017.
 */
public class Product {
    private int id;
    private String name; //
    private String model;
    private String description;
    private int price; //
    private boolean status; //
    private String imageUrl; //

    public Product() {
    }



    public Product(int id, String name, String model, String description, int price, boolean status, String imageUrl) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.description = description;
        this.price = price;
        this.status = status;
        this.imageUrl = imageUrl;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getModel() {
        return model;
    }
    public String getDescription() {
        return description;
    }
    public int getPrice() {
        return price;
    }
    public boolean getStatus() {
        return status;
    }
    public String getImageUrl() {
        return imageUrl;
    }


    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }





}
