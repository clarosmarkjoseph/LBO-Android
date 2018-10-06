package com.system.mobile.lay_bare.ClientTransactions;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Mark on 24/11/2017.
 */

public class ClientTransactions extends AppCompatActivity {

    TextView forTitle,lblNoTransaction;
    private Typeface myTypeface;
    private Utilities utilities;
    LinearLayout linear_content_no_internet,linear_loading,linear_content;
    String SERVER_URL,client_id,client_email;
    FrameLayout frameTransaction;
    ImageButton imgNoInternet;
    RecyclerView recyclerLogs;
    RecyclerView.LayoutManager recyclerLogs_layout;
    RecyclerView.Adapter recyclerLogs_adapter;
    NestedScrollView nestedScrollView;
    boolean isLoading = false;
    boolean hasMore   = true;
    ArrayList<JSONObject> arrayListTransactionLogs;
    private ImageButton imgBtnBack;
    DataHandler handler;
    int localOffset         = 0;
    SwipeRefreshLayout swipRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListner;
    static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_transaction);
        setToolbar();
        utilities               = new Utilities(this);
        SERVER_URL              = utilities.getClientEmail();
        initElements();
    }


    private void initElements() {

        handler                     = new DataHandler(getApplicationContext());
        utilities                   = new Utilities(getApplicationContext());
        SERVER_URL                  = utilities.returnIpAddress();
        client_id                   = utilities.getClientID();
        client_email                = utilities.getClientEmail();
        frameTransaction            = (FrameLayout)findViewById(R.id.frameTransaction);
        imgNoInternet               = (ImageButton)findViewById(R.id.imgNoInternet);
        linear_content              = (LinearLayout)findViewById(R.id.linear_content);
        linear_content_no_internet  = (LinearLayout)findViewById(R.id.linear_content_no_internet);
        linear_loading              = (LinearLayout)findViewById(R.id.linear_loading);
        lblNoTransaction            = (TextView) findViewById(R.id.lblNoTransaction);
        recyclerLogs                = (RecyclerView)findViewById(R.id.recycler_transactions);
        arrayListTransactionLogs    = new ArrayList<JSONObject>();
        nestedScrollView            = (NestedScrollView)findViewById(R.id.nestedScrollView);
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView.getScrollY()));
                if (diff == 0) {
                    if(hasMore == true){
                        if(isLoading == false){
                            isLoading = true;
                            loadLogs();
                            setTransactions();
                        }
                    }
                }
            }
        });
        swipRefreshLayout   = (SwipeRefreshLayout)findViewById(R.id.swipRefreshLayout);
        utilities.returnRefreshColor(swipRefreshLayout);
        swipeConfiguration();
        loadFragments(false);
    }

    private void swipeConfiguration() {

        swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipRefreshLayout.setRefreshing(true);

                loadFragments(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipRefreshLayout.setRefreshing(false);
                    }
                },4000);
            }
        };
        swipRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshListner.onRefresh();
            }
        });
    }

    private void loadFragments(boolean ifRestarted) {
        if (ifRestarted == true){

        }
        else{
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameTransaction, new FragmentTotalTransaction());
            fragmentTransaction.commit();
        }
        loadLogs();
        new getTransactions().execute();
        setAdapter();
    }






    class getTransactions extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            getTransactionLogs();
            return "yes";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setTransactions();
        }
    }


    private void getTransactionLogs() {

        handler.open();
        Cursor queryUser = handler.returnUserAccount();
        if (queryUser.getCount() > 0){
            queryUser.moveToFirst();
            try {
                JSONObject objectUser       = new JSONObject(queryUser.getString(1));
                JSONArray arrayTransaction  = new JSONArray(objectUser.getString("transaction_data"));

                for (int x = 0; x < arrayTransaction.length(); x++){

                    JSONObject objectParams          = arrayTransaction.getJSONObject(x);
                    int tr_id                        = objectParams.optInt("transaction_id");
                    int type                         = objectParams.optInt("type",0);
                    String dateTime                  = objectParams.optString("date","0000-00-00");

                    Cursor cursorLogs                = handler.returnTransactionLogsID(tr_id);
                    int countRes                     = cursorLogs.getCount();
                    if(cursorLogs != null) {
                        cursorLogs.close();
                    }
                    try{
                        if (countRes > 0){
                            handler.updateTransactionLogs(tr_id,type,dateTime,objectParams);
                        }
                        else{
                            handler.insertTransactionLogs(tr_id,type,dateTime,objectParams);
                        }
                    }
                    catch (Exception e){
                        Log.e("error", "err->" + e.getLocalizedMessage());
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        handler.close();
    }

    private void setTransactions() {

        if (active == true){
            handler.open();
            Cursor cursorQuery = handler.returnTransactionLogs(localOffset);
            localOffset+= cursorQuery.getCount();
            if(cursorQuery.getCount() > 0){
                while(cursorQuery.moveToNext()){
                    try {
                        String stringQuery  = cursorQuery.getString(3);
                        JSONObject myObject = new JSONObject(stringQuery);
                        arrayListTransactionLogs.add(myObject);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            handler.close();

            isLoading = false;
            if (arrayListTransactionLogs.size() > 0){
                lblNoTransaction.setVisibility(View.GONE);
                recyclerLogs.setVisibility(View.VISIBLE);
            }
            else{
                lblNoTransaction.setVisibility(View.VISIBLE);
                recyclerLogs.setVisibility(View.GONE);
            }
            recyclerLogs_adapter.notifyDataSetChanged();
            unloadLogs();

        }
    }


    private void setAdapter(){
        recyclerLogs_adapter        = new RecyclerTransactionLog(ClientTransactions.this,arrayListTransactionLogs);
        recyclerLogs_layout         = new LinearLayoutManager(getApplicationContext());
        recyclerLogs.setNestedScrollingEnabled(false);
        recyclerLogs.setHasFixedSize(false);
        recyclerLogs.setLayoutManager(recyclerLogs_layout);
        recyclerLogs.setItemAnimator(new DefaultItemAnimator());
        recyclerLogs.setAdapter(recyclerLogs_adapter);
    }



    void loadLogs(){
        linear_loading.setVisibility(View.VISIBLE);
    }

    void unloadLogs(){
        linear_loading.setVisibility(View.GONE);
    }


    private void setToolbar() {
        myTypeface          = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle            = (TextView)findViewById(R.id.forTitle);
        imgBtnBack          = (ImageButton) findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forTitle.setTypeface(myTypeface);
        forTitle.setText("My transaction");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }
}
