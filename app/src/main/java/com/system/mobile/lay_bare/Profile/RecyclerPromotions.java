package com.system.mobile.lay_bare.Profile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.system.mobile.lay_bare.Notifications.NotificationDetails;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 10/11/2017.
 */


public class RecyclerPromotions extends RecyclerView.Adapter<RecyclerPromotions.ViewHolder>{
    Context context;
    Utilities utilities;
    String SERVER_URL = "";
    View layout;
    InputMethodManager imm;
    JSONArray arrayPromotions;

    public RecyclerPromotions(FragmentActivity activity, JSONArray arrayNav1) {
        this.context            = activity;
        this.utilities          = new Utilities(activity);
        this.arrayPromotions    = arrayNav1;
    }

    @Override
    public RecyclerPromotions.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        layout                  = LayoutInflater.from(context).inflate(R.layout.promotion_recycler,parent,false);
        SERVER_URL              = utilities.returnIpAddress();
        ViewHolder vh           = new RecyclerPromotions.ViewHolder(layout);
        imm                     = (InputMethodManager)context.getSystemService(Service.INPUT_METHOD_SERVICE);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerPromotions.ViewHolder holder, final int position) {
        final ViewHolder view = holder;

        try {
            JSONObject jsonObject = arrayPromotions.getJSONObject(position);

            final int id                    = jsonObject.optInt("id");
            final String title              = jsonObject.getString("title");
            final String type               = jsonObject.getString("type");
            String image                    = jsonObject.getString("image");
            String date_start               = jsonObject.optString("date_start",utilities.getCurrentDate());
            String date_end                 = jsonObject.optString("date_end",utilities.getCurrentDate());

            view.lblTitle.setText(title);
            view.lblDate.setText(title);

            if(type.equals("promo")){
                String datetime = utilities.getCompleteDateMonth(date_start)+" - "+utilities.getCompleteDateMonth(date_end);
                view.lblDate.setText("Promo duration: "+datetime);
                view.lblContent.setText("Click to see details");
            }
            else{
                view.lblDate.setText("Regular Promo");
                view.lblContent.setText("");
            }

            view.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type.equals("promo")){
                        Intent repeating_intent = new Intent();
                        repeating_intent = new Intent(context,NotificationDetails.class);
                        repeating_intent.putExtra("unique_id",id);
                        repeating_intent.putExtra("type","promotion");
                        context.startActivity(repeating_intent);
                    }
                }
            });
            utilities.setUniversalImage(view.imgPosted,SERVER_URL+"/images/promotions/"+image);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }





    @Override
    public int getItemCount() {
        return arrayPromotions.length();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView lblTitle,lblDate,lblContent;
        ImageView imgPosted;
        CardView cardView;

        public ViewHolder(final View itemView) {
            super(itemView);
            lblTitle        = (TextView)itemView.findViewById(R.id.lblTitle);
            lblDate         = (TextView)itemView.findViewById(R.id.lblDate);
            lblContent      = (TextView)itemView.findViewById(R.id.lblContent);
            imgPosted       = (ImageView)itemView.findViewById(R.id.imgPosted);
            cardView        = (CardView)itemView.findViewById(R.id.cardPromotions);
        }


    }
}