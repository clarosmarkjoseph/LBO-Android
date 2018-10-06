package com.system.mobile.lay_bare.GeneralActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.GeneralActivity.Adapter.TechnicianAdapter;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.TextView;

import static com.android.volley.toolbox.ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT;

/**
 * Created by Mark on 04/10/2017.
 */

public class TechnicianSelection extends AppCompatActivity {

    JSONArray jsonArray       = new JSONArray();
    JSONArray jsonArrayHolder = new JSONArray();
    ListView listView_tech;
    TechnicianAdapter technicianAdapter;
    Utilities utilities;
    String SERVER_URL = "";
    String tech_id,tech_name,tech_start,tech_end = "";
    EditText txtSearch;
    LinearLayout linear_content_no_internet,linear_loading,linear_content;
    ImageButton imgNoInternet;
    TextView lblRemarks;
    String branch_id    = "";
    String app_reserved = "";
    DataHandler handler;
    private ArrayList<String> arrayErrorResponse;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technician_selection);
        initElements();
    }

    void initElements(){

        handler         = new DataHandler(getApplicationContext());
        utilities       = new Utilities(this);
        SERVER_URL      = utilities.returnIpAddress();
        txtSearch       = (EditText)findViewById(R.id.txtSearchTech);
        listView_tech   = (ListView)findViewById(R.id.listView_tech);
        linear_content_no_internet  = (LinearLayout)findViewById(R.id.linear_content_no_internet);
        linear_loading              = (LinearLayout)findViewById(R.id.linear_loading);
        linear_content              = (LinearLayout)findViewById(R.id.linear_content);
        imgNoInternet               = (ImageButton)findViewById(R.id.imgNoInternet);
        lblRemarks                  = (TextView)findViewById(R.id.lblRemarks);
        listView_tech.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                try {
                    JSONObject object   = jsonArrayHolder.getJSONObject(position);
                    String tech_id      = object.getString("id");
                    intent.putExtra("technician_object",object.toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(String.valueOf(s));
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        imgNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTechnicians();
            }
        });

        getExtraData();

    }

    public void getExtraData(){

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            branch_id       = bundle.getString("branch_id");
            app_reserved    = bundle.getString("app_reserved");
            getTechnicians();
        }
        else{
            hideLoadingShowNoInternet();
        }
    }

    void getTechnicians(){

        handler.open();
        Cursor queryBranch = handler.returnBranchSchedule(branch_id);
        if(queryBranch.getCount() > 0) {
            while (queryBranch.moveToNext()) {
                try {
                    JSONArray arrayTechnicianSchedule = new JSONArray(queryBranch.getString(2));
                    String time_updated               = queryBranch.getString(3);
                    if(utilities.getCurrentDate().equals(time_updated)){
                        jsonArray       = arrayTechnicianSchedule;
                        jsonArrayHolder = arrayTechnicianSchedule;
                        displayTechnicians();
                    }
                    else{
                        searchTechnician();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            searchTechnician();
        }
        handler.close();
    }

    private void searchTechnician() {

        showLoadingHideNoInternet();
        utilities.showProgressDialog("Loading Technicians....");
        String branch_url = SERVER_URL+"/api/mobile/getBranchSchedules/"+branch_id+"/"+app_reserved;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, branch_url,null,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arrayBranchSchedule = response.getJSONArray("branch");
                            JSONArray arrayTechSchedule   = response.getJSONArray("technician");
                            handler.open();
                            Cursor queryBranch = handler.returnBranchSchedule(branch_id);
                            if(queryBranch.getCount() > 0){
                                handler.updateBranchSchedule(branch_id,arrayBranchSchedule.toString(),arrayTechSchedule.toString(),utilities.getCurrentDate());
                            }
                            else{
                                handler.insertBranchSchedule(branch_id,arrayBranchSchedule.toString(),arrayTechSchedule.toString(),utilities.getCurrentDate());
                            }
                            handler.close();

                            jsonArray       = arrayTechSchedule;
                            jsonArrayHolder = arrayTechSchedule;
                            utilities.hideProgressDialog();
                            showContent();
                            displayTechnicians();
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
                        hideLoadingShowNoInternet();
                    }
                });

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                7000,
                3,
                DEFAULT_IMAGE_BACKOFF_MULT));

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }


    private void displayTechnicians() {
        if (jsonArrayHolder.length() <= 0){
            lblRemarks.setVisibility(View.VISIBLE);
            lblRemarks.setGravity(Gravity.CENTER);
            lblRemarks.setText("Sorry, no matching options.");
            listView_tech.setVisibility(View.GONE);
        }
        else{
            listView_tech.setVisibility(View.VISIBLE);
        }

        technicianAdapter = new TechnicianAdapter(this,jsonArrayHolder);
        listView_tech.setAdapter(technicianAdapter);
        if (jsonArrayHolder.length() > 0){
            lblRemarks.setVisibility(View.GONE);
        }
        else{
            lblRemarks.setVisibility(View.VISIBLE);
        }
        showContent();
    }

    private void hideLoadingShowNoInternet(){
        linear_content.setVisibility(View.GONE);
        linear_loading.setVisibility(View.GONE);
        linear_content_no_internet.setVisibility(View.VISIBLE);
    }

    private void showLoadingHideNoInternet(){
        linear_content.setVisibility(View.GONE);
        linear_content_no_internet.setVisibility(View.GONE);
        linear_loading.setVisibility(View.VISIBLE);
    }

    private void showContent(){
        linear_content.setVisibility(View.VISIBLE);
        linear_loading.setVisibility(View.GONE);
        linear_content_no_internet.setVisibility(View.GONE);
    }

    private void filterData(String s) {

        if(s.length() == 0){
            jsonArrayHolder     = jsonArray;
            lblRemarks.setVisibility(View.GONE);
            technicianAdapter   = new TechnicianAdapter(getApplicationContext(),jsonArrayHolder);
            listView_tech.setAdapter(technicianAdapter);
        }
        else {
            jsonArrayHolder = new JSONArray();
            for (int y = 0; y < jsonArray.length(); y++) {
                try {
                    tech_id          = jsonArray.getJSONObject(y).getString("id");
                    tech_name        = jsonArray.getJSONObject(y).getString("name");
                    if (tech_name.toLowerCase().startsWith(s.toLowerCase()) == true) {
                        lblRemarks.setVisibility(View.GONE);
                        try {
                            jsonArrayHolder.put(jsonArray.get(y));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        lblRemarks.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            technicianAdapter   = new TechnicianAdapter(getApplicationContext(),jsonArrayHolder);
            listView_tech.setAdapter(technicianAdapter);
        }
    }


}
