package com.system.mobile.lay_bare.Location;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.Queuing.Queuing;
import com.system.mobile.lay_bare.Queuing.RecyclerQueuing;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Transactions.AppointmentForm;
import com.system.mobile.lay_bare.Transactions.AppointmentSingleton;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

/**
 * Created by paolohilario on 1/12/18.
 */

public class LocationQueuingFragment extends Fragment {

    Utilities utilities;
    String SERVER_URL;
    TextView lblCountServing,lblCountAvailable,lblEmptyQueuing,lblQueueCount,lblBranch;
    JSONArray arrayQueuing = new JSONArray(),arrayServing = new JSONArray(),arrayCalling = new JSONArray(),arrayMyAppointment = new JSONArray();
    int roomCount       = 0;
    int remainingCount  = 0;
    Button btnAddAppointment;
    boolean ifFirstLoad = true;
    ArrayList<String > arrayResponse;
    View layout;
    RecyclerView recyclerQueuing;
    RecyclerView.LayoutManager recyclerLayoutManager;
    RecyclerView.Adapter recyclerQueuingAdapter;
    CardView cardviewSelectBranch;
    boolean ifVisible = false;
    LinearLayout linearContent;
    RelativeLayout relativeLoading;
    LinearLayout linearToolbar;
    int branch_id = 0;
    LocationClassSingleton locationClassSingleton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.queuing, container, false);
        return layout;
    }

    private void initElements() {

        utilities               = new Utilities(getActivity());
        linearToolbar           = (LinearLayout) layout.findViewById(R.id.linearToolbar);
        cardviewSelectBranch    = (CardView)layout.findViewById(R.id.cardviewSelectBranch);
        lblCountServing         = (TextView)layout.findViewById(R.id.lblCountServing);
        lblCountAvailable       = (TextView)layout.findViewById(R.id.lblCountAvailable);
        lblEmptyQueuing         = (TextView)layout.findViewById(R.id.lblEmptyQueuing);
        lblQueueCount           = (TextView)layout.findViewById(R.id.lblQueueCount);
        lblBranch               = (TextView)layout.findViewById(R.id.lblBranch);
        SERVER_URL              = utilities.returnIpAddress();
        recyclerQueuing         = (RecyclerView)layout. findViewById(R.id.recyclerQueuing);
        btnAddAppointment       = (Button)layout.findViewById(R.id.btnAddAppointment);
        linearContent           = (LinearLayout) layout.findViewById(R.id.linearContent);
        relativeLoading         = (RelativeLayout) layout.findViewById(R.id.relativeLoading);
        locationClassSingleton  = new LocationClassSingleton();
        cardviewSelectBranch.setVisibility(View.GONE);
        linearToolbar.setVisibility(View.GONE);

        JSONObject objectBranch = locationClassSingleton.Instance().getBranchDetails();
        branch_id               = objectBranch.optInt("id",0);
        roomCount               = objectBranch.optInt("rooms_count",0);

        if(ifFirstLoad == true){
            relativeLoading.setVisibility(View.VISIBLE);
            linearContent.setVisibility(View.GONE);
        }

        Log.e("branch_id", String.valueOf(branch_id));
        if (branch_id > 0){
            loadQueuedData();
        }
        else{
            relativeLoading.setVisibility(View.GONE);
            linearContent.setVisibility(View.VISIBLE);
        }

        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (utilities.getClientID() == null){
                  utilities.showDialogMessage("Aunthentication Required","Sorry, you must logged-in before you proceed in Online Appointment","info");
                  return;
              }
              else{
                  AppointmentSingleton appointmentSingleton = new AppointmentSingleton();
                  appointmentSingleton.Instance().setAppReserved(utilities.getCurrentDate());
                  Intent intent = new Intent(getActivity(), AppointmentForm.class);
                  startActivity(intent);
              }
            }
        });

    }



    private void loadQueuedData() {

        String booking_url = SERVER_URL+"/api/appointment/getAppointments/queue/"+branch_id+"/queue";
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, booking_url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        arrayQueuing = new JSONArray();
                        int resultCount = arrayQueuing.length();

                        try{
                            roomCount-=resultCount;
                            lblQueueCount.setText("Queue Count: "+resultCount);
                            for (int x = 0; x < response.length(); x++){
                                JSONObject objectResponse = response.getJSONObject(x);
                                String status             = objectResponse.getString("transaction_status");
                                if(status.equals("reserved")){
                                    arrayQueuing.put(objectResponse);
                                }
                            }
                            if(resultCount > 0){
                                recyclerQueuing.setVisibility(View.VISIBLE);
                                lblEmptyQueuing.setVisibility(View.GONE);
                                recyclerQueuingAdapter.notifyDataSetChanged();
                                getDataChanged();
                            }
                            else{
                                lblEmptyQueuing.setVisibility(View.VISIBLE);
                                recyclerQueuing.setVisibility(View.GONE);
                                if(ifFirstLoad == true){
                                    ifFirstLoad = false;
                                    utilities.hideProgressDialog();
                                }
                                lblCountServing.setText("0");
                                lblCountAvailable.setText(String.valueOf(roomCount));
                            }
                            setAdapter();

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        arrayResponse = utilities.errorHandling(error);
                        if(ifFirstLoad == true){
                            ifFirstLoad = false;
                            utilities.hideProgressDialog();
                        }
//                        utilities.showDialogMessage("Connection Error",arrayResponse.get(1).toString(),"error");
                    }
                });
        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }


    private void getDataChanged() {

        String booking_url = "http://lbo-express.azurewebsites.net/api/queuing/"+branch_id;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, booking_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("Object result queuing",response.toString()+" queue: "+ arrayQueuing);
                        try {
                            arrayServing            = response.getJSONArray("serving");
                            arrayCalling            = response.getJSONArray("calling");
                            recyclerQueuingAdapter = new RecyclerQueuing(getActivity(),arrayQueuing,arrayServing,arrayCalling);
                            recyclerQueuing.setAdapter(recyclerQueuingAdapter);

                            remainingCount = roomCount - arrayServing.length();
                            lblCountServing.setText(String.valueOf(arrayServing.length()));
                            if(remainingCount < 0){
                                lblCountAvailable.setText("0");
                            }
                            else{
                                lblCountAvailable.setText(String.valueOf(remainingCount));
                            }
                            if(ifFirstLoad == true){
                                ifFirstLoad = false;
                                utilities.hideProgressDialog();
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        arrayResponse = utilities.errorHandling(error);
                        Toast.makeText(getActivity(),arrayResponse.get(1).toString(),Toast.LENGTH_LONG).show();
                    }
                });
        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);

    }

    private void setAdapter(){

        relativeLoading.setVisibility(View.GONE);
        linearContent.setVisibility(View.VISIBLE);

        recyclerQueuingAdapter         = new RecyclerQueuing(getActivity(),arrayQueuing,arrayServing,arrayCalling);
        recyclerLayoutManager   = new LinearLayoutManager(getActivity());
        recyclerQueuing.setHasFixedSize(true);
        recyclerQueuing.setAdapter(recyclerQueuingAdapter);
        recyclerQueuing.setLayoutManager(recyclerLayoutManager);
        recyclerQueuing.setItemAnimator(new DefaultItemAnimator());
        recyclerQueuing.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }


    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible & ifVisible == false) {
            ifVisible = true;
            initElements();
        }
    }



}
