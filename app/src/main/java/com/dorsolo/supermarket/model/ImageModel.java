package com.dorsolo.supermarket.model;

import android.graphics.Bitmap;

import com.dorsolo.supermarket.utilities.Constants.ImageConstants;
import com.dorsolo.supermarket.utilities.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for a Image obj, This class represents a Image in the app,It helps holding the necessary
 * data. Default constructor is private.
 */
public class ImageModel {

    private String title, description;
    private Bitmap image;

    private ImageModel() {

    }

    /**
     * Create an instance of Image obj from 3 of the required fields
     *
     * @param title       String which represent the title of the image, can't be null
     * @param description String which represent the description of the image, can't be null
     * @param image       Bitmap obj which represent the Image itself, can't be null
     * @return newly created instance of Image with all of the fields populated based on the giving params
     */
    public static ImageModel getInstance(String title, String description, Bitmap image) {
        return new ImageModel().setTitle(title).setDescription(description).setImage(image);
    }

    /**
     * Create an instance of Image obj from a JSONObject
     *
     * @param jsonImage JSONObject containing all of the params for the Image obj
     * @param fileUtils FileUtils obj for converting the Base64 String to Bitmap obj
     * @return New instance of Image obj
     * @throws JSONException Exception thrown in case of an exception while getting the data from the JSONObject
     */
    private static ImageModel getInstance(JSONObject jsonImage, FileUtils fileUtils) throws JSONException {
        return new ImageModel()
                .setTitle(jsonImage.getString(ImageConstants.TITLE))
                .setDescription(jsonImage.getString(ImageConstants.DESCRIPTION))
                .setImage(fileUtils.byteArrayToBitmap(fileUtils.base64StringToByteArray(jsonImage.getString(ImageConstants.IMAGE))));
    }

    /**
     * Create a List of Image objects from a JSONArray by iterating over it and using each JSONObject it contains to create
     * an Image obj by it's fields
     *
     * @param jsonImages JSONArray obj containing all of the JSONObjects to use for creating the List<Image>
     * @param fileUtils  FileUtils obj for converting the base64 String to Bitmap obj
     * @return List<Image>
     * @throws JSONException Exception thrown in case of an exception while getting the data from the JSONObject or JSONArray
     */
    public static List<ImageModel> getImagesFromJsonArray(JSONArray jsonImages, FileUtils fileUtils) throws JSONException {
        List<ImageModel> images = new ArrayList<>();
        for (int i = 0; i < jsonImages.length(); i++) {
            JSONObject jsonImage = jsonImages.getJSONObject(i);
            images.add(ImageModel.getInstance(jsonImage, fileUtils));
        }
        return images;
    }

    public String getTitle() {
        return title;
    }

    private ImageModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    private ImageModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public Bitmap getImage() {
        return image;
    }

    private ImageModel setImage(Bitmap image) {
        this.image = image;
        return this;
    }
}
