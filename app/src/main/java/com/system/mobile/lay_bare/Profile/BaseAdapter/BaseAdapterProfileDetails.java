package com.system.mobile.lay_bare.Profile.BaseAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.system.mobile.lay_bare.R;

import java.util.ArrayList;

/**
 * Created by Mark on 13/09/2017.
 */

public class BaseAdapterProfileDetails extends BaseAdapter {

    Context ctx;
    ArrayList<String > arrayDetails;
    ArrayList<String > arrayTitle;
    public BaseAdapterProfileDetails(Context applicationContext, ArrayList<String> arrayDetail,ArrayList<String> arrayTitle) {
        this.ctx            = applicationContext;
        this.arrayDetails   = arrayDetail;
        this.arrayTitle     = arrayTitle;
    }

    @Override
    public int getCount() {
        return arrayDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("POSITION INDEX", String.valueOf(position));
        View v;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            v = inflater.inflate(R.layout.client_profile_cardview, parent,false);
        }
        else {
            v = convertView;
        }
//        EditText txtEditProfile          = (EditText) v.findViewById(R.id.txtEditProfile);
//        txtEditProfile.setText(arrayDetails.get(position));
//        TextView lblProfile_Content            = (TextView) v.findViewById(R.id.lblProfile_Content);
//        lblProfile_Content.setText(arrayDetails.get(position));
        return v;
    }
}
