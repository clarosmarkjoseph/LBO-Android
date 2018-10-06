package com.system.mobile.lay_bare.Location;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.ChatMessage.ChatMessage;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.NavigationDrawer.NavigationDrawerActivity;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Transactions.Appointment;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 01/12/2017.
 */

public class OverviewFragment extends Fragment {
    View view;
    LinearLayout linear_call,linear_message,linear_book,linear_share;
    RecyclerView recycler_branch_details;
    RecyclerView.LayoutManager recycler_branch_details_layoutManager;
    RecyclerView.Adapter recycler_branch_details_adapter;
    LocationClassSingleton locationClassSingleton;
    int branch_id = 0;
    private String clientID;
    Utilities utilities;
    String SERVER_URL = "",branch_name = "",branch_contact_no = "";
    private ArrayList<String> arrayErrorResponse;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.location_overview_fragment, container, false);
        initElements();
        return view;
    }

    private void initElements() {
        utilities                   = new Utilities(getActivity());
        recycler_branch_details     = (RecyclerView)view.findViewById(R.id.recycler_branch_details);
        linear_call                 = (LinearLayout)view.findViewById(R.id.linear_call);
        linear_message              = (LinearLayout)view.findViewById(R.id.linear_message);
        linear_book                 = (LinearLayout)view.findViewById(R.id.linear_book);
        linear_share                = (LinearLayout)view.findViewById(R.id.linear_share);
        locationClassSingleton      = new LocationClassSingleton();
        SERVER_URL                  = utilities.returnIpAddress();
        clientID                    = utilities.getClientID();
        getBranchDetails();

        linear_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHeaderMethod(0);
            }
        });

        linear_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHeaderMethod(3);
            }
        });

        if (clientID == null){
            linear_book.setClickable(false);
            linear_message.setClickable(false);
            linear_message.setAlpha((float) 0.5);
            linear_message.setFocusable(false);
            linear_book.setFocusable(false);
            linear_book.setAlpha((float) 0.5);
        }
        else{
            linear_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getHeaderMethod(1);
                }
            });
            linear_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getHeaderMethod(2);
                }
            });
        }



    }

    private void getBranchDetails() {

        JSONObject objectBranch = locationClassSingleton.Instance().getBranchDetails();
        String[] getContactNo = new String[0];
        branch_id               = objectBranch.optInt("id",0);
        branch_name             = objectBranch.optString("branch_name","None");

        String contact          = objectBranch.optString("branch_contact","");
        if (contact.contains(" or ")){
            getContactNo            = contact.split(" or ");
            branch_contact_no       = getContactNo[1];
        }
        if (contact.contains(" / ")){
            getContactNo            = contact.split(" / ");
            branch_contact_no       = getContactNo[1];
        }
        if (contact.contains("; ")){
            getContactNo            = contact.split("; ");
            branch_contact_no       = getContactNo[1];
        }
        if (contact.contains(" and ")){
            getContactNo            = contact.split(" and ");
            branch_contact_no       = getContactNo[1];
        }
        if (contact.contains(" / Land-line - ")){
            getContactNo            = contact.split(" / Land-line - ");
            branch_contact_no       = getContactNo[1];
        }
        if (contact.contains("/")){
            getContactNo            = contact.split("/");
            branch_contact_no       = getContactNo[1];
        }
        else{
            branch_contact_no       = contact;
        }
        setBranchDetails();
    }

    private void getHeaderMethod(int i) {

        if(i == 0){
            try{
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", branch_contact_no, null));
                startActivity(intent);
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(),"Sorry, cannot make a call right now.",Toast.LENGTH_SHORT).show();
            }
        }
        if(i == 1){

            utilities.showProgressDialog("Loading contact....");
            getUsersDetails();

        }
        if(i == 2){
            Intent intent = new Intent(getActivity(),Appointment.class);
            getActivity().startActivity(intent);

        }
        if(i == 3){
            openFacebookApp();
        }
    }


    //open Lay Bare Facebook Fan Page
    private void openFacebookApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/7037766039"));
            startActivity(intent);
        }
        catch(Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/OfficialLayBare/")));
        }
    }


    private void getUsersDetails() {

        final JSONObject objectBranch = locationClassSingleton.Instance().getBranchDetails();
        String email            = objectBranch.optString("branch_email","N/A");
        String token            = utilities.getToken();
        final String branch_name  = objectBranch.optString("branch_name","N/A");
        if(email.equals("N/A")){
            utilities.hideProgressDialog();
            utilities.showDialogMessage("No Attendant available","Sorry, there is no attendant available. For more inquiries, please email us at customercare@lay-bare.com.","info");
            return;
        }
        else{
            email        = email.replace(" ","%20");
            String url   = SERVER_URL+"/api/mobile/contactBranchSupervisor/"+branch_id+"/"+email+"?token="+token;
            Log.e("url:",url);
            StringRequest stringRequest  = new StringRequest
                    (Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("overview",response);
                            utilities.hideProgressDialog();
                            if (response.equals("false")){
                                utilities.showDialogMessage("No Attendant available","Sorry, there is no attendant available. For more inquiries, please email us at customercare@lay-bare.com.","info");
                                return;
                            }
                            else{
                                try {

                                    JSONObject objectParams = new JSONObject(response);
                                    JSONObject objectDetails = objectParams.optJSONObject("thread_details");

                                    if (objectDetails.isNull("id")) {
                                        utilities.showDialogMessage("No Attendant available", "Sorry, there is no attendant available. For more inquiries, please email us at customercare@lay-bare.com.", "info");
                                        return;
                                    }
                                    else {

                                        int recipient_id    = objectParams.optInt("recipient_id", 0);
                                        int thread_id       = objectParams.optInt("thread_id", 0);
                                        String thread_name  = objectDetails.optString("thread_name");

                                        Log.e("res before intent",String.valueOf(recipient_id));
                                        Intent intent = new Intent(getActivity(),ChatMessage.class);
                                        intent.putExtra("recipient_id",recipient_id);
                                        intent.putExtra("userName",thread_name);
                                        intent.putExtra("thread_id",thread_id);
                                        intent.putExtra("chat_type","message");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
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
                            utilities.hideProgressDialog();
                            arrayErrorResponse = utilities.errorHandling(error);
                            Toast.makeText(getActivity(),arrayErrorResponse.get(1).toString(),Toast.LENGTH_LONG).show();
                            return;
                        }
                    });
            stringRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            10000,
                            3,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        }
    }

    private void setBranchDetails(){
        recycler_branch_details.setNestedScrollingEnabled(false);
        recycler_branch_details_layoutManager = new LinearLayoutManager(getActivity());
        recycler_branch_details.setLayoutManager(recycler_branch_details_layoutManager);
        recycler_branch_details_adapter = new RecyclerBranchDetails(getActivity());
        recycler_branch_details.setAdapter(recycler_branch_details_adapter);
    }


}
