package com.dorsolo.supermarket.data.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Entity which represent a Table in our Database where each of the fields of the class are a column
 * and the columns names are based on the field name
 */
@Entity
public class MinUser {

    @PrimaryKey(autoGenerate = true)
    private int minUserId;

    private int numOfProducts;

    private String username;

    private String phoneNumber;

    private String profileImg;

    public MinUser(int numOfProducts, String username, String phoneNumber, String profileImg) {
        this.numOfProducts = numOfProducts;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.profileImg = profileImg;
    }

    public int getMinUserId() {
        return minUserId;
    }

    public void setMinUserId(int minUserId) {
        this.minUserId = minUserId;
    }

    public int getNumOfProducts() {
        return numOfProducts;
    }

    public void setNumOfProducts(int numOfProducts) {
        this.numOfProducts = numOfProducts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}