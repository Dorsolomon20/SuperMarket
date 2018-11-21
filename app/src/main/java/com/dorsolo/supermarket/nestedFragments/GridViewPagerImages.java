package com.dorsolo.supermarket.nestedFragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.adapters.GridViewPagerImagesAdapter;
import com.dorsolo.supermarket.base.BaseActivity;
import com.dorsolo.supermarket.base.BaseFragment;
import com.dorsolo.supermarket.data.viewModel.ProfileViewModel;
import com.dorsolo.supermarket.listeners.ImagesListener;
import com.dorsolo.supermarket.model.ImageModel;

import java.util.List;

import static com.dorsolo.supermarket.utilities.Constants.HomeConstants.HOME_VIEW_PAGER_GRID_ITEM_SPAN_COUNT;
import static com.dorsolo.supermarket.utilities.Constants.HomeConstants.HOME_VIEW_PAGER_GRID_SPAN_COUNT;

/**
 * Fragment class that manages the displaying of images in a GridLayout for the HomeFragment ViewPager
 */
public class GridViewPagerImages extends BaseFragment {

    private static final String TAG = GridViewPagerImages.class.getSimpleName();

    private ImagesListener imagesListener;
    private ProfileViewModel profileViewModel;
    private GridViewPagerImagesAdapter gridViewPagerImagesAdapter;

    /**
     * Observe changes to the List<Image> in the ProfileViewModel LiveData to update the GridViewPagerImagesAdapter
     */
    private Observer<List<ImageModel>> observerImagesChanges = new Observer<List<ImageModel>>() {
        @Override
        public void onChanged(@Nullable List<ImageModel> images) {
            if (images != null)
                gridViewPagerImagesAdapter.updateRecycler(images);
        }
    };

    /**
     * Observe changes in the viewModel numOfProducts LiveData to update the GridViewPagerImagesAdapter
     */
    private Observer<Integer> observerNumOfProductsChanges = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer totalAmount) {
            if (totalAmount != null)
                gridViewPagerImagesAdapter.setTotalAmount(totalAmount);
        }
    };

    /**
     * Observe changes in the viewModel numOfProductsRequested to update the GridViewPagerImagesAdapter
     */
    private Observer<Integer> observerNumOfProductsRequested = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer currentAmount) {
            if (currentAmount != null)
                gridViewPagerImagesAdapter.setNumOfProductsRequested(currentAmount);
        }
    };

    /**
     * Listen for the position of each item in the recyclerView, if the position equals the images.size() meaning that
     * it's the last item in the array, we know that it can only be the ProgressBar we set it's span count to the full amount
     * so it would center in the middle, for the rest we provide 1 span count
     */
    private GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return gridViewPagerImagesAdapter.getImageSize() == position ? HOME_VIEW_PAGER_GRID_SPAN_COUNT : HOME_VIEW_PAGER_GRID_ITEM_SPAN_COUNT;
        }
    };

    /**
     * Get's a new instance of GridViewPagerImages with all of it's fields set to the giving params
     * Credentials are for requesting the images as scrolling
     *
     * @return Newly created instance of GridViewPagerImages
     */
    public static GridViewPagerImages getInstance(ImagesListener imagesListener) {
        if (imagesListener == null)
            throw new IllegalArgumentException("ImagesListener can't be null, required for communicating between the AsyncTask and the HomeFragment");
        GridViewPagerImages gridViewPagerImages = new GridViewPagerImages();
        gridViewPagerImages.imagesListener = imagesListener;
        return gridViewPagerImages;
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
        GridLayoutManager gridLayoutManager;
        RecyclerView recyclerGridImages = view.findViewById(R.id.recyclerImages);
        recyclerGridImages.setHasFixedSize(true);
        recyclerGridImages.setLayoutManager(gridLayoutManager = new GridLayoutManager(getActivity(), HOME_VIEW_PAGER_GRID_SPAN_COUNT));
        recyclerGridImages.setAdapter(gridViewPagerImagesAdapter = new GridViewPagerImagesAdapter((BaseActivity) getActivity(),
                imagesListener, profileViewModel.getImages().getValue(), getMainActivityListener().getEmail(),
                getMainActivityListener().getPassword()));
        gridLayoutManager.setSpanSizeLookup(spanSizeLookup);
    }
}
