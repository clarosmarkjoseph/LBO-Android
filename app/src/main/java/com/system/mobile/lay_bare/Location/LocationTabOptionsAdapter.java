package com.system.mobile.lay_bare.Location;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.system.mobile.lay_bare.Transactions.FragmentAppointment;
import com.system.mobile.lay_bare.Transactions.FragmentHistory;
import com.system.mobile.lay_bare.Utilities.Utilities;

/**
 * Created by Mark on 01/12/2017.
 */


public class LocationTabOptionsAdapter extends FragmentStatePagerAdapter {

    int fragmentCount = 0;
    Context context;
    Utilities utilities;

    public LocationTabOptionsAdapter(FragmentManager fm, Context ctx, int fragmentCount1) {
        super(fm);
        this.fragmentCount  = fragmentCount1;
        this.context        = ctx;
        this.utilities      = new Utilities(ctx);

    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0){
            fragment = new OverviewFragment();
        }
        else if (position == 1){
            fragment = new LocationQueuingFragment();
        }
        else if(position == 2){
            fragment = new LocationReview();
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
