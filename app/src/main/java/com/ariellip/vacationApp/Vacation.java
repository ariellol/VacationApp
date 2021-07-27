package com.ariellip.vacationApp;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Vacation extends Item implements Serializable {

    private ArrayList<String> takenDates;
    private boolean available;
    private int amountOfRooms;
    private int amountOfGuests;
    private int apartmentSize;
    private int priceForWeekend;
    private String cartTime;



    public Vacation(String title, String description, boolean available,
                    int amountOfRooms, int amountOfGuests,int apartmentSize, int priceForWeekend, ArrayList<String> images) {
        super(images.get(0),title,description,priceForWeekend,images);
        this.amountOfRooms = amountOfRooms;
        this.amountOfGuests = amountOfGuests;
        this.apartmentSize = apartmentSize;
        this.available = available;
    }



    public Vacation(){}

    public String getUid() {
        return super.getUid();
    }

    public void setUid(String uid) {
        super.setUid(uid);
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
        return super.getFirstImage();
    }

    public void setFirstImage(String firstImage) {
        super.setFirstImage(firstImage);
    }

    public String getTitle() {
        return super.getTitle();
    }

    public void setTitle(String title) {
        super.setTitle(title);
    }

    public String getDescription() {
        return super.getDescription();
    }

    public void setDescription(String description) {
        super.setDescription(description);
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


    public ArrayList<String> getImages() {
        return super.getImages();
    }

    public void setImages(ArrayList<String> images) {
        super.setImages(images);
    }

    public String getCartTime() {
        return cartTime;
    }

    public void setCartTime(String cartTime) {
        this.cartTime = cartTime;
    }

    @Override
    public String toString() {
        return super.toString() + " Vacation{" +
                "takenDates=" + takenDates +
                ", available=" + available +
                ", amountOfRooms=" + amountOfRooms +
                ", amountOfGuests=" + amountOfGuests +
                ", apartmentSize=" + apartmentSize +
                ", priceForWeekend=" + priceForWeekend +
                ", cartTime='" + cartTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item vacation = (Vacation) o;

        return super.getUid().equals(vacation.getUid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUid());
    }
}
