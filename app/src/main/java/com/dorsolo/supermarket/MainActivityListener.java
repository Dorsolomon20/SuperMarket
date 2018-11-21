package com.dorsolo.supermarket;

import com.dorsolo.supermarket.data.db.MinUser;
import com.dorsolo.supermarket.model.UserModel;

/**
 * This interface provide as the main communication channel between every fragment and the MainActivity which is
 * the host activity, passed to notify the activity about changes and user progresses
 */
public interface MainActivityListener {

    /**
     * Called from the FirstTimerFragment when the user pressed the Done button after passing ALL of
     * the sliders and his ready to pass to the CredentialsFragment for the first time. This method should only be
     * called from the FirstTimerFragment
     */
    void passToCredentials();

    /**
     * Called from the CredentialsFragment when the user successfully created an account or logged in to an existing one,
     * The CredentialsFragment will get destroyed and the HomeFragment will replace it. This method should only be called from
     * the CredentialsFragment
     *
     * @param email    the logged in user email
     * @param password the logged in user password
     */
    void passToHome(String email, String password);

    /**
     * Called from the HomeFragment once the user have chosen to exit the account and go back to the CredentialsFragment
     */
    void exitAccount();

    /**
     * Called by one of displayed fragments when they require the logged in user Email
     *
     * @return String value of the logged in user Email
     */
    String getEmail();

    /**
     * Called by one of displayed fragments when they require the logged in user Password
     *
     * @return String value of the logged in user Password
     */
    String getPassword();

    /**
     * Called from HomeFragment when the information about the logged in user first arrived, persist the data to
     * a local DB (SqLite) for not fetching the same data over and over again
     *
     * @param user User obj containing the data to persist about the user such as the username, phoneNumber, numOfProducts and profileImg
     */
    void persistUser(UserModel user);

    /**
     * Called when one of the Fragments require an instance of the MinUser from the DB, which contain the information about the logged in user
     *
     * @return first and only record from the 'Users' DB 'MinUser' table
     */
    MinUser getMinUser();
}