package com.ariellip.vacationApp;

import java.util.ArrayList;

public class EntryTicket extends Item{


    int maxGuests;

    public EntryTicket(){}

    public EntryTicket(String image, String title, String description, int price, int quantity, ArrayList<String> images) {
        super(image,title,description,price, images);
        this.maxGuests = quantity;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }
}
