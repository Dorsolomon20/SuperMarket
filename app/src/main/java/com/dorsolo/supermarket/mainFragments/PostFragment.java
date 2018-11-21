package com.dorsolo.supermarket.mainFragments;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.VectorDrawable;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dorsolo.supermarket.MainActivity;
import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.background.AppExecutor;
import com.dorsolo.supermarket.background.UpdateMinUser;
import com.dorsolo.supermarket.base.BaseFragment;
import com.dorsolo.supermarket.data.viewModel.ProfileViewModel;
import com.dorsolo.supermarket.dialogFragments.ImageSourceDialog;
import com.dorsolo.supermarket.listeners.ImageSourceListener;
import com.dorsolo.supermarket.listeners.PostListener;
import com.dorsolo.supermarket.model.ImageModel;
import com.dorsolo.supermarket.networking.PostImage;
import com.dorsolo.supermarket.utilities.Constants.CodesConstants;
import com.dorsolo.supermarket.utilities.Constants.UserUpdateConstants;
import com.dorsolo.supermarket.utilities.ImageUtils;
import com.dorsolo.supermarket.utilities.NetworkUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static com.dorsolo.supermarket.utilities.Constants.PostConstants.REQ_HEIGHT;
import static com.dorsolo.supermarket.utilities.Constants.PostConstants.REQ_WIDTH;
import static com.dorsolo.supermarket.utilities.Constants.ResponsesConstants.SUCCESS;

/**
 * PostFragment class manages the posting process of images by the user, Here the user can provide an
 * image from different sources (Camera, Gallery), title and description and post an image to the server.
 * This class is responsible for handling the all process while keeping the user informed about his state
 * in the process. (Success, Failure, Progress etc.)
 */
public class PostFragment extends BaseFragment implements PostListener, ImageSourceListener {

    private static final String TAG = PostFragment.class.getSimpleName();

    private EditText txtImgTitle, txtImgDescription;
    private ImageView imgSelected;
    private ProgressBar pbPostingImg;

    private File imageTempFile;
    private Bitmap bitmap;
    private ProfileViewModel profileViewModel;

    /**
     * Listen for imeOptions of Done from the txtImgDescription EditText for posting the image with
     * the title and description by calling the postImg() method
     */
    private TextView.OnEditorActionListener txtImgDescriptionActionDoneListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                postImg();
                return true;
            }
            return false;
        }
    };

    /**
     * Listen for clicks on the btnPost button for validating the information and posting the image
     */
    private View.OnClickListener btnPostImgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postImg();
        }
    };

    /**
     * Listen for clicks on the imgSelected for launching the ImageSourceDialog so the user can choice his
     * preference for the source of the image
     */
    private View.OnClickListener imgSelectedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getFragmentManager() != null)
                ImageSourceDialog.getInstance(PostFragment.this).show(getFragmentManager(), null);
        }
    };

    /**
     * Acquire a new instance of PostFragment, Fragment class that handle the posting image process
     *
     * @return newly created instance of PostFragment
     */
    public static PostFragment getInstance() {
        return new PostFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtImgTitle = view.findViewById(R.id.txtImgTitle);
        txtImgDescription = view.findViewById(R.id.txtImgDescription);
        imgSelected = view.findViewById(R.id.imgSelected);
        pbPostingImg = view.findViewById(R.id.pbPostingImg);

        imgSelected.setOnClickListener(imgSelectedClickListener);
        txtImgDescription.setOnEditorActionListener(txtImgDescriptionActionDoneListener);
        view.findViewById(R.id.btnPostImg).setOnClickListener(btnPostImgClickListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case CodesConstants.CAMERA_CODE:
                    imageTempFile = getAppUtils().launchFullSizeCamera(true);
                    break;
                case CodesConstants.GALLERY_CODE:
                    getAppUtils().launchGallery(true);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case CodesConstants.CAMERA_CODE:
                    bitmap = ImageUtils.decodeSampledBitmapFromFile(imageTempFile.getAbsolutePath(), REQ_WIDTH, REQ_HEIGHT);
                    break;
                case CodesConstants.GALLERY_CODE:
                    try {
                        bitmap = getImageUtils().decodeSampledBitmapFromStream(data.getData(), REQ_WIDTH, REQ_HEIGHT);
                    } catch (FileNotFoundException e) {
                        bitmap = null;
                    }
                    break;
            }
            if (bitmap != null)
                imgSelected.setImageBitmap(bitmap);
        }
    }

    /**
     * Logic method for validating everything before posting an the image with the title and description
     */
    private void postImg() {
        String title = txtImgTitle.getText().toString().trim();
        String description = txtImgDescription.getText().toString().trim();
        View focus = null;
        if (imgSelected.getDrawable() instanceof VectorDrawable)
            focus = imgSelected;
        if (title.isEmpty()) {
            txtImgTitle.setError(getString(R.string.error_title_empty));
            focus = txtImgTitle;
        }
        if (description.isEmpty()) {
            txtImgDescription.setError(getString(R.string.error_description_empty));
            focus = txtImgDescription;
        }
        if (focus == null) {
            new PostImage(getActivity(), this,
                    getMainActivityListener().getEmail(), getMainActivityListener().getPassword())
                    .execute(ImageModel.getInstance(title, description, bitmap));
            if (getActivity() != null)
                getAppUtils().changeKeyboardState(getActivity().getCurrentFocus(), false);
        } else {
            focus.requestFocus();
            getAppUtils().changeKeyboardState(focus, true);
        }
    }

    /**
     * Delete the file where the posted image was writing to, due to the fact that it's not required
     * any more once it's been persisted to the server Database
     */
    private void deleteFile() {
        if (imageTempFile != null) {
            if (imageTempFile.delete())
                imageTempFile = null;
        }
    }

    /**
     * Called once the user selected the image source for the image
     *
     * @param code Code of the selected image source, can be either Camera (Full size image) Or Gallery
     */
    @Override
    public void imageSource(int code) {
        if (code == CodesConstants.CAMERA_CODE) {
            if (getAppUtils().checkPermission(Manifest.permission.CAMERA, code, true))
                imageTempFile = getAppUtils().launchFullSizeCamera(true);
        } else {
            if (getAppUtils().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, code, true))
                getAppUtils().launchGallery(true);
        }
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
     * Called when the communication with the server has started and the process of posting the image
     * has started
     */
    @Override
    public void communicationStarted() {
        getAppUtils().changeVisibility(View.VISIBLE, pbPostingImg);
    }

    /**
     * Called when the communication with the server has completed, the response can be either SUCCESS
     * for image successfully posted or FAILURE for image not successfully posted
     *
     * @param response String value which is the server response to the http request
     */
    @Override
    public void communicationCompleted(String response) {
        getAppUtils().changeVisibility(View.GONE, pbPostingImg);
        if (response.equals(SUCCESS)) {
            getAppUtils().clearText(txtImgTitle, txtImgDescription);
            imgSelected.setImageResource(R.drawable.ic_camera);
            if (profileViewModel == null)
                profileViewModel = ViewModelProviders.of(getAppUtils().getActivity()).get(ProfileViewModel.class);
            Integer numOfProducts = profileViewModel.getNumOfProducts().getValue();
            numOfProducts = 1 + (numOfProducts == null ? 0 : numOfProducts);
            profileViewModel.getNumOfProducts().setValue(numOfProducts);
            AppExecutor.getAppExecutor().getDiskIO().execute(new UpdateMinUser(getActivity(), UserUpdateConstants.UPDATE_NUM_OF_PRODUCTS, String.valueOf(numOfProducts)));
            deleteFile();
        } else
            getAppUtils().toastMsg(R.string.error_posting_image);
    }

    /**
     * Called when the image posting process has failed
     */
    @Override
    public void communicationFailed() {
        getAppUtils().changeVisibility(View.GONE, pbPostingImg);
        getAppUtils().toastMsg(R.string.error_posting_image);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteFile();
    }
}