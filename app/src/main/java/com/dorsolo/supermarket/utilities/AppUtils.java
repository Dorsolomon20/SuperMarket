package com.dorsolo.supermarket.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.utilities.Constants.CodesConstants;
import com.dorsolo.supermarket.utilities.Constants.UserConstants;

import java.io.File;
import java.security.InvalidParameterException;

/**
 * General utilities that are used across the app
 */
public final class AppUtils {

    private FragmentActivity activity;
    private Fragment fragment;

    public AppUtils(FragmentActivity activity) {
        if (activity == null)
            throw new RuntimeException("Activity required for some of the method, can't be null");
        this.activity = activity;
    }

    public AppUtils(FragmentActivity activity, Fragment fragment) {
        if (activity == null)
            throw new RuntimeException("Activity required for some of the method, can't be null");
        if (fragment == null)
            throw new RuntimeException("Fragment required for some of the method, can't be null");
        this.activity = activity;
        this.fragment = fragment;
    }

    /**
     * Launch a new fragment to the foreground into the fragmentMainContainer FrameLayout
     *
     * @param fragment       the fragment to be launched
     * @param addToBackStack boolean whether or not to add the fragment to the backStack
     * @param tag            String which represent the tag of the launched fragment, can be used to find it and get a pointer to it
     * @param animations     int varargs which are the animations references to be added can only be nothing for no animation 2 or 4
     */
    public void launchFragment(@NonNull Fragment fragment, boolean addToBackStack, String tag, int... animations) {
        if (animations.length != 0 && animations.length != 2 && animations.length != 4)
            throw new IllegalArgumentException("Animations length can only be 0, 2 or 4");
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (addToBackStack)
            transaction.addToBackStack(null);
        if (animations.length == 2)
            transaction.setCustomAnimations(animations[0], animations[1]);
        else if (animations.length == 4)
            transaction.setCustomAnimations(animations[0], animations[1], animations[2], animations[3]);
        transaction.replace(R.id.fragmentMainContainer, fragment, tag).commit();
    }

    /**
     * Changes the state of the keyboard, based on the passed boolean can either open or close it
     *
     * @param view   Current focus view
     * @param toOpen boolean whether to open or close the keyboard, true - open, false - close
     */
    public final void changeKeyboardState(View view, boolean toOpen) {
        if (view == null)
            throw new IllegalArgumentException("View can't be null, InputMethodManager require to know the current focus view");
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (toOpen)
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            else
                imm.hideSoftInputFromWindow(view.getWindowToken(), activity.getResources().getInteger(R.integer.zero));
        }
    }

    /**
     * Checks for internet connection based on the current available NetworkInfo instance passed
     *
     * @param networkInfo the current NetworkInfo that contains all of the information about the current active network
     * @return boolean whether there's a valid intent connection or not
     */
    public final boolean hasInternet(@NonNull NetworkInfo networkInfo) {
        return networkInfo.isConnected() || networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * Clear text for an undefined amount of views that extends from TextView
     *
     * @param views The views to clear the text from
     * @param <T>   Generic Type object that must extends from the TextView class
     */
    @SafeVarargs
    public final <T extends TextView> void clearText(T... views) {
        for (TextView view : views) {
            if (view == null)
                throw new RuntimeException("View can't be null, please check the giving params");
            view.setText("");
        }
    }

    /**
     * Changes the visibility of undefined amount of views to a specific visibility type
     *
     * @param visibility the visibility type must be one of the following: View.VISIBLE, View.INVISIBLE, View.GONE
     * @param views      undefined amount of views to change there visibility, must extend the View class
     * @param <T>        Generic Object type that must extends the View class
     */
    @SafeVarargs
    public final <T extends View> void changeVisibility(int visibility, T... views) {
        if (visibility != View.VISIBLE && visibility != View.INVISIBLE && visibility != View.GONE)
            throw new InvalidParameterException("visibility parameter cant be equal to: " + visibility);
        for (View view : views) {
            if (view == null)
                throw new RuntimeException("View can't be null, please check the giving params");
            view.setVisibility(visibility);
        }
    }

    /**
     * Populate a shared preference with the given email and password, can be used to populate the field and to clear them
     *
     * @param editor      SharedPreference.Editor set to edit the shared preference file
     * @param credentials undefined amount of strings that in this case represent 1. email, 2. password
     */
    public void populateSharedPreference(SharedPreferences.Editor editor, String... credentials) {
        if (editor == null)
            throw new IllegalArgumentException("SharedPreferences.Editor is required to persist the credentials passed");
        if (credentials.length != 2)
            throw new IllegalArgumentException("Credentials length must be equals to 2 and contain either the logged in user email and password or null");
        editor.putString(UserConstants.EMAIL, credentials[0]);
        editor.putString(UserConstants.PASSWORD, credentials[1]);
        editor.apply();
    }

    /**
     * Checks whether there's a permission for an operation
     *
     * @param permission     String which represent the permission to check
     * @param permissionCode int which is the code for the permission request for the onRequestPermissionsResult
     * @param toFragment     boolean for whether to return the answer to the fragment or activity
     * @return boolean whether there's a permission or not, True - permission granted, false otherwise
     */
    public boolean checkPermission(String permission, int permissionCode, boolean toFragment) {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED)
            return true;
        if (toFragment) {
            if (fragment == null)
                throw new RuntimeException("Requested result for fragment, Fragment instance giving in constructor found to be null");
            fragment.requestPermissions(new String[]{permission}, permissionCode);
        } else
            ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionCode);
        return false;
    }

    /**
     * Launch the device camera for a full size image, this should be used ONLY for when large high resolution images are required
     *
     * @param toFragment boolean True - Return the image result to the launching fragment, false - Return the image to the launching activity
     * @return File obj where the return image has been writing to
     */
    public File launchFullSizeCamera(boolean toFragment) {
        File imageTempFile = null;
        Intent fullSizeCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (resolveIntent(fullSizeCameraIntent) && hasCamera() && (imageTempFile = FileUtils.createInternalTempFile(activity)) != null) {
            fullSizeCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", imageTempFile));
            targetHandler(fullSizeCameraIntent, toFragment, CodesConstants.CAMERA_CODE);
        }
        return imageTempFile;
    }

    /**
     * Launch the device camera for a thumbnail image, this should be used ONLY for when small images are required
     *
     * @param toFragment boolean True - Return the image result to the launching fragment, false - Return the image to the launching activity
     */
    public void launchCamera(boolean toFragment) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (resolveIntent(cameraIntent) && hasCamera())
            targetHandler(cameraIntent, toFragment, CodesConstants.THUMBNAIL_CAMERA_CODE);
    }

    /**
     * Launch the gallery activity for retrieving full size images, any size of image there are in the gallery
     *
     * @param toFragment boolean True - Return the image result to the launching fragment, false - Return the image to the launching activity
     */
    public void launchGallery(boolean toFragment) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        if (resolveIntent(galleryIntent))
            targetHandler(galleryIntent, toFragment, CodesConstants.GALLERY_CODE);
    }

    /**
     * Make a phone call from within the app
     *
     * @param phoneNumber the number to make the call to
     */
    @SuppressLint("MissingPermission")
    public void makePhoneCall(String phoneNumber) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (resolveIntent(phoneIntent))
            activity.startActivity(phoneIntent);
    }

    /**
     * Checks whether the given intent has an activity on the device to לְהָגִיב to it
     *
     * @param intent the intent to validate
     * @return boolean true - For valid intent ready for use, false - For not valid intent not ready for use
     */
    private boolean resolveIntent(@NonNull Intent intent) {
        return intent.resolveActivity(activity.getPackageManager()) != null;
    }

    /**
     * Launch an intent and retrieve the result to a fragment or activity based on the toFragment boolean
     *
     * @param intent      the intent to be launched
     * @param toFragment  boolean to whether return result to the fragment or activity
     * @param requestCode the request code for the onActivityResult
     */
    private void targetHandler(Intent intent, boolean toFragment, int requestCode) {
        if (toFragment) {
            if (fragment == null)
                throw new RuntimeException("Requested result for fragment, Fragment instance giving in constructor found to be null");
            fragment.startActivityForResult(intent, requestCode);
        } else
            activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Remove all the fragments from the backStack if there are any
     */
    public void clearBackStack() {
        activity.getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Checks whether there's a valid camera on the device
     *
     * @return boolean true - For valid camera, false - For not valid camera
     */
    private boolean hasCamera() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    /**
     * Toast a message to the user based on a reference to a string in the Strings.xml file
     *
     * @param msg the reference to the Strings.xml file
     */
    public void toastMsg(int msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Contains the flags for the settings of the screen
     *
     * @return an Int which is all of the flags for hiding the status bar and navigation bar
     */
    public int fullScreen() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

    public FragmentActivity getActivity() {
        return activity;
    }
}