package com.system.mobile.lay_bare.PLC;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.system.mobile.lay_bare.R;

/**
 * Created by paolohilario on 9/18/18.
 */

public class FragmentPremier extends Fragment {

    View layout;

    public static FragmentPremier getInstance(int position) {
        FragmentPremier fragmentService = new FragmentPremier();
        Bundle args = new Bundle();
        args.putInt("position",position);
        fragmentService.setArguments(args);
        return fragmentService;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout      = inflater.inflate(R.layout.premier_fragment, container, false);
        initElements();
        return layout;
    }


    private void initElements(){

    }


}
