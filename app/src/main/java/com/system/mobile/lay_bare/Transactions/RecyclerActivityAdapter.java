package com.system.mobile.lay_bare.Transactions;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 07/09/2017.
 */

public class RecyclerActivityAdapter extends RecyclerView.Adapter<RecyclerActivityAdapter.ViewHolder>{

    Context context;
    View view;
    ViewHolder viewHolder;
    JSONArray arrayTransaction;
    Utilities utilities;

    public RecyclerActivityAdapter(Context context, JSONArray array) {
        this.context            = context;
        this.arrayTransaction   = array;
        this.utilities          = new Utilities(context);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view        = LayoutInflater.from(context).inflate(R.layout.recycler_activity_adapter,parent,false);
        viewHolder  = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try{
            JSONObject objectTransaction  = arrayTransaction.getJSONObject(position);
            Log.e("objectTransaction",objectTransaction.toString());
            String branch                 = objectTransaction.getString("branch_name");
            String time_formatted         = objectTransaction.getString("transaction_time_formatted");
            String transaction_tech       = objectTransaction.getString("technician_name");
            String tech = "";
            String transaction_status    = objectTransaction.getString("transaction_status");

            if(transaction_status.equals("cancelled")){
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_red));
                holder.imgStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_small_red));
            }
            else if(transaction_status.equals("completed")){
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_green));
                holder.imgStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_small_green));
            }
            else if(transaction_status.equals("expired")){
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_yellow));
                holder.imgStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_small_yellow));
            }
            else{
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_blue));
                holder.imgStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_small_brown));
            }

            if(transaction_tech.equals("null") || transaction_tech.equals("") || transaction_tech.isEmpty()){
                tech = "N/A";
            }
            else{
                tech = transaction_tech;
            }

            holder.lblBranch.setText(branch);
            holder.lblTime.setText(time_formatted);
            holder.lblStatus.setText(utilities.capitalize(transaction_status));
            holder.lblRemarks.setText(tech);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.e("arrayTransaction.length()", String.valueOf(arrayTransaction.length()));
        return arrayTransaction.length();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblBranch,lblTime,lblStatus,lblRemarks;
        ImageView imgStatus;


        public ViewHolder(final View itemView) {
            super(itemView);
            lblBranch     = (TextView)itemView.findViewById(R.id.lblBranch);
            lblTime       = (TextView)itemView.findViewById(R.id.lblTime);
            lblStatus     = (TextView)itemView.findViewById(R.id.lblStatus);
            lblRemarks    = (TextView)itemView.findViewById(R.id.lblRemarks);
            imgStatus     = (ImageView) itemView.findViewById(R.id.imgStatus);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    try{
                        JSONObject objectTransaction    = arrayTransaction.getJSONObject(index);
                        String transaction_id           = objectTransaction.getString("id");
                        Intent intent = new Intent(context.getApplicationContext(), AppointmentPreview.class);
                        intent.putExtra("transaction_id",transaction_id);
                        context.startActivity(intent);
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
