package com.system.mobile.lay_bare.ClientTransactions;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paolohilario on 4/27/18.
 */

public class RecyclerTransactionLog extends RecyclerView.Adapter<RecyclerTransactionLog.ViewHolder> {
    Context context;
    ArrayList<JSONObject> arrayList;
    Utilities utilities;
    View view;

    public RecyclerTransactionLog(Context context,ArrayList<JSONObject> objectItem){
        this.arrayList = objectItem;
        this.context   = context;
        this.utilities = new Utilities(context);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.recycler_transaction_logs, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        JSONObject objTransactions     = arrayList.get(position);
        String date         = null;
        try {
            date                = objTransactions.getString("date");
            String branch       = objTransactions.getString("branch");
            String net_amount   = objTransactions.getString("net_amount");
            holder.lblBranch.setText(branch);
            holder.lblDate.setText(utilities.getCompleteDateMonth(date));
            holder.lblTotalPrice.setText("₱ "+utilities.convertToCurrency(net_amount));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView lblDate, lblTotalPrice, lblBranch;
        LinearLayout linear_recycler;

        public ViewHolder(View itemView) {
            super(itemView);

            lblDate         = (TextView) itemView.findViewById(R.id.lblDate);
            lblTotalPrice   = (TextView) itemView.findViewById(R.id.lblTotalPrice);
            lblBranch       = (TextView) itemView.findViewById(R.id.lblBranch);
            linear_recycler = (LinearLayout) itemView.findViewById(R.id.linear_recycler);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemIndex = getAdapterPosition();
                    JSONObject objectPass = arrayList.get(itemIndex);
                    Intent intent = new Intent(context, ClientTransactionDetails.class);
                    intent.putExtra("object_services", objectPass.toString());
                    context.startActivity(intent);
                }
            });
        }
    }
}
