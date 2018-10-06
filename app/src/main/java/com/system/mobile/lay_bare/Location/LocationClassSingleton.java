package com.system.mobile.lay_bare.Location;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Mark on 04/12/2017.
 */

public class LocationClassSingleton {

    JSONObject objectBranchDetails;
    JSONArray arrayReviews;
    int offset = 0;
    private static LocationClassSingleton instance;
    ViewPager viewPager;
    double totalRatings = 0;
    int totalReview     = 0;
    JSONArray arrayListRatings;


    public LocationClassSingleton Instance(){
        if (instance == null) {
            instance = new LocationClassSingleton();
        }
        return instance;
    }

    public void setBranchDetails(JSONObject object){
        this.objectBranchDetails = object;
    }

    public JSONObject getBranchDetails(){
        return this.objectBranchDetails;
    }

    //get / set total review
    public void setTotalReview(int totalReview){
        this.totalReview = totalReview;
    }

    public int getTotalReview(){
        return this.totalReview;
    }

    //get / set total ratings
    public void setTotalRatings(double totalRatings){
        this.totalRatings = totalRatings;
    }
    public double getTotalRatings(){
        return this.totalRatings;
    }


    //set / get offset
    public void setOffset(int myOffset){
        this.offset = myOffset;
    }

    public int getOffset(){
        return this.offset;
    }

    //set / get Array reviews
    public void setArrayReviews(JSONArray arrayReviews){
        this.arrayReviews = arrayReviews;
    }

    public JSONArray getArrayReviews(){
        return this.arrayReviews;
    }


    //set Nested scrollview
    public void setViewPager(ViewPager viewPager){
        this.viewPager = viewPager;
    }

    public ViewPager getViewPager(){
        return this.viewPager;
    }


    public void setArrayListRatings(JSONArray arrayListRatings) {
        this.arrayListRatings = arrayListRatings;
    }
    public JSONArray getArrayListRatings() {
        return this.arrayListRatings;
    }

    public void clearSingleton(){
        objectBranchDetails = new JSONObject();
        arrayReviews = new JSONArray();
        offset = 0;
        totalRatings = 0;
        totalReview  = 0;
        arrayListRatings = new JSONArray();
    }

}
