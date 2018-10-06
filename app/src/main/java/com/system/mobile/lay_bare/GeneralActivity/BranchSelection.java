package com.system.mobile.lay_bare.GeneralActivity;


import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.TimerTask;

import com.system.mobile.lay_bare.GeneralActivity.Adapter.BranchAdapter;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;
import com.system.mobile.lay_bare.Classes.VersionClass;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Mark on 04/10/2017.
 */

public class BranchSelection extends AppCompatActivity {

    JSONArray arrayBranch   = new JSONArray();
    JSONArray arrayHolder   = new JSONArray();
    ListView listView_branch;
    BranchAdapter branchAdapter;
    Utilities utilities;
    String SERVER_URL = "";
    ArrayList<String> arrayErrorResponse = new ArrayList<>();
    EditText txtSearch;
    LinearLayout linear_content_no_internet,linear_loading,linear_content;
    ImageButton imgNoInternet;
    boolean isAppointment;
    DataHandler handler;
    VersionClass versionClass;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.branch_selection);
        initElements();

    }

    private void initElements() {
        utilities                   = new Utilities(this);
        handler                     = new DataHandler(getApplicationContext());
        versionClass                = new VersionClass(getApplicationContext());
        SERVER_URL                  = utilities.returnIpAddress();
        txtSearch                   = (EditText)findViewById(R.id.txtSearchBranch);
        listView_branch             = (ListView)findViewById(R.id.listView_branch);
        linear_content_no_internet  = (LinearLayout)findViewById(R.id.linear_content_no_internet);
        linear_loading              = (LinearLayout)findViewById(R.id.linear_loading);
        linear_content              = (LinearLayout)findViewById(R.id.linear_content);
        imgNoInternet               = (ImageButton)findViewById(R.id.imgNoInternet);

        imgNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOfflineBranches();
            }
        });

        listView_branch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
//                    JSONObject object = arrayHolder.getJSONObject(position);
//                    String branch_id  = object.getString("id");
//                    String branchIndex = arrayBranch.has
//                    Toast.makeText(getApplicationContext(),"HEHE BRANCH ID: "+branch_id,Toast.LENGTH_SHORT).show();
                    JSONObject object   = arrayHolder.getJSONObject(position);
                    Intent intent       = new Intent();
                    intent.putExtra("branch_object",object.toString());
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
        getExtra();

    }

    void getExtra(){
        Bundle bundle = getIntent().getExtras();
        if(bundle   !=  null){
            isAppointment = true;
        }
        else{
            isAppointment = false;

        }
//        getBranches();
        getOfflineBranches();
    }

//    private void getBranches() {
//
//        arrayBranch = new JSONArray();
//        arrayHolder = new JSONArray();
//        showLoadingHideNoInternet();
//        String branch_url = SERVER_URL+"/api/branch/getBranches/active";
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
//                (Request.Method.GET, branch_url, null, new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        JSONObject j = null;
//                        handler.open();
//                        handler.insertBranches(String.valueOf(versionClass.returnBranchesVersion()),response.toString());
//                        handler.close();
//                        getOfflineBranches();
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        arrayErrorResponse = utilities.errorHandling(error);
//                        getOfflineBranches();
//                    }
//                });
//        jsonArrayRequest.setRetryPolicy(
//                new DefaultRetryPolicy(
//                        10000,
//                        0,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                )
//        );
//        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
//
//    }

    private void getOfflineBranches() {

        handler.open();
        Cursor queryBranch = handler.returnBranches();
        if(queryBranch.getCount() > 0){
            queryBranch.moveToFirst();
            final String getBranches = queryBranch.getString(1);
            runOnUiThread(new TimerTask() {
                @Override
                public void run() {
                    try {
                        arrayBranch = new JSONArray(getBranches);
                        arrayHolder = new JSONArray(getBranches);
                        if(arrayBranch.length() > 0){
                            generateListviewContent();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Sorry, Branch is no no no",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        handler.close();
    }

    public void generateListviewContent(){

        branchAdapter   = new BranchAdapter(getApplicationContext(),arrayHolder);
        listView_branch.setAdapter(branchAdapter);
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
            arrayHolder = arrayBranch;
            branchAdapter   = new BranchAdapter(getApplicationContext(),arrayHolder);
            listView_branch.setAdapter(branchAdapter);
        }
        else{
            arrayHolder = new JSONArray();
            for (int y = 0; y < arrayBranch.length(); y++){
                try {
                    String id   = arrayBranch.getJSONObject(y).getString("id");
                    String name = arrayBranch.getJSONObject(y).getString("branch_name");
                    String desc = arrayBranch.getJSONObject(y).getString("branch_address");
                    if(name.toLowerCase().contains(s.toLowerCase()) == true || desc.toLowerCase().contains(s.toLowerCase())){
                        try {
                            arrayHolder.put(arrayBranch.get(y));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            branchAdapter   = new BranchAdapter(getApplicationContext(),arrayHolder);
            listView_branch.setAdapter(branchAdapter);
        }
    }


}
