package com.dorsolo.supermarket.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.dorsolo.supermarket.base.BaseAsyncTask;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utilities specifically for network handling used across the app
 */
public final class NetworkUtils {

    private static ConnectivityManager connectivityManager;

    /**
     * Get's an instance of ConnectivityManager for passing NetworkInfo to check connectivity state
     *
     * @return an instance of NetworkInfo which represent the current internet connection
     */
    public static NetworkInfo getConnectivityManager(Activity activity) {
        if (connectivityManager == null)
            connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
    }

    /**
     * Used to read data from inputStream and turn it into a String which represent all of the data stream.
     * Should be used only from within AsyncTask class
     *
     * @param asyncTask   the AsyncTask which preforming the server communication, Checking that his not cancelled
     * @param inputStream the inputStream to read from
     * @param bufferSize  int which represent the buffer size for the inputStream reading
     * @return String value which is all of the data from the stream
     * @throws IOException Exception is cases there as an IOException, must likely a close inputStream
     */
    public static String serverResponse(BaseAsyncTask asyncTask, InputStream inputStream, int bufferSize) throws IOException {
        if (inputStream == null)
            throw new IllegalArgumentException("InputStream can't be null");
        if (asyncTask == null)
            throw new IllegalArgumentException("AsyncTask can't be null");
        byte[] buffer = new byte[bufferSize];
        int actuallyRead;
        StringBuilder serverResponse = new StringBuilder();
        while ((actuallyRead = inputStream.read(buffer)) != -1) {
            if (asyncTask.isCancelled())
                return null;
            serverResponse.append(new String(buffer, 0, actuallyRead));
        }
        return serverResponse.toString();
    }

    /**
     * Build a url with query parameters if added, This is the safe approach for building urls with query params
     *
     * @param baseUrl This provide as the base url containing the url: protocol, sub-domain and domain
     * @param pairs   Not required, Provide as the url key:value query parameters
     * @return String which is the final url
     */
    public static String buildUrl(String baseUrl, String... pairs) {
        if (baseUrl == null)
            throw new IllegalArgumentException("BaseUrl can't be null, as it's the base for the url building process");
        if (pairs.length % 2 != 0)
            throw new IllegalArgumentException("Pairs must come in pairs, as they are used for key : value in the url build");
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        for (int i = 0; i < pairs.length; i += 2)
            builder.appendQueryParameter(pairs[i], pairs[i + 1]);
        return builder.toString();
    }
}