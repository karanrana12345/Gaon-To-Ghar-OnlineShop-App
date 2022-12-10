package com.example.rbenterprise.Splash.Main.Fragments;

import static com.example.rbenterprise.Splash.Main.DBQueries.lists;
import static com.example.rbenterprise.Splash.Main.DBQueries.loadFragmentData;
import static com.example.rbenterprise.Splash.Main.DBQueries.loadedCategoriesNames;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.rbenterprise.Splash.Main.Fragments.Model.WishListModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.WorkersSectionModel;
import com.example.rbenterprise.Splash.Main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rb.enterprise.R;
import com.example.rbenterprise.Splash.Main.Fragments.Adapter.HomePageAdapter;
import com.example.rbenterprise.Splash.Main.Fragments.Model.HomePageModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.HorizontalProductScrollModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.SliderModel;
import com.example.rbenterprise.Splash.Main.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment()
    {

    }

    private RecyclerView homePageRecyclerView;
    private AppCompatButton searchbuttonPass;
    private HomePageAdapter adapter;
    private LottieAnimationView noInternetConnection;
    private Button retryBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        noInternetConnection = view.findViewById(R.id.no_internet_connection);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        retryBtn = view.findViewById(R.id.retry_btn);

        homePageRecyclerView = view.findViewById(R.id.home_page_recyclerview);
        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(RecyclerView.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLayoutManager);
        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.purple_700),getContext().getResources().getColor(R.color.purple_700),getContext().getResources().getColor(R.color.purple_700));

        //Home Page Fake List

        List<SliderModel>sliderModelFakeList = new ArrayList<>();
        sliderModelFakeList.add(new SliderModel("null"));
        sliderModelFakeList.add(new SliderModel("null"));
        sliderModelFakeList.add(new SliderModel("null"));
        sliderModelFakeList.add(new SliderModel("null"));
        sliderModelFakeList.add(new SliderModel("null"));
        sliderModelFakeList.add(new SliderModel("null"));
        sliderModelFakeList.add(new SliderModel("null"));
        sliderModelFakeList.add(new SliderModel("null"));

        List<HorizontalProductScrollModel> horizontalProductScrollModeFakelList = new ArrayList<>();
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","",""));

        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(2,"",horizontalProductScrollModeFakelList,new ArrayList<WishListModel>()));
        homePageModelFakeList.add(new HomePageModel(3,"",horizontalProductScrollModeFakelList));
        //Home Page Fake List

        adapter = new HomePageAdapter(homePageModelFakeList);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true)
        {
            noInternetConnection.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.VISIBLE);
            searchbuttonPass = view.findViewById(R.id.searchbutton);

            searchbuttonPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SearchActivity.class);
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View,String>(searchbuttonPass,"searchTransition");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),pairs);

                    startActivity(intent,options.toBundle());
                }
            });

            if (lists.size() ==  0)
            {
                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(swipeRefreshLayout,homePageRecyclerView,getContext(),0);
            }
            else
            {
                adapter = new HomePageAdapter(lists.get(0));
                adapter.notifyDataSetChanged();
            }
            homePageRecyclerView.setAdapter(adapter);
        }
        else
        {
            homePageRecyclerView.setVisibility(View.GONE);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
        }

        // Refresh Layout

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });

        // Refresh Layout

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadPage();
            }
        });

        return view;
    }

    private void reloadPage()
        {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            lists.clear();
            loadedCategoriesNames.clear();

            if (networkInfo != null && networkInfo.isConnected() == true) {
                retryBtn.setVisibility(View.GONE);
                noInternetConnection.setVisibility(View.GONE);
                homePageRecyclerView.setVisibility(View.VISIBLE);
                adapter = new HomePageAdapter(homePageModelFakeList);
                homePageRecyclerView.setAdapter(adapter);
                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(swipeRefreshLayout, homePageRecyclerView, getContext(), 0);
            } else
            {
                Toast.makeText(getContext(),"No Internet Connection Available !",Toast.LENGTH_SHORT).show();
                homePageRecyclerView.setVisibility(View.GONE);
                noInternetConnection.setVisibility(View.VISIBLE);
                retryBtn.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }