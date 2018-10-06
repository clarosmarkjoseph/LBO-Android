package com.system.mobile.lay_bare.NavigationDrawer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.ChatMessage.ChatInbox;
import com.system.mobile.lay_bare.ChatMessage.ChatMessage;
import com.system.mobile.lay_bare.ClientTransactions.ClientTransactions;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MainActivity;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.NewLogin;
import com.system.mobile.lay_bare.Notifications.NotificationCenter;
import com.system.mobile.lay_bare.PLC.PremierClient;
import com.system.mobile.lay_bare.Promotions;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Sockets.SocketApplication;
import com.system.mobile.lay_bare.Transactions.Appointment;
import com.system.mobile.lay_bare.Transactions.AppointmentHistory;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import io.socket.client.Socket;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by paolohilario on 1/18/18.
 */


public class RecyclerNavigation extends RecyclerView.Adapter<RecyclerNavigation.ViewHolder> {

    Context context;
    Utilities utilities;
    String SERVER_URL = "",clientID = "";
    View layout;
    InputMethodManager imm;

    String[] primaryHeader   = new String[]{
        "My Transaction",
        "My Appointments",
        "My Calendar",
        "Premiere Loyalty Card",
        "Promotions",
        "Notifications",
        "Messages",
        "Contact us",
        "Like us on Facebook",
        "Log Out"};

    int notifCount = 0;
    int msgCount = 0;

    int[] intDrawables = new int[]{
            R.drawable.a_transaction,
            R.drawable.a_booking,
            R.drawable.a_calendar,
            R.drawable.a_card,
            R.drawable.a_promo,
            R.drawable.a_notifications,
            R.drawable.a_message,
            R.drawable.a_contact,
            R.drawable.a_testimonials,
            R.drawable.a_logout
    };


    private DataHandler handler;
    private ArrayList<String> arrayErrorResponse;
    private Bitmap[] images;


    public RecyclerNavigation(Context activity) {
        this.context    = activity;
        this.utilities  = new Utilities(activity);
        this.clientID   = utilities.getClientID();
        this.handler    = new DataHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        layout = LayoutInflater.from(context).inflate(R.layout.recycler_navigation, parent, false);
        SERVER_URL = utilities.returnIpAddress();
        ViewHolder vh = new ViewHolder(layout);
        imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ViewHolder view   = holder;
        String title            = primaryHeader[position];


        Drawable myDrawable     = context.getResources().getDrawable(intDrawables[position]);
        view.imgList.setImageDrawable(myDrawable);

        if (position == 0){
            view.lblCaption.setVisibility(View.VISIBLE);
            view.lblCaption.setText("Dashboard");
        }
        if(position == 3){
            title            = primaryHeader[position];
        }
        if (position == 5) {
            handler.open();
            Cursor queryNotif = handler.returnAllNotification();
            if(queryNotif.getCount() > 0){
                int countRead = 0;
                while (queryNotif.moveToNext()){
                    int isPop                = Integer.parseInt(queryNotif.getString(4));
                    int isRead               = Integer.parseInt(queryNotif.getString(5));
                    if(isPop <= 0) {
                        if (isRead == 0){
                            countRead++;
                        }
                    }
                }
                notifCount = countRead;
            }
            handler.close();
            if (notifCount > 0){
                view.lblNotifications.setBackground(context.getResources().getDrawable(R.drawable.a_badge));
                view.lblNotifications.setText(String.valueOf(notifCount));
            }
            else{
                notifCount = 0;
                view.lblNotifications.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }
        }
        if (position == 6){
            handler.open();
            msgCount = handler.countUnreadMessage();
            if (msgCount > 0){
                view.lblNotifications.setBackground(context.getResources().getDrawable(R.drawable.a_badge));
                view.lblNotifications.setText(String.valueOf(msgCount));
            }
            else{
                msgCount = 0;
                view.lblNotifications.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }
            Log.e("msgCount",String.valueOf(msgCount));
            handler.close();
        }
        if (position == 7){
            view.lblCaption.setVisibility(View.VISIBLE);
            view.lblCaption.setText("Others");
        }
        view.lblTitle.setText(title);
    }


    private void specifyFunction(int position) {


        if (position == 0) {
            Intent intent = new Intent(context, ClientTransactions.class);
            context.startActivity(intent);
        }
        if (position == 1) {
            Intent intent = new Intent(context, AppointmentHistory.class);
            context.startActivity(intent);
        }
        else if (position == 2) {
            Intent intent = new Intent(context, Appointment.class);
            context.startActivity(intent);
        }
        else if (position == 3) {
            if (utilities.ifClientIsPremier() == true){
                Intent intent = new Intent(context, PremierClient.class);
                context.startActivity(intent);
            }
            else{
                Intent intent = new Intent(context, PremierClient.class);
                context.startActivity(intent);
            }
        }
        else if (position == 4) {
            Intent intent = new Intent(context, Promotions.class);
            context.startActivity(intent);
        }
        else if (position == 5) {
            Intent intent = new Intent(context, NotificationCenter.class);
            context.startActivity(intent);
        }
        else if (position == 6) {
            Intent intent = new Intent(context, ChatInbox.class);
            context.startActivity(intent);
        }
        else if (position == 7) {
            showContactVia();

        }
        else if (position == 8) {
            openFacebookApp();

        }
        else if (position == 9) {
            utilities.showProgressDialog("Logging-Out....");
            String token            = utilities.getToken();
            JSONObject objParams    = new JSONObject();
            try {
                objParams.put("user_id",clientID);
                String url = SERVER_URL+"/api/user/destroyToken?token="+token;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url,objParams, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                utilities.hideProgressDialog();
                                handler.open();
                                handler.deleteUserAccount();
                                handler.deleteToken();
                                handler.deleteTotalTransactions();
                                handler.deleteAppointment();
                                handler.deletePromotion();
                                handler.deleteApplicationAndRequest();
                                handler.deleteBranchRating();
                                handler.deleteAllChatMessage();
                                handler.deleteAllNotification();
                                handler.deleteTransactionLogs();
                                handler.deleteAllChatThread();
                                handler.close();

                                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.cancelAll();

                                Intent intent = new Intent(context,NewLogin.class);
                                context.startActivity(intent);
                                ((Activity)context).finish();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                utilities.hideProgressDialog();
                                arrayErrorResponse = utilities.errorHandling(error);
                                utilities.showDialogMessage("Connection Error",arrayErrorResponse.get(1).toString(),"error");
                            }
                        });
                jsObjRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                10000,
                                1,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
            }

            catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {

        if (clientID == null) {
            return 0;
        }
        return primaryHeader.length;
    }

    //open Lay Bare Facebook Fan Page
    private void openFacebookApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/7037766039"));
            context.startActivity(intent);
        }
        catch(Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/OfficialLayBare/")));
        }
    }

    private void showContactVia() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_dialog);
        TextView lbldialog_title            = (TextView) dialog.findViewById(R.id.lbldialog_title);
        TextView lbldialog_message          = (TextView) dialog.findViewById(R.id.lbldialog_message);
        Button btnEmail                     = (Button) dialog.findViewById(R.id.btndialog_cancel);
        Button btnChat                      = (Button) dialog.findViewById(R.id.btndialog_confirm);
        ImageButton imgBtnClose             = (ImageButton) dialog.findViewById(R.id.imgBtn_dialog_close);

        RelativeLayout relativeToolbar      = (RelativeLayout) dialog.findViewById(R.id.relativeToolbar);
        relativeToolbar.setBackgroundColor(context.getResources().getColor(R.color.laybareInfo));
        btnEmail.setVisibility(View.VISIBLE);



        Drawable img = context.getResources().getDrawable(R.drawable.app_logo );
        lbldialog_title.setText("Contact Us");
        lbldialog_message.setText( "Please select how you want to connect with us.");

        btnEmail.setText("Email Us");
        btnChat.setText("Live Chat");

        btnEmail.setBackgroundColor(context.getResources().getColor(R.color.laybareInfo));
        btnChat.setBackgroundColor(context.getResources().getColor(R.color.laybareGreen));

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmailSelection();
                dialog.dismiss();
            }
        });
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestChatForCustomerService();
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void requestChatForCustomerService() {

        utilities.showProgressDialog("Loading Contacts...");
        String token    = utilities.getToken();
        String url      = SERVER_URL+ "/api/mobile/contactCustomerService?token="+token;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        utilities.hideProgressDialog();
                        Log.e("response",response.toString());
                        try {
                            JSONObject objectDetails = response.optJSONObject("thread_details");
                            if (!objectDetails.isNull("thread_name")){
                                String thread_name  = objectDetails.getString("thread_name");
                                int thread_id       = response.getInt("thread_id");
                                int recipientID     = response.getInt("recipient_id");

                                Log.e("thread_id: ",String.valueOf(thread_id));

                                Intent intent = new Intent(context, ChatMessage.class);
                                intent.putExtra("recipient_id",recipientID);
                                intent.putExtra("userName",thread_name);
                                intent.putExtra("thread_id",thread_id);
                                intent.putExtra("chat_type","message");
                                context.startActivity(intent);
                            }
                            else{
                                utilities.showDialogMessage("Agent is Offline","Sorry, our Customer Service Agent is not present. Please leave an email or try again.","info");
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
        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    //contact us via email pop up
    private void showEmailSelection() {

        Bundle bundle = new Bundle();
        int position = bundle.getInt("position");
        final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(context);
        helpBuilder.setIcon(R.drawable.app_logo);
        helpBuilder.setTitle("Contact Us")
                .setItems(R.array.contact_email, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String array_contact = context.getResources().getStringArray(R.array.contact_email)[which];

                        if(array_contact.equals("General Concern")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri data = Uri.parse("mailto:info@lay-bare.com"
                                    + "?subject=" + "Lay Bare General Concern" + "&body=" + "");
                            intent.setData(data);
                            context.startActivity(intent);
                        }
                        else if(array_contact.equals("Marketing Concern")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri data = Uri.parse("mailto:marketing@lay-bare.com"
                                    + "?subject=" + "Lay Bare Marketing Concern" + "&body=" + "");
                            intent.setData(data);
                            context.startActivity(intent);
                        }
                        else if(array_contact.equals("Franchising Concern")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri data = Uri.parse("mailto:franchising@lay-bare.com"
                                    + "?subject=" + "Lay Bare Franchising Concern" + "&body=" + "");
                            intent.setData(data);
                            context.startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri data = Uri.parse("mailto:customercare@lay-bare.com"
                                    + "?subject=" + "Customer Service Concern" + "&body=" + "");
                            intent.setData(data);
                            context.startActivity(intent);
                        }
                    }
                });
        helpBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblTitle, lblNotifications,lblCaption;
        ImageView imgList;

        public ViewHolder(final View itemView) {
            super(itemView);

            lblCaption          = (TextView) itemView.findViewById(R.id.lblCaption);
            lblTitle            = (TextView) itemView.findViewById(R.id.lblTitle);
            lblNotifications    = (TextView) itemView.findViewById(R.id.lblNotifications);
            imgList             = (ImageView) itemView.findViewById(R.id.imgList);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    specifyFunction(position);
                }
            });
        }

    }


}