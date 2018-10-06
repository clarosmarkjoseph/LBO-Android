package com.system.mobile.lay_bare.ClientTransactions;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.PLC.PLCRequests;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paolohilario on 1/17/18.
 */

public class FragmentTotalTransaction extends Fragment {
    View layout;
    TextView lblStatus,lblCaption,lblTotalTransaction,lblTotalDiscount;
    Utilities utilities;
    String SERVER_URL = "";
    LinearLayout linear_transaction_status,linear_transaction_loading;
    private ArrayList<String> arrayErrorResponse;
    DataHandler handler;
    double total_transaction = 0.00;
    double total_discount    = 0.00;
    double minimum_total     = 0.00;

    Button btnAction;
    String stringParams      = "";
    JSONObject objectRequest = new JSONObject();
    JSONObject objectPremier = new JSONObject();
    static boolean ifVisible = false;


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_total_transaction,container,false);
        initElement();
        return layout;
    }

    private void initElement() {
        utilities                   = new Utilities(getActivity());
        SERVER_URL                  = utilities.returnIpAddress();
        handler                     = new DataHandler(getActivity());
        linear_transaction_status   = (LinearLayout)layout.findViewById(R.id.linear_transaction_status);
        linear_transaction_loading  = (LinearLayout)layout.findViewById(R.id.linear_transaction_loading);
        lblStatus                   = (TextView)layout.findViewById(R.id.lblStatus);
        lblCaption                  = (TextView)layout.findViewById(R.id.lblCaption);
        lblTotalTransaction         = (TextView)layout.findViewById(R.id.lblTotalTransaction);
        lblTotalDiscount            = (TextView)layout.findViewById(R.id.lblTotalDiscount);
        btnAction                   = (Button) layout.findViewById(R.id.btnAction);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActionForButton(stringParams);
            }
        });

        getPremiereLogs();
        loadInitialTransaction();
    }

    //request transactions(display saved data from sqlite)
    private void validateAction() {

        handler.open();
        Cursor queryTransaction = handler.returnTotalTransactions();
        if(queryTransaction.getCount() > 0){
            queryTransaction.moveToFirst();
            String updatedDate = queryTransaction.getString(1);
            if(!updatedDate.equals(utilities.getCurrentDate())){
                requestTransactions();
            }
            else{
                initializeRequestResponse();
            }
        }
        else{
            requestTransactions();
        }
        handler.close();
    }

    private void requestTransactions(){

        String token            = utilities.getToken();
        String transaction_url  = SERVER_URL+"/api/mobile/getTotalTransactionAmount?token="+token;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, transaction_url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (ifVisible == true) {
                            handler.open();
                            Cursor queryTransaction = handler.returnTotalTransactions();

                            if (queryTransaction.getCount() > 0) {
                                queryTransaction.moveToFirst();
                                handler.updateTotalTransactions(response.toString(), utilities.getCurrentDate());
                            } else {
                                handler.insertTotalTransactions(response.toString(), utilities.getCurrentDate());
                            }

                            handler.close();
                            initializeRequestResponse();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //on error response
                        arrayErrorResponse = utilities.errorHandling(error);
                        Toast.makeText(getActivity(),arrayErrorResponse.get(1).toString(),Toast.LENGTH_SHORT).show();
                        initializeRequestResponse();
                    }
                });
        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        60000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    public void loadInitialTransaction() {
        linear_transaction_loading.setVisibility(View.VISIBLE);
        linear_transaction_status.setVisibility(View.GONE);
        utilities.showProgressDialog("Loading Transactions....");
        handler.open();
        Cursor queryTransaction = handler.returnTotalTransactions();
        if (queryTransaction.getCount() > 0) {
            try {
                queryTransaction.moveToFirst();
                objectRequest       = new JSONObject(queryTransaction.getString(0));
                minimum_total       = objectRequest.getDouble("minimum_amount");
                total_transaction   = objectRequest.getDouble("total_price");
                total_discount      = objectRequest.getDouble("total_discount");
                lblTotalTransaction.setText("₱ "+utilities.convertToCurrency(String.valueOf(total_transaction)));
                lblTotalDiscount.setText("₱ "+utilities.convertToCurrency(String.valueOf(total_discount)));
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        handler.close();
        validateAction();
    }

    private void getPremiereLogs(){

        String token                    = utilities.getToken();
        String plc_url                  = SERVER_URL+"/api/mobile/getPLCDetails/true?token="+token;
        JsonObjectRequest objectRequest  = new JsonObjectRequest
                (Request.Method.GET, plc_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (ifVisible == true){
                            try {
                                JSONArray arrayPLCLogs     = response.getJSONArray("application");
                                JSONArray arrayReviewLogs  = response.getJSONArray("request");
                                handler         = new DataHandler(getActivity());
                                handler.open();
                                Cursor c = handler.returnPLCApplicationAndReviewLogs();
                                if(c.getCount() > 0){
                                    handler.updatePLCApplicationAndReviewLogs(arrayPLCLogs.toString(),arrayReviewLogs.toString());
                                }
                                else{
                                    handler.insertPLCApplicationAndReviewLogs(arrayPLCLogs.toString(),arrayReviewLogs.toString());
                                }
                                handler.close();
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
                        8000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
    }


    public void initializeRequestResponse() {

        handler.open();
        Cursor queryTransaction = handler.returnTotalTransactions();
        if(queryTransaction.getCount() > 0){
            try {
                queryTransaction.moveToFirst();
                objectRequest           =  new JSONObject(queryTransaction.getString(0));
                minimum_total           = objectRequest.getDouble("minimum_amount");
                total_transaction       = objectRequest.getDouble("total_price");
                total_discount          = objectRequest.getDouble("total_discount");
                lblTotalTransaction.setText("₱ "+utilities.convertToCurrency(String.valueOf(total_transaction)));
                lblTotalDiscount.setText("₱ "+utilities.convertToCurrency(String.valueOf(total_discount)));

                Cursor cursorQueryPremier = handler.returnPLCApplicationAndReviewLogs();
                if(cursorQueryPremier.getCount() > 0){
                    cursorQueryPremier.moveToFirst();
                    JSONArray arrayPremier     = new JSONArray(cursorQueryPremier.getString(0));
                    for (int x = 0; x < arrayPremier.length(); x++){
                        objectPremier              = arrayPremier.getJSONObject(x);
                        break;
                    }
                }

                if(total_transaction >= minimum_total){

                    if(objectPremier.length() > 0){
                        String status  = objectPremier.getString("status");
                        String remarks = objectPremier.getString("remarks");
                        lblStatus.setTextColor(getResources().getColor(R.color.laybareGreen));
                        lblStatus.setText(status.toUpperCase());
                        if(status.equals("denied")){
                            lblStatus.setTextColor(getResources().getColor(R.color.themeRed));
                            lblCaption.setText("*" + utilities.getPLCCaption(7) + " " + remarks);
                            btnAction.setVisibility(View.VISIBLE);
                            btnAction.setText("Apply For Premiere Loyalty Card");
                            btnAction.setEnabled(true);
                            btnAction.setAlpha(1);
                            stringParams = "PLCApplication";
                        }
                        else if(status.equals("approved")){
                            lblStatus.setTextColor(getResources().getColor(R.color.a_info));
                            lblCaption.setText("*"+utilities.getPLCCaption(2));
                            btnAction.setVisibility(View.GONE);
                        }
                        else if(status.equals("processing")){
                            lblStatus.setTextColor(getResources().getColor(R.color.caution));
                            lblCaption.setText("*"+utilities.getPLCCaption(3));
                            btnAction.setVisibility(View.GONE);
                        }
                        else if(status.equals("delivery")){
                            lblStatus.setTextColor(getResources().getColor(R.color.caution));
                            lblCaption.setText("*"+utilities.getPLCCaption(4));
                            btnAction.setVisibility(View.GONE);
                        }
                        else if(status.equals("ready")){
                            lblStatus.setTextColor(getResources().getColor(R.color.laybareGreen));
                            lblCaption.setText("*"+utilities.getPLCCaption(5));
                            btnAction.setVisibility(View.GONE);
//                            btnAction.setText("Set as Picked-Up");
//                            btnAction.setEnabled(false);
//                            btnAction.setAlpha(0/5);
                            stringParams = "MarkedAsReceived";
                        }
                        else if(status.equals("picked_up")){
                            lblStatus.setTextColor(getResources().getColor(R.color.laybareGreen));
                            lblCaption.setText("*"+utilities.getPLCCaption(6));
                            btnAction.setVisibility(View.VISIBLE);
                            btnAction.setText("Apply For Card Replacement");
                            btnAction.setEnabled(true);
                            btnAction.setAlpha(1);
                            stringParams = "MarkAsLost";
                        }
                        else if(status.equals("deleted")){
                            if(total_transaction >= minimum_total){
                                lblCaption.setText("*"+utilities.getPLCCaption(1));
                                lblStatus.setTextColor(getResources().getColor(R.color.laybareGreen));
                                lblStatus.setText("Qualified for Premier Loyalty Card");
                                lblStatus.setTextColor(getResources().getColor(R.color.laybareGreen));
                                btnAction.setVisibility(View.VISIBLE);
                                btnAction.setText("Apply For Premiere Loyalty Card");
                                btnAction.setEnabled(true);
                                btnAction.setAlpha(1);
                                stringParams = "PLCApplication";
                            }
                            else{
                                stringParams = "TransactionRequest";
                                lblStatus.setTextColor(getResources().getColor(R.color.themeRed));
                                lblStatus.setText("Not yet Qualified");
                                lblCaption.setText("*"+utilities.getPLCCaption(0));
                                btnAction.setEnabled(true);
                                btnAction.setAlpha(1);
                                btnAction.setText("Request For Transaction Review");
                            }
                        }
                    }
                    else{
                        lblCaption.setText("*"+utilities.getPLCCaption(1));
                        lblStatus.setTextColor(getResources().getColor(R.color.laybareGreen));
                        lblStatus.setText("Qualified for Premier Loyalty Card");
                        lblStatus.setTextColor(getResources().getColor(R.color.laybareGreen));
                        btnAction.setVisibility(View.VISIBLE);
                        btnAction.setText("Apply For Premiere Loyalty Card");
                        btnAction.setEnabled(true);
                        btnAction.setAlpha(1);
                        stringParams = "PLCApplication";
                    }
                }
                else{
                    stringParams = "TransactionRequest";
                    lblStatus.setTextColor(getResources().getColor(R.color.themeRed));
                    lblStatus.setText("Not yet Qualified");
                    lblCaption.setText("*"+utilities.getPLCCaption(0));
                    btnAction.setEnabled(true);
                    btnAction.setAlpha(1);
                    btnAction.setText("Request For Transaction Review");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            handler.close();
        }
        else{
            handler.close();
            utilities.showDialogMessage("Connection Error!","Cannot load your transactions, please check your connection and try again.","error");
            lblStatus.setText("Connection Lost!\nSwipe to refresh");
        }
        linear_transaction_loading.setVisibility(View.GONE);
        linear_transaction_status.setVisibility(View.VISIBLE);
        utilities.hideProgressDialog();
        handler.close();
    }

    private void setActionForButton(String stringParams){
        Intent intent = new Intent(getActivity(),PLCRequests.class);
        intent.putExtra("category",stringParams);
        getActivity().startActivity(intent);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        ifVisible = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        ifVisible = false;
    }
}
