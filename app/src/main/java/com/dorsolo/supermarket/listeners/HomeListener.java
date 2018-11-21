package com.dorsolo.supermarket.listeners;

import com.dorsolo.supermarket.base.BaseNetworkingListener;
import com.dorsolo.supermarket.model.UserModel;

/**
 * Listener for the HomeFragment class
 */
public interface HomeListener extends BaseNetworkingListener<UserModel> {

    /**
     * Called from the UpdateUser AsyncTask onPostExecute() method with the server response to the update
     *
     * @param response Whether the operation completed successfully or not
     * @param key      String value of what the response is for, so that client will know what to update
     */
    void updateProcessCompleted(String response, String key);

    /**
     * Called SettingsFragment when the user has provided a valid new info change to his profile information
     *
     * @param newData the new data the user has provided
     * @param key     Key which represent the info the user has updated
     */
    void updateUser(String newData, String key);

    /**
     * Called from the SettingsFragment when the user clicked the Preference to exit the account
     */
    void exitAccount();
}
