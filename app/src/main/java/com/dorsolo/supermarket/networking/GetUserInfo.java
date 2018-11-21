package com.dorsolo.supermarket.networking;

import android.support.v4.app.FragmentActivity;

import com.dorsolo.supermarket.base.BaseAsyncTask;
import com.dorsolo.supermarket.listeners.HomeListener;
import com.dorsolo.supermarket.model.UserModel;
import com.dorsolo.supermarket.utilities.Constants.NetworkingConstants;
import com.dorsolo.supermarket.utilities.Constants.UserConstants;
import com.dorsolo.supermarket.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * GetUserInfo is called on the first time the user is logged in into his account to retrieve his profile
 * information and several images if he has any (Amount is decided by the server). Then we perform the conversation
 * from String data format to a User obj
 * GetUserInfo require email and password for validation
 */
public class GetUserInfo extends BaseAsyncTask<String, Void, UserModel> {

    private HomeListener homeListener;

    public GetUserInfo(FragmentActivity activity, HomeListener homeListener) {
        super(activity, homeListener);
        if (homeListener == null)
            throw new IllegalArgumentException("homeListener required for AsyncTask HomeFragment communication, can't be null");
        this.homeListener = homeListener;
    }

    @Override
    protected UserModel doInBackground(String... credentials) {
        if (credentials.length != 2 || credentials[0] == null || credentials[1] == null)
            throw new IllegalArgumentException("Credentials length must be equal to 2, and contain a valid Email and Password");
        URL url;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            url = new URL(NetworkUtils.buildUrl(NetworkingConstants.HOME_URL, UserConstants.EMAIL, credentials[0], UserConstants.PASSWORD, credentials[1], UserConstants.CLIENT_IMAGES_AMOUNT, "0"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(NetworkingConstants.GET);
            connection.setRequestProperty(NetworkingConstants.CONTENT_TYPE, NetworkingConstants.APPLICATION_JSON);
            connection.connect();

            String serverResponse = NetworkUtils.serverResponse(this, inputStream = connection.getInputStream(), NetworkingConstants.MAX_BUFFER_SIZE);
            if (serverResponse == null)
                return null;
            return UserModel.getInstance(new JSONObject(serverResponse), getFileUtils());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            cancel(true);
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(UserModel user) {
        super.onPostExecute(user);
        if (homeListener != null)
            homeListener.communicationCompleted(user);
    }
}