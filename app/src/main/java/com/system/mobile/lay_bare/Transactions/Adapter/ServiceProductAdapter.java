package com.system.mobile.lay_bare.Transactions.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.system.mobile.lay_bare.GeneralActivity.FragmentPackage;
import com.system.mobile.lay_bare.GeneralActivity.FragmentProduct;
import com.system.mobile.lay_bare.GeneralActivity.FragmentService;

/**
 * Created by Mark on 17/10/2017.
 */

public class ServiceProductAdapter extends FragmentStatePagerAdapter {
    Context context;
    int fragmentCount = 0;

    public ServiceProductAdapter(FragmentManager fm, Context ctx, int fragmentCount1) {
        super(fm);
        this.fragmentCount = fragmentCount1;
        this.context = ctx;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;
        if (position == 0){
            fragment =  new FragmentService();
        }
        if (position == 1){
            fragment =  new FragmentPackage();
        }
        else{
            fragment =  new FragmentProduct();
        }

        Bundle bundle= new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public int getCount() {
        return fragmentCount;
    }
}
