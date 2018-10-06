package com.system.mobile.lay_bare.Transactions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 26/10/2017.
 */

public class RecyclerOnGoingAppointment extends RecyclerView.Adapter<RecyclerOnGoingAppointment.ViewHolder> {

    View view;
    ArrayList<JSONObject> arrayOngoingAppointment;
    Context context;
    Utilities utilities;
    JSONObject objAppointment;
    String SERVER_URL = "";
    private ArrayList<String> arrayErrorResponse = new ArrayList<>();
    DataHandler handler;


    public RecyclerOnGoingAppointment(Context context, ArrayList<JSONObject> arrayAppointmentsPass) {
        this.context                    = context;
        this.arrayOngoingAppointment    = arrayAppointmentsPass;
        this.utilities                  = new Utilities(context);
        this.objAppointment             = new JSONObject();
        this.SERVER_URL                 = utilities.returnIpAddress();
        this.handler                    = new DataHandler(context);
    }

    @Override
    public RecyclerOnGoingAppointment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view                    = LayoutInflater.from(context).inflate(R.layout.recycler_ongoing_appointment,parent,false);
        ViewHolder viewHolder   = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerOnGoingAppointment.ViewHolder holder, final int position) {

        try {
            objAppointment            = arrayOngoingAppointment.get(position);
            String transaction_id     = objAppointment.getString("id");
            String reference_no       = objAppointment.getString("reference_no");
            String dateTime           = objAppointment.getString("transaction_datetime");
            String time               = objAppointment.getString("transaction_time_formatted");
            String branch_name        = objAppointment.getString("branch_name");
            String technician_name    = objAppointment.getString("technician_name");
            String transaction_status = objAppointment.getString("transaction_status");
            String transaction_type   = objAppointment.getString("transaction_type");

            String[] dateTimeSplit    = dateTime.split(" ");
            String date               = dateTimeSplit[0];
            String month              = utilities.getMonthAsWord(date,false);
            String day                = utilities.getSpecificInDate(2,date);
            String year               = utilities.getSpecificInDate(0,date);

            if(transaction_type.equals("branch_booking")){
                holder.lblServiceType.setText("Branch Appointment");
            }
            else{
                holder.lblServiceType.setText("Home Appointment");
            }

            holder.transaction_id     = transaction_id;
            holder.lblDescription.setText(branch_name);
            holder.lblTechnician.setText(technician_name);
            holder.lblTime.setText(time);
            holder.lblMonth.setText(month);
            holder.lblDay.setText(day);
            holder.lblYear.setText(year);
            holder.lblStatus.setText(transaction_status.toUpperCase());

            if(transaction_status.equals("cancelled")){
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_red));
            }
            else if(transaction_status.equals("completed")){
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_green));
            }
            else if(transaction_status.equals("expired")){
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_yellow));
            }
            else{
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_blue));
                holder.btnCancelAppointment.setVisibility(View.VISIBLE);
            }

            holder.btnViewTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), AppointmentPreview.class);
                    intent.putExtra("transaction_id",holder.transaction_id);
                    context.startActivity(intent);
                }
            });
            holder.btnCancelAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertCancelReason(holder.transaction_id);
                }
            });


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void alertCancelReason(final String transaction_id) {

        final AlertDialog.Builder myBuilder1 = new AlertDialog.Builder(context);
        myBuilder1.setIcon(R.drawable.app_logo);
        myBuilder1.setTitle("Reason of Cancellation");
        final String[] items = {"Hair Length", "Monthly Cycle", "Medical Condition","Skin Surface Condition","No Show","Multiple Input"};
        myBuilder1.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do anything you want here
                alertCancelation(transaction_id,items[which]);
            }
        });

        myBuilder1.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog myDialog1  = myBuilder1.create();
        myDialog1.show();
    }

    private void alertCancelation(final String transaction_id, final String item) {

        final AlertDialog.Builder myBuilder1 = new AlertDialog.Builder(context);
        myBuilder1.setIcon(R.drawable.app_logo);
        myBuilder1.setTitle("Confirmation");
        myBuilder1.setMessage("Are you sure you want to cancel this entire appointment?");
        myBuilder1.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAppointment(transaction_id,dialog,item);
                    }
                });
        myBuilder1.setNegativeButton("Dismiss",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog myDialog1  = myBuilder1.create();
        myDialog1.show();
    }

    private void cancelAppointment(final String transaction_id, final DialogInterface dialog, String reason){
        utilities.showProgressDialog("Cancelling Appointment...");
        JSONObject payloadRequest = new JSONObject();
        try {
            payloadRequest.put("id",transaction_id);
            payloadRequest.put("reason",reason);
            payloadRequest.put("reason_text","");
            String token = utilities.getToken();
            String url = SERVER_URL+"/api/appointment/cancelAppointment?token="+token;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, payloadRequest, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            utilities.hideProgressDialog();
                            try {
                                String result = response.getString("result");
                                utilities.hideProgressDialog();
                                if (result.equals("success")){
                                    handler.open();
                                    handler.setTransactionAsCancelled(transaction_id);
                                    handler.deleteNotification(Integer.parseInt(transaction_id));
//                                    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//                                    notificationManager.cancel(Integer.parseInt(transaction_id));
//                                    NotificationSetter notificationSetter = new NotificationSetter(context);
//                                    notificationSetter.setNotificationForAppointment(utilities.getCurrentDateTime(),"reserved", Integer.parseInt(transaction_id));
                                    handler.close();
                                    dialog.dismiss();
                                    Toast.makeText(context,"Appointment Cancelled",Toast.LENGTH_LONG).show();
                                    ((Activity)context).finish();
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            utilities.hideProgressDialog();
                            arrayErrorResponse = utilities.errorHandling(error);
                            Toast.makeText(context,arrayErrorResponse.get(1),Toast.LENGTH_LONG).show();
                        }
                    });

            MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
       return arrayOngoingAppointment.size();
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
        TextView lblDay,lblMonth,lblYear,lblServiceType,lblTime,lblDescription,lblTechnician,lblStatus;
        Button btnViewTransaction,btnCancelAppointment;
        String transaction_id = "";

        public ViewHolder(View itemView) {
            super(itemView);
            lblDay              = (TextView)itemView.findViewById(R.id.lblDay);
            lblMonth            = (TextView)itemView.findViewById(R.id.lblMonth);
            lblYear             = (TextView)itemView.findViewById(R.id.lblYear);
            lblServiceType      = (TextView)itemView.findViewById(R.id.lblServiceType);
            lblTime             = (TextView)itemView.findViewById(R.id.lblTime);
            lblDescription      = (TextView)itemView.findViewById(R.id.lblDescription);
            lblTechnician       = (TextView)itemView.findViewById(R.id.lblTechnician);
            lblStatus           = (TextView)itemView.findViewById(R.id.lblStatus);
            btnCancelAppointment   = (Button)itemView.findViewById(R.id.btnCancelAppointment);
            btnViewTransaction  = (Button)itemView.findViewById(R.id.btnViewTransaction);
        }
    }
}
