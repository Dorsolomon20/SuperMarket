package com.dorsolo.supermarket;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;

import com.dorsolo.supermarket.background.AppExecutor;
import com.dorsolo.supermarket.background.DeleteMinUser;
import com.dorsolo.supermarket.background.GetMinUser;
import com.dorsolo.supermarket.background.PersistMinUser;
import com.dorsolo.supermarket.base.BaseActivity;
import com.dorsolo.supermarket.data.db.AppDatabase;
import com.dorsolo.supermarket.data.db.MinUser;
import com.dorsolo.supermarket.data.viewModel.ProfileViewModel;
import com.dorsolo.supermarket.mainFragments.CredentialsFragment;
import com.dorsolo.supermarket.mainFragments.FirstTimerFragment;
import com.dorsolo.supermarket.mainFragments.HomeFragment;
import com.dorsolo.supermarket.mainFragments.PostFragment;
import com.dorsolo.supermarket.mainFragments.SettingsFragment;
import com.dorsolo.supermarket.model.UserModel;
import com.dorsolo.supermarket.utilities.Constants.UserConstants;
import com.dorsolo.supermarket.utilities.Constants.ViewPagerConstants;

import java.util.concurrent.ExecutionException;

/**
 * MainActivity class manages the entire flow of the app, it's responsible for managing the transition between
 * fragments based on certain events, the status bar and navigation bar visibility, BottomNavigationView
 * clicks and MinUser changes. The app is built with one activity and many fragments (One to many ratio).
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private View decorView;
    private BottomNavigationView navApp;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    /**
     * Listen for changes in the system UI visibility in cases where the status bar and navigation bar visible, and close them
     */
    private View.OnSystemUiVisibilityChangeListener systemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            if (visibility == getResources().getInteger(R.integer.zero))
                decorView.setSystemUiVisibility(getAppUtils().fullScreen());
        }
    };

    /**
     * Listen for changes in the bottomNav selected item, manage the navigation of the user based on his selected items.
     * Handle the fragment transactions and backSTack handling
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navAppItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.btnProfile:
                    if (getSupportFragmentManager().findFragmentById(R.id.fragmentMainContainer) instanceof SettingsFragment)
                        getSupportFragmentManager().popBackStack();
                    else if ((!(getSupportFragmentManager().findFragmentById(R.id.fragmentMainContainer) instanceof HomeFragment)))
                        getAppUtils().launchFragment(HomeFragment.getInstance(), false, null);
                    return true;
                case R.id.btnPost:
                    if (!(getSupportFragmentManager().findFragmentById(R.id.fragmentMainContainer) instanceof PostFragment))
                        getAppUtils().launchFragment(PostFragment.getInstance(), false, null);
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        navigateLanding();
    }

    /**
     * Initialize variables in the onCreate()
     */
    @SuppressLint("CommitPrefEdits")
    private void init() {
        navApp = findViewById(R.id.navApp);
        navApp.setSelectedItemId(R.id.btnProfile);
        navApp.setOnNavigationItemSelectedListener(navAppItemSelectedListener);
        (decorView = getWindow().getDecorView()).setOnSystemUiVisibilityChangeListener(systemUiVisibilityChangeListener);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    /**
     * Navigate the landing fragment, which fragment to display to the user when he first launch the app,
     * based on his current state
     */
    private void navigateLanding() {
        //Logged in user
        if (sharedPreferences.getString(UserConstants.EMAIL, null) != null) {
            getAppUtils().launchFragment(HomeFragment.getInstance(), false, null);
            return;
        }
        getAppUtils().changeVisibility(View.GONE, navApp);
        //First timer user
        if (sharedPreferences.getBoolean(ViewPagerConstants.FIRST_TIMER, true))
            getAppUtils().launchFragment(FirstTimerFragment.getInstance(), false, null);
            //Logged off user
        else
            getAppUtils().launchFragment(CredentialsFragment.getInstance(), false, null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            decorView.setSystemUiVisibility(getAppUtils().fullScreen());
    }

    /**
     * Pass to the CredentialsFragment and save the state of the user, Not firstTimer any more
     */
    @Override
    public void passToCredentials() {
        getAppUtils().launchFragment(CredentialsFragment.getInstance(), false, null, R.anim.enter_from_right, R.anim.exit_to_left);
        sharedPreferencesEditor.putBoolean(ViewPagerConstants.FIRST_TIMER, false);
        sharedPreferencesEditor.apply();
    }

    /**
     * Pass to the HomeFragment and save the state of the user, Logged in
     *
     * @param email    the logged in user email
     * @param password the logged in user password
     */
    @Override
    public void passToHome(String email, String password) {
        getAppUtils().changeVisibility(View.VISIBLE, navApp);
        getAppUtils().launchFragment(HomeFragment.getInstance(), false, null);
        getAppUtils().populateSharedPreference(sharedPreferencesEditor, email, password);
    }

    /**
     * Called by the presented (Foreground) fragment when the logged in password is required
     *
     * @return String value of the logged in user Email
     */
    @Override
    public String getEmail() {
        return sharedPreferences.getString(UserConstants.EMAIL, null);
    }

    /**
     * Called by the presented (Foreground) fragment when the logged in password is required
     *
     * @return String value of the logged in user Password
     */
    @Override
    public String getPassword() {
        return sharedPreferences.getString(UserConstants.PASSWORD, null);
    }

    /**
     * Pass to the CredentialsFragment and save the state of the user, logged out
     */
    @Override
    public void exitAccount() {
        getAppUtils().clearBackStack();
        getAppUtils().changeVisibility(View.GONE, navApp);
        getAppUtils().launchFragment(CredentialsFragment.getInstance(), false, null);
        getAppUtils().populateSharedPreference(sharedPreferencesEditor, null, null);
        AppExecutor.getAppExecutor().getDiskIO().execute(new DeleteMinUser(getApplicationContext()));
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.clearUser();
    }

    /**
     * Called to persist the logged in user information
     *
     * @param user User obj containing the data to persist about the user such as the username, phoneNumber, numOfProducts and profileImg
     */
    @Override
    public void persistUser(final UserModel user) {
        AppExecutor.getAppExecutor().getDiskIO().execute(new PersistMinUser(this, getFileUtils(), user));
    }

    /**
     * Called to retrieve the first and only record from the DB 'MinUser' table
     *
     * @return MinUser instance with all of the current data about the logged in user
     */
    @Override
    public MinUser getMinUser() {
        try {
            return AppExecutor.getAppExecutor().getExecutorService().submit(new GetMinUser(getApplicationContext())).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    /**
     * onDestroy() - Close DB connection and end all process in the final stage of the App lifeCycle
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        if (appDatabase != null && appDatabase.isOpen())
            appDatabase.closeConnection();
    }
}