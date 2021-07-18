package com.ariellip.vacationApp;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class User implements Serializable{

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private boolean isManager;
    private String uid;

    public User(String firstName, String lastName, String email, String phoneNumber,boolean isManager,String uid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isManager = isManager;
        this.uid = uid;
    }

    public User(String firstName, String lastName, String email, String phoneNumber,boolean isManager) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isManager = isManager;
    }


    public User(){}

    public boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
