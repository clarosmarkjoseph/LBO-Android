package com.system.mobile.lay_bare.Transactions;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.Location.BranchReview;
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

public class AppointmentPreview extends AppCompatActivity {

    TextView lblDate,lblTime,lblBranch,lblTechnician,lblStatus,lblType,lblNetPrice,lblReferenceNo;
    String SERVER_URL = "";
    Utilities utilities;
    RecyclerView recyclerItems;
    RecyclerView.LayoutManager recyclerItems_layoutmanager;
    RecyclerView.Adapter recyclerItems_adapter;
    JSONArray arrayServices;
    double total_price = 0;
    SwipeRefreshLayout swipeRefresh;
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListner;
    Button btnWaiver;
    DataHandler handler;
    String transaction_id = "";
    private ArrayList<String> arrayErrorResponse = new ArrayList<>();
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;
    boolean ifWaiverExist = false;
    RelativeLayout relativeContent;
    LinearLayout linear_content_no_internet;
    LinearLayout linear_loading;
    ImageButton imgNoInternet;
    TextView lblPlatform;
    boolean ifFirstLoad = true;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_preview);
        utilities  = new Utilities(this);
        handler    = new DataHandler(getApplicationContext());
        SERVER_URL = utilities.returnIpAddress();
        setToolbar();
        initElements();

    }

    private void initElements() {

        arrayServices   = new JSONArray();
        lblBranch       = (TextView)findViewById(R.id.lblBranch);
        lblTechnician   = (TextView)findViewById(R.id.lblTechnician);
        lblDate         = (TextView)findViewById(R.id.lblDate);
        lblTime         = (TextView)findViewById(R.id.lblTime);
        recyclerItems   = (RecyclerView)findViewById(R.id.recyclerItems);
        lblType         = (TextView)findViewById(R.id.lblType);
        lblStatus       = (TextView)findViewById(R.id.lblStatus);
        lblNetPrice     = (TextView)findViewById(R.id.lblNetPrice);
        lblReferenceNo  = (TextView)findViewById(R.id.lblReferenceNo);
        swipeRefresh    = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        btnWaiver       = (Button)findViewById(R.id.btnWaiver);
        relativeContent = (RelativeLayout)findViewById(R.id.relativeContent);
        linear_content_no_internet = (LinearLayout) findViewById(R.id.linear_content_no_internet);
        linear_loading  = (LinearLayout) findViewById(R.id.linear_loading);
        imgNoInternet   = (ImageButton)findViewById(R.id.imgNoInternet);
        lblPlatform     = (TextView)findViewById(R.id.lblPlatform);

        imgNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeRefreshListner.onRefresh();
            }
        });
        showLoading();
        swipeConfiguration();
        swipeRefreshListner.onRefresh();
    }



    private void getExtra(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            transaction_id = bundle.getString("transaction_id");
            if(ifFirstLoad == true){
                ifFirstLoad = false;
                if (relativeContent.getVisibility() != View.VISIBLE){
                    showContent();
                }
                offlineModeData();
            }
            else{
                try {

                    String url = SERVER_URL+"/api/appointment/getAppointment/"+transaction_id;
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    String result_datetime  = response.optString("transaction_datetime");
                                    String result_status    = response.optString("transaction_status");
                                    JSONArray arrayItems    = response.optJSONArray("items");
                                    handler.open();
                                    Cursor cursorTransaction = handler.returnSpecificAppointments(transaction_id);
                                    if(cursorTransaction.getCount() > 0){
                                        handler.updateAppointments(transaction_id,result_datetime,result_status, String.valueOf(response),arrayItems.toString());
                                    }
                                    else{
                                        handler.insertAppointments(transaction_id,result_datetime,result_status, String.valueOf(response),arrayItems.toString());
                                    }
                                    handler.close();
                                    if (relativeContent.getVisibility() != View.VISIBLE){
                                        showContent();
                                    }
                                    offlineModeData();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    arrayErrorResponse = utilities.errorHandling(error);
                                    Toast.makeText(getApplicationContext(),arrayErrorResponse.get(1),Toast.LENGTH_LONG).show();
                                    swipeRefresh.setRefreshing(false);
                                    if (relativeContent.getVisibility() != View.VISIBLE){
                                        showContent();
                                    }
                                    offlineModeData();
                                }
                            });
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            btnWaiver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkWaiverIfExist(ifWaiverExist);
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"Sorry, something's error. Please try again",Toast.LENGTH_LONG).show();
            finish();
        }


    }

    private void checkWaiverIfExist(boolean ifWaiverExist) {
        if (ifWaiverExist == true){
            String token = utilities.getToken();
            String parse_url = SERVER_URL+"/waiver/"+transaction_id+"?token="+token;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(parse_url));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }
        else{
            utilities.showDialogMessage("No Waiver Exist!","You have no waiver or please sign your waiver to the branch that you booked","info");
        }
    }


    void getAppointmentReview(){

        String clientID = utilities.getClientID();
        if(clientID != null){
            String token  = utilities.getToken();
            String branch_url = SERVER_URL+"/api/mobile/getAppointmentReview?token="+token;
            JsonObjectRequest objectRequest  = new JsonObjectRequest
                    (Request.Method.GET, branch_url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response.length() > 0){
                                try {

                                    int review_id           = response.optInt("review_id",0);
                                    String branch_name      = response.getString("branch_name");
                                    String tech_name        = response.getString("first_name")+" "+response.getString("last_name");
                                    String reference_no     = response.getString("reference_no");
                                    String transactionID    = response.getString("id");
                                    String dateTime         = response.getString("transaction_datetime");
                                    if(review_id <= 0){
                                        handler.open();
                                        Cursor queryReview = handler.returnBranchRating(transactionID);
                                        if(queryReview.getCount() <= 0){
                                            showPopUp(branch_name,tech_name,reference_no,dateTime,transactionID);
                                        }
                                        handler.close();
                                    }
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            arrayErrorResponse = utilities.errorHandling(error);
                        }
                    });
            objectRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            10000,
                            2,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
        }
    }


    private void showPopUp(final String branch_name, String tech_name, String reference_no, final String dateTime, final String transactionID){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_review);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        RatingBar ratingBar     = (RatingBar) dialog.findViewById(R.id.ratingBar);
        TextView lblDate        = (TextView)dialog.findViewById(R.id.lblDate);
        TextView lblBranch      = (TextView)dialog.findViewById(R.id.lblBranch);
        TextView lblTechnician  = (TextView)dialog.findViewById(R.id.lblTechnician);
        TextView lblReferenceNo = (TextView)dialog.findViewById(R.id.lblReferenceNo);
        final TextView lblRate  = (TextView)dialog.findViewById(R.id.lblRate);
        final ImageButton imgBtnClose = (ImageButton) dialog.findViewById(R.id.imgBtnClose);
        lblBranch.setText(branch_name);
        lblTechnician.setText(tech_name);
        lblDate.setText(utilities.getCompleteDateTime(dateTime));
        lblReferenceNo.setText(reference_no);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                String rateCaption = ratingActionCaption(rating);
                lblRate.setText(rateCaption);
                submitReview(dialog,rating,transactionID,dateTime,branch_name);
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.open();
                handler.insertBranchRating(transactionID);
                handler.close();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void submitReview(final Dialog dialog, final float v, final String transactionID, final String dateTime, final String branch_name) {
        utilities.showProgressDialog("Submitting review.....");
        JSONObject objectParams = new JSONObject();
        try {
            objectParams.put("review_id",0);
            objectParams.put("transaction_id",transactionID);
            objectParams.put("rating",v);
            objectParams.put("feedback",null);
            String token            = utilities.getToken();
            String transaction_url  = SERVER_URL+"/api/mobile/reviews/reviewTransaction?token="+token;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, transaction_url, objectParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject objectIntentParam = new JSONObject();
                                JSONObject objectResultData  = response.getJSONObject("data_response");
                                int reviewID = objectResultData.getInt("id");
                                objectIntentParam.put("review_id",reviewID);
                                objectIntentParam.put("review_rating",v);
                                objectIntentParam.put("transaction_id",transactionID);
                                objectIntentParam.put("transaction_datetime",dateTime);
                                objectIntentParam.put("branch_name",branch_name);

                                handler.open();
                                handler.insertBranchRating(transactionID);
                                handler.close();
                                utilities.hideProgressDialog();
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Rating saved! Please comment your feedback",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AppointmentPreview.this, BranchReview.class);
                                intent.putExtra("appointment_review_object",objectIntentParam.toString());
                                startActivity(intent);

                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //on error response
                            utilities.hideProgressDialog();
                            arrayErrorResponse = utilities.errorHandling(error);
                            utilities.showDialogMessage("Connection Error",arrayErrorResponse.get(1).toString(),"error");
                        }
                    });
            jsObjRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            6000,
                            2,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String ratingActionCaption(float value) {
        String remarks = "";
        if(value >= 1){
            remarks = "Unsatisfied!";
        }
        if(value  >= 2){
            remarks = "Slightly Disliked!";
        }
        if(value >= 3){
            remarks = "It's Okay!";
        }
        if(value == 4){
            remarks = "Liked it!";
        }
        if(value == 5){
            remarks ="Loved it!";
        }
        return remarks;
    }

    private void offlineModeData() {
        try{
            handler.open();
            Cursor query = handler.returnSpecificAppointments(transaction_id);
            if(query.getCount() > 0){

                query.moveToFirst();
                String transaction_details          = query.getString(3);
                JSONObject objectAppointment        = new JSONObject(transaction_details);
                iterateJSONResponse(objectAppointment);
            }
            else{
                showNoInternet();
            }
            handler.close();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void showLoading(){
        linear_content_no_internet.setVisibility(View.GONE);
        relativeContent.setVisibility(View.GONE);
        linear_loading.setVisibility(View.VISIBLE);
    }

    private void showNoInternet(){
        relativeContent.setVisibility(View.GONE);
        linear_loading.setVisibility(View.GONE);
        linear_content_no_internet.setVisibility(View.VISIBLE);
    }
    private void showContent(){
        linear_content_no_internet.setVisibility(View.GONE);
        linear_loading.setVisibility(View.GONE);
        relativeContent.setVisibility(View.VISIBLE);
    }

    private void iterateJSONResponse(JSONObject response) {

        try {
            String reference_no                 = response.getString("reference_no");
            String transaction_date             = response.getString("transaction_datetime");
            String transaction_time_formatted   = utilities.convert12Hours(utilities.removeDateFromDateTime(transaction_date));
            String technician_name              = response.getString("technician_name");
            String branch_name                  = response.getString("branch_name");
            String transaction_status           = response.getString("transaction_status");
            String transaction_type             = response.getString("transaction_type");
            String platform                     = response.optString("platform");
            JSONObject objectAcknowledgement    = response.getJSONObject("acknowledgement_data");
            JSONObject objectWaiver             = new JSONObject(response.getString("waiver_data"));

            if (utilities.checkWaiverIfExist(objectWaiver) == false){
                btnWaiver.setText("No Waiver! ");
                ifWaiverExist = false;
            }
            else{
                ifWaiverExist = true;
            }

            String title                        = "";
            arrayServices                       = new JSONArray(response.getString("items"));

            if(transaction_type.equals("branch_booking")){
                title = "Branch Booking";
            }
            else{
                title = "Home Service";
            }
            String[] dateTimeSplit    = transaction_date.split(" ");
            String date               = dateTimeSplit[0];
            String time               = dateTimeSplit[1];
            String dateTimeString     = utilities.getCompleteDateMonth(date);
            String week               = utilities.getDayOfWeeks(date);

            String concatWeek = "";
            if(utilities.getCurrentDate().equals(date)){
                concatWeek = "Today";
            }
            else{
                concatWeek = week;
            }

            lblBranch.setText(branch_name);
            lblTechnician.setText(technician_name);
            lblDate.setText(concatWeek+", "+dateTimeString);
            lblTime.setText(transaction_time_formatted);
            lblType.setText(title);
            lblReferenceNo.setText(reference_no.toUpperCase());
            lblPlatform.setText(platform);

            if(transaction_status.equals("cancelled")){
                lblStatus.setBackground(getResources().getDrawable(R.drawable.circle_red));
            }
            else if(transaction_status.equals("completed")){
                lblStatus.setBackground(getResources().getDrawable(R.drawable.circle_green));
                getAppointmentReview();
            }
            else if(transaction_status.equals("expired")){
                lblStatus.setBackground(getResources().getDrawable(R.drawable.circle_yellow));
            }
            else{
                lblStatus.setBackground(getResources().getDrawable(R.drawable.circle_blue));
            }
            lblStatus.setText(transaction_status.toUpperCase());
            double gross_amount = 0;
            for (int x = 0; x < arrayServices.length(); x++){
                JSONObject objItems = arrayServices.getJSONObject(x);
                double amount       = objItems.getDouble("amount");
                String status       = objItems.getString("item_status");
                String item_type    = objItems.getString("item_type");
                int quantity        = objItems.getInt("quantity");
                if(item_type.equals("product")){
                    amount = amount*quantity;
                }
                if (status.equals("reserved") || status.equals("completed")){
                    gross_amount+=amount;
                }
            }
            total_price = gross_amount;
            requestAppointmentPreview();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestAppointmentPreview() {

        showContent();
        lblNetPrice.setText("Php "+utilities.convertToCurrency(String.valueOf(total_price)));
        recyclerItems_adapter       = new RecyclerAppointmentPreview(this,arrayServices);
        recyclerItems_layoutmanager = new LinearLayoutManager(this);
        recyclerItems.setAdapter(recyclerItems_adapter);
        recyclerItems.setLayoutManager(recyclerItems_layoutmanager);
        recyclerItems.setItemAnimator(new DefaultItemAnimator());
        recyclerItems.setNestedScrollingEnabled(false);
        swipeRefresh.setRefreshing(false);
    }

    public void setToolbar(){
        myTypeface          = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle                = (TextView)findViewById(R.id.forTitle);
        imgBtnBack              = (ImageButton) findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forTitle.setTypeface(myTypeface);
        forTitle.setText("Appointment Details");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void swipeConfiguration() {

        utilities.returnRefreshColor(swipeRefresh);

        swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                getExtra();
            }
        };
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshListner.onRefresh();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        swipeRefreshListner.onRefresh();
    }




}
