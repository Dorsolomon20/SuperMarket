package com.dorsolo.supermarket.networking;

import android.support.v4.app.FragmentActivity;

import com.dorsolo.supermarket.base.BaseAsyncTask;
import com.dorsolo.supermarket.listeners.CredentialsListener;
import com.dorsolo.supermarket.utilities.Constants.CredentialsConstants;
import com.dorsolo.supermarket.utilities.Constants.NetworkingConstants;
import com.dorsolo.supermarket.utilities.Constants.UserConstants;
import com.dorsolo.supermarket.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * PostCredentials AsyncTask posts the given credentials, email and password, to the server along with
 * the action the user want to perform (Sign in / Sign up). Then the server return to us whether the action
 * was successful or not
 */
public class PostCredentials extends BaseAsyncTask<String, Void, String> {

    private static final String TAG = PostCredentials.class.getSimpleName();

    private CredentialsListener credentialsListener;

    public PostCredentials(FragmentActivity activity, CredentialsListener credentialsListener) {
        super(activity, credentialsListener);
        if (credentialsListener == null)
            throw new IllegalArgumentException("CredentialsListener required for AsyncTask CredentialsFragment communication, can't be null");
        this.credentialsListener = credentialsListener;
    }

    @Override
    protected String doInBackground(String... credentials) {
        if (credentials.length != 3 || credentials[0] == null || credentials[1] == null || credentials[2] == null)
            throw new IllegalArgumentException("Credentials length must be equal to 3, and contain a valid Email, Password and Mode");
        URL url;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            url = new URL(NetworkUtils.buildUrl(NetworkingConstants.CREDENTIALS_URL));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(NetworkingConstants.POST);
            connection.setRequestProperty(NetworkingConstants.CONTENT_TYPE, NetworkingConstants.APPLICATION_JSON);
            connection.connect();

            JSONObject jsonCredentials = new JSONObject();
            jsonCredentials.put(UserConstants.EMAIL, credentials[0]);
            jsonCredentials.put(UserConstants.PASSWORD, credentials[1]);
            jsonCredentials.put(CredentialsConstants.MODE, credentials[2]);

            outputStream = connection.getOutputStream();
            outputStream.write(jsonCredentials.toString().getBytes());

            return NetworkUtils.serverResponse(this, inputStream = connection.getInputStream(), NetworkingConstants.MIN_BUFFER_SIZE);
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
        if (credentialsListener != null)
            credentialsListener.communicationCompleted(response);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (credentialsListener != null)
            credentialsListener.communicationFailed();
    }
}