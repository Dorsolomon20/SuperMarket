package com.dorsolo.supermarket.networking;

import android.support.v4.app.FragmentActivity;

import com.dorsolo.supermarket.base.BaseAsyncTask;
import com.dorsolo.supermarket.listeners.HomeListener;
import com.dorsolo.supermarket.utilities.Constants;
import com.dorsolo.supermarket.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * UpdateUser AsyncTask send to the server the data we want to update by a key in a String format and the data
 * itself in a String format. Then we get a response whether the action was successful or not
 * UpdateUser require email and password for validation
 */
public class UpdateUser extends BaseAsyncTask<String, Void, String> {

    private String key;
    private HomeListener homeListener;

    public UpdateUser(FragmentActivity activity, HomeListener homeListener, String key) {
        super(activity, homeListener);
        if (homeListener == null)
            throw new IllegalArgumentException("HomeListener required for AsyncTask HomeFragment communication, can't be null");
        if (key == null)
            throw new IllegalArgumentException("Key required for passing the server response the calling class");
        this.homeListener = homeListener;
        this.key = key;
    }

    @Override
    protected String doInBackground(String... data) {
        URL url;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            url = new URL(NetworkUtils.buildUrl(Constants.NetworkingConstants.HOME_URL));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(Constants.NetworkingConstants.POST);
            connection.setRequestProperty(Constants.NetworkingConstants.CONTENT_TYPE, Constants.NetworkingConstants.APPLICATION_JSON);
            connection.connect();

            JSONObject jsonUserUpdate = new JSONObject();
            jsonUserUpdate.put(Constants.UserUpdateConstants.CODE, key);
            jsonUserUpdate.put(Constants.UserUpdateConstants.DATA, data[0]);
            jsonUserUpdate.put(Constants.UserConstants.EMAIL, data[1]);
            jsonUserUpdate.put(Constants.UserConstants.PASSWORD, data[2]);

            outputStream = connection.getOutputStream();
            outputStream.write(jsonUserUpdate.toString().getBytes());

            return NetworkUtils.serverResponse(this, inputStream = connection.getInputStream(), Constants.NetworkingConstants.MIN_BUFFER_SIZE);
        } catch (IOException | JSONException e) {
            cancel(true);
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (homeListener != null)
            homeListener.updateProcessCompleted(response, key);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (homeListener != null)
            homeListener.communicationFailed();
    }
}