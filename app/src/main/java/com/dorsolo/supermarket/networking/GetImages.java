package com.dorsolo.supermarket.networking;

import android.support.v7.app.AppCompatActivity;

import com.dorsolo.supermarket.base.BaseAsyncTask;
import com.dorsolo.supermarket.listeners.ImagesListener;
import com.dorsolo.supermarket.model.ImageModel;
import com.dorsolo.supermarket.utilities.Constants.NetworkingConstants;
import com.dorsolo.supermarket.utilities.Constants.UserConstants;
import com.dorsolo.supermarket.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * GetImages AsyncTask fetches images from the server based on the given params, we pass the current amount
 * and the total amount so the server will know what images to give us and how many. Here we perform the conversation'
 * from String data format to a List of Image obj
 * GetImages require email and password for validation
 */
public class GetImages extends BaseAsyncTask<String, Void, List<ImageModel>> {

    private int currentAmount, totalAmount;
    private ImagesListener imagesListener;

    public GetImages(AppCompatActivity activity, ImagesListener imagesListener, int currentAmount, int totalAmount) {
        super(activity, imagesListener);
        if (imagesListener == null)
            throw new IllegalArgumentException("ImagesListener required for AsyncTask HomeFragment communication, can't be null");
        this.imagesListener = imagesListener;
        this.currentAmount = currentAmount;
        this.totalAmount = totalAmount;
    }

    @Override
    protected List<ImageModel> doInBackground(String... credentials) {
        if (credentials.length != 2 || credentials[0] == null || credentials[1] == null)
            throw new IllegalArgumentException("Credentials length must be equal to 2, and contain a valid Email and Password");
        URL url;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            url = new URL(NetworkUtils.buildUrl(NetworkingConstants.HOME_URL, UserConstants.EMAIL, credentials[0], UserConstants.PASSWORD, credentials[1], UserConstants.CLIENT_IMAGES_AMOUNT, String.valueOf(currentAmount), UserConstants.TOTAL_IMAGES_AMOUNT, String.valueOf(totalAmount)));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty(NetworkingConstants.CONTENT_TYPE, NetworkingConstants.APPLICATION_JSON);
            connection.setRequestMethod(NetworkingConstants.GET);
            connection.connect();

            String serverResponse = NetworkUtils.serverResponse(this, inputStream = connection.getInputStream(), NetworkingConstants.MAX_BUFFER_SIZE);
            if (serverResponse == null)
                return null;
            return ImageModel.getImagesFromJsonArray(new JSONArray(serverResponse), getFileUtils());
        } catch (IOException | JSONException e) {
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
    protected void onPostExecute(List<ImageModel> images) {
        super.onPostExecute(images);
        imagesListener.newImages(images);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        imagesListener.communicationFailed();
    }
}