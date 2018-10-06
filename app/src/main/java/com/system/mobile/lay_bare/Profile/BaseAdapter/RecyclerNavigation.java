package com.system.mobile.lay_bare.Profile.BaseAdapter;

import android.app.Service;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Transactions.RecyclerWaiverForm;
import com.system.mobile.lay_bare.Transactions.AppointmentSingleton;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Mark on 08/11/2017.
 */

public class RecyclerNavigation extends RecyclerView.Adapter<RecyclerNavigation.ViewHolder>{
    Context context;
    Utilities utilities;
    String SERVER_URL = "";
    View layout;
    String myGender = "";
    InputMethodManager imm;
    RecyclerWaiverForm.ViewHolder viewLast;

    AppointmentSingleton stepperSingleton;

    JSONObject objWaiverAnswer;
    JSONObject objWaiverAnswerReplica;
    JSONArray arrayNav;


    public RecyclerNavigation(FragmentActivity activity,JSONArray arrayNav1) {
        this.context            = activity;
        this.utilities          = new Utilities(activity);
        this.arrayNav           = arrayNav1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        layout                  = LayoutInflater.from(context).inflate(R.layout.card_waiver_details,parent,false);
        SERVER_URL              = utilities.returnIpAddress();
        ViewHolder vh           = new ViewHolder(layout);
        imm                     = (InputMethodManager)context.getSystemService(Service.INPUT_METHOD_SERVICE);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerNavigation.ViewHolder holder, final int position) {
        final RecyclerNavigation.ViewHolder view = holder;

    }


    @Override
    public int getItemCount() {
        return arrayNav.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{


        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
