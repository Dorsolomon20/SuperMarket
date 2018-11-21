package com.dorsolo.supermarket.mainFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.animation.SharedElementTransition;
import com.dorsolo.supermarket.base.BaseFragment;
import com.dorsolo.supermarket.model.ImageModel;

/**
 * This fragment is launched once one of the images from the GridViewPagerImages as been clicked to show
 * a larger size of the image. It implements the SharedElement transition to create a nice transition of the image
 * from the GridViewPagerImages to the ImageView that contains it in this fragment.
 */
public class ImageDisplayFragment extends BaseFragment {

    private ImageModel image;

    /**
     * Acquire a new instance of ImageDisplayFragment for displaying a Image object content
     *
     * @param image the Image obj to display
     * @return New instance of ImageDisplayFragment with fields populated by the giving params
     */
    public static ImageDisplayFragment getInstance(ImageModel image) {
        if (image == null)
            throw new IllegalArgumentException("Image obj can't be null, otherwise there is nothing to display");
        ImageDisplayFragment imageDisplayFragment = new ImageDisplayFragment();
        SharedElementTransition sharedElementTransition = new SharedElementTransition();
        imageDisplayFragment.setSharedElementEnterTransition(sharedElementTransition);
        imageDisplayFragment.setSharedElementReturnTransition(sharedElementTransition);
        imageDisplayFragment.image = image;
        return imageDisplayFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_image_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView lblImgTitle = view.findViewById(R.id.lblImgTitle);
        ImageView imageView = view.findViewById(R.id.imgLargeContainer);
        TextView lblImgDescription = view.findViewById(R.id.lblImgDescription);

        lblImgTitle.setText(image.getTitle());
        imageView.setImageBitmap(image.getImage());
        lblImgDescription.setText(image.getDescription());
    }
}
