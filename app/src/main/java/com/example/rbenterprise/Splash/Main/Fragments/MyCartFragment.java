package com.example.rbenterprise.Splash.Main.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rbenterprise.Splash.Main.DBQueries;
import com.example.rbenterprise.Splash.Main.Fragments.Adapter.WishListAdapter;
import com.example.rbenterprise.Splash.Main.MainActivity;
import com.example.rbenterprise.Splash.Main.ProductDetailsActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.rb.enterprise.R;
import com.example.rbenterprise.Splash.Main.AddAddressActivity;
import com.example.rbenterprise.Splash.Main.DeliveryActivity;
import com.example.rbenterprise.Splash.Main.Fragments.Adapter.CartAdapter;
import com.example.rbenterprise.Splash.Main.Fragments.Model.CartItemModel;

import java.util.ArrayList;
import java.util.List;

public class MyCartFragment extends Fragment {


    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemsRecyclerView;
    private Button continueBtn;
    private Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    private TextView totalAmount;
    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        //loading dialog

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        //loading dialog

        continueBtn = view.findViewById(R.id.cart_continue_btn);
        totalAmount = view.findViewById(R.id.total_cart_amount);

        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(layoutManager);

        if (DBQueries.cartItemModelList.size() == 0)
        {
            DBQueries.cartList.clear();
            DBQueries.loadCartList(getContext(),loadingDialog,true,totalAmount);
        }
        else
        {
            if (DBQueries.cartItemModelList.get(DBQueries.cartItemModelList.size()-1).getType() == CartItemModel.TOTAL_AMOUNT)
            {
                LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }

        cartAdapter = new CartAdapter(DBQueries.cartItemModelList,totalAmount,true);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DeliveryActivity.cartItemModelList = new ArrayList<>();

                for (int x = 0 ; x < DBQueries.cartItemModelList.size() ; x++)
                {
                    CartItemModel cartItemModel = DBQueries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock())
                    {
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }

                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                loadingDialog.show();
                if (DBQueries.addressesModelList.size() == 0)
                {
                    DBQueries.loadAddresses(getContext(),loadingDialog);
                }
                else
                {
                    loadingDialog.dismiss();
                    Intent intent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(intent);
                }
            }
        });

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return view;
    }
}