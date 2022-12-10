package com.example.rbenterprise.Splash.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.example.rbenterprise.Splash.Login.LoginActivity;
import com.example.rbenterprise.Splash.Main.Fragments.Model.CartItemModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.ProductOtherDetailsModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.WishListModel;
import com.example.rbenterprise.Splash.Main.Fragments.MyCartFragment;
import com.example.rbenterprise.Splash.Main.Fragments.ProductOtherDetailsFragment;
import com.example.rbenterprise.Splash.Register.RegisterActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rb.enterprise.R;
import com.example.rbenterprise.Splash.Main.Fragments.Adapter.ProductDetailsAdapter;
import com.example.rbenterprise.Splash.Main.Fragments.Adapter.ProductImagesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    private ViewPager productImagesViewPager;
    private TabLayout viewpagerIndicator;

    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    private ConstraintLayout productDetailsTabsContainer;

    // Rating Layout
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNoContainer;
    private TextView totalRatingsFigure;
    private LinearLayout ratingsProgressBarContainer;
    private TextView averageRating;
    public static int initialRating;
    // Rating Layout

    private Button buyNowBtn;
    private Dialog signInDialog;
    private Dialog loadingDialog;
    private LinearLayout addToCartBtn;

    public static FloatingActionButton addToWishListBtn;
    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;

    private FirebaseFirestore firebaseFirestore;
    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView totalRatingMiniView;
    private TextView productPrice;
    private TextView cuttedPrice;
    private ImageView codIndicator;
    private TextView tvCodIndicator;

    private TextView descriptionTitle;
    private TextView descriptionBody1;
    private TextView descriptionBody2;
    private TextView descriptionBody3;
    private TextView descriptionBody4;
    private TextView descriptionBody5;
    private FirebaseUser currentUser;
    public static String productID;

    private DocumentSnapshot documentSnapshot;

    public static List<ProductOtherDetailsModel> productOtherDetailsModelList = new ArrayList<>();
    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;

    public static boolean fromSearch = false;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewpagerIndicator = findViewById(R.id.viewpager_indicator);
        addToWishListBtn = findViewById(R.id.add_to_wishlist_button);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);

        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_details_tablayout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        buyNowBtn = findViewById(R.id.buy_now_btn);
        productTitle = findViewById(R.id.product_title);
        averageRatingMiniView = findViewById(R.id.tv_product_rating_miniview);
        totalRatingMiniView = findViewById(R.id.total_ratings_miniview);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        tvCodIndicator = findViewById(R.id.tv_cod_indicator);
        codIndicator = findViewById(R.id.cod_indicator_imageview);
        totalRatings = findViewById(R.id.total_ratings);
        ratingsNoContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingsFigure = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer = findViewById(R.id.ratings_progressbar_container);
        averageRating = findViewById(R.id.average_rating);

        descriptionTitle = findViewById(R.id.description_title);
        descriptionBody1 = findViewById(R.id.description_body_1);
        descriptionBody2 = findViewById(R.id.description_body_2);
        descriptionBody3 = findViewById(R.id.description_body_3);
        descriptionBody4 = findViewById(R.id.description_body_4);
        descriptionBody5 = findViewById(R.id.description_body_5);
        productDetailsTabsContainer = findViewById(R.id.product_details_tabs_container);
        initialRating = -1;

        //loading dialog
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        productID = getIntent().getStringExtra("PRODUCT_ID");
        List<String> productImages = new ArrayList<>();
        firebaseFirestore.collection("Products").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();

                            for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                productImages.add(documentSnapshot.get("product_image_" + x).toString());
                            }
                            ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                            productImagesViewPager.setAdapter(productImagesAdapter);

                            productTitle.setText(documentSnapshot.get("product_title").toString());
                            averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                            totalRatingMiniView.setText("(" + (long) documentSnapshot.get("total_ratings") + ")ratings");
                            productPrice.setText("Rs." + documentSnapshot.get("product_price") + "/-".toString());
                            cuttedPrice.setText("Rs." + documentSnapshot.get("cutted_price") + "/-".toString());

                            if ((boolean) documentSnapshot.get("COD")) {
                                codIndicator.setVisibility(View.VISIBLE);
                                tvCodIndicator.setVisibility(View.VISIBLE);
                            } else {
                                codIndicator.setVisibility(View.INVISIBLE);
                                tvCodIndicator.setVisibility(View.INVISIBLE);
                            }

                            descriptionTitle.setText(documentSnapshot.get("description_title").toString());
                            descriptionBody1.setText(documentSnapshot.get("description_body_1").toString());
                            descriptionBody2.setText(documentSnapshot.get("description_body_2").toString());
                            descriptionBody3.setText(documentSnapshot.get("description_body_3").toString());
                            descriptionBody4.setText(documentSnapshot.get("description_body_4").toString());
                            descriptionBody5.setText(documentSnapshot.get("description_body_5").toString());

                            if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                productDetailsTabsContainer.setVisibility(View.VISIBLE);
                                for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {
                                    productOtherDetailsModelList.add(new ProductOtherDetailsModel(0, documentSnapshot.get("spec_title_" + x).toString()));
                                    for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_fields") + 1; y++) {
                                        productOtherDetailsModelList.add(new ProductOtherDetailsModel(1, documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(), documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));
                                    }
                                }
                            } else {
                                productDetailsTabsContainer.setVisibility(View.GONE);
                            }

                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");

                            for (int x = 0; x < 5; x++) {
                                TextView rating = (TextView) ratingsNoContainer.getChildAt(x);
                                rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));

                                ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                int maxprogress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                progressBar.setMax(maxprogress);
                                progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                            }

                            totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                            averageRating.setText(documentSnapshot.get("average_rating").toString());
                            productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount()));

                            if (currentUser != null) {
                                if (DBQueries.myRating.size() == 0) {
                                    DBQueries.loadRatingList(ProductDetailsActivity.this);
                                }

                                if (DBQueries.cartList.size() == 0) {
                                    DBQueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, new TextView(ProductDetailsActivity.this));
                                }

                                if (DBQueries.wishList.size() == 0) {
                                    DBQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                                } else {
                                    loadingDialog.dismiss();
                                }

                            } else {
                                loadingDialog.dismiss();
                            }

                            if (DBQueries.myRatedIds.contains(productID)) {
                                int index = DBQueries.myRatedIds.indexOf(productID);
                                initialRating = Integer.parseInt(String.valueOf(DBQueries.myRating.get(index))) - 1;
                                setRating(initialRating);
                            }

                            if (DBQueries.cartList.contains(productID)) {
                                ALREADY_ADDED_TO_CART = true;

                            } else {
                                ALREADY_ADDED_TO_CART = false;
                            }

                            if (DBQueries.wishList.contains(productID)) {
                                ALREADY_ADDED_TO_WISHLIST = true;
                                addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.emergency));
                            } else {
                                addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                ALREADY_ADDED_TO_WISHLIST = false;
                            }

                            if ((boolean) documentSnapshot.get("in_stock")) {
                                addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (currentUser == null) {
                                            signInDialog.show();
                                        } else {
                                            if (!running_cart_query) {
                                                running_cart_query = true;
                                                if (ALREADY_ADDED_TO_CART) {
                                                    running_cart_query = false;
                                                    Toast.makeText(ProductDetailsActivity.this, "Already added to cart !", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Map<String, Object> addProduct = new HashMap<>();
                                                    addProduct.put("product_ID_" + String.valueOf(DBQueries.cartList.size()), productID);
                                                    addProduct.put("list_size", (long) (DBQueries.cartList.size() + 1));

                                                    firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART")
                                                            .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {

                                                                        if (DBQueries.cartItemModelList.size() != 0) {
                                                                            DBQueries.cartItemModelList.add(0, new CartItemModel(CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                                                                                    , documentSnapshot.get("product_title").toString()
                                                                                    , documentSnapshot.get("product_price").toString()
                                                                                    , documentSnapshot.get("cutted_price").toString()
                                                                                    , (long) 1
                                                                                    , (boolean) documentSnapshot.get("in_stock")));
                                                                        }

                                                                        ALREADY_ADDED_TO_CART = true;
                                                                        DBQueries.cartList.add(productID);
                                                                        Toast.makeText(ProductDetailsActivity.this, "Product added to your Cart !", Toast.LENGTH_SHORT).show();
                                                                        invalidateOptionsMenu();
                                                                        running_cart_query = false;

                                                                    } else {
                                                                        running_cart_query = false;
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    }
                                });
                            } else {
                                buyNowBtn.setVisibility(View.GONE);
                                TextView outOfStock = (TextView) addToCartBtn.getChildAt(0);
                                outOfStock.setText("Out of Stock");
                                outOfStock.setTextColor(getResources().getColor(R.color.red));
                                outOfStock.setCompoundDrawables(null, null, null, null);
                            }

                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        viewpagerIndicator.setupWithViewPager(productImagesViewPager, true);

        addToWishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            int index = DBQueries.wishList.indexOf(productID);
                            DBQueries.removeFromWishList(index, ProductDetailsActivity.this);
                            addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                        } else {
                            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.emergency));
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DBQueries.wishList.size()), productID);
                            addProduct.put("list_size", (long) (DBQueries.wishList.size() + 1));

                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (DBQueries.wishListModelList.size() != 0) {
                                                    DBQueries.wishListModelList.add(new WishListModel(productID, documentSnapshot.get("product_image_1").toString()
                                                            , documentSnapshot.get("product_title").toString()
                                                            , documentSnapshot.get("average_rating").toString()
                                                            , (long) documentSnapshot.get("total_ratings")
                                                            , documentSnapshot.get("product_price").toString()
                                                            , documentSnapshot.get("cutted_price").toString()
                                                            , (boolean) documentSnapshot.get("COD")
                                                            , (boolean) documentSnapshot.get("in_stock")));
                                                }

                                                ALREADY_ADDED_TO_WISHLIST = true;
                                                addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.emergency));
                                                DBQueries.wishList.add(productID);
                                                Toast.makeText(ProductDetailsActivity.this, "Product added to Wishlist !", Toast.LENGTH_SHORT).show();
                                            } else {
                                                addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                                String error = task.getException().getMessage();
                                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                            running_wishlist_query = false;
                                        }
                                    });
                        }
                    }
                }
            }
        });

        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Rating Layout
        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int startPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (startPosition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;

                                setRating(startPosition);
                                Map<String, Object> updateRating = new HashMap<>();

                                if (DBQueries.myRatedIds.contains(productID)) {

                                    TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingsNoContainer.getChildAt(5 - startPosition - 1);

                                    updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put(startPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) startPosition - initialRating, true));

                                } else {
                                    updateRating.put(startPosition + 1 + "_star", (long) documentSnapshot.get(startPosition + 1 + "_star") + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) startPosition + 1, false));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);

                                }
                                firebaseFirestore.collection("Products").document(productID)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Map<String, Object> myrating = new HashMap<>();

                                                    if (DBQueries.myRatedIds.contains(productID)) {
                                                        myrating.put("rating_" + DBQueries.myRatedIds.indexOf(productID), (long) startPosition + 1);
                                                    } else {
                                                        myrating.put("list_size", (long) DBQueries.myRatedIds.size() + 1);
                                                        myrating.put("product_ID_" + DBQueries.myRatedIds.size(), productID);
                                                        myrating.put("rating_" + DBQueries.myRatedIds.size(), (long) startPosition + 1);
                                                    }

                                                    firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                            .update(myrating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {

                                                                        if (DBQueries.myRatedIds.contains(productID)) {
                                                                            DBQueries.myRating.set(DBQueries.myRatedIds.indexOf(productID), (long) startPosition + 1);

                                                                            TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
                                                                            TextView finalRating = (TextView) ratingsNoContainer.getChildAt(5 - startPosition - 1);
                                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));
                                                                        } else {
                                                                            DBQueries.myRatedIds.add(productID);
                                                                            DBQueries.myRating.add((long) startPosition + 1);

                                                                            TextView rating = (TextView) ratingsNoContainer.getChildAt(5 - startPosition - 1);
                                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                                            totalRatingMiniView.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")ratings");
                                                                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                                            totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));
                                                                            Toast.makeText(ProductDetailsActivity.this, "Thank you for rating !", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        for (int x = 0; x < 5; x++) {
                                                                            TextView ratingfigures = (TextView) ratingsNoContainer.getChildAt(x);

                                                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                                            int maxprogress = Integer.parseInt(totalRatingsFigure.getText().toString());
                                                                            progressBar.setMax(maxprogress);
                                                                            progressBar.setProgress(Integer.parseInt(ratingfigures.getText().toString()));
                                                                        }

                                                                        initialRating = startPosition;
                                                                        averageRating.setText(calculateAverageRating(0, true));
                                                                        averageRatingMiniView.setText(calculateAverageRating(0, true));

                                                                        if (DBQueries.wishList.contains(productID) && DBQueries.wishListModelList.size() != 0) {
                                                                            int index = DBQueries.wishList.indexOf(productID);
                                                                            DBQueries.wishListModelList.get(index).setRating(averageRating.getText().toString());
                                                                            DBQueries.wishListModelList.get(index).setTotalRatings(Long.parseLong(totalRatingsFigure.getText().toString()));
                                                                        }

                                                                    } else {
                                                                        setRating(initialRating);
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    running_rating_query = false;
                                                                }
                                                            });
                                                } else {
                                                    running_rating_query = false;
                                                    setRating(initialRating);
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
            });
        }
        // Rating Layout

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingDialog.show();
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                            , documentSnapshot.get("product_title").toString()
                            , documentSnapshot.get("product_price").toString()
                            , documentSnapshot.get("cutted_price").toString()
                            , (long) 1
                            , (boolean) documentSnapshot.get("in_stock")));
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                    if (DBQueries.addressesModelList.size() == 0) {
                        DBQueries.loadAddresses(ProductDetailsActivity.this, loadingDialog);
                    } else {
                        loadingDialog.dismiss();
                        Intent intent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        //SignIn Dialog
        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInDialog.dismiss();
                Intent intent = new Intent(ProductDetailsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInDialog.dismiss();
                Intent intent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //SignIn Dialog
    }

    @Override
    protected void onStart() {
        super.onStart();

        MobileAds.initialize(ProductDetailsActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            if (DBQueries.myRating.size() == 0) {
                DBQueries.loadRatingList(ProductDetailsActivity.this);
            }

            if (DBQueries.wishList.size() == 0) {
                DBQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
            } else {
                loadingDialog.dismiss();
            }

        } else {
            loadingDialog.dismiss();
        }

        if (DBQueries.myRatedIds.contains(productID)) {
            int index = DBQueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBQueries.myRating.get(index))) - 1;
            setRating(initialRating);
        }

        if (DBQueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;

        } else {
            ALREADY_ADDED_TO_CART = false;
        }

        if (DBQueries.wishList.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.emergency));
        } else {
            addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
            ALREADY_ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();
    }

    public static void setRating(int startPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView startBtn = (ImageView) rateNowContainer.getChildAt(x);
            startBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));

            if (x <= startPosition) {
                startBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    private String calculateAverageRating(long cureentUserRating, boolean update) {
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingsNoContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStars = totalStars + cureentUserRating;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(totalRatingsFigure.getText().toString())).substring(0, 3);
        } else {
            return String.valueOf(totalStars / (Long.parseLong(totalRatingsFigure.getText().toString()) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.main_search_icon) {
            if (fromSearch) {
                finish();
            } else {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            }
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }
}