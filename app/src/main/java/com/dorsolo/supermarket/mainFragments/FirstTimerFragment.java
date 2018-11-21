package com.dorsolo.supermarket.mainFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.adapters.FirstTimerFragmentAdapter;
import com.dorsolo.supermarket.animation.ParallaxPageTransformer;
import com.dorsolo.supermarket.base.BaseFragment;

/**
 * FirstTimerFragment is shown only for the first time when the user first launches the app after
 * installing it (or deleting the data from the settings), It provides a ViewPager with 3 fragment that
 * are a 'walkthrough' to the app, It's purpose is to introduce the user to the app.
 */
public class FirstTimerFragment extends BaseFragment {

    private static final String TAG = FirstTimerFragment.class.getSimpleName();

    private int sliderCount;

    private ViewPager pagerFirstTimer;
    private Button btnGoBack, btnGoForward;

    /**
     * Listen for viewpager changes to the sliders
     */
    private ViewPager.OnPageChangeListener pagerFirstTimerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * Update the buttons text and visibility based on the current slider fragment
         *
         * @param position the position of the new slider fragment, called after swipe and finger release
         */
        @Override
        public void onPageSelected(int position) {
            if ((sliderCount = position) == 0)
                btnGoBack.setVisibility(View.GONE);
            else if (sliderCount == 1) {
                btnGoBack.setVisibility(View.VISIBLE);
                btnGoForward.setText(R.string.forward);
            } else
                btnGoForward.setText(R.string.done);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * Listen for both btnGoBack and btnGoForward button clicks, manage viewPager accordingly
     */
    private View.OnClickListener btnChangePageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btnGoBack)
                pagerFirstTimer.setCurrentItem(sliderCount - 1, true);
            else {
                if (sliderCount < 2)
                    pagerFirstTimer.setCurrentItem(sliderCount + 1, true);
                else
                    getMainActivityListener().passToCredentials();
            }
        }
    };

    /**
     * Acquire a new instance of FirstTimerFragment
     *
     * @return Newly created instance of FirstTimerFragment
     */
    public static FirstTimerFragment getInstance() {
        return new FirstTimerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_first_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pagerFirstTimer = view.findViewById(R.id.pagerFirstTimer);
        btnGoBack = view.findViewById(R.id.btnGoBack);
        btnGoForward = view.findViewById(R.id.btnGoForward);

        //Listeners
        pagerFirstTimer.addOnPageChangeListener(pagerFirstTimerChangeListener);
        btnGoBack.setOnClickListener(btnChangePageClickListener);
        btnGoForward.setOnClickListener(btnChangePageClickListener);

        //Adapters and attachments
        pagerFirstTimer.setAdapter(new FirstTimerFragmentAdapter(getChildFragmentManager()));
        pagerFirstTimer.setPageTransformer(true, new ParallaxPageTransformer());
        ((TabLayout) view.findViewById(R.id.tabDots)).setupWithViewPager(pagerFirstTimer, true);
    }
}