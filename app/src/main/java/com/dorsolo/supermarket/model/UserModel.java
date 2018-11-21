package com.dorsolo.supermarket.model;

import android.graphics.Bitmap;

import com.dorsolo.supermarket.utilities.Constants.UserConstants;
import com.dorsolo.supermarket.utilities.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for a User obj, This class represents a user in the app, it can be either the logged in user
 * or another user. It helps holding the necessary data. Default constructor is private.
 */
public class UserModel {

    private int numOfProducts;
    private String username, phoneNumber;
    private Bitmap profileImg;
    private List<ImageModel> images;

    private UserModel() {
    }

    /**
     * Create an instance of User obj from a JSONObject
     *
     * @param jsonUser  JSONObject containing all of the params for the User obj
     * @param fileUtils FileUtils obj for converting the Base64 String to a Bitmap obj
     * @return new instance of User obj
     * @throws JSONException Exception thrown in case of an exception while getting the data from the JSONObject
     */
    public static UserModel getInstance(JSONObject jsonUser, FileUtils fileUtils) throws JSONException {
        if (jsonUser == null)
            throw new IllegalArgumentException("JSONObject required for creating the User object, can't be null");
        if (fileUtils == null)
            throw new IllegalArgumentException("FileUtils required for creating the User object, can't be null");
        UserModel user = new UserModel();
        if (jsonUser.has(UserConstants.USERNAME))
            user.setUsername(jsonUser.getString(UserConstants.USERNAME));
        if (jsonUser.has(UserConstants.PHONE_NUMBER))
            user.setPhoneNumber(jsonUser.getString(UserConstants.PHONE_NUMBER));
        if (jsonUser.has(UserConstants.PROFILE_IMAGE))
            user.setProfileImg(fileUtils.byteArrayToBitmap(fileUtils.base64StringToByteArray(jsonUser.getString(UserConstants.PROFILE_IMAGE))));
        if (jsonUser.has(UserConstants.NUM_OF_PRODUCTS))
            user.setNumOfProducts(jsonUser.getInt(UserConstants.NUM_OF_PRODUCTS));
        user.setImages(ImageModel.getImagesFromJsonArray(jsonUser.getJSONArray(UserConstants.IMAGES), fileUtils));
        return user;
    }


    public int getNumOfProducts() {
        return numOfProducts;
    }

    private void setNumOfProducts(int numOfProducts) {
        this.numOfProducts = numOfProducts;
    }

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    private void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Bitmap getProfileImg() {
        return profileImg;
    }

    private void setProfileImg(Bitmap profileImg) {
        this.profileImg = profileImg;
    }

    public List<ImageModel> getImages() {
        if (images == null)
            images = new ArrayList<>();
        return images;
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
    }
}
