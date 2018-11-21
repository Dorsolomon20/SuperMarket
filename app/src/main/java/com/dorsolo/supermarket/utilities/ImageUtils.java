package com.dorsolo.supermarket.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;

/**
 * Utilities specifically for image handling used across the app
 */
public final class ImageUtils {

    private Activity activity;

    public ImageUtils(Activity activity) {
        this.activity = activity;
    }

    /**
     * Calculate the inSampleSize attribute for the BitmapFactory.Options based on the image dimensions and the requested dimensions
     *
     * @param options   BitmapFactory.Options set to the requested image
     * @param reqWidth  the requested width of the loaded image
     * @param reqHeight the requested height of the loaded image
     * @return int which is the inSampleSize calculated from the width and height of the image on disk and requested width and height
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth)
                inSampleSize *= 2;
        }
        return inSampleSize;
    }

    /**
     * Decode an image from it's file path to a Bitmap instance scaled to the requested dimensions
     *
     * @param imagePath the path to the file where the image is writing
     * @param reqWidth  the requested width of the loaded into memory image
     * @param reqHeight the requested height of the loaded into memory image
     * @return newly created instance of Bitmap which is the loaded image based on the requested dimensions
     */
    public static Bitmap decodeSampledBitmapFromFile(String imagePath, int reqWidth, int reqHeight) {
        //First decode with inJustDecodeBounds - true to check dimensions without loading to heap and allocating memory space
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        //Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * Decode an image from a InputStream to a Bitmap instance scaled to the requested dimensions
     *
     * @param uri       Uri to open the inputStream to, associated with the image content
     * @param reqWidth  the requested width of the loaded into memory image
     * @param reqHeight the requested height of the loaded into memory image
     * @return newly created instance of Bitmap which is the loaded image based on the requested dimensions
     * @throws FileNotFoundException Exception thrown in cases the URI could not be opened
     */
    public Bitmap decodeSampledBitmapFromStream(Uri uri, int reqWidth, int reqHeight) throws FileNotFoundException {
        //First decode with inJustDecodeBounds - true to check dimensions without loading to heap and allocating memory space
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri), null, options);
        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        //Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri), null, options);
    }
}