package com.dorsolo.supermarket.base;

import android.net.NetworkInfo;

/**
 * BaseNetworkingListener provides to each listener interface that extends it, it contains common methods
 * that are often been used to perform networking operations
 *
 * @param <T> The value passed by the child Listener
 */
public interface BaseNetworkingListener<T> {

    /**
     * Require an instance of NetworkInfo which represent the current active network
     *
     * @return the current instance of NetworkInfo with all of the information
     */
    NetworkInfo getActiveNetwork();

    /**
     * Called when the communication with the server has started at this time we should display or notify
     * the user that a network process has started and is under way
     */
    void communicationStarted();

    /**
     * Called when the communication ended successfully and the server answered a response to the http request
     *
     * @param response Generic Object which is the response to the http request
     */
    void communicationCompleted(T response);

    /**
     * Called when the communication with the server failed, can happen due to several reasons such as bad or non
     * internet connection, bad parameters etc. This method should handle the error and present the user an
     * indication to what happened.
     */
    void communicationFailed();
}
