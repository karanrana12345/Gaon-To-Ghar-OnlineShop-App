package com.example.rbenterprise.Splash.Main.Fragments.Model;

import java.util.List;

public class HomePageModel
{

    public static final int BANNER_SLIDER = 0;
    public static final int STRIP_AD_BANNER = 1;
    public static final int HORIZONTAL_PRODUCT_VIEW = 2;
    public static final int GRID_PRODUCT_VIEW = 3;
    public static final int ADMOB_AD = 4;
    public static final int ABOUT_US_SECTION = 5;

    private int type;

    //Banner Slider
    private List<SliderModel> sliderModelList;

    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }

    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }
    //Banner Slider

    //Strip Banner
    private int resource;

    public HomePageModel(int type, int resource) {
        this.type = type;
        this.resource = resource;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    //Strip Banner

    //Horizontal Product Layout && Grid Product Layout
    private String title;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    private List<WishListModel> viewAllProductList;

    public HomePageModel(int type, String title, List<HorizontalProductScrollModel> horizontalProductScrollModelList,List<WishListModel>viewAllProductList) {
        this.type = type;
        this.title = title;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public List<WishListModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishListModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    public HomePageModel(int type, String title, List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.type = type;
        this.title = title;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    //Horizontal Product Layout && Grid Product Layout

    //Facebook Ad1

    public HomePageModel(int type) {
        this.type = type;
    }

    //Facebook Ad1

    //Worker Section

    private String workertitle;
    private List<WorkersSectionModel> workersSectionModelList;

    public HomePageModel(List<WorkersSectionModel> workersSectionModelList,int type) {
        this.type = type;
        this.workersSectionModelList = workersSectionModelList;
    }

    public String getWorkertitle() {
        return workertitle;
    }

    public void setWorkertitle(String workertitle) {
        this.workertitle = workertitle;
    }

    public List<WorkersSectionModel> getWorkersSectionModelList() {
        return workersSectionModelList;
    }

    public void setWorkersSectionModelList(List<WorkersSectionModel> workersSectionModelList) {
        this.workersSectionModelList = workersSectionModelList;
    }

    //Worker Section


}
