package com.dorsolo.supermarket.networking;

import android.support.v4.app.FragmentActivity;

import com.dorsolo.supermarket.base.BaseAsyncTask;
import com.dorsolo.supermarket.listeners.PostListener;
import com.dorsolo.supermarket.model.ImageModel;
import com.dorsolo.supermarket.utilities.Constants.ImageConstants;
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
 * PostImage AsyncTask post an image to the server by the given params, title, description and the image,
 * Then just like with the PostCredentials the server only returns whether the action was successful or not.
 * PostImage require email and password for validation
 */
public class PostImage extends BaseAsyncTask<ImageModel, Void, String> {

    private String email, password;
    private PostListener postListener;

    public PostImage(FragmentActivity activity, PostListener postListener, String... credentials) {
        super(activity, postListener);
        if (postListener == null)
            throw new IllegalArgumentException("PostListener required for AsyncTask PostFragment communication, can't be null");
        if (credentials[0] != null && credentials[1] != null && credentials.length != 2)
            throw new IllegalArgumentException("Credentials must contain the logged in user email and password nothing more nothing less, and they can't be null");
        this.postListener = postListener;
        email = credentials[0];
        password = credentials[1];
    }

    @Override
    protected String doInBackground(ImageModel... images) {
        ImageModel image = images[0];
        if (image == null)
            throw new RuntimeException("Image obj on index 0 can't be null");
        URL url;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            url = new URL(NetworkUtils.buildUrl(NetworkingConstants.POST_URL));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(NetworkingConstants.POST);
            connection.setRequestProperty(NetworkingConstants.CONTENT_TYPE, NetworkingConstants.APPLICATION_JSON);
            connection.connect();

            JSONObject jsonImg = new JSONObject();
            jsonImg.put(UserConstants.EMAIL, email);
            jsonImg.put(UserConstants.PASSWORD, password);
            jsonImg.put(ImageConstants.TITLE, image.getTitle());
            jsonImg.put(ImageConstants.DESCRIPTION, image.getDescription());
            jsonImg.put(ImageConstants.IMAGE, getFileUtils().byteArrayToBase64String(getFileUtils().bitmapToByteArray(image.getImage(), ImageConstants.MID_QUALITY)));

            outputStream = connection.getOutputStream();
            outputStream.write(jsonImg.toString().getBytes());

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
        if (postListener != null)
            postListener.communicationCompleted(response);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (postListener != null)
            postListener.communicationFailed();
    }
}