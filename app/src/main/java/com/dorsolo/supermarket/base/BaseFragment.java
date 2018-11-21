package com.dorsolo.supermarket.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.dorsolo.supermarket.MainActivityListener;
import com.dorsolo.supermarket.utilities.AppUtils;
import com.dorsolo.supermarket.utilities.FileUtils;
import com.dorsolo.supermarket.utilities.ImageUtils;

/**
 * Base class for every Fragment in the app, contains useful variables and methods that are often been used.
 * Also it as some common logic in the LifeCycle stages that help subClasses save code.
 */
public abstract class BaseFragment extends Fragment {

    private AppUtils appUtils;
    private FileUtils fileUtils;
    private ImageUtils imageUtils;

    private MainActivityListener mainActivityListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils = new AppUtils(getActivity(), this);
        fileUtils = new FileUtils(getActivity());
        imageUtils = new ImageUtils(getActivity());
    }

    public AppUtils getAppUtils() {
        return appUtils;
    }

    public FileUtils getFileUtils() {
        return fileUtils;
    }

    public ImageUtils getImageUtils() {
        return imageUtils;
    }

    public MainActivityListener getMainActivityListener() {
        return mainActivityListener;
    }

    /**
     * Override the onAttach method to initialize the MainActivityListener. Called when a fragment is first
     * attached to it's context.
     *
     * @param context Context of the holding class, In our case the MainActivity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainActivityListener = (MainActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("The parent activity of the fragment should implement the MainActivityListener");
        }
    }
}
