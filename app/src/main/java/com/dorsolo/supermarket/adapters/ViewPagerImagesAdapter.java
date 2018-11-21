package com.dorsolo.supermarket.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dorsolo.supermarket.listeners.ImagesListener;
import com.dorsolo.supermarket.nestedFragments.GridViewPagerImages;
import com.dorsolo.supermarket.nestedFragments.LinearViewPagerImages;
import com.dorsolo.supermarket.utilities.Constants;

/**
 * Adapter class for the ViewPager in the HomeFragment, manage the
 * fragments for the viewPager
 */
public class ViewPagerImagesAdapter extends FragmentStatePagerAdapter {

    private ImagesListener imagesListener;

    public ViewPagerImagesAdapter(FragmentManager fm, ImagesListener imagesListener) {
        super(fm);
        if (imagesListener == null)
            throw new IllegalArgumentException("ImagesListener can't be null, required for server communication between GetImages and HomeFragment");
        this.imagesListener = imagesListener;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return GridViewPagerImages.getInstance(imagesListener);
            default:
                return LinearViewPagerImages.getInstance(imagesListener);
        }
    }

    @Override
    public int getCount() {
        return Constants.HomeConstants.NUM_OF_HOME_VIEW_PAGER_SLIDERS;
    }
}