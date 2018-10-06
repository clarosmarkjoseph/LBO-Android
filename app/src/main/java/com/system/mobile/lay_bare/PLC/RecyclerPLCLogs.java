package com.system.mobile.lay_bare.PLC;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by paolohilario on 9/18/18.
 */
public class RecyclerPLCLogs extends RecyclerView.Adapter<RecyclerPLCLogs.ViewHolder> {

    Context context;
    View view;
    ViewHolder viewHolder;
    ArrayList<JSONObject> arrayResult;
    boolean ifPLCApplication;
    Utilities utilities;
    String SERVER_URL;
    ArrayList<String> arrayErrorResponse;


    public RecyclerPLCLogs(Context context,  ArrayList<JSONObject> arrayData, boolean ifPLCApplications) {
        this.context            = context;
        this.arrayResult        = arrayData;
        this.ifPLCApplication   = ifPLCApplications;
        this.utilities          = new Utilities(context);
        this.SERVER_URL         = utilities.returnIpAddress();
        this.arrayErrorResponse = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        view        = LayoutInflater.from(context).inflate(R.layout.recycler_plc_logs, parent,false);
        viewHolder  = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {
            JSONObject jsonObject = arrayResult.get(position);
            if (ifPLCApplication == false) {

                String status           = jsonObject.getString("status");
                String rawDate          = jsonObject.getString("created_at");
                String processed_date   = jsonObject.getString("processed_date");
                String date             = utilities.removeTimeFromDate(rawDate);

                if(!processed_date.equals("null")){
                    processed_date = utilities.getCompleteDateMonth(processed_date);
                }
                else{
                    processed_date = "Not yet processed!";
                }
                holder.imgCard.setImageDrawable(context.getResources().getDrawable(R.drawable.no_image));
                holder.lblBranch.setText("Request For Transaction Review");
                holder.lblDate.setText(utilities.getCompleteDateTime(rawDate));
                holder.lblContent.setText(processed_date);
                holder.lblStatus.setText(status.toUpperCase());

                //other status might needed to application logs
                if(status.equals("denied") ){
                    holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_red));
                }
                else if(status.equals("pending") || status.equals("processing")){
                    holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_yellow));
                }
                else if(status.equals("approved")){
                    holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_blue));
                }
                else{
                    holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_green));
                }
            }
            else {
                String application_type = jsonObject.getString("application_type");
                String status           = jsonObject.getString("status");
                String reference_no     = jsonObject.getString("reference_no");
                String branch_name      = jsonObject.getString("branch_name");
                String date_applied     = jsonObject.getString("created_at");
                String gender           = utilities.getGender();
                if (gender.equals("Male") || gender.equals("male")){
                    holder.imgCard.setImageDrawable(context.getResources().getDrawable(R.drawable.plc_male));
                }
                else {
                    holder.imgCard.setImageDrawable(context.getResources().getDrawable(R.drawable.plc_female));
                }
                holder.lblContent.setText("ID: "+reference_no+" ("+utilities.capitalize(application_type)+")");
                holder.lblDate.setText(utilities.getCompleteDateTime(date_applied));
                holder.lblBranch.setText(branch_name);

                if(status.equals("denied") ){
                    holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_red));
                    holder.cardviewCard.setVisibility(View.GONE);
                }
                else if(status.equals("pending") || status.equals("processing")){
                    holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_yellow));
                }
                else if(status.equals("approved")){
                    holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_blue));
                }
                else{
                    if(status.equals("picked_up")){
                        status = "Picked-Up";
                    }
                    holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_green));
                }
                holder.lblStatus.setText(status.toUpperCase());
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.e("Count result", String.valueOf(arrayResult.size()));
        return arrayResult.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView lblStatus,lblDate,lblContent,lblBranch;
        CardView cardviewCard;
        ImageView imgCard;


        public ViewHolder(final View itemView) {
            super(itemView);
            lblStatus           = (TextView) itemView.findViewById(R.id.lblStatus);
            lblDate             = (TextView) itemView.findViewById(R.id.lblDate);
            lblContent          = (TextView) itemView.findViewById(R.id.lblContent);
            lblBranch           = (TextView) itemView.findViewById(R.id.lblBranch);
            cardviewCard        = (CardView) itemView.findViewById(R.id.cardviewCard);
            imgCard             = (ImageView) itemView.findViewById(R.id.imgCard);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            Intent intent = new Intent(context,PLCPreviewLogs.class);
            intent.putExtra("result",arrayResult.get(index).toString());
            if(ifPLCApplication == true){
                intent.putExtra("type","plc_application");
            }
            else{
                intent.putExtra("type","transaction_request");
            }
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            int index = getAdapterPosition();
            if(ifPLCApplication == false){
//                showPopUp(itemView,true,index);
            }
            return false;
        }
    }

//   void showPopUp(final View view, boolean ifList, final int position){
//
//       //list alertdialog
//       final CharSequence[] items = { "Delete", "Cancel"};
//       AlertDialog.Builder builder = new AlertDialog.Builder(context);
//       builder.setTitle("Options");
//       builder.setItems(items,new DialogInterface.OnClickListener() {
//           @Override
//           public void onClick(DialogInterface dialog, int item) {
//               if (items[item].equals("Delete")) {
//                   showDeletePopUp(view,position);
//               }
//               else {
//                   dialog.dismiss();
//               }
//           }
//       });
//       builder.show();
//   }

    private void showDeletePopUp(final View view, final int position) {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(context);
        helpBuilder.setTitle("Delete Confirmation");
        helpBuilder.setMessage("Are you sure you want to delete this record?");
        helpBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        utilities.showProgressDialog("Loading... Please wait..");
                        deleteRequest(view,position);
                    }
                });
        helpBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                    }
                });
        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    private void deleteRequest(final View view, final int position) {

        final String request_id   = getRequestID(position);
        String token        = utilities.getToken();
        JSONObject objPayload = new JSONObject();
        try {
            objPayload.put("id",request_id);
            String url          = SERVER_URL+ "/api/premier/deleteRequest?token="+token;
            JsonObjectRequest strRequest = new JsonObjectRequest(Request.Method.POST, url,objPayload,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                utilities.hideProgressDialog();
                                String  result        = response.getString("result");
                                Log.e("ER",response.toString()+"|");
                                Toast.makeText(context,"Successfully Deleted!",Toast.LENGTH_SHORT).show();
                                arrayResult.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, arrayResult.size());
                                view.setVisibility(View.GONE);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            utilities.errorHandling(error);
                            arrayErrorResponse = utilities.errorHandling(error);
                            Toast.makeText(context,arrayErrorResponse.get(1),Toast.LENGTH_SHORT).show();
                        }
                    });
            strRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            10000,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(strRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getRequestID(int position) {
        String request_id   = "";
        try {
            JSONObject obj      = arrayResult.get(position);
            request_id          = obj.getString("id");
            return  request_id;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
