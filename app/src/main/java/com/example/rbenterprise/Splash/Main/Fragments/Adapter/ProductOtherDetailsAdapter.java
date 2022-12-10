package com.example.rbenterprise.Splash.Main.Fragments.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rb.enterprise.R;
import com.example.rbenterprise.Splash.Main.Fragments.Model.ProductOtherDetailsModel;

import java.util.List;

public class ProductOtherDetailsAdapter extends RecyclerView.Adapter<ProductOtherDetailsAdapter.ViewHolder> {

    private List<ProductOtherDetailsModel> productOtherDetailsModelList;

    public ProductOtherDetailsAdapter(List<ProductOtherDetailsModel> productOtherDetailsModelList) {
        this.productOtherDetailsModelList = productOtherDetailsModelList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (productOtherDetailsModelList.get(position).getType()) {
            case 0:
                return ProductOtherDetailsModel.SPECIFICATION_TITLE;
            case 1:
                return ProductOtherDetailsModel.SPECIFICATION_BODY;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public ProductOtherDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {

            case ProductOtherDetailsModel.SPECIFICATION_TITLE:
                TextView title = new TextView(parent.getContext());
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(setDp(16, parent.getContext()), setDp(16, parent.getContext()), setDp(16, parent.getContext()), setDp(8, parent.getContext()));
                title.setLayoutParams(layoutParams);
                return new ViewHolder(title);

            case ProductOtherDetailsModel.SPECIFICATION_BODY:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_other_details_item_layout, parent, false);
                return new ViewHolder(view);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOtherDetailsAdapter.ViewHolder holder, int position) {

        switch (productOtherDetailsModelList.get(position).getType()) {
            case ProductOtherDetailsModel.SPECIFICATION_TITLE:
                holder.setTitle(productOtherDetailsModelList.get(position).getTitle());
                break;
            case ProductOtherDetailsModel.SPECIFICATION_BODY:
                String featureTitle = productOtherDetailsModelList.get(position).getFeatureName();
                String featureDetail = productOtherDetailsModelList.get(position).getFeatureValue();

                holder.setFeatures(featureTitle, featureDetail);
                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return productOtherDetailsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView featureName;
        private TextView featureValue;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void setTitle(String titleText) {
            title = (TextView) itemView;
            title.setText(titleText);
        }

        private void setFeatures(String featureTitle, String featuredetail) {
            featureName = itemView.findViewById(R.id.feature_name);
            featureValue = itemView.findViewById(R.id.feature_value);
            featureName.setText(featureTitle);
            featureValue.setText(featuredetail);
        }
    }


    private int setDp(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
