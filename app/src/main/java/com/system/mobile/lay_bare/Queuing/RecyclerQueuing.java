package com.system.mobile.lay_bare.Queuing;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by paolohilario on 3/14/18.
 */

public class RecyclerQueuing extends RecyclerView.Adapter<RecyclerQueuing.ViewHolder> {

    JSONArray arrayQueuing;
    JSONArray arrayServing;
    JSONArray arrayCalling;
    Context context;
    View layout;


    public RecyclerQueuing(Context ctx, JSONArray arrayQueuing, JSONArray arrayServing, JSONArray arrayCalling){
        this.context = ctx;
        this.arrayQueuing = arrayQueuing;
        this.arrayServing = arrayServing;
        this.arrayCalling = arrayCalling;
    }

    @Override
    public RecyclerQueuing.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layout                  =  LayoutInflater.from(context).inflate(R.layout.recycler_queuing,parent,false);
        ViewHolder vh           = new ViewHolder(layout);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerQueuing.ViewHolder holder, int position) {
        try {
            JSONObject objectQueued = arrayQueuing.getJSONObject(position);
            int ID                  = objectQueued.getInt("id");
            int client_id           = objectQueued.getInt("client_id");
            String client_name      = objectQueued.getString("client_shortname");
            String technician_name  = objectQueued.getString("technician_shortname");
            String time             = objectQueued.getString("transaction_time_formatted");

            if(arrayCalling.length() > 0){
                for(int x = 0; x < arrayCalling.length(); x++){

                    JSONObject objectCalling = arrayCalling.getJSONObject(x);
                    int callClientID         = objectCalling.getInt("client_id");
                    Log.e("check ID",client_id+"=="+callClientID);
                    if(client_id == callClientID){
                        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.laybarePink));
                        holder.lblID.setTextColor(context.getResources().getColor(R.color.themeWhite));
                        holder.lblName.setTextColor(context.getResources().getColor(R.color.themeWhite));
                        holder.lblTech.setTextColor(context.getResources().getColor(R.color.themeWhite));
                        holder.lblTime.setTextColor(context.getResources().getColor(R.color.themeWhite));
                    }
                }
            }
            if(arrayServing.length() > 0){
                for(int x = 0; x < arrayServing.length(); x++){
                    JSONObject objectServing = arrayServing.getJSONObject(x);
                    int callServingID        = objectServing.getInt("client_id");
                    Log.e("check ID",client_id+"=="+callServingID);
                    if(client_id == callServingID){
                        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.laybareGreen));
                        holder.lblID.setTextColor(context.getResources().getColor(R.color.themeWhite));
                        holder.lblName.setTextColor(context.getResources().getColor(R.color.themeWhite));
                        holder.lblTech.setTextColor(context.getResources().getColor(R.color.themeWhite));
                        holder.lblTime.setTextColor(context.getResources().getColor(R.color.themeWhite));
                    }
                }
            }

            holder.lblID.setText("ID: "+String.valueOf(ID));
            holder.lblName.setText(client_name);
            holder.lblTech.setText(technician_name);
            holder.lblTime.setText(time);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayQueuing.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView lblName,lblID,lblTech,lblTime;



        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView)itemView.findViewById(R.id.cardView);
            lblID    = (TextView)itemView.findViewById(R.id.lblID);
            lblName    = (TextView)itemView.findViewById(R.id.lblName);
            lblTech    = (TextView)itemView.findViewById(R.id.lblTech);
            lblTime    = (TextView)itemView.findViewById(R.id.lblTime);

        }
    }
}
