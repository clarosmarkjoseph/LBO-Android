package com.system.mobile.lay_bare.PLC;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
 * Created by Mark on 22/11/2017.
 */


public class PLCLogHistory extends AppCompatActivity {

    boolean isVisible;
    String SERVER_URL = "",client_ID = "";
    static boolean active = false;
    PremierApplicationAdapter viewPagerAdapter;
    DataHandler handler;
    ViewPager viewPager;
    TabLayout tabBarPLC;
    RelativeLayout relativeLoader;
    ArrayList<String> arrayErrorResponse = new ArrayList<>();
    Typeface myTypeface;
    TextView forTitle;
    Utilities utilities;
    private ImageButton imgBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plc_logs_history);
        initElements();
    }


    private void initElements() {

        utilities                   = new Utilities(getApplicationContext());
        SERVER_URL                  = utilities.returnIpAddress();
        client_ID                   = utilities.getClientID();
        handler                     = new DataHandler(getApplicationContext());
        viewPager                   = (ViewPager)findViewById(R.id.viewPager);
        tabBarPLC                   = (TabLayout)findViewById(R.id.tabBarPLC);
        relativeLoader              = (RelativeLayout)findViewById(R.id.relativeLoader);
        tabBarPLC.addTab(tabBarPLC.newTab().setText("Premier Application"));
        tabBarPLC.addTab(tabBarPLC.newTab().setText("Transaction Requests"));
        setToolbar();
        setupLogs();

    }

//    private void getPremiereLogs(){
//
//        showLoading();
//        String token                    = utilities.getToken();
//        String plc_url                  = SERVER_URL+"/api/mobile/getPLCDetails/true?token="+token;
//        JsonObjectRequest objectRequest  = new JsonObjectRequest
//                (Request.Method.GET, plc_url, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray arrayPLCLogs     = response.getJSONArray("application");
//                            JSONArray arrayReviewLogs  = response.getJSONArray("request");
//                            handler.open();
//                            Cursor c = handler.returnPLCApplicationAndReviewLogs();
//                            if(c.getCount() > 0){
//                                handler.updatePLCApplicationAndReviewLogs(arrayPLCLogs.toString(),arrayReviewLogs.toString());
//                            }
//                            else{
//                                handler.insertPLCApplicationAndReviewLogs(arrayPLCLogs.toString(),arrayReviewLogs.toString());
//                            }
//                            handler.close();
//
//                        }
//                        catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        arrayErrorResponse = utilities.errorHandling(error);
//                        Log.e("arrayErrorResponse", String.valueOf(arrayErrorResponse));
//                    }
//                });
//        objectRequest.setRetryPolicy(
//                new DefaultRetryPolicy(
//                        8000,
//                        3,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                )
//        );
//        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
//    }

    private void setupLogs(){
        showLoading();
        viewPagerAdapter = new PremierApplicationAdapter(getSupportFragmentManager(),getApplicationContext(),tabBarPLC.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);
        int limit = (viewPagerAdapter.getCount() > 1 ? viewPagerAdapter.getCount() - 1 : 1);

        viewPager.setOffscreenPageLimit(limit);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabBarPLC));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setNestedScrollingEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setNestedScrollingEnabled(false);
        }

        tabBarPLC.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        showContent();
    }


    private void showContent(){
        relativeLoader.setVisibility(View.GONE);
    }

    private void showLoading(){
        relativeLoader.setVisibility(View.VISIBLE);
    }



//    private void setAdapter() {
//
//        handler.open();
//        Cursor cursor = handler.returnPLCApplicationAndReviewLogs();
//
//        if(cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            try {
//                String arrayStringPLC       = cursor.getString(0);
//                String arrayStringReview    = cursor.getString(1);
//                arrayPLCLogs                = new JSONArray(arrayStringPLC);
//                arrayReviewLogs             = new JSONArray(arrayStringReview);
//
//                if (arrayPLCLogs.length() > 0){
//                    recycler_logs_activity_adapter     = new RecyclerReviewLogs(getActivity(),arrayPLCLogs,true);
//                    recycler_logs_layoutManager        = new LinearLayoutManager(getActivity());
//                    recycler_application_logs.setAdapter(recycler_logs_activity_adapter);
//                    recycler_application_logs.setLayoutManager(recycler_logs_layoutManager);
//                    recycler_application_logs.setItemAnimator(new DefaultItemAnimator());
//                    recycler_application_logs.setVisibility(View.VISIBLE);
//                    lblEmptyApplication.setVisibility(View.GONE);
//                }
//                else{
//                    lblEmptyApplication.setVisibility(View.VISIBLE);
//                    recycler_application_logs.setVisibility(View.GONE);
//                }
//                if (arrayReviewLogs.length() > 0){
//                    recycler_logs_activity_adapter     = new RecyclerReviewLogs(getActivity(),arrayReviewLogs,false);
//                    recycler_logs_layoutManager        = new LinearLayoutManager(getActivity());
//                    recycler_plc_logs.setAdapter(recycler_logs_activity_adapter);
//                    recycler_plc_logs.setLayoutManager(recycler_logs_layoutManager);
//                    recycler_plc_logs.setItemAnimator(new DefaultItemAnimator());
//                    recycler_plc_logs.setVisibility(View.VISIBLE);
//                    lblEmptyRequest.setVisibility(View.GONE);
//                }
//                else{
//                    recycler_plc_logs.setVisibility(View.GONE);
//                    lblEmptyRequest.setVisibility(View.VISIBLE);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        else{
//            lblEmptyApplication.setVisibility(View.VISIBLE);
//            recycler_application_logs.setVisibility(View.GONE);
//            recycler_plc_logs.setVisibility(View.GONE);
//            lblEmptyRequest.setVisibility(View.VISIBLE);
//            utilities.showDialogMessage("Error Connection!",arrayErrorResponse.get(1).toString(),"error");
//        }
//        handler.close();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                linearLoading.setVisibility(View.GONE);
//                linearContent.setVisibility(View.VISIBLE);
//            }
//        }, 1500);
//
//    }

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
        forTitle.setText("PLC / Transaction History");
    }

















}
