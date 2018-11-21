package com.dorsolo.supermarket.animation;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dorsolo.supermarket.nestedFragments.FirstTimerSliderFragment;

/**
 * This class provide custom animation in the transitions between each fragments of a ViewPager,
 * This class is implemented in the FirstTimerFragment ViewPager. It creates a simple parallax effect
 * when swiping between the fragments
 */
public class ParallaxPageTransformer implements ViewPager.PageTransformer {

    /**
     * Called when a swipe detected in the ViewPager
     */
    @Override
    public void transformPage(@NonNull View view, float position) {
        int pageWidth = view.getWidth();
        if (position < -1)
            view.setAlpha(1);
        else if (position <= 1)
            ((FirstTimerSliderFragment) view.getTag()).transformPage(-position * (pageWidth / 2));
        else
            view.setAlpha(1);
    }
}
