package com.dorsolo.supermarket.data.viewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dorsolo.supermarket.data.db.MinUser;
import com.dorsolo.supermarket.model.ImageModel;
import com.dorsolo.supermarket.model.UserModel;

import java.util.List;

/**
 * ViewModel class with MutableLiveData. This class provides as a data holder class,
 * The ViewModel will survive Configuration changes of our app, and it will survive until the onDestroy()
 * of our MainActivity (Not when rotating the device, then ViewModel will survive) This will help manage
 * the data without the need to address the SavedInstanceState bundle. The MutableLiveData objects are
 * observable data holder classes that are lifeCycle-aware, this ensures LiveData only updates app
 * components observers that are in an active lifeCycle stage. MutableLiveData are just mutable,
 * by exposing the setValue(T) and postValue(T) methods.
 */
public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> username, phoneNumber;
    private MutableLiveData<Integer> numOfProducts, numOfProductsRequested;
    private MutableLiveData<Bitmap> imgProfile;
    private MutableLiveData<List<ImageModel>> images;

    public MutableLiveData<String> getUsername() {
        if (username == null)
            username = new MutableLiveData<>();
        return username;
    }

    public MutableLiveData<String> getPhoneNumber() {
        if (phoneNumber == null)
            phoneNumber = new MutableLiveData<>();
        return phoneNumber;
    }

    public MutableLiveData<Integer> getNumOfProducts() {
        if (numOfProducts == null)
            numOfProducts = new MutableLiveData<>();
        return numOfProducts;
    }

    public MutableLiveData<Integer> getNumOfProductsRequested() {
        if (numOfProductsRequested == null)
            numOfProductsRequested = new MutableLiveData<>();
        return numOfProductsRequested;
    }

    public MutableLiveData<Bitmap> getImgProfile() {
        if (imgProfile == null)
            imgProfile = new MutableLiveData<>();
        return imgProfile;
    }

    public MutableLiveData<List<ImageModel>> getImages() {
        if (images == null)
            images = new MutableLiveData<>();
        return images;
    }

    /**
     * Called when needing to persist all of the fields of a user
     *
     * @param user The user to be persisted
     */
    public void persistUser(UserModel user) {
        if (user.getUsername() != null)
            username.setValue(user.getUsername());
        if (user.getPhoneNumber() != null)
            phoneNumber.setValue(user.getPhoneNumber());
        numOfProducts.setValue(user.getNumOfProducts());
        if (user.getProfileImg() != null)
            imgProfile.setValue(user.getProfileImg());
        if (user.getImages().size() > 0)
            images.setValue(user.getImages());
    }

    /**
     * Called if a MinUser record was found in the DB to pass it to the LiveData and alert to the observers
     *
     * @param minUser MinUser obj containing all of the information to alert the observers to update the UI
     */
    public void persistMinUser(MinUser minUser) {
        if (minUser.getUsername() != null)
            username.setValue(minUser.getUsername());
        if (minUser.getPhoneNumber() != null)
            phoneNumber.setValue(minUser.getPhoneNumber());
        numOfProducts.setValue(minUser.getNumOfProducts());
        if (minUser.getProfileImg() != null)
            imgProfile.setValue(BitmapFactory.decodeFile(minUser.getProfileImg()));
    }

    /**
     * Clear data from ViewModel, occur when user exit his account
     */
    public void clearUser() {
        username.setValue(null);
        phoneNumber.setValue(null);
        numOfProducts.setValue(null);
        numOfProductsRequested.setValue(null);
        imgProfile.setValue(null);
        images.setValue(null);
    }
}