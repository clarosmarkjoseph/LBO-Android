package com.system.mobile.lay_bare.Notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by paolohilario on 3/26/18.
 */

public class NotificationCenter extends AppCompatActivity implements UpdateNotificationStatus {
    int notificationID = 0;
    DataHandler handler;
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;

    RecyclerView recyclerNotification;
    RecyclerView.LayoutManager recyclerLayoutmanager;
    RecyclerView.Adapter recyclerAdapter;

    ArrayList<JSONObject> arrayNotification   = new ArrayList<>();
    String SERVER_URL = "";
    Utilities utilities;
    NotificationManager notificationManager;

    TextView lblCaption;

    SwipeRefreshLayout swipeRefresh;
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListner;

    int latest_notif_id = 0;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_center);
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        setToolbar();
        initElements();

    }

    private void initElements() {
        utilities               = new Utilities(this);
        handler                 = new DataHandler(getApplicationContext());
        recyclerNotification    = (RecyclerView)findViewById(R.id.recyclerNotification);
        swipeRefresh            = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        SERVER_URL              = utilities.returnIpAddress();
        lblCaption              = (TextView)findViewById(R.id.lblCaption);

        utilities.returnRefreshColor(swipeRefresh);
        getOfflineNotification();
        swipeConfiguration();
    }

    //swipe refresh confirgurations
    private void swipeConfiguration() {

        swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getNotificationsOnline();
                            }
                        }, 500);
                    }
                });
            }
        };
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshListner.onRefresh();
            }
        });
    }

    private void getOfflineNotification(){
        handler.open();
        Cursor cursorNotification = handler.returnAllNotification();
        if(cursorNotification.getCount() > 0){
            while (cursorNotification.moveToNext()){
                try {
                    JSONObject objectData = new JSONObject(cursorNotification.getString(3));
                    arrayNotification.add(objectData);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setAdapter();
        }
        handler.close();
    }




    private void getNotificationsOnline() {

        latest_notif_id = utilities.getlastNotificationID();
        String token    = utilities.getToken();
        String url   = SERVER_URL+"/api/mobile/getNotifications/"+latest_notif_id+"?token="+token;
        final JsonArrayRequest arrayRequest = new JsonArrayRequest
                (Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        swipeRefresh.setRefreshing(false);
                        Log.e("response: ",String.valueOf(response));
                        for(int x = 0; x < response.length(); x++){
                            try {

                                JSONObject objectNotification = response.getJSONObject(x);
                                int id              = objectNotification.getInt("id");
                                String  dateTime    = objectNotification.getString("created_at");
                                String type         = objectNotification.getString("notification_type");
                                int isRead          = objectNotification.getInt("isRead");
                                handler.open();
                                handler.insertNotification(id,dateTime,type,objectNotification,isRead,isRead);
                                arrayNotification.add(objectNotification);
                                handler.close();
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        setAdapter();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ArrayList<String > arrayResponse = utilities.errorHandling(error);
                        Log.e("arrayResponse",String.valueOf(arrayResponse));
                        swipeRefresh.setRefreshing(false);
                        setAdapter();
                    }
                });
        arrayRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);
    }


    private void setAdapter() {

        if (arrayNotification.size() <= 0){
            recyclerNotification.setVisibility(View.GONE);
            lblCaption.setVisibility(View.VISIBLE);
        }
        else{
            lblCaption.setVisibility(View.GONE);
            recyclerNotification.setVisibility(View.VISIBLE);
        }
        Collections.sort(arrayNotification, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    double s1 = o1.getDouble("id");
                    double s2 = o2.getDouble("id");
                    return Double.compare(s2, s1);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        recyclerAdapter         = new RecyclerNotification(NotificationCenter.this,arrayNotification);
        recyclerLayoutmanager   = new LinearLayoutManager(getApplicationContext());
        recyclerNotification.setAdapter(recyclerAdapter);
        recyclerNotification.setLayoutManager(recyclerLayoutmanager);
        recyclerNotification.setItemAnimator(new DefaultItemAnimator());
        recyclerNotification.setNestedScrollingEnabled(false);
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
        forTitle.setText("Notifications");
    }


    @Override
    public void setNotificationAsSeen(final int notification_id) {

        String token = utilities.getToken();
        String url  = SERVER_URL+"/api/mobile/setNotificationAsSeen?token="+token;
        StringRequest arrayRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handler.open();
                        handler.setNotificationAsRead(notification_id);
                        handler.close();
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(notification_id));
                return params;
            }
        };

        arrayRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);
    }
}
