package com.system.mobile.lay_bare.Location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Mark on 29/11/2017.
 */

public class LocationSearchAdapter extends  RecyclerView.Adapter<LocationSearchAdapter.ViewHolder> {

    View view;
    private Activity mActivity;
    Context context;
    List<JSONObject> arrayListBranch   =   new ArrayList<>();
    List<JSONObject> arrayListHolder   =   new ArrayList<>();
    private CallbackInterface mCallback;


    //callback MEthod (From LocationSearch Class)
    public interface CallbackInterface{
        void onHandleSelection(int position);
    }


    public LocationSearchAdapter(Context context, ArrayList<JSONObject> arrayBranches) {
        this.context            = context;
        this.arrayListBranch    = arrayBranches;
        this.arrayListHolder    = arrayBranches;
        mCallback = (CallbackInterface) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.location_recycler,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        if(arrayListBranch.size() > 0){
            try {

                JSONObject jsonObject           = arrayListBranch.get(position);
                String branch_id                = jsonObject.getString("id");
                String branch_name              = jsonObject.getString("branch_name");
                String branch_address           = jsonObject.getString("branch_address");
                String duration                 = jsonObject.getString("duration");
                double distance                 = jsonObject.getDouble("distance");
                String elements                 = "";

                if (!duration.equals("N/A")){
                    elements = String.format("%.2f", distance)+" km Away ("+duration+" from your current location).";
                }
                else{
                    elements = String.format("%.2f", distance)+" km Away";
                }

                holder.lblName.setText(branch_name);
                holder.lblAddress.setText(branch_address);
                holder.lblDistance.setText(elements);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mCallback != null){
//                            arrayListHolder
                            mCallback.onHandleSelection(position);
                        }
                    }
                });
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayListBranch.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblName;
        TextView lblAddress;
        TextView lblDistance;
        TextView lblTitle;
        CardView cardView;

        public ViewHolder(final View itemView) {
            super(itemView);
            lblName           = (TextView) itemView.findViewById(R.id.lblName);
            lblAddress        = (TextView) itemView.findViewById(R.id.lblAddress);
            lblDistance       = (TextView) itemView.findViewById(R.id.lblDistance);
            lblTitle          = (TextView) itemView.findViewById(R.id.lblTitle);
            cardView          = (CardView)itemView.findViewById(R.id.cardview);
        }
    }




}
