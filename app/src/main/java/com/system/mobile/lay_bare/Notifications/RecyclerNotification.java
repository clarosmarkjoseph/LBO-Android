package com.system.mobile.lay_bare.Notifications;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.PLC.PremierClient;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Transactions.AppointmentHistory;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paolohilario on 4/3/18.
 */

public class RecyclerNotification extends RecyclerView.Adapter<RecyclerNotification.ViewHolder>  {

    ArrayList<JSONObject> arrayNotification;
    Context context;
    View layout;
    Utilities utilities;
    int uniqueID;
    static boolean ifCaptionShowed = false;

    public RecyclerNotification(Context ctx,ArrayList<JSONObject> jsonArray){
        this.context            = ctx;
        this.arrayNotification  = jsonArray;
        this.utilities          = new Utilities(context);
    }

    @Override
    public RecyclerNotification.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layout                  = LayoutInflater.from(context).inflate(R.layout.recycler_notification, parent, false);
        ViewHolder viewHolder   = new ViewHolder(layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerNotification.ViewHolder holder, int position) {

        try {

            JSONObject objectNotification   = arrayNotification.get(position);
            int isRead                      = objectNotification.optInt("isRead",0);
            String updated_at               = objectNotification.optString("created_at","N/A");
            JSONObject objectData           = new JSONObject(objectNotification.getString("notification_data"));
            String title                    = objectData.getString("title");
            String body                     = objectData.getString("body");

            if(isRead <= 0){
                holder.lblTitle.setTypeface(Typeface.DEFAULT_BOLD);
                holder.lblBody.setTypeface(Typeface.DEFAULT_BOLD);
                holder.lblTime.setTypeface(Typeface.DEFAULT_BOLD);
                holder.lblTitle.setTextColor(context.getResources().getColor(R.color.themeBlack));
                holder.lblBody.setTextColor(context.getResources().getColor(R.color.themeBlack));
                holder.lblTime.setTextColor(context.getResources().getColor(R.color.themeBlack));
            }
            else{
                holder.lblTitle.setTypeface(Typeface.DEFAULT);
                holder.lblBody.setTypeface(Typeface.DEFAULT);
                holder.lblTime.setTypeface(Typeface.DEFAULT);
                holder.lblTitle.setTextColor(context.getResources().getColor(R.color.themeGray));
                holder.lblBody.setTextColor(context.getResources().getColor(R.color.themeGray));
                holder.lblTime.setTextColor(context.getResources().getColor(R.color.themeGray));
            }

            Spanned wew = Html.fromHtml(body);
            holder.lblBody.setText(wew);
            holder.lblTitle.setText(title);
            holder.lblTime.setText(utilities.formatDatetimeAnyThing(updated_at,"MMM dd yy"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.arrayNotification.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearNotification;
        TextView lblTitle,lblBody,lblTime,lblCaption;


        public ViewHolder(final View itemView) {
            super(itemView);

            linearNotification  = (LinearLayout)itemView.findViewById(R.id.linearNotification);
            lblTitle            = (TextView) itemView.findViewById(R.id.lblTitle);
            lblBody             = (TextView) itemView.findViewById(R.id.lblBody);
            lblTime             = (TextView) itemView.findViewById(R.id.lblTime);
            lblCaption          = (TextView) itemView.findViewById(R.id.lblCaption);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        ifCaptionShowed                 = false;
                        int position                    = getAdapterPosition();

                        final JSONObject objectNotif    = arrayNotification.get(position);
                        Log.e("objectNotif: ", String.valueOf(objectNotif));
                        final int id                          = objectNotif.optInt("id",0);
                        final String notif_type         = objectNotif.getString("notification_type");
                        final JSONObject objectData     = new JSONObject(objectNotif.getString("notification_data"));
//                        objectNotif.put("isRead",1);
//                        arrayNotification.add(position,objectNotif);
//                        notifyItemChanged(position);

                        UpdateNotificationStatus updateNotificationSeen = (UpdateNotificationStatus) context;
                        updateNotificationSeen.setNotificationAsSeen(id);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(notif_type.equals("appointment")){
                                    Intent intent = new Intent(context, AppointmentHistory.class);
                                    intent.putExtra("position",1);
                                    context.startActivity(intent);
                                }
                                if(notif_type.equals("promotion")){
                                    if(objectData.has("unique_id")){
                                        try {
                                            int uniqueID = objectData.getInt("unique_id");
                                            Intent intent = new Intent(context, NotificationDetails.class);
                                            intent.putExtra("id", id);
                                            intent.putExtra("unique_id", uniqueID);
                                            intent.putExtra("type", notif_type);
                                            intent.putExtra("object", String.valueOf(objectNotif));
                                            context.startActivity(intent);
                                        }
                                        catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if(notif_type.equals("campaign_manager")){
                                    try {
                                        int uniqueID        = objectData.getInt("unique_id");
                                        Intent intent   = new Intent(context, NotificationDetails.class);
                                        intent.putExtra("unique_id", uniqueID);
                                        intent.putExtra("type", notif_type);
                                        intent.putExtra("object", String.valueOf(objectData));
                                        context.startActivity(intent);
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(notif_type.equals("PLC")){
                                    Intent intent   = new Intent(context, PremierClient.class);
                                    context.startActivity(intent);
                                }

                            }
                        }, 500);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }


    void setUniqueID(int uniqueID){
        this.uniqueID = uniqueID;
    }
    private int getUniqueID(){
        return this.uniqueID;
    }

}
