package com.example.rbenterprise.Splash.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rbenterprise.Splash.Main.Fragments.Adapter.HomePageAdapter;
import com.example.rbenterprise.Splash.Main.Fragments.Model.AddressesModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.CartItemModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.HomePageModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.HorizontalProductScrollModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.SliderModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.WishListModel;
import com.example.rbenterprise.Splash.Main.Fragments.Model.WorkersSectionModel;
import com.example.rbenterprise.Splash.Main.Fragments.MyCartFragment;
import com.example.rbenterprise.Splash.Main.Fragments.MyWishListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rb.enterprise.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBQueries {
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static String email,fullname,profile;

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();

    public static List<String> wishList = new ArrayList<>();
    public static List<WishListModel> wishListModelList = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<CartItemModel>();

    public static int selectedAddress = -1;
    public static List<AddressesModel> addressesModelList = new ArrayList<>();

    public static void loadFragmentData(SwipeRefreshLayout swipeRefreshLayout, RecyclerView homePageRecyclerView, Context context, final int index) {
        firebaseFirestore.collection("HomeScreen")
                .document("HOME")
                .collection("HOME_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                if ((long) documentSnapshot.get("view_type") == 0)
                                {
                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    long no_of_banners = (long) documentSnapshot.get("no_of_banners");

                                    for (long x = 1; x < no_of_banners + 1; x++) {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString()));
                                    }

                                    lists.get(index).add(new HomePageModel(0, sliderModelList));
                                }
                                else if ((long) documentSnapshot.get("view_type") == 4) {
                                    lists.get(index).add(new HomePageModel(4));

                                }
                                else if ((long) documentSnapshot.get("view_type") == 2)
                                {
                                    List<WishListModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();

                                    long no_of_products = (long) documentSnapshot.get("no_of_products");

                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));

                                        viewAllProductList.add(new WishListModel(documentSnapshot.get("product_ID_" + x).toString(), documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("average_rating_" + x).toString()
                                                , (long) documentSnapshot.get("total_ratings_" + x)
                                                , documentSnapshot.get("product_price_" + x).toString()
                                                , documentSnapshot.get("cutted_price_" + x).toString()
                                                , (boolean) documentSnapshot.get("COD_" + x)
                                                ,(boolean) documentSnapshot.get("in_stock_"+ x)));
                                    }

                                    lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), horizontalProductScrollModelList, viewAllProductList));
                                }
                                else if ((long) documentSnapshot.get("view_type") == 3)
                                {
                                    List<HorizontalProductScrollModel> gridProductScrollModelList = new ArrayList<>();
                                    long no_of_products = (long) documentSnapshot.get("no_of_products");

                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        gridProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));
                                    }

                                    lists.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_title").toString(), gridProductScrollModelList));

                                } else if ((long) documentSnapshot.get("view_type") == 5) {
                                    lists.get(index).add(new HomePageModel(5));
                                }
                            }
                            HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                            homePageRecyclerView.setAdapter(homePageAdapter);
                            homePageAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadWishList(Context context, Dialog dialog, final boolean loadProductData) {
        wishList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                wishList.add(task.getResult().get("product_ID_" + x).toString());

                                if (DBQueries.wishList.contains(ProductDetailsActivity.productID)) {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;
                                    if (ProductDetailsActivity.addToWishListBtn != null) {
                                        ProductDetailsActivity.addToWishListBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.emergency));
                                    }
                                } else {
                                    if (ProductDetailsActivity.addToWishListBtn != null) {
                                        ProductDetailsActivity.addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                    }
                                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                                }

                                if (loadProductData) {
                                    wishListModelList.clear();
                                    String productId = task.getResult().get("product_ID_" + x).toString();
                                    firebaseFirestore.collection("Products").document(productId)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        wishListModelList.add(new WishListModel(productId, task.getResult().get("product_image_1").toString()
                                                                , task.getResult().get("product_title").toString()
                                                                , task.getResult().get("average_rating").toString()
                                                                , (long) task.getResult().get("total_ratings")
                                                                , "Rs." + task.getResult().get("product_price") + "/-".toString()
                                                                , "Rs." + task.getResult().get("cutted_price") + "/-".toString()
                                                                , (boolean) task.getResult().get("COD")
                                                                ,(boolean) task.getResult().get("in_stock")));
                                                        MyWishListFragment.wishListAdapter.notifyDataSetChanged();
                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    public static void removeFromWishList(int index, Context context) {
        String removedProductId = wishList.get(index);
        wishList.remove(index);
        Map<String, Object> updateWishList = new HashMap<>();

        for (int x = 0; x < wishList.size(); x++) {
            updateWishList.put("product_ID_" + x, wishList.get(x));
        }
        updateWishList.put("list_size", (long) wishList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .set(updateWishList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (wishListModelList.size() != 0) {
                                wishListModelList.remove(index);
                                MyWishListFragment.wishListAdapter.notifyDataSetChanged();
                            }
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                            Toast.makeText(context, "Removed Successfully !", Toast.LENGTH_SHORT).show();
                        } else {
                            if (ProductDetailsActivity.addToWishListBtn != null) {
                                ProductDetailsActivity.addToWishListBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.emergency));
                            }
                            wishList.add(index, removedProductId);
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.running_wishlist_query = false;
                    }
                });
    }

    public static void loadRatingList(Context context) {
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            myRatedIds.clear();
            myRating.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                    myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                                    myRating.add((long) task.getResult().get("rating_" + x));

                                    if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productID)) {
                                        ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                        if (ProductDetailsActivity.rateNowContainer != null) {
                                            ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                        }
                                    }

                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                            ProductDetailsActivity.running_rating_query = false;
                        }
                    });
        }
    }

    public static void loadCartList(Context context, Dialog dialog, boolean loadProductData,TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                cartList.add(task.getResult().get("product_ID_" + x).toString());

                                if (DBQueries.cartList.contains(ProductDetailsActivity.productID)) {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                                } else {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                                }

                                if (loadProductData) {
                                    cartItemModelList.clear();
                                    String productId = task.getResult().get("product_ID_" + x).toString();
                                    firebaseFirestore.collection("Products").document(productId)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        int index = 0;
                                                        if (cartList.size() >= 2) {
                                                            index = cartList.size() - 2;
                                                        }

                                                        cartItemModelList.add(index, new CartItemModel(CartItemModel.CART_ITEM, productId, task.getResult().get("product_image_1").toString()
                                                                , task.getResult().get("product_title").toString()
                                                                , task.getResult().get("product_price").toString()
                                                                , task.getResult().get("cutted_price").toString()
                                                                , (long) 1
                                                                , (boolean) task.getResult().get("in_stock")));

                                                        if (cartList.size() == 1) {
                                                            cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                            parent.setVisibility(View.VISIBLE);
                                                        }
                                                        if (cartList.size() == 0) {
                                                            cartItemModelList.clear();
                                                        }

                                                        MyCartFragment.cartAdapter.notifyDataSetChanged();

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    public static void removeFromCart(int index, Context context,TextView cartTotalAmount ) {
        String removedProductId = cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateCartList = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++) {
            updateCartList.put("product_ID_" + x, cartList.get(x));
        }
        updateCartList.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (cartItemModelList.size() != 0) {
                                cartItemModelList.remove(index);
                                MyCartFragment.cartAdapter.notifyDataSetChanged();
                            }
                            if (cartList.size() == 0) {
                                LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                parent.setVisibility(View.GONE);
                                cartItemModelList.clear();
                            }
                            Toast.makeText(context, "Removed from cart successfully !", Toast.LENGTH_SHORT).show();
                        } else {
                            cartList.add(index, removedProductId);
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.running_cart_query = false;
                    }
                });
    }

    public static void loadAddresses(Context context, Dialog loadingDialog) {
        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Intent deliveryIntent;

                            if ((long) task.getResult().get("list_size") == 0) {
                                deliveryIntent = new Intent(context, AddAddressActivity.class);
                                deliveryIntent.putExtra("INTENT", "deliveryIntent");
                            } else {
                                for (long x = 1; x < (long) task.getResult().get("list_size") + 1; x++) {

                                    addressesModelList.add(new AddressesModel(task.getResult().get("fullname_" + x).toString()
                                            , task.getResult().get("address_" + x).toString()
                                            , task.getResult().get("pincode_" + x).toString()
                                            , (boolean) task.getResult().get("selected_" + x)
                                            ,task.getResult().get("mobile_no_" + x).toString()));

                                    if ((boolean) task.getResult().get("selected_" + x))
                                    {
                                        selectedAddress = Integer.parseInt(String.valueOf(x - 1));
                                    }
                                }
                                deliveryIntent = new Intent(context, DeliveryActivity.class);
                            }
                            context.startActivity(deliveryIntent);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    public static void clearData() {
        lists.clear();
        loadedCategoriesNames.clear();
        wishList.clear();
        wishListModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
    }
}
