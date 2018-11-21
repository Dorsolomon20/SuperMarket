package com.dorsolo.supermarket.nestedFragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.adapters.LinearViewPagerImagesAdapter;
import com.dorsolo.supermarket.base.BaseActivity;
import com.dorsolo.supermarket.base.BaseFragment;
import com.dorsolo.supermarket.data.viewModel.ProfileViewModel;
import com.dorsolo.supermarket.listeners.ImagesListener;
import com.dorsolo.supermarket.model.ImageModel;

import java.util.List;

/**
 * Fragment class that manages the displaying of images in a LinearLayout for the HomeFragment ViewPager
 */
public class LinearViewPagerImages extends BaseFragment {

    private static final String TAG = LinearViewPagerImages.class.getSimpleName();

    private ImagesListener imagesListener;
    private ProfileViewModel profileViewModel;
    private LinearViewPagerImagesAdapter linearViewPagerImagesAdapter;

    /**
     * Observe changes in the ViewModel Images LiveData to update the LinearViewPagerImagesAdapter
     */
    private Observer<List<ImageModel>> observerImagesChanges = new Observer<List<ImageModel>>() {
        @Override
        public void onChanged(@Nullable List<ImageModel> images) {
            if (images != null)
                linearViewPagerImagesAdapter.updateRecycler(images);
        }
    };

    /**
     * Observe changes in the viewModel numOfProducts LiveData to update the LinearViewPagerImagesAdapter
     */
    private Observer<Integer> observerNumOfProductsChanges = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer totalAmount) {
            if (totalAmount != null)
                linearViewPagerImagesAdapter.setTotalAmount(totalAmount);
        }
    };

    /**
     * Observe changes in the viewModel numOfProductsRequested LiveData to update the LinearViewPagerImagesAdapter
     */
    private Observer<Integer> observerNumOfProductsRequested = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer currentAmount) {
            if (currentAmount != null)
                linearViewPagerImagesAdapter.setNumOfProductsRequested(currentAmount);
        }
    };

    /**
     * Get's a new instance of LinearViewPagerImages with all of it's fields set to the giving params
     * Credentials are for requesting the images as scrolling
     *
     * @return Newly created instance of LinearViewPagerImages
     */
    public static LinearViewPagerImages getInstance(ImagesListener imagesListener) {
        LinearViewPagerImages linearViewPagerImages = new LinearViewPagerImages();
        linearViewPagerImages.imagesListener = imagesListener;
        return linearViewPagerImages;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = ViewModelProviders.of(getAppUtils().getActivity()).get(ProfileViewModel.class);
        profileViewModel.getImages().observe(this, observerImagesChanges);
        profileViewModel.getNumOfProducts().observe(this, observerNumOfProductsChanges);
        profileViewModel.getNumOfProductsRequested().observe(this, observerNumOfProductsRequested);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recycler_view_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerImages = view.findViewById(R.id.recyclerImages);
        recyclerImages.setHasFixedSize(true);
        recyclerImages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerImages.setAdapter(linearViewPagerImagesAdapter = new LinearViewPagerImagesAdapter((BaseActivity) getActivity(),
                imagesListener, profileViewModel.getImages().getValue(), getMainActivityListener().getEmail(),
                getMainActivityListener().getPassword()));
    }
}
