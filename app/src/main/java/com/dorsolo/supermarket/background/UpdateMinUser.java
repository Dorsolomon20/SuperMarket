package com.dorsolo.supermarket.background;

import android.content.Context;

import com.dorsolo.supermarket.data.db.AppDatabase;
import com.dorsolo.supermarket.data.db.MinUser;
import com.dorsolo.supermarket.data.db.MinUserDao;
import com.dorsolo.supermarket.utilities.Constants.UserUpdateConstants;

/**
 * Given a key and data in a String format, this Runnable implementation will update the Database for any updates
 * requested to either the Profile image, username or phoneNumber
 */
public class UpdateMinUser implements Runnable {

    private String key, data;
    private Context context;

    public UpdateMinUser(Context context, String key, String data) {
        if (context == null || key == null || data == null)
            throw new IllegalArgumentException("Context, Key and data are required for the minUser update process");
        this.context = context;
        this.key = key;
        this.data = data;
    }

    @Override
    public void run() {
        MinUserDao minUserDao = AppDatabase.getInstance(context).minUserDao();
        MinUser minUser = minUserDao.getUser();
        switch (key) {
            case UserUpdateConstants.UPDATE_PROFILE_IMAGE:
                minUser.setProfileImg(data);
                break;
            case UserUpdateConstants.UPDATE_USERNAME:
                minUser.setUsername(data);
                break;
            case UserUpdateConstants.UPDATE_PHONE_NUMBER:
                minUser.setPhoneNumber(data);
                break;
            case UserUpdateConstants.UPDATE_NUM_OF_PRODUCTS:
                minUser.setNumOfProducts(Integer.valueOf(data));
                break;
            default:
                return;
        }
        minUserDao.updateRecord(minUser);
    }
}
