package com.dorsolo.supermarket.mainFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.listeners.HomeListener;
import com.dorsolo.supermarket.utilities.Constants.UserConstants;
import com.dorsolo.supermarket.utilities.Constants.UserUpdateConstants;

/**
 * SettingsFragment class from here the user can customize his account and profile information and other settings.
 * Each customization option should be added here
 */
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private String username, phoneNumber;

    private HomeListener homeListener;

    /**
     * Acquire a new instance of SettingsFragment, A Preference class that enable the user to customize his experience
     * by changing preset settings of the app, and personalize features of it and change information about his account
     *
     * @return newly created instance of SettingsFragment
     */
    public static SettingsFragment getInstance(HomeListener homeListener, String... credentials) {
        if (homeListener == null)
            throw new IllegalArgumentException("HomeListener can't be null, required for SettingsFragment to communicate with HomeFragment");
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.homeListener = homeListener;
        settingsFragment.username = credentials[0];
        settingsFragment.phoneNumber = credentials[1];
        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);
        setPreferenceChangeListener();

        if (username != null)
            ((EditTextPreference) findPreference(UserConstants.USERNAME)).setText(username);
        if (phoneNumber != null)
            ((EditTextPreference) findPreference(UserConstants.PHONE_NUMBER)).setText(phoneNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Set OnPreferenceChangeListener for every Preference in the xml file, for validating the new settings the user
     * have provided before they get persisted and sent to the server
     */
    private void setPreferenceChangeListener() {
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof PreferenceCategory) {
                PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
                for (int j = 0; j < preferenceCategory.getPreferenceCount(); j++) {
                    Preference pref = preferenceCategory.getPreference(j);
                    if (pref instanceof EditTextPreference) {
                        pref.setOnPreferenceChangeListener(this);
                    } else
                        pref.setOnPreferenceClickListener(this);
                }
            }
        }
    }

    /**
     * Called Before data persisting
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case UserConstants.USERNAME:
                String newUsername = String.valueOf(newValue).trim();
                return !newUsername.isEmpty() && newUsername.length() <= getResources().getInteger(R.integer.username_max_length);
            case UserConstants.PHONE_NUMBER:
                String newPhoneNumber = String.valueOf(newValue).trim();
                return !newPhoneNumber.isEmpty() && newPhoneNumber.length() <= getResources().getInteger(R.integer.phone_number_max_length) && newPhoneNumber.matches("[0-9]+");
            default:
                return false;
        }
    }

    /**
     * Called After data persisting
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        homeListener.updateUser(sharedPreferences.getString(key, null),
                key.equals(UserConstants.USERNAME) ? UserUpdateConstants.UPDATE_USERNAME
                        : UserUpdateConstants.UPDATE_PHONE_NUMBER);
    }

    /**
     * Called when a preference as been clicked
     */
    @Override
    public boolean onPreferenceClick(Preference preference) {
        homeListener.exitAccount();
        return true;
    }
}