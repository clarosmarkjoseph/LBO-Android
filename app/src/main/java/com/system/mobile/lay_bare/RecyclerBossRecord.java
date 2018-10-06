package com.system.mobile.lay_bare;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by paolohilario on 5/1/18.
 */

public class RecyclerBossRecord extends RecyclerView.Adapter<RecyclerBossRecord.ViewHolder> {

    JSONArray arrayBossRecords;
    View layout;
    Context context;
    Utilities utilities;
    String SERVER_URL = "";
    boolean ifShowRefresh = false;


    public RecyclerBossRecord(Context context,JSONArray array,boolean ifShowRefresh){
        this.context          = context;
        this.arrayBossRecords = array;
        this.utilities        = new Utilities(context);
        this.SERVER_URL       = utilities.returnIpAddress();
        this.ifShowRefresh    = ifShowRefresh;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layout = LayoutInflater.from(context).inflate(R.layout.recycler_boss_record, parent, false);
        SERVER_URL = utilities.returnIpAddress();
        ViewHolder vh = new ViewHolder(layout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {

            JSONObject objectRecord = arrayBossRecords.getJSONObject(position);
            String bday             = objectRecord.getString("birthdate");
            String boss_id          = objectRecord.getString("custom_client_id");
            String branch_name      = "N/A";
            String date_visited     = "N/A";
            if (objectRecord.has("last_transaction")){
                JSONObject objectTransaction = objectRecord.optJSONObject("last_transaction");
                branch_name                  = objectTransaction.optString("branch","N/A");
                date_visited                 = objectTransaction.optString("date","N/A");
            }

            holder.lblLastAppointment.setText(date_visited);
            holder.lblLastBranch.setText(branch_name);
            if (ifShowRefresh == true){
                holder.imgBtnAgree.setVisibility(View.VISIBLE);
                holder.lblBday.setText(bday);
            }
            else{
                holder.imgBtnAgree.setVisibility(View.GONE);
                holder.lblBday.setText(boss_id);
                holder.lblBday.setTextSize(15);
                holder.lblLastAppointment.setTextSize(15);
                holder.lblLastBranch.setTextSize(15);
                holder.lblBday.setTextColor(context.getResources().getColor(R.color.brownLoading));
                holder.lblLastAppointment.setTextColor(context.getResources().getColor(R.color.brownLoading));
                holder.lblLastBranch.setTextColor(context.getResources().getColor(R.color.brownLoading));
            }
            holder.imgBtnAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCustomPrompt(position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showCustomPrompt(final int position) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_dialog);
        TextView lbldialog_title            = (TextView) dialog.findViewById(R.id.lbldialog_title);
        TextView lbldialog_message          = (TextView) dialog.findViewById(R.id.lbldialog_message);
        Button btndialog_cancel             = (Button) dialog.findViewById(R.id.btndialog_cancel);
        Button btndialog_confirm            = (Button) dialog.findViewById(R.id.btndialog_confirm);
        ImageButton imgBtnClose             = (ImageButton) dialog.findViewById(R.id.imgBtn_dialog_close);
        RelativeLayout relativeToolbar      = (RelativeLayout) dialog.findViewById(R.id.relativeToolbar);

        btndialog_cancel.setVisibility(View.VISIBLE);

        relativeToolbar.setBackgroundColor(context.getResources().getColor(R.color.laybareInfo));
        btndialog_confirm.setBackgroundColor(context.getResources().getColor(R.color.laybareInfo));
        btndialog_cancel.setBackgroundColor(context.getResources().getColor(R.color.themeRed));

        lbldialog_title.setText("Confirmation");
        lbldialog_message.setText("Do you want to choose this as your transaction record account? Once validated, you'll be notified via email and credit this account's transaction into your account.");

        final Dialog myDialog = dialog;
        btndialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSetBOSSArrayIndex onSetBOSSArrayIndex = (OnSetBOSSArrayIndex) context;
                onSetBOSSArrayIndex.OnSetBOSSArrayIndex(position);
                myDialog.dismiss();
            }
        });
        btndialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return arrayBossRecords.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblBday,lblLastAppointment,lblLastBranch;
        ImageButton  imgBtnAgree;

        public ViewHolder(View itemView) {
            super(itemView);

            imgBtnAgree         = (ImageButton) itemView.findViewById(R.id.imgBtnAgree);
            lblBday             = (TextView)itemView.findViewById(R.id.lblBday);
            lblLastAppointment  = (TextView)itemView.findViewById(R.id.lblLastAppointment);
            lblLastBranch       = (TextView)itemView.findViewById(R.id.lblLastBranch);
        }
    }
}
