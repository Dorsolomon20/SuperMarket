package com.dorsolo.supermarket.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.dorsolo.supermarket.utilities.AppUtils;

/**
 * Base class for every DialogFragment in the app, contains useful variables and methods that are often been used
 */
public abstract class BaseDialogFragment extends DialogFragment {

    private AppUtils appUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils = new AppUtils(getActivity());
    }

    public AppUtils getAppUtils() {
        return appUtils;
    }
}
