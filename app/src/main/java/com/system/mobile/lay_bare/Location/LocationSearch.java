package com.system.mobile.lay_bare.Location;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Mark on 29/11/2017.
 */

public class LocationSearch extends AppCompatActivity implements LocationSearchAdapter.CallbackInterface {

    EditText txtSearch;
    TextView forTitle,lblResult;
    RecyclerView recycler_branch;
    LinearLayout linear_loading,linear_content;
    private static final int REQUEST_SEARCH = 3;

    JSONArray arrayBranch;

    ArrayList<JSONObject> arrayListObj      = new ArrayList<>();
    ArrayList<JSONObject> arrayListHolder   = new ArrayList<>();
    ArrayList<Integer> arrayListID          = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_search);
        setToolbar();

        recycler_branch    = (RecyclerView) findViewById(R.id.recycler_branch);
        lblResult          = (TextView)findViewById(R.id.lblResult);
        linear_loading     = (LinearLayout)findViewById(R.id.linear_loading);
        linear_content     = (LinearLayout)findViewById(R.id.linear_content);


        txtSearch = (EditText)findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("Status","beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("Status","onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                goLoading();
                filterData(String.valueOf(s));
                Log.e("Status","afterTextChanged");
            }
        });

        getExtra();

    }

    private void getExtra() {
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            try {
                arrayBranch      = new JSONArray(bundle.getString("branches"));
                sortArrayList();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void goLoading(){
        linear_loading.setVisibility(View.VISIBLE);
        linear_content.setVisibility(View.GONE);
    }
    void stopLoading(){
        linear_content.setVisibility(View.VISIBLE);
        linear_loading.setVisibility(View.GONE);
    }

    public void sortArrayList(){

        for(int x = 0; x < arrayBranch.length();x++){
            try {
                JSONObject jsonObject   = arrayBranch.getJSONObject(x);
                int id                  = jsonObject.getInt("id");
                arrayListID.add(x,id);
                arrayListObj.add(x,jsonObject);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        arrayListHolder = arrayListObj;
        displayAdapter();
    }


    private void filterData(String s) {


        if(s.length() == 0){
            arrayListHolder = arrayListObj;
            displayAdapter();
        }
        else{
            arrayListHolder = new ArrayList<>();
            for (int y = 0; y < arrayListObj.size(); y++){
                try {
                    JSONObject object   = arrayListObj.get(y);

                    String branch_id    =  object.getString("id");
                    String index        =  object.getString("branch_name");
                    String name         =  object.getString("branch_name");
                    String address      =  object.getString("branch_address");

                    if(name.toLowerCase().contains(s.toLowerCase()) == true || address.toLowerCase().contains(s.toLowerCase()) == true){
                        arrayListHolder.add(arrayListObj.get(y));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(arrayListHolder.size() == 0){
                lblResult.setVisibility(View.VISIBLE);
            }
            else{
                lblResult.setVisibility(View.GONE);
            }
            displayAdapter();
        }
    }



    private void displayAdapter() {
        Collections.sort(arrayListHolder, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    double s1 = o1.getDouble("distance");
                    double s2 = o2.getDouble("distance");
                    return Double.compare(s1, s2);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        recycler_branch.setAdapter( new LocationSearchAdapter(this,arrayListHolder));
        recycler_branch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_branch.setItemAnimator(new DefaultItemAnimator());
        recycler_branch.setNestedScrollingEnabled(false);
        stopLoading();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    private void setToolbar() {
        txtSearch   = (EditText) findViewById(R.id.txtSearch);
        forTitle    = (TextView) findViewById(R.id.forTitle);
        forTitle.setVisibility(View.GONE);
        txtSearch.setVisibility(View.VISIBLE);
    }


    //handle selection - triggered by recyclerAdapter viewHolder Onclick
    @Override
    public void onHandleSelection(int positionOfRecycler) {

        JSONObject object = arrayListHolder.get(positionOfRecycler);
        int selectedIndex = 0;
        try {
            selectedIndex = arrayListID.indexOf(object.getInt("id"));

            Log.e("positionOfRecycler", String.valueOf(positionOfRecycler));
            Log.e("Actual Position of array", String.valueOf(selectedIndex));

            Intent resultIntent = new Intent();
            resultIntent.putExtra("position", selectedIndex);
            setResult(RESULT_OK, resultIntent);
            finish();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
