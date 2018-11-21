package com.dorsolo.supermarket.mainFragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.base.BaseFragment;
import com.dorsolo.supermarket.listeners.CredentialsListener;
import com.dorsolo.supermarket.networking.PostCredentials;
import com.dorsolo.supermarket.utilities.Constants.CredentialsConstants;
import com.dorsolo.supermarket.utilities.NetworkUtils;

import static com.dorsolo.supermarket.utilities.Constants.ResponsesConstants.SUCCESS;

/**
 * CredentialsFragment allow the user to perform two action, Sign in / Sign up. The fragment is responsible for
 * taking the credentials given from the user manage the validation process with the server and based on the answer
 * perform certain actions
 */
public class CredentialsFragment extends BaseFragment implements CredentialsListener {

    private static final String TAG = CredentialsFragment.class.getSimpleName();

    private boolean isSignUp = true;
    private String email, password;

    private AnimatorSet loadingAnimator;

    private ImageView imgAnimation;
    private Animation credentialsError;
    private EditText txtEmail, txtPassword;
    private Button btnSubmitCredentials;
    private PostCredentials postCredentials;

    /**
     * Listen for changes in the tab selection in the tabCredentialsMode TabLayout, each change will effect the ways the
     * submitted credentials will be handled
     */
    private TabLayout.OnTabSelectedListener tabCredentialsModeSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(Tab tab) {
            isSignUp = !isSignUp;
            btnSubmitCredentials.setText(getString(isSignUp ? R.string.sign_up : R.string.sign_in));
        }

        @Override
        public void onTabUnselected(Tab tab) {

        }

        @Override
        public void onTabReselected(Tab tab) {

        }
    };

    /**
     * Listen for clicks on the btnSubmitCredentials button for submitting the credentials by calling the
     * submitCredentials() method.
     */
    private View.OnClickListener btnSubmitCredentialsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            submitCredentials();
        }
    };

    /**
     * Listen for imeOptions of Done from the txtPassword EditText for submitting the Credentials
     * by calling the submitCredentials() method
     */
    private TextView.OnEditorActionListener txtPasswordActionDoneListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
            if (action == EditorInfo.IME_ACTION_DONE) {
                submitCredentials();
                return true;
            }
            return false;
        }
    };

    /**
     * Acquire a new instance of CredentialsFragment
     *
     * @return Newly created instance of CredentialsFragment
     */
    public static CredentialsFragment getInstance() {
        return new CredentialsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_credentials, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabCredentialsMode = view.findViewById(R.id.tabCredentialsMode);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPassword = view.findViewById(R.id.txtPassword);
        btnSubmitCredentials = view.findViewById(R.id.btnSubmitCredentials);
        imgAnimation = view.findViewById(R.id.imgAnimation);

        tabCredentialsMode.addOnTabSelectedListener(tabCredentialsModeSelectedListener);
        txtPassword.setOnEditorActionListener(txtPasswordActionDoneListener);
        btnSubmitCredentials.setOnClickListener(btnSubmitCredentialsClickListener);
    }

    /**
     * Validate the submitted credentials and if approved pass them to AsyncTask class for dispatching them to
     * the server for approval
     */
    private void submitCredentials() {
        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
        View error = null;
        if (email.length() < CredentialsConstants.MIN_EMAIL_LENGTH) {
            txtEmail.setError(getString(email.isEmpty() ? R.string.error_email_required : R.string.error_email_not_valid));
            error = txtEmail;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError(getString(R.string.error_email_not_valid));
            error = txtEmail;
        }
        if (password.length() < CredentialsConstants.MIN_PASSWORD_LENGTH) {
            txtPassword.setError(getString(email.isEmpty() ? R.string.error_password_required : R.string.error_password_not_valid));
            error = txtPassword;
        }
        if (error == null) {
            if (loadingAnimator != null && loadingAnimator.isRunning())
                stopLoadingAnimation();
            if (postCredentials != null)
                postCredentials.stopRunning();
            postCredentials = new PostCredentials(getActivity(), this);
            postCredentials.execute(email, password, String.valueOf(isSignUp));
            if (getActivity() != null)
                getAppUtils().changeKeyboardState(getActivity().getCurrentFocus(), false);
        } else {
            error.requestFocus();
            if (credentialsError == null)
                credentialsError = AnimationUtils.loadAnimation(getActivity(), R.anim.credentials_error);
            btnSubmitCredentials.startAnimation(credentialsError);
        }
    }

    /**
     * Called to stop the loading animation and reset the pre existing state
     */
    private void stopLoadingAnimation() {
        getAppUtils().changeVisibility(View.GONE, imgAnimation);
        loadingAnimator.cancel();
    }

    /**
     * Get's a NetworkInfo instance for the onPreExecute() method for checking internet connection
     *
     * @return instance of NetworkInfo
     */
    @Override
    public NetworkInfo getActiveNetwork() {
        return NetworkUtils.getConnectivityManager(getActivity());
    }

    /**
     * Called when the communication with the server has started and the process of submitting credentials
     * has started
     */
    @Override
    public void communicationStarted() {
        getAppUtils().clearText(txtEmail, txtPassword);
        getAppUtils().changeVisibility(View.VISIBLE, imgAnimation);
        if (loadingAnimator == null) {
            loadingAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.base_loading);
            loadingAnimator.setTarget(imgAnimation);
        }
        loadingAnimator.start();
    }

    /**
     * Called when the communication with the server has completed, the response can be either SUCCESS
     * for credentials validated or FAILURE for credentials not validated
     *
     * @param response String value which is the server response to the http request
     */
    @Override
    public void communicationCompleted(String response) {
        stopLoadingAnimation();
        if (response.equals(SUCCESS))
            getMainActivityListener().passToHome(email, password);
        else
            getAppUtils().toastMsg(R.string.error_credentials_submit_failed);
        postCredentials = null;
    }

    /**
     * Called when the credentials submit process has failed
     */
    @Override
    public void communicationFailed() {
        stopLoadingAnimation();
        getAppUtils().toastMsg(R.string.error_credentials_submit_failed);
        postCredentials = null;
    }

    /**
     * onDestroy() - finish any remaining operations
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //If there's an AsyncTask still running
        if (postCredentials != null)
            postCredentials.stopRunning();
    }
}