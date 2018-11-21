package com.dorsolo.supermarket.dialogFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.base.BaseDialogFragment;
import com.dorsolo.supermarket.listeners.ImageSourceListener;

import static com.dorsolo.supermarket.utilities.Constants.CodesConstants.CAMERA_CODE;
import static com.dorsolo.supermarket.utilities.Constants.CodesConstants.GALLERY_CODE;

/**
 * DialogFragment class with two ImageViews for the user to decide which image source he would like to use.
 * Each image (Camera, Gallery) will launch a different app on the device in order to retrieve the image
 * from the location the user want
 */
public class ImageSourceDialog extends BaseDialogFragment {

    private ImageSourceListener imageSourceListener;

    /**
     * Listen for clicks for both of the imageViews for passing there code to the
     * launching class to tell which image source the user selected
     */
    private View.OnClickListener imgSourceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imageSourceListener.imageSource(v.getId() == R.id.imgCameraSource ?
                    CAMERA_CODE :
                    GALLERY_CODE
            );
            dismiss();
        }
    };

    /**
     * Acquire a new instance of ImageSourceDialog
     *
     * @param imageSourceListener Interface that provides as a listener between the Dialog to a class
     *                            to pass the code of the selected image source
     * @return newly created instance of ImageSourceDialog
     */
    public static ImageSourceDialog getInstance(ImageSourceListener imageSourceListener) {
        if (imageSourceListener == null)
            throw new IllegalArgumentException("ImageSourceListener can't be null, required for communication between the Dialog and launcher class");
        ImageSourceDialog imageSourceDialog = new ImageSourceDialog();
        imageSourceDialog.imageSourceListener = imageSourceListener;
        return imageSourceDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image_source, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.imgCameraSource).setOnClickListener(imgSourceClickListener);
        view.findViewById(R.id.imgGallerySource).setOnClickListener(imgSourceClickListener);
    }
}