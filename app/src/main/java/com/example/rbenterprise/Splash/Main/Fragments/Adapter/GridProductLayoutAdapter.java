package com.example.rbenterprise.Splash.Main.Fragments.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rb.enterprise.R;
import com.example.rbenterprise.Splash.Main.Fragments.Model.HorizontalProductScrollModel;
import com.example.rbenterprise.Splash.Main.ProductDetailsActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridProductLayoutAdapter extends BaseAdapter {

    List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public GridProductLayoutAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @Override
    public int getCount() {
        return horizontalProductScrollModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
       View view;
       if (convertView == null)
       {
           view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_scroll_item_layout,null);

           CircleImageView productImage = view.findViewById(R.id.h_s_product_image);
           TextView productTitle = view.findViewById(R.id.h_s_product_title);
           TextView productPrice = view.findViewById(R.id.h_s_product_price);

           Glide.with(viewGroup.getContext()).load(horizontalProductScrollModelList.get(i).getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.placeholder)).into(productImage);
           productTitle.setText(horizontalProductScrollModelList.get(i).getProductTitle());
           productPrice.setText(horizontalProductScrollModelList.get(i).getProductPrice());

       }
       else
       {
        view = convertView;
       }

       view.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent productDetailsIntent = new Intent(view.getContext(), ProductDetailsActivity.class);
               productDetailsIntent.putExtra("PRODUCT_ID",horizontalProductScrollModelList.get(i).getProductID());
               view.getContext().startActivity(productDetailsIntent);
           }
       });

       return view;

    }
}
