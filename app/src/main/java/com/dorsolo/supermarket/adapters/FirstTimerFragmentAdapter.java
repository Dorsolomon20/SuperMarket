package com.dorsolo.supermarket.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.nestedFragments.FirstTimerSliderFragment;
import com.dorsolo.supermarket.utilities.Constants.ViewPagerConstants;

/**
 * Adapter class for the ViewPager in the FirstTimerFragment, manage the
 * fragments for the viewPager
 */
public class FirstTimerFragmentAdapter extends FragmentStatePagerAdapter {

    public FirstTimerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FirstTimerSliderFragment.getInstance(R.color.first_slider_background,
                        R.string.first_slider_title, R.string.first_slider_description);
            case 1:
                return FirstTimerSliderFragment.getInstance(R.color.second_slider_background,
                        R.string.second_slider_title, R.string.second_slider_description);
            default:
                return FirstTimerSliderFragment.getInstance(R.color.third_slider_background,
                        R.string.third_slider_title, R.string.third_slider_description);
        }
    }

    @Override
    public int getCount() {
        return ViewPagerConstants.PAGER_SLIDER_COUNT;
    }
}