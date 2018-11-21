package com.dorsolo.supermarket.background;

import android.content.Context;

import com.dorsolo.supermarket.data.db.AppDatabase;
import com.dorsolo.supermarket.data.db.MinUser;
import com.dorsolo.supermarket.model.UserModel;
import com.dorsolo.supermarket.utilities.Constants;
import com.dorsolo.supermarket.utilities.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Save the MinUser to the DB, including it's profileImg if exists (Path to a file where it was writing to), Number of products,
 * username and phone number
 */
public class PersistMinUser implements Runnable {

    private Context context;
    private FileUtils fileUtils;
    private UserModel user;

    public PersistMinUser(Context context, FileUtils fileUtils, UserModel user) {
        if (context == null)
            throw new IllegalArgumentException("Context is required for opening a connection with the SqLite database");
        if (fileUtils == null || user == null)
            throw new IllegalArgumentException("FileUtils and user are required for persisting the data locally");
        this.context = context;
        this.fileUtils = fileUtils;
        this.user = user;
    }

    @Override
    public void run() {
        String filePath = null;
        if (user.getProfileImg() != null) {
            OutputStream outputStream = null;
            try {
                File file = fileUtils.createInternalFile(Constants.CodesConstants.TEXT);
                outputStream = new FileOutputStream(file);
                outputStream.write(fileUtils.bitmapToByteArray(user.getProfileImg(), Constants.ImageConstants.HIGH_QUALITY));
                filePath = file.getAbsolutePath();
            } catch (IOException e) {
                return;
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        appDatabase.minUserDao().insertRecord(new MinUser(user.getNumOfProducts(), user.getUsername(), user.getPhoneNumber(), filePath));
    }
}
