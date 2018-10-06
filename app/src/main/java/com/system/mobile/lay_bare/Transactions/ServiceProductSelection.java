package com.system.mobile.lay_bare.Transactions;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.system.mobile.lay_bare.Transactions.Adapter.ServiceProductAdapter;
import com.system.mobile.lay_bare.R;

/**
 * Created by Mark on 17/10/2017.
 */

public class ServiceProductSelection extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager_service;

    ServiceProductAdapter serviceProductAdapter;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_products);

        tabLayout           = (TabLayout)findViewById(R.id.tab_layout);
        viewPager_service   = (ViewPager)findViewById(R.id.viewPager_service);

        tabLayout.addTab(tabLayout.newTab().setText("SERVICES"));
        tabLayout.addTab(tabLayout.newTab().setText("PACKAGES"));
        tabLayout.addTab(tabLayout.newTab().setText("PRODUCTS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        serviceProductAdapter = new ServiceProductAdapter(getSupportFragmentManager(),getApplicationContext(),tabLayout.getTabCount());
        viewPager_service.setAdapter(serviceProductAdapter);
        viewPager_service.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        int limit = (serviceProductAdapter.getCount() > 1 ? serviceProductAdapter.getCount() - 1 : 1);
        viewPager_service.setOffscreenPageLimit(limit);
        viewPager_service.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager_service.setNestedScrollingEnabled(false);
        }
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
    }
}
