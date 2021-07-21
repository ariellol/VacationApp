package com.ariellip.vacationApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Cart {

    private ArrayList<String> vacationsReferences;
    private String cartUid;
    String addedToCartTime;
    String takenDate;

    public Cart(ArrayList<String> vacations, String cartUid, String takenDate) {
        this.vacationsReferences = vacations;
        this.cartUid = cartUid;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
        addedToCartTime = dateFormat.format(Calendar.getInstance().getTime());
        this.takenDate = takenDate;
    }

    public Cart(ArrayList<String > vacations, String takenDate) {
        this.vacationsReferences = vacations;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
        addedToCartTime = dateFormat.format(Calendar.getInstance().getTime());
        this.takenDate = takenDate;
    }

    public Cart(){}

    public ArrayList<String> getVacations() {
        return vacationsReferences;
    }

    public void setVacations(ArrayList<String> vacations) {
        this.vacationsReferences = vacations;
    }

    public String getCartUid() {
        return cartUid;
    }

    public void setCartUid(String cartUid) {
        this.cartUid = cartUid;
    }

    public String getAddedToCartTime() {
        return addedToCartTime;
    }

    public String getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(String takenDate) {
        this.takenDate = takenDate;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "vacationsReferences=" + vacationsReferences +
                ", cartUid='" + cartUid + '\'' +
                ", addedToCartTime='" + addedToCartTime + '\'' +
                ", takenDate='" + takenDate + '\'' +
                '}';
    }
}
