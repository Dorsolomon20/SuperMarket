package com.dorsolo.supermarket.listeners;

import com.dorsolo.supermarket.base.BaseNetworkingListener;
import com.dorsolo.supermarket.model.ImageModel;
import com.dorsolo.supermarket.model.UserModel;

import java.util.List;

/**
 * Listener for new images arrival from the server
 */
public interface ImagesListener extends BaseNetworkingListener<UserModel> {

    /**
     * Called from the GetImages AsyncTask from the LinearViewPagerImagesAdapter and GridViewPagerImagesAdapter classes in response
     * to user scrolling, the call is to fetch new images for him from the server
     *
     * @param images the new images in response to the user scroll
     */
    void newImages(List<ImageModel> images);

    /**
     * Called from the adapters to update the current amount requested of products to avoid requesting
     * for the same products more the once, can occur when scrolling down fast from both RecyclerViews
     *
     * @param currentAmount the amount requested
     */
    void numOfProductsRequested(int currentAmount);
}
