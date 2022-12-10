package com.example.rbenterprise.Splash.Main.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rbenterprise.Splash.Main.ProductDetailsActivity;
import com.rb.enterprise.R;
import com.example.rbenterprise.Splash.Main.Fragments.Adapter.ProductOtherDetailsAdapter;
import com.example.rbenterprise.Splash.Main.Fragments.Model.ProductOtherDetailsModel;

import java.util.ArrayList;
import java.util.List;

public class ProductOtherDetailsFragment extends Fragment {

    public ProductOtherDetailsFragment() {
        // Required empty public constructor
    }

    private RecyclerView productOtherDetailsRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_other_details, container, false);

        productOtherDetailsRecyclerView = view.findViewById(R.id.product_other_details_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        productOtherDetailsRecyclerView.setLayoutManager(linearLayoutManager);

        ProductOtherDetailsAdapter productOtherDetailsAdapter = new ProductOtherDetailsAdapter(ProductDetailsActivity.productOtherDetailsModelList);
        productOtherDetailsRecyclerView.setAdapter(productOtherDetailsAdapter);
        productOtherDetailsAdapter.notifyDataSetChanged();

        return view;
    }
}