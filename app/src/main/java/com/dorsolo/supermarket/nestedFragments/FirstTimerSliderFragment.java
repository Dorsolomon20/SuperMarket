package com.dorsolo.supermarket.nestedFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.base.BaseFragment;
import com.dorsolo.supermarket.listeners.PageTransformerDelegate;

/**
 * FirstTimerSliderFragment used in the FirstTimerFragment ViewPager for every slider,
 * used with different parameters (BackgroundColor, Title, Description)
 */
public class FirstTimerSliderFragment extends BaseFragment implements PageTransformerDelegate {

    private int backgroundColor, sliderTitle, sliderDescription;

    private ImageView imgSlider;

    /**
     * Acquire new instance of FirstSliderFragment
     *
     * @param backgroundColor   Reference to the color.xml resource file for the slider background color
     * @param sliderTitle       Reference to the strings.xml recourse file for the slider title
     * @param sliderDescription Reference to the strings.xml recourse file for the slider description
     * @return Newly created instance of FirstSliderFragment
     */
    public static FirstTimerSliderFragment getInstance(int backgroundColor, int sliderTitle, int sliderDescription) {
        FirstTimerSliderFragment firstTimerSliderFragment = new FirstTimerSliderFragment();
        firstTimerSliderFragment.backgroundColor = backgroundColor;
        firstTimerSliderFragment.sliderTitle = sliderTitle;
        firstTimerSliderFragment.sliderDescription = sliderDescription;
        return firstTimerSliderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_first_timer_slider, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setTag(this);

        imgSlider = view.findViewById(R.id.imgSlider);
        TextView lblSliderTitle = view.findViewById(R.id.lblSliderTitle);
        TextView lblSliderDescription = view.findViewById(R.id.lblSliderDescription);

        view.setBackgroundColor(ActivityCompat.getColor(getFileUtils().getActivity(), backgroundColor));
        lblSliderTitle.setText(sliderTitle);
        lblSliderDescription.setText(sliderDescription);
    }

    /**
     * Called to perform animations on the UI based on the given position
     *
     * @param position Position of the page relative to the current front and center position of the pager
     */
    @Override
    public void transformPage(float position) {
        imgSlider.setTranslationX(position);
    }
}