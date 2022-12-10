package com.example.rbenterprise.Splash.Main.Fragments.Model;

import java.util.ArrayList;

public class WishListModel {
    public WishListModel(int cartItem, String productId, String product_image_1, String title, String product_price, String cutted_price, long l) {

    }

    private String productId;
    private String productImage;
    private String productTitle;
    private String rating;
    private long totalRatings;
    private String productPrice;
    private String cuttedPrice;
    private Boolean COD;
    private boolean inStock;
    private ArrayList<String> tags;

    public WishListModel(String productId,String productImage, String productTitle, String rating, long totalRatings, String productPrice, String cuttedPrice, boolean COD,boolean inStock) {
        this.productId = productId;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.rating = rating;
        this.totalRatings = totalRatings;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
        this.COD = COD;
        this.inStock = inStock;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public Boolean getCOD() {
        return COD;
    }

    public void setCOD(Boolean COD) {
        this.COD = COD;
    }
}