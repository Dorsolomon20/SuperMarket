package com.dorsolo.supermarket.base;

import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.dorsolo.supermarket.utilities.AppUtils;
import com.dorsolo.supermarket.utilities.FileUtils;

/**
 * Base class for every AsyncTask in the app, contains useful variables and methods that are often been used.
 * Also it as some common logic in the LifeCycle stages that helps the subClasses manage there networking operations
 * and save code.
 */
public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private BaseNetworkingListener baseNetworkingListener;

    private AppUtils appUtils;
    private FileUtils fileUtils;

    public <Listener extends BaseNetworkingListener> BaseAsyncTask(FragmentActivity activity, Listener listener) {
        appUtils = new AppUtils(activity);
        fileUtils = new FileUtils(activity);
        baseNetworkingListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        NetworkInfo networkInfo = baseNetworkingListener.getActiveNetwork();
        if (networkInfo != null && getAppUtils().hasInternet(networkInfo))
            baseNetworkingListener.communicationStarted();
        else
            cancel(true);
    }

    /**
     * Called to stop the thread from running, can be called when the activity or fragment have been destroyed.
     * Also can prevent memory leaks
     */
    public void stopRunning() {
        baseNetworkingListener = null;
        cancel(true);
    }

    protected AppUtils getAppUtils() {
        return appUtils;
    }

    protected FileUtils getFileUtils() {
        return fileUtils;
    }
}
