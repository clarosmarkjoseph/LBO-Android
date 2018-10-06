package com.system.mobile.lay_bare.PLC;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.system.mobile.lay_bare.Location.LocationQueuingFragment;
import com.system.mobile.lay_bare.Location.OverviewFragment;
import com.system.mobile.lay_bare.Transactions.FragmentAppointment;
import com.system.mobile.lay_bare.Utilities.Utilities;

/**
 * Created by paolohilario on 9/18/18.
 */

public class PremierApplicationAdapter extends FragmentStatePagerAdapter {

    int fragmentCount = 0;
    Context context;
    Utilities utilities;

    public PremierApplicationAdapter(FragmentManager fm, Context ctx, int fragmentCount1) {
        super(fm);
        this.fragmentCount  = fragmentCount1;
        this.context        = ctx;
        this.utilities      = new Utilities(ctx);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0){
            fragment = new FragmentApplication();
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            fragment.setArguments(bundle);
            return fragment;
        }
        else{
            fragment = new FragmentTransactionReview();
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            fragment.setArguments(bundle);
            return fragment;
        }

    }

    @Override
    public int getCount() {
        return fragmentCount;
    }
}
