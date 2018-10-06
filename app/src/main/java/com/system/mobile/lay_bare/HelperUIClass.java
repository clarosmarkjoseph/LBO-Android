package com.system.mobile.lay_bare;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by paolohilario on 12/26/17.
 */

public class HelperUIClass {
    private static HelperUIClass instance;
    Context context;
    public HelperUIClass Instance(Context ctx) {
        if (instance == null) {
            instance = new HelperUIClass();
        }
        this.context = ctx;
        return instance;
    }


    private void customMessagePrompt(String status,String message) {


        AlertDialog.Builder myBuilder = new AlertDialog.Builder(context);
        View mView =  LayoutInflater.from(context).inflate(R.layout.a_popup,null);
        myBuilder.setView(mView);
        LinearLayout linear_header  = (LinearLayout)mView.findViewById(R.id.linear_header);
        ImageView imgHeader         = (ImageView) mView.findViewById(R.id.imgHeader);;
        Button btnCancel            = (Button)mView.findViewById(R.id.btnCancel);
        Button btnFinish            = (Button)mView.findViewById(R.id.btnFinish);
        TextView lblTitle           = (TextView)mView.findViewById(R.id.lblTitle);
        TextView lblMessage         = (TextView)mView.findViewById(R.id.lblMessage);
        final AlertDialog helpDialog = myBuilder.create();
        if(status.equals("success")){
            linear_header.setBackgroundColor(context.getResources().getColor(R.color.laybareGreen));
            imgHeader.setImageDrawable(context.getResources().getDrawable(R.drawable.z_icon_success));
            lblTitle.setText("Successfully registered");
            lblMessage.setText(message);
            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helpDialog.dismiss();
//                    getActivity().recreate();
                }
            });
        }
        else{
            linear_header.setBackgroundColor(context.getResources().getColor(R.color.themeRed));
            imgHeader.setImageDrawable(context.getResources().getDrawable(R.drawable.z_icon_error));
            lblTitle.setText("Error");
            lblMessage.setText(message);
            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helpDialog.dismiss();
                }
            });
        }
        helpDialog.show();
    }



}
