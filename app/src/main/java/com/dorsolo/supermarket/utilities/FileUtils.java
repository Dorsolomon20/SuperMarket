package com.dorsolo.supermarket.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilities specifically for file handling used across the app
 */
public final class FileUtils {

    private Activity activity;

    public FileUtils(Activity activity) {
        this.activity = activity;
    }

    /**
     * Generate a unique String based on the current timeStamp used for naming files
     *
     * @return the new unique file name
     */
    private static String uniqueFileName() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss.SSS", Locale.getDefault()).format(new Date());
    }

    /**
     * Create a unique temp cache internal file in a private directory which is public, the images will be deleted if the app will be uninstalled
     *
     * @return newly created temp cache file in the private public directory
     */
    static File createInternalTempFile(Activity activity) {
        try {
            return File.createTempFile(uniqueFileName(), Constants.CodesConstants.JPEG, activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Generate a new file in the app internal directory, which only accessible to the app
     *
     * @param format String value which represent the format of the file
     * @return newly created file in the requested format
     */
    public File createInternalFile(String format) {
        if (format == null)
            throw new IllegalArgumentException("Format can't be null, required for persisting the file correctly");
        return new File(activity.getFilesDir(), uniqueFileName() + (format));
    }

    /**
     * Convert base64 String data format to a byte array data format
     *
     * @param base64 the String to be converted to byte array
     * @return the converted base64 string now in byte array format
     */
    public byte[] base64StringToByteArray(@NonNull String base64) {
        return Base64.decode(base64, Base64.NO_WRAP);
    }

    /**
     * Convert byte array data format to a Bitmap object
     *
     * @param bytes the byte array to be converted to Bitmap object
     * @return the converted byte array now is Bitmap format
     */
    public Bitmap byteArrayToBitmap(@NonNull byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Convert Bitmap to byte array data format
     *
     * @param picture the Bitmap to be converted to byte array
     * @param quality Hint to the compressor, int val 0 - 100. 0 meaning compress for small size, 100 meaning compress for max quality
     * @return byte array data format which is the converted Bitmap
     */
    public byte[] bitmapToByteArray(@NonNull Bitmap picture, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    /**
     * Convert byte array to Base64 String
     *
     * @param bytes the byte array data format to be converted to Base64 String
     * @return the Base64 String data format which is the converted byte array
     */
    public String byteArrayToBase64String(@NonNull byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public Activity getActivity() {
        return activity;
    }
}