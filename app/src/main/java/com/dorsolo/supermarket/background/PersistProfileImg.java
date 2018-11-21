package com.dorsolo.supermarket.background;

import android.graphics.Bitmap;

import com.dorsolo.supermarket.utilities.Constants;
import com.dorsolo.supermarket.utilities.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

/**
 * Write the given profile image in a Bitmap format to a file after converting it to a byteArray format.
 * Then return the path to the file where the image was written.
 */
public class PersistProfileImg implements Callable<String> {

    private FileUtils fileUtils;
    private Bitmap profileImg;

    public PersistProfileImg(FileUtils fileUtils, Bitmap profileImg) {
        this.fileUtils = fileUtils;
        this.profileImg = profileImg;
    }

    @Override
    public String call() {
        String filePath;
        OutputStream outputStream = null;
        try {
            File file = fileUtils.createInternalFile(Constants.CodesConstants.TEXT);
            outputStream = new FileOutputStream(file);
            outputStream.write(fileUtils.bitmapToByteArray(profileImg, Constants.ImageConstants.HIGH_QUALITY));
            filePath = file.getAbsolutePath();
        } catch (IOException e) {
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }
}
