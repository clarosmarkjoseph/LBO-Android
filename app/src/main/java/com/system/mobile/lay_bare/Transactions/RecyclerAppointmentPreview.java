package com.system.mobile.lay_bare.Transactions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 30/10/2017.
 */

public class RecyclerAppointmentPreview extends RecyclerView.Adapter<RecyclerAppointmentPreview.ViewHolder> {

    View view;
    JSONArray arrayItems;
    Context context;
    Utilities utilities;
    boolean isClicked = false;
    String SERVER_URL = "";
    String reason = "";
    ViewGroup parentUniversal;
    JSONObject objAppointment = null;

    private ArrayList<String> arrayErrorResponse = new ArrayList<>();

    public RecyclerAppointmentPreview(FragmentActivity appointments, JSONArray arrayAppointmentsPass) {
        this.context                    = appointments;
        this.arrayItems                 = arrayAppointmentsPass;
        this.utilities                  = new Utilities(appointments);
    }

    @Override
    public RecyclerAppointmentPreview.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentUniversal         = parent;
        view                    = LayoutInflater.from(context).inflate(R.layout.recycler_appointment_preview,parent,false);
        ViewHolder viewHolder   = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerAppointmentPreview.ViewHolder holder, final int position) {

        try {
            objAppointment          = arrayItems.getJSONObject(position);
            String item_type        = objAppointment.getString("item_type");
            double amount           = objAppointment.getDouble("amount");
            String item_name        = objAppointment.getString("item_name");
            String item_status      = objAppointment.getString("item_status");
            String image            = objAppointment.getString("item_image");
            JSONObject item_data    = new JSONObject(objAppointment.optString("item_data","{}"));
            String cancel_reason    = item_data.optString("cancel_reason","N/A");

            holder.lblStatus.setText(item_status.toUpperCase());
            holder.lblItemName.setText(item_name);

            if(item_status.equals("reserved")){
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_blue));
                holder.btnCancelItem.setVisibility(View.VISIBLE);
            }
            else if(item_status.equals("completed")){
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_green));
            }
            else if(item_status.equals("expired")){
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_yellow));
            }
            else{
                holder.lblCancelReason.setVisibility(View.VISIBLE);
                holder.lblCancelReason.setText("Reason of cancellation: "+cancel_reason);
                holder.lblStatus.setBackground(context.getResources().getDrawable(R.drawable.circle_red));
            }

            if(item_type.equals("service")){

                String time_start       = objAppointment.optString("serve_time");
                String time_end         = objAppointment.optString("complete_time");
                String duration         = objAppointment.optString("item_duration","");

                if(objAppointment.isNull("serve_time")){
                    time_start = objAppointment.getString("book_start_time");
                }
                if(objAppointment.isNull("complete_time")){
                    time_end = objAppointment.getString("book_end_time");
                }

                time_start              =  utilities.convert12Hours(utilities.getScheduleTime(time_start));
                time_end                =  utilities.convert12Hours(utilities.getScheduleTime(time_end));

                holder.lbl1.setText("Item Type: ");
                holder.lblAnswer1.setText(utilities.capitalize(item_type));
                holder.lbl2.setVisibility(View.GONE);
                holder.lblAnswer2.setVisibility(View.GONE);
                holder.lbl3.setText("Summary Time: ");
                holder.lblAnswer3.setText(time_start+" - "+time_end);
                holder.lbl4.setText("Sub-total: ");
                holder.lblAnswer4.setText("Php "+utilities.convertToCurrency(String.valueOf(amount)));
                holder.lblQuantity.setText(duration+" minutes");
                utilities.setUniversalBigImage(holder.imgItem,SERVER_URL+"/images/services/"+image);


            }
            else{

                int quantity         = objAppointment.getInt("quantity");
                JSONObject objProd   = objAppointment.getJSONObject("item_info");
                String variant       = objProd.getString("variant");
                String size          = objProd.getString("size");
                double subtotal      = amount * quantity;
                holder.lblQuantity.setText(quantity+" pc(s)");
                holder.lbl1.setText("Product Variant: ");
                holder.lbl2.setText("Product Size: ");
                holder.lbl3.setText("Product Price: ");
                holder.lbl4.setText("Sub total: ");
                holder.lblAnswer1.setText(variant);
                holder.lblAnswer2.setText(size);
                holder.lblAnswer3.setText("Php "+utilities.convertToCurrency(String.valueOf(amount)));
                holder.lblAnswer4.setText("Php "+utilities.convertToCurrency(String.valueOf(subtotal)));
                utilities.setUniversalBigImage(holder.imgItem,SERVER_URL+"/images/products/"+image);
            }

            holder.btnCancelItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertCancelReason(position);
                }
            });


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void alertCancelReason(final int position) {

        final AlertDialog.Builder myBuilder1 = new AlertDialog.Builder(context);
        myBuilder1.setIcon(R.drawable.app_logo);
        myBuilder1.setTitle("Reason of Cancellation");
        final String[] items = {"Hair Length", "Monthly Cycle", "Medical Condition","Skin Surface Condition","No Show","Multiple Input"};
        myBuilder1.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do anything you want here
                removeItem(position,items[which]);
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

    private void removeItem(final int position, final String reason){

       String item_name = "";
       try {
           objAppointment   = arrayItems.getJSONObject(position);
           item_name        = objAppointment.getString("item_name");
       } catch (JSONException e) {
           e.printStackTrace();
       }

       final AlertDialog.Builder myBuilder1 = new AlertDialog.Builder(context);
       myBuilder1.setIcon(R.drawable.app_logo);
       myBuilder1.setTitle("Confirmation");
       myBuilder1.setMessage("Are you sure you? want to remove "+item_name+" from the list?");
       myBuilder1.setPositiveButton("Ok, Cancel this item",
               new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       cancelItem(position,dialog,reason);
                   }
               });
       myBuilder1.setNegativeButton("Close",
               new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
       final AlertDialog myDialog1  = myBuilder1.create();
       myDialog1.show();

   }

    private void cancelItem(int position, final DialogInterface dialog,String reason) {

        utilities.showProgressDialog("Cancelling item....");
        JSONObject payloadRequest = new JSONObject();
        try {
            objAppointment  = arrayItems.getJSONObject(position);
            String type     = objAppointment.getString("item_type");
            final int id          = objAppointment.getInt("id");
            payloadRequest.put("id",id);
            payloadRequest.put("reason",reason);
            payloadRequest.put("reason_text",reason);
            payloadRequest.put("type",type);
            String token = utilities.getToken();

            String url = SERVER_URL+"/api/appointment/cancelItem?token="+token;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, payloadRequest, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            utilities.hideProgressDialog();
                            try {
                                String result = response.getString("result");
                                if (result.equals("success")){

                                    dialog.dismiss();
                                    Toast.makeText(context,"Successfully Cancelled",Toast.LENGTH_LONG).show();
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
                            utilities.showDialogMessage("Connection Error",arrayErrorResponse.get(1).toString(),"error");
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
        return arrayItems.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblQuantity,lblItemName,lblCancelReason,lblStatus,lbl1,lbl2,lbl3,lbl4,lblAnswer1,lblAnswer2,lblAnswer3,lblAnswer4;
        CardView cardDetails;
        ImageView imgItem;
        LinearLayout linear_content;
        Button btnCancelItem;

        public ViewHolder(View itemView) {
            super(itemView);
            SERVER_URL          = utilities.returnIpAddress();
            linear_content      = (LinearLayout)itemView.findViewById(R.id.linear_content);
            btnCancelItem       = (Button) itemView.findViewById(R.id.btnCancelItem);
            imgItem             = (ImageView)itemView.findViewById(R.id.imgItem);
            cardDetails         = (CardView)itemView.findViewById(R.id.cardDetails);
            lblQuantity         = (TextView)itemView.findViewById(R.id.lblQuantity);
            lblItemName         = (TextView)itemView.findViewById(R.id.lblItemName);
            lblStatus           = (TextView)itemView.findViewById(R.id.lblStatus);
            lbl1                = (TextView)itemView.findViewById(R.id.lbl1);
            lbl2                = (TextView)itemView.findViewById(R.id.lbl2);
            lbl3                = (TextView)itemView.findViewById(R.id.lbl3);
            lbl4                = (TextView)itemView.findViewById(R.id.lbl4);
            lblAnswer1          = (TextView)itemView.findViewById(R.id.lblAnswer1);
            lblAnswer2          = (TextView)itemView.findViewById(R.id.lblAnswer2);
            lblAnswer3          = (TextView)itemView.findViewById(R.id.lblAnswer3);
            lblAnswer4          = (TextView)itemView.findViewById(R.id.lblAnswer4);
            lblCancelReason     = (TextView)itemView.findViewById(R.id.lblCancelReason);
        }
    }
}
