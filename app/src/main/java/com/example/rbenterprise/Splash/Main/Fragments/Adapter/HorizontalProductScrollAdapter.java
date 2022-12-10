package com.example.rbenterprise.Splash.Main.Fragments.Adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.rb.enterprise.R;
import com.example.rbenterprise.Splash.Main.Fragments.Model.HorizontalProductScrollModel;
import com.example.rbenterprise.Splash.Main.ProductDetailsActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdapter.ViewHolder holder, int position) {
        String resource = horizontalProductScrollModelList.get(position).getProductImage();
        String title = horizontalProductScrollModelList.get(position).getProductTitle();
        String price = horizontalProductScrollModelList.get(position).getProductPrice();
        String productId = horizontalProductScrollModelList.get(position).getProductID();
        holder.setData(productId,resource,title,price);
    }

    @Override
    public int getItemCount() {

        if (horizontalProductScrollModelList.size() > 8)
        {
            return 8;
        }
        else
        {
            return horizontalProductScrollModelList.size();
        }

    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private final String TAG = HomePageAdapter.class.getSimpleName();
        private InterstitialAd mInterstitialAd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.h_s_product_image);
            productTitle = itemView.findViewById(R.id.h_s_product_title);
            productPrice = itemView.findViewById(R.id.h_s_product_price);

        }

        private void setData(String productId,String resource,String title,String price) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.placeholder)).into(productImage);
            productPrice.setText(price);
            productTitle.setText(title);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID",productId);
                    itemView.getContext().startActivity(productDetailsIntent);

                    MobileAds.initialize(itemView.getContext(), new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {}
                    });
                    AdRequest iadRequest = new AdRequest.Builder().build();

                    InterstitialAd.load(itemView.getContext(), "ca-app-pub-7407231061173432/1391441200", iadRequest,
                            new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                    // The mInterstitialAd reference will be null until
                                    // an ad is loaded.
                                    mInterstitialAd = interstitialAd;
                                    mInterstitialAd.show((Activity) itemView.getContext());
                                    Log.i(TAG, "onAdLoaded");
                                }
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Handle the error
                                    Log.d(TAG, loadAdError.toString());
                                    mInterstitialAd = null;
                                }
                            });

                }
            });
        }
    }
}
