package com.system.mobile.lay_bare.Transactions;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.DetectionConnection;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Mark on 26/10/2017.
 */

public class FragmentAppointment extends Fragment {

    View layout;
    String SERVER_URL   = "";
    String clientID     = "";
    Utilities utilities;
    ArrayList<JSONObject> arrayAppointments;
    boolean isLoaded = false;
    DataHandler handler;
    private ArrayList<String> arrayErrorResponse;
    RecyclerView recyclerAppointment;
    RecyclerView.LayoutManager recyclerAppointment_layoutManager;
    RecyclerView.Adapter recyclerAppointment_adapter;
    SwipeRefreshLayout swipeRefresh;
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListner;
    TextView lblStatus;
    static boolean ifVisible = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_appointment, container, false);
        initElements();
        return layout;
    }

    private void initElements() {
        arrayAppointments       = new ArrayList<>();
        arrayErrorResponse      = new ArrayList<>();
        utilities               = new Utilities(getActivity());
        handler                 = new DataHandler(getActivity());
        SERVER_URL              = utilities.returnIpAddress();
        clientID                = utilities.getClientID();
        lblStatus               = (TextView) layout.findViewById(R.id.lblStatus);
        swipeRefresh            = (SwipeRefreshLayout)layout.findViewById(R.id.swipeRefresh);
        utilities.returnRefreshColor(swipeRefresh);
        recyclerAppointment          = (RecyclerView)layout.findViewById(R.id.recyclerOngoing_Appointment);
        recyclerAppointment.setHasFixedSize(false);
        swipeConfiguration();
        swipeRefreshListner.onRefresh();
    }

    private void requestAppointments() {

        if(!DetectionConnection.isNetworkAvailable(getActivity())){
            displayAppointments();
        }
        else{
            String url = SERVER_URL+"/api/appointment/getAppointments/client/"+clientID+"/active";
            JsonArrayRequest jsObjRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {

                                for (int x = 0; x < response.length(); x++){
                                    handler.open();
                                    JSONObject objectResult     = response.getJSONObject(x);
                                    final int transaction_id    = objectResult.getInt("id");
                                    String dateTime             = objectResult.getString("transaction_datetime");
                                    String transaction_status   = objectResult.getString("transaction_status");
                                    JSONArray arrayItems        = objectResult.getJSONArray("items");
                                    Cursor cursorAppointment    = handler.returnSpecificAppointments(String.valueOf(transaction_id));
                                    if(cursorAppointment.getCount() > 0){
                                        handler.updateAppointments(String.valueOf(transaction_id),utilities.removeTimeFromDate(dateTime),transaction_status,objectResult.toString(),arrayItems.toString());
                                    }
                                    else{
                                        handler.insertAppointments(String.valueOf(transaction_id),utilities.removeTimeFromDate(dateTime),transaction_status,objectResult.toString(),arrayItems.toString());
                                    }
                                    handler.close();
                                }
                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }
                            displayAppointments();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("ERROR",error.toString());
                            arrayErrorResponse = utilities.errorHandling(error);
                            utilities.showDialogMessage("Connection Error",arrayErrorResponse.get(1).toString(),"error");
                            displayAppointments();
                        }
                    });
            MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
        }

    }



    private void displayAppointments() {

        arrayAppointments.clear();
        if(ifVisible == true){
            handler.open();
            Cursor query = handler.returnStatusAppointments("reserved", true);
            while(query.moveToNext()){
                try {
                    JSONObject objectAppointment = new JSONObject(query.getString(3));
                    arrayAppointments.add(objectAppointment);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            handler.close();
            appointmentList();
        }
    }

    public void appointmentList(){

        Collections.sort(arrayAppointments, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    String  stringDate1 = o1.getString("transaction_datetime");
                    String  stringDate2 = o2.getString("transaction_datetime");
                    Date date1          = utilities.convertStringToDateTime(stringDate1);
                    Date date2          = utilities.convertStringToDateTime(stringDate2);
//                    return (d1.getTime() > d2.getTime() ? 1 : -1); //ascending
                    return (date1.getTime() > date2.getTime() ? -1 : 1); //descending
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        if(arrayAppointments.size() <= 0){
            lblStatus.setVisibility(View.VISIBLE);
        }
        else{
            lblStatus.setVisibility(View.GONE);
        }
        recyclerAppointment_adapter       = new RecyclerOnGoingAppointment(getActivity(),arrayAppointments);
        recyclerAppointment_layoutManager = new LinearLayoutManager(getActivity());
        recyclerAppointment.setHasFixedSize(true);
        recyclerAppointment.setAdapter(recyclerAppointment_adapter);
        recyclerAppointment.setLayoutManager(recyclerAppointment_layoutManager);
        recyclerAppointment.setItemAnimator(new DefaultItemAnimator());
        swipeRefresh.setRefreshing(false);

    }

    private void swipeConfiguration() {
        swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                requestAppointments();
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
    public void onStart() {
        super.onStart();
        ifVisible = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        ifVisible = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && !isLoaded){
            isLoaded = true;
            Handler hander = new Handler();
            hander.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initElements();
                }
            }, 500);

        }
    }




}



