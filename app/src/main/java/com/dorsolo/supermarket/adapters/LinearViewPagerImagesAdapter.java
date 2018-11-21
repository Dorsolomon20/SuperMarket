package com.dorsolo.supermarket.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dorsolo.supermarket.R;
import com.dorsolo.supermarket.background.AppExecutor;
import com.dorsolo.supermarket.base.BaseActivity;
import com.dorsolo.supermarket.base.BaseAdapter;
import com.dorsolo.supermarket.listeners.ImagesListener;
import com.dorsolo.supermarket.model.ImageModel;
import com.dorsolo.supermarket.networking.GetImages;

import java.util.List;

import static com.dorsolo.supermarket.utilities.Constants.NetworkingConstants.THREAD_POOL_SIZE;

/**
 * Adapter class for the ViewPager in the HomeFragment class, this adapter is the bridge between the
 * data given and the UI, it also manages the requesting of more images if necessary. This adapter display
 * the data in a LinearLayout
 */
public class LinearViewPagerImagesAdapter extends BaseAdapter<LinearViewPagerImagesAdapter.ViewHolder> {

    private String email, password;
    private Integer totalAmount, numOfProductsRequested;

    private List<ImageModel> images;
    private ImagesListener imagesListener;
    private BaseActivity activity;
    private AppExecutor appExecutor;

    public LinearViewPagerImagesAdapter(BaseActivity activity, ImagesListener imagesListener, List<ImageModel> images, String... credentials) {
        super(activity);
        if (activity == null)
            throw new IllegalArgumentException("Activity can't be null, required for the adapter to perform new network operations");
        if (credentials.length != 2 || credentials[0] == null || credentials[1] == null)
            throw new IllegalArgumentException("Credentials length must be equal to 2, and contain the logged in user email and password, Values can't be null");
        if (imagesListener == null)
            throw new IllegalArgumentException("ImagesListener can't be null, required for server communication between GetImages and HomeFragment");
        this.activity = activity;
        this.imagesListener = imagesListener;
        this.images = images;
        email = credentials[0];
        password = credentials[1];
    }

    @Override
    public int getItemViewType(int position) {
        return position == images.size() ? 0 : 1;
    }

    @NonNull
    @Override
    public LinearViewPagerImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return viewType == 0 ? new FooterViewHolder(LayoutInflater.from(activity).inflate(R.layout.footer_row_image, viewGroup, false))
                : new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.row_image_linear, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LinearViewPagerImagesAdapter.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof FooterViewHolder) {
            if (images.size() == totalAmount)
                getAppUtils().changeVisibility(View.GONE, ((FooterViewHolder) viewHolder).pbLoading);
            return;
        }
        //Checks there's more images to load and that the user has scrolled
        if (position == images.size() - 1 && images.size() < totalAmount && (numOfProductsRequested == null ? -1 : numOfProductsRequested) < images.size()) {
            imagesListener.numOfProductsRequested(images.size());
            if (appExecutor == null)
                appExecutor = AppExecutor.getAppExecutor(THREAD_POOL_SIZE);
            new GetImages(activity, imagesListener, images.size(), totalAmount).executeOnExecutor(appExecutor.getNetworkIO(), email, password);
        }
        ImageModel image = images.get(position);
        if (image != null) {
            viewHolder.lblTitle.setText(image.getTitle());
            viewHolder.lblDescription.setText(image.getDescription());
            viewHolder.imgProfileLinearRecycler.setImageBitmap(image.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size() + 1;
    }

    /**
     * Called from the GridViewPagerImages fragment to update the recycler with newly received images from the server
     *
     * @param images List of Image objects
     */
    public void updateRecycler(List<ImageModel> images) {
        notifyItemRangeInserted((this.images == null ? 0 : this.images.size()) + 1, (this.images = images).size());
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setNumOfProductsRequested(Integer numOfProductsRequested) {
        this.numOfProductsRequested = numOfProductsRequested;
    }

    /**
     * Main ViewHolder class for each Item that present's an image
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView lblTitle, lblDescription;
        private ImageView imgProfileLinearRecycler;

        ViewHolder(View itemView) {
            super(itemView);
            lblTitle = itemView.findViewById(R.id.lblImgTitle);
            lblDescription = itemView.findViewById(R.id.lblImgDescription);
            imgProfileLinearRecycler = itemView.findViewById(R.id.imgProfileLinearRecycler);
        }
    }

    /**
     * Secondary FooterViewHolder for the footer item when new items are loading it show's a loading progressBar at the bottom to indicate
     * to the user that there are more images loading currently
     */
    class FooterViewHolder extends ViewHolder {

        private ProgressBar pbLoading;

        FooterViewHolder(View itemView) {
            super(itemView);
            pbLoading = itemView.findViewById(R.id.pbLoading);
        }
    }
}