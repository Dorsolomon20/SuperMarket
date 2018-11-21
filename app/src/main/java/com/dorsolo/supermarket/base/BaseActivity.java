package com.dorsolo.supermarket.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dorsolo.supermarket.MainActivityListener;
import com.dorsolo.supermarket.utilities.AppUtils;
import com.dorsolo.supermarket.utilities.FileUtils;

/**
 * Base class for every activity in the app, contains useful variables and methods that are often been used.
 */
public abstract class BaseActivity extends AppCompatActivity implements MainActivityListener {

    private AppUtils appUtils;
    private FileUtils fileUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils = new AppUtils(this);
        fileUtils = new FileUtils(this);
    }

    public AppUtils getAppUtils() {
        return appUtils;
    }

    public FileUtils getFileUtils() {
        return fileUtils;
    }
}
