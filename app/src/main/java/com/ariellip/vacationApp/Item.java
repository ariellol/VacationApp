package com.ariellip.vacationApp;

import java.util.ArrayList;

public class Item {

    private String firstImage;
    private String title;
    private String description;
    private int priceForWeekend;
    private String uid;
    private ArrayList<String> images;

    public Item(String firstImage, String title, String description, int priceForWeekend, ArrayList<String> images) {
        this.firstImage = firstImage;
        this.title = title;
        this.description = description;
        this.priceForWeekend = priceForWeekend;
        this.images = images;
    }

    public Item(){}

    public String getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriceForWeekend() {
        return priceForWeekend;
    }

    public void setPriceForWeekend(int priceForWeekend) {
        this.priceForWeekend = priceForWeekend;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Item{" +
                "firstImage='" + firstImage + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priceForWeekend=" + priceForWeekend +
                ", uid='" + uid + '\'' +
                ", images=" + images +
                '}';
    }
}
