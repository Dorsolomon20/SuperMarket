package com.dorsolo.supermarket.mainFragments;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dorsolo.supermarket.MainActivity;
import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.adapters.ViewPagerImagesAdapter;
import com.dorsolo.supermarket.background.AppExecutor;
import com.dorsolo.supermarket.background.PersistProfileImg;
import com.dorsolo.supermarket.background.UpdateMinUser;
import com.dorsolo.supermarket.base.BaseFragment;
import com.dorsolo.supermarket.data.db.MinUser;
import com.dorsolo.supermarket.data.viewModel.ProfileViewModel;
import com.dorsolo.supermarket.listeners.HomeListener;
import com.dorsolo.supermarket.listeners.ImagesListener;
import com.dorsolo.supermarket.model.ImageModel;
import com.dorsolo.supermarket.model.UserModel;
import com.dorsolo.supermarket.networking.GetImages;
import com.dorsolo.supermarket.networking.GetUserInfo;
import com.dorsolo.supermarket.networking.UpdateUser;
import com.dorsolo.supermarket.utilities.Constants.CodesConstants;
import com.dorsolo.supermarket.utilities.Constants.HomeConstants;
import com.dorsolo.supermarket.utilities.Constants.ImageConstants;
import com.dorsolo.supermarket.utilities.Constants.NetworkingConstants;
import com.dorsolo.supermarket.utilities.Constants.UserUpdateConstants;
import com.dorsolo.supermarket.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.dorsolo.supermarket.utilities.Constants.ResponsesConstants.SUCCESS;

/**
 * HomeFragment is the Profile class of the logged in user, from here the user can change the settings
 * of his account, Change the information about is profile (Image, Username, Phone number etc.), See all
 * of his images in two different layouts, Grid and linear. It provides as the entry point for the app,
 * If the user is logged in to an account this fragment will first get launched. It's responsible for managing the process
 * of fetching images from the server
 */
public class HomeFragment extends BaseFragment implements HomeListener, ImagesListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private String updatedInfo;

    private ImageView imgProfile, imgNotPostsYet;
    private TextView lblProfileName, lblPhoneNumber, lblNumOfProducts;
    private Button btnProfileSettings;
    private TabLayout tabImagesMode;
    private ViewPager viewPagerImages;

    private Bitmap capturedImg;
    private ProfileViewModel profileViewModel;

    /**
     * Observe changes to the ViewModel imgProfile LiveData
     */
    private Observer<Bitmap> observerImgProfile = new Observer<Bitmap>() {
        @Override
        public void onChanged(@Nullable Bitmap profileImage) {
            if (profileImage != null)
                imgProfile.setImageBitmap(profileImage);
        }
    };

    /**
     * Observe changes to the ViewModel lblProfileName LiveData
     */
    private Observer<String> observerUsername = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String username) {
            if (username != null)
                lblProfileName.setText(username);
        }
    };

    /**
     * Observe changes to the ViewModel lblPhoneNumber LiveData
     */
    private Observer<String> observerPhoneNumber = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String phoneNumber) {
            if (phoneNumber != null)
                lblPhoneNumber.setText(phoneNumber);
        }
    };

    /**
     * Observe changes to the ViewModel lblNumOfProducts LiveData
     */
    private Observer<Integer> observerNumOfProducts = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer numOfProducts) {
            if (numOfProducts != null)
                lblNumOfProducts.setText(String.valueOf(numOfProducts));
        }
    };

    /**
     * Listen for clicks on the btnProfileSettings for launching the profile settings page
     */
    private View.OnClickListener btnProfileSettingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getAppUtils().launchFragment(SettingsFragment.getInstance(HomeFragment.this, profileViewModel.getUsername().getValue(),
                    profileViewModel.getPhoneNumber().getValue()), true, null);
        }
    };

    /**
     * Listen for clicks on the imgProfile for launching a selection menu for the source of the new profile image
     * (Camera || Gallery)
     */
    private View.OnClickListener imgProfileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getAppUtils().checkPermission(Manifest.permission.CAMERA, CodesConstants.THUMBNAIL_CAMERA_CODE, true))
                getAppUtils().launchCamera(true);
        }
    };

    /**
     * Listen for clicks on the lblPhoneNumber for calling the clicked number of the profile page
     */
    private View.OnClickListener lblPhoneNumberClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getAppUtils().checkPermission(Manifest.permission.CALL_PHONE, CodesConstants.PHONE_CALL_CODE, true))
                getAppUtils().makePhoneCall(profileViewModel.getPhoneNumber().getValue());
        }
    };

    /**
     * Acquire an instance of HomeFragment
     *
     * @return new instance of HomeFragment
     */
    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = ViewModelProviders.of(getAppUtils().getActivity()).get(ProfileViewModel.class);
        profileViewModel.getUsername().observe(this, observerUsername);
        profileViewModel.getImgProfile().observe(this, observerImgProfile);
        profileViewModel.getPhoneNumber().observe(this, observerPhoneNumber);
        profileViewModel.getNumOfProducts().observe(this, observerNumOfProducts);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setUpViews();
        setTabLayoutIcons();

        MinUser minUser = getMainActivityListener().getMinUser();
        if (minUser == null) {
            if (profileViewModel.getImages().getValue() == null) {
                new GetUserInfo(getActivity(), this)
                        .execute(getMainActivityListener().getEmail(), getMainActivityListener().getPassword());
            }
        } else {
            profileViewModel.persistMinUser(minUser);
            if (minUser.getNumOfProducts() > 0 && profileViewModel.getImages().getValue() == null) {
                new GetImages((AppCompatActivity) getActivity(),
                        this, getResources().getInteger(R.integer.zero), minUser.getNumOfProducts())
                        .executeOnExecutor(AppExecutor.getAppExecutor(NetworkingConstants.THREAD_POOL_SIZE).getNetworkIO(), getMainActivityListener().getEmail(),
                                getMainActivityListener().getPassword());
            } else if (minUser.getNumOfProducts() == 0)
                noPostsYet();
        }
    }

    /**
     * Initialize all of the necessary views from the fragment_home layout, using the findViewById() method
     *
     * @param view The ViewGroup container for all of the layout views, Provide as a reference to all of the other views in the hierarchy
     */
    private void initializeViews(View view) {
        imgProfile = view.findViewById(R.id.imgProfile);
        lblProfileName = view.findViewById(R.id.lblProfileName);
        lblPhoneNumber = view.findViewById(R.id.lblPhoneNumber);
        lblNumOfProducts = view.findViewById(R.id.lblNumOfProducts);
        btnProfileSettings = view.findViewById(R.id.btnProfileSettings);
        tabImagesMode = view.findViewById(R.id.tabImagesMode);
        viewPagerImages = view.findViewById(R.id.viewPagerImages);
        imgNotPostsYet = view.findViewById(R.id.imgNoPostsYet);
    }

    /**
     * Setup the views for user use, by setting click listeners and attaching adapter
     */
    private void setUpViews() {
        imgProfile.setOnClickListener(imgProfileClickListener);
        lblPhoneNumber.setOnClickListener(lblPhoneNumberClickListener);
        btnProfileSettings.setOnClickListener(btnProfileSettingsClickListener);
        viewPagerImages.setAdapter(new ViewPagerImagesAdapter(getChildFragmentManager(), this));
        tabImagesMode.setupWithViewPager(viewPagerImages, true);
    }

    /**
     * Set's the icons for the tab layout
     */
    private void setTabLayoutIcons() {
        for (int i = 0; i < tabImagesMode.getTabCount(); i++) {
            TabLayout.Tab tab = tabImagesMode.getTabAt(i);
            if (tab != null)
                tab.setIcon(HomeConstants.icons[i]);
        }
    }

    /**
     * Called after a user responded to a request for a permission, here we perform checks whether the user granted
     * the permission or not and based on that we continue the flow of the action
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case CodesConstants.PHONE_CALL_CODE:
                    getAppUtils().makePhoneCall(profileViewModel.getPhoneNumber().getValue());
                    break;
                case CodesConstants.THUMBNAIL_CAMERA_CODE:
                    getAppUtils().launchCamera(true);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case CodesConstants.THUMBNAIL_CAMERA_CODE:
                    Bundle dataExtras = data.getExtras();
                    if (dataExtras != null)
                        capturedImg = (Bitmap) dataExtras.get(CodesConstants.CAPTURED_IMAGE);
                    break;
            }
            if (capturedImg != null)
                updateImgProfile(capturedImg);
        }
    }

    /**
     * Updates the user profile image, encode the giving Bitmap to Base64 String
     *
     * @param picture The Bitmap to be sent to the server to replace the current profile image
     */
    private void updateImgProfile(final Bitmap picture) {
        final AppExecutor appExecutor = AppExecutor.getAppExecutor();
        appExecutor.getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final String encodedPicture = getFileUtils().byteArrayToBase64String(getFileUtils().bitmapToByteArray(picture, ImageConstants.HIGH_QUALITY));
                appExecutor.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        new UpdateUser(getActivity(), HomeFragment.this, UserUpdateConstants.UPDATE_PROFILE_IMAGE)
                                .execute(encodedPicture, getMainActivityListener().getEmail(), getMainActivityListener().getPassword());
                    }
                });
            }
        });
    }

    /**
     * If the user has not posts yet any products, display an image to indicate that
     */
    private void noPostsYet() {
        getAppUtils().changeVisibility(View.GONE, viewPagerImages);
        getAppUtils().changeVisibility(View.VISIBLE, imgNotPostsYet);
    }

    /**
     * Called from the GetUserInfo AsyncTask from the onPreExecute() method for checking whether there's an inherent connection
     *
     * @return Current instance of NetworkInfo containing information about the current internet connection
     */
    @Override
    public NetworkInfo getActiveNetwork() {
        return NetworkUtils.getConnectivityManager(getActivity());
    }

    /**
     * Called when the communication has started
     */
    @Override
    public void communicationStarted() {
        Log.d(TAG, "Networking operation started");
    }

    /**
     * Called when the communication with the server has completed, and successfully fetched the user information
     *
     * @param user User object containing all of the logged in user information
     */
    @Override
    public void communicationCompleted(UserModel user) {
        profileViewModel.persistUser(user);
        getMainActivityListener().persistUser(user);
        if (user.getNumOfProducts() == 0)
            noPostsYet();
    }

    /**
     * Called when communication with the server for the HomeFragment has failed
     */
    @Override
    public void communicationFailed() {
        getAppUtils().toastMsg(R.string.error_credentials_submit_failed);
    }

    /**
     * Called to check whether the update process completed successfully and to update the UI if so
     *
     * @param response Whether the operation completed successfully or not
     * @param key      Identifier key for knowing what UI view to update
     */
    @Override
    public void updateProcessCompleted(String response, String key) {
        if (response.equals(SUCCESS)) {
            switch (key) {
                case UserUpdateConstants.UPDATE_PROFILE_IMAGE:
                    try {
                        updatedInfo = AppExecutor.getAppExecutor().getExecutorService().submit(new PersistProfileImg(getFileUtils(), capturedImg)).get();
                        profileViewModel.getImgProfile().setValue(capturedImg);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    capturedImg = null;
                    break;
                case UserUpdateConstants.UPDATE_USERNAME:
                    profileViewModel.getUsername().setValue(updatedInfo);
                    break;
                case UserUpdateConstants.UPDATE_PHONE_NUMBER:
                    profileViewModel.getPhoneNumber().setValue(updatedInfo);
                    break;
            }
            if (updatedInfo != null) {
                AppExecutor.getAppExecutor().getDiskIO().execute(new UpdateMinUser(getAppUtils().getActivity(), key, updatedInfo));
                updatedInfo = null;
            }
        }
    }

    /**
     * Called from the SettingsFragment once the user has provided a valid new profile information
     *
     * @param newData the new data the user has provided
     * @param key     Key which represent the info the user has updated
     */
    @Override
    public void updateUser(String newData, String key) {
        new UpdateUser(getAppUtils().getActivity(), this, key)
                .execute(updatedInfo = newData, getMainActivityListener().getEmail(), getMainActivityListener().getPassword());
    }

    /**
     * Called to exit the account, call the MainActivityListener exitAccount() method to implement the exit logic
     */
    @Override
    public void exitAccount() {
        getMainActivityListener().exitAccount();
    }

    /**
     * Called from the GetImages AsyncTask after getting new images from the server
     *
     * @param images the new images in response to the user scroll
     */
    @Override
    public void newImages(List<ImageModel> images) {
        List<ImageModel> tempImages = profileViewModel.getImages().getValue();
        if (tempImages == null)
            tempImages = new ArrayList<>();
        tempImages.addAll(images);
        profileViewModel.getImages().setValue(tempImages);
    }

    /**
     * Called from either the GridViewImagesAdapter or the LinearViewPagerImagesAdapter to update the current
     * max amount requested
     *
     * @param currentAmount the amount requested
     */
    @Override
    public void numOfProductsRequested(int currentAmount) {
        profileViewModel.getNumOfProductsRequested().setValue(currentAmount);
    }
}