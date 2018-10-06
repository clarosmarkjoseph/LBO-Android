package com.system.mobile.lay_bare.Location;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.GeneralActivity.FragmentPackageAdapter;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.NavigationDrawer.ViewPagerAdapter;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 01/12/2017.
 */

public class LocationDetails extends AppCompatActivity {

    private Typeface myTypeface;
    JSONObject objectBranch;
    ViewPager viewpager_branch_image,viewpager_branch_fragments;
    TabLayout tabBranchOptions;
    Toolbar toolbar;
    TextView lblBranchName;
    ViewPagerAdapter viewPagerAdapter;
    LocationTabOptionsAdapter locationTabOptionsAdapter;
    private JSONArray arrayCarousel = new JSONArray();
    LinearLayout pagerIndicator;
    int dotsCount;
    ImageView[] imgDots;
    CollapsingToolbarLayout collapse_toolbar;
    LocationClassSingleton locationClassSingleton;
    RatingBar ratingBarReview;
    String SERVER_URL;
    Utilities utilities;
    private ArrayList<String> arrayErrorResponse;
    int offset = 0;
    LinearLayout linearRating;
    CoordinatorLayout coordinatorLayout;
    TextView lblCountReview,lblAverageReview;
    JSONArray arrayReview = new JSONArray();
    int branch_id = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_details);
        setToolbar();
        getExtra();

    }

    private void getExtra() {
        utilities               = new Utilities(this);
        SERVER_URL              = utilities.returnIpAddress();
        locationClassSingleton  = new LocationClassSingleton().Instance();

        Bundle bundle           = getIntent().getExtras();
        if(bundle!=null){
            try {
                objectBranch = new JSONObject(bundle.getString("object_branch"));
                branch_id    = objectBranch.getInt("id");
                locationClassSingleton.Instance().setBranchDetails(objectBranch);
                setElements();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void setElements() {

        tabBranchOptions            = (TabLayout)findViewById(R.id.tabBranchOptions);
        viewpager_branch_image      = (ViewPager) findViewById(R.id.viewpager_branch_image);
        viewpager_branch_fragments  = (ViewPager) findViewById(R.id.viewpager_branch_fragments);
        lblBranchName               = (TextView)findViewById(R.id.lblBranchName);
        pagerIndicator              = (LinearLayout)findViewById(R.id.viewPagerCountDots);
        ratingBarReview             = (RatingBar) findViewById(R.id.ratingBarReview);
        linearRating                = (LinearLayout)findViewById(R.id.linearRating);
        collapse_toolbar            = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        lblCountReview              = (TextView)findViewById(R.id.lblCountReview);
        lblAverageReview            = (TextView)findViewById(R.id.lblAverageReview);
        coordinatorLayout           = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        collapse_toolbar.setContentScrimColor(ContextCompat.getColor(this,R.color.laybareGreen));
        collapse_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.themeWhite));
        collapse_toolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapse_toolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        collapse_toolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        tabBranchOptions.addTab(tabBranchOptions.newTab().setText("Overview"));
        tabBranchOptions.addTab(tabBranchOptions.newTab().setText("Queuing"));
        tabBranchOptions.addTab(tabBranchOptions.newTab().setText("Reviews"));
        locationTabOptionsAdapter = new LocationTabOptionsAdapter(getSupportFragmentManager(),getApplicationContext(),tabBranchOptions.getTabCount());
        viewpager_branch_fragments.setAdapter(locationTabOptionsAdapter);
        int limit = (locationTabOptionsAdapter.getCount() > 1 ? locationTabOptionsAdapter.getCount() - 1 : 1);

        viewpager_branch_fragments.setOffscreenPageLimit(limit);
        viewpager_branch_fragments.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabBranchOptions));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewpager_branch_fragments.setNestedScrollingEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewpager_branch_fragments.setNestedScrollingEnabled(false);
        }

        tabBranchOptions.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager_branch_fragments.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        locationClassSingleton.Instance().setViewPager(viewpager_branch_fragments);
        iterateJSONObject();
    }

    private void loadReview() {

        offset                  = locationClassSingleton.Instance().getOffset();
        arrayReview             = locationClassSingleton.Instance().getArrayReviews();
        int totalReviews        = locationClassSingleton.Instance().getTotalReview();
        double totalRatings     = locationClassSingleton.Instance().getTotalRatings();
        lblAverageReview.setText(String.valueOf(utilities.roundOffDecimal(totalRatings)));
        ratingBarReview.setRating((float) totalRatings);

        if(totalReviews > 1){
            lblCountReview.setText(String.valueOf(totalReviews)+" reviews");
        }
        else{
            lblCountReview.setText(String.valueOf(totalReviews)+" review");
        }

    }



    private void iterateJSONObject() {

        try {
            String branch_name           = objectBranch.getString("branch_name");
            String picture               = objectBranch.getString("branch_pictures");
            JSONArray arrayPictures      = new JSONArray(picture);
            collapse_toolbar.setTitle(branch_name);
            collapse_toolbar.setCollapsedTitleTypeface(myTypeface);
            collapse_toolbar.setExpandedTitleTypeface(myTypeface);
            if(arrayCarousel.length() > 0){
                for (int x = 0; x < arrayPictures.length(); x++){
                    String image_name = arrayPictures.getString(x);
                    JSONObject objPass    = new JSONObject();
                    objPass.put("image",image_name);
                    arrayCarousel.put(objPass);
                }
            }
            else{
                arrayCarousel = new JSONArray();
                JSONObject objPass    = new JSONObject();
                objPass.put("image","no photo.jpg");
                arrayCarousel.put(objPass);
            }
            lblBranchName.setText(branch_name);
            loadReview();
            displayBranchImage();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        toolbar     = (Toolbar) findViewById(R.id.myToolbar);
        myTypeface  = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void displayBranchImage() {

        viewPagerAdapter =  new ViewPagerAdapter(getApplicationContext(),arrayCarousel,"branches");
        viewpager_branch_image.setAdapter(viewPagerAdapter);
        viewpager_branch_image.setCurrentItem(0);
        dotsCount   = viewPagerAdapter.getCount();
        imgDots     = new ImageView[dotsCount];
        if(dotsCount>0){
            for (int x = 0; x<dotsCount;x++){
                imgDots[x] = new ImageView(getApplicationContext());
                imgDots[x].setImageDrawable(getResources().getDrawable(R.drawable.nonselectable));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imgDots[x].setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeWhite)));
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(4,0,4,0);
                pagerIndicator.addView(imgDots[x],params);
            }
        }
        imgDots[0].setImageDrawable(getResources().getDrawable(R.drawable.selectable));
        viewpager_branch_image.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                position = viewpager_branch_image.getCurrentItem();
                for (int i = 0; i < dotsCount; i++) {
                    imgDots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselectable));
                }
                imgDots[position].setImageDrawable(getResources().getDrawable(R.drawable.selectable));
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        } );

    }

}
