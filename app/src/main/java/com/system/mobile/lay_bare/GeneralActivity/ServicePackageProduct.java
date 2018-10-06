package com.system.mobile.lay_bare.GeneralActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.ServiceProductAdapter;
import com.system.mobile.lay_bare.Utilities.Utilities;

import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;


/**
 * Created by Mark on 27/09/2017.
 */

public class ServicePackageProduct extends AppCompatActivity {


    Utilities utilities;
    TabLayout tabLayout;
    ViewPager viewPager_service;
    ServiceProductAdapter serviceProductAdapter;
    JSONArray arrayCart;
    Animation shake;
    String start_time = "";
    ImageView imgPreview;
    private ImageButton imgBtnBack;

    TextView forTitle;
    private Typeface myTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_package_product);
        utilities = new Utilities(this);
        arrayCart = new JSONArray();

        shake = AnimationUtils.loadAnimation(this, R.anim.animation_shake);
        getPreviousData();

        viewPager_service = (ViewPager)findViewById(R.id.viewPager_service);
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Services"));
        tabLayout.addTab(tabLayout.newTab().setText("Packages"));
        tabLayout.addTab(tabLayout.newTab().setText("Products"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        serviceProductAdapter = new ServiceProductAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager_service.setAdapter(serviceProductAdapter);
        viewPager_service.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        int limit = (serviceProductAdapter.getCount() > 1 ? serviceProductAdapter.getCount() - 1 : 1);
        viewPager_service.setOffscreenPageLimit(limit);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_service.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setToolbars();
    }

    //get extra from previous form
    private void getPreviousData() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            start_time   = bundle.getString("start_time");
        }
    }



    @Override
    public void onBackPressed() {
        finish();
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setToolbars() {

        myTypeface          = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle                = (TextView)findViewById(R.id.forTitle);
        imgBtnBack              = (ImageButton) findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forTitle.setTypeface(myTypeface);
        forTitle.setText("Services / Products");
    }


}
