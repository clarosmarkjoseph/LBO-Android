package com.system.mobile.lay_bare;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.system.mobile.lay_bare.GeneralActivity.FragmentPackage;
import com.system.mobile.lay_bare.GeneralActivity.FragmentProduct;
import com.system.mobile.lay_bare.GeneralActivity.FragmentService;

/**
 * Created by ITDevJr1 on 4/24/2017.
 */

public class ServiceProductAdapter extends FragmentStatePagerAdapter {
    int count_tabs;
    DataHandler handler;
    Context context;

    public ServiceProductAdapter(FragmentManager fm, int tabs) {
        super(fm);
        Log.e("TABS COOOOO", String.valueOf(tabs));
        this.count_tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            FragmentService serviceTab = new FragmentService();
            return serviceTab;
        }
        else if(position==1){
            FragmentPackage serviceTab1 = new FragmentPackage();
            return  serviceTab1;
        }
        else{
            FragmentProduct productTab = new FragmentProduct();
            return  productTab;
        }
    }


    @Override
    public int getCount() {
        return count_tabs;
    }


}
