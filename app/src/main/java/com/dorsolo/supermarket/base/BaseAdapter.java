package com.dorsolo.supermarket.base;

import android.support.v7.widget.RecyclerView;

import com.dorsolo.supermarket.utilities.AppUtils;

/**
 * Base class for every RecyclerView.Adapter in the app, contains useful variables and methods that are often been used
 */
public abstract class BaseAdapter<ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {

    private AppUtils appUtils;

    public BaseAdapter(BaseActivity activity) {
        appUtils = new AppUtils(activity);
    }

    public AppUtils getAppUtils() {
        return appUtils;
    }
}
