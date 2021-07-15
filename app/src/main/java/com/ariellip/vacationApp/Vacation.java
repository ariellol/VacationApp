package com.ariellip.vacationApp;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Vacation implements Serializable {

    private String title;
    private String description;
    private ArrayList<String> availableDates;
    private ArrayList<String> takenDates;
    private String adress;
    private boolean available;
    private int amountOfRooms;
    private int amountOfGuests;
    private ArrayList<String> images;
    private int apartmentSize;
    private int priceForWeekend;
    private String firstImage;
    private String uid;

    public Vacation(String title, String description, boolean available,
                    int amountOfRooms, int amountOfGuests,int apartmentSize, int priceForNight, String adress, ArrayList<String> images, String uid) {
        this.title = title;
        this.description = description;
        this.available = available;
        this.amountOfRooms = amountOfRooms;
        this.amountOfGuests = amountOfGuests;
        this.apartmentSize = apartmentSize;
        this.priceForWeekend = priceForNight;
        this.adress = adress;
        this.images = images;
        this.firstImage = images.get(0);
        this.uid = uid;
    }

    public Vacation(String title, String description, boolean available,
                    int amountOfRooms, int amountOfGuests,int apartmentSize, int priceForNight, ArrayList<String> images) {
        this.title = title;
        this.description = description;
        this.available = available;
        this.amountOfRooms = amountOfRooms;
        this.amountOfGuests = amountOfGuests;
        this.apartmentSize = apartmentSize;
        this.priceForWeekend = priceForNight;
        this.images = images;
        this.firstImage = images.get(0);
    }

    public Vacation(String title, String description, ArrayList<String> availableDates, ArrayList<String> takenDates,
                    String adress, boolean available, int amountOfRooms, int amountOfGuests, ArrayList<String> images,
                    int apartmentSize, int priceForNight,String uid) {
        this.title = title;
        this.description = description;
        this.availableDates = availableDates;
        this.takenDates = takenDates;
        this.adress = adress;
        this.available = available;
        this.amountOfRooms = amountOfRooms;
        this.amountOfGuests = amountOfGuests;
        this.images = images;
        this.apartmentSize = apartmentSize;
        this.priceForWeekend = priceForNight;
        this.firstImage = images.get(0);
        this.uid = uid;
    }

    public Vacation(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getApartmentSize() {
        return apartmentSize;
    }

    public void setApartmentSize(int apartmentSize) {
        this.apartmentSize = apartmentSize;
    }

    public int getPriceForWeekend() {
        return priceForWeekend;
    }

    public void setPriceForWeekend(int priceForNight) {
        this.priceForWeekend = priceForNight;
    }

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

    public ArrayList<String> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(ArrayList<String> availableDates) {
        this.availableDates = availableDates;
    }

    public ArrayList<String> getTakenDates() {
        return takenDates;
    }

    public void setTakenDates(ArrayList<String> takenDates) {
        this.takenDates = takenDates;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getAmountOfRooms() {
        return amountOfRooms;
    }

    public void setAmountOfRooms(int amountOfRooms) {
        this.amountOfRooms = amountOfRooms;
    }

    public int getAmountOfGuests() {
        return amountOfGuests;
    }

    public void setAmountOfGuests(int amountOfGuests) {
        this.amountOfGuests = amountOfGuests;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Vacation{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", availableDates=" + availableDates +
                ", takenDates=" + takenDates +
                ", adress='" + adress + '\'' +
                ", available=" + available +
                ", amountOfRooms=" + amountOfRooms +
                ", amountOfGuests=" + amountOfGuests +
                ", images=" + images +
                ", apartmentSize=" + apartmentSize +
                ", priceForNight=" + priceForWeekend +
                '}';
    }
}
