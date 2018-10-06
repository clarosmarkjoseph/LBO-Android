package com.system.mobile.lay_bare;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.system.mobile.lay_bare.Profile.RecyclerPromotions;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 11/09/2017.
 */

public class Promotions extends AppCompatActivity{

    DataHandler handler;
    Utilities utilities;
    String SERVER_URL = "";
    RecyclerView recycler_promotions;
    RecyclerView.LayoutManager  recycler_promotions_manager;
    RecyclerView.Adapter recycler_promotions_adapter;
    LinearLayout linearHome_menu;
    SwipeRefreshLayout swipeRefresh;
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListner;
    private JSONArray arrayPromotions = new JSONArray();
    private ArrayList<String> arrayErrorResponse = new ArrayList<>();
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotions);
        setToolbar();
        utilities           = new Utilities(this);
        handler             = new DataHandler(getApplicationContext());
        SERVER_URL          = utilities.returnIpAddress();
        linearHome_menu     = (LinearLayout)findViewById(R.id.relativeHome_menu);

        recycler_promotions = (RecyclerView)findViewById(R.id.recycler_promotions);
        swipeRefresh        =(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        utilities.returnRefreshColor(swipeRefresh);
        swipeConfiguration();
        swipeRefreshListner.onRefresh();

    }

    private void getOffline() {
        swipeRefresh.setRefreshing(true);
        arrayPromotions      = new JSONArray();
        handler.open();
        Cursor query  = handler.returnPromotion();
        if(query.getCount() > 0){
            while(query.moveToNext()){

                String id            = query.getString(0);
                String title         = query.getString(1);
                String type          = query.getString(6);
                String description   = query.getString(2);
                String posted_by     = query.getString(3);
                String image         = query.getString(7);
                String date_start    = query.getString(4);
                String date_end      = query.getString(5);
                JSONObject obj       = new JSONObject();
                try {
                    obj.put("id",id);
                    obj.put("ago_posted",returnTimePassed(type,date_start));
                    obj.put("title",title);
                    obj.put("type",type);
                    obj.put("description",description);
                    obj.put("posted_by",posted_by);
                    obj.put("image",image);
                    obj.put("date_start",date_start);
                    obj.put("date_end",date_end);
                    arrayPromotions.put(obj);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            swipeRefresh.setRefreshing(true);
            displayPromotions();
        }
        else{
            getPromotions();
        }
        handler.close();
    }


    private void getPromotions() {
        arrayPromotions = new JSONArray();
        String url = SERVER_URL+"/api/promotion/getPromotions";
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        handler.open();
                        handler.deletePromotion();
                        handler.close();

                        for (int x = 0; x < response.length(); x++)
                            try {

                                JSONObject objResult    = response.getJSONObject(x);
                                int id                  = objResult.optInt("id");
                                String title            = objResult.getString("title");
                                String type             = objResult.getString("type");
                                String description      = objResult.getString("description");
                                String image            = objResult.getString("promo_picture");
                                String date_start       = objResult.getString("date_start");
                                String date_end         = objResult.getString("date_end");
                                String promotions_data  = objResult.optString("promotions_data","{}");


                                handler.open();
                                Cursor c = handler.returnSpecificPromotion(id);
                                if(c.getCount() > 0){
                                    handler.updatePromotions(id,title,description,date_start,date_end,type,image,promotions_data);
                                }
                                else{
                                    handler.insertPromotions(id,title,description,date_start,date_end,type,image,promotions_data);
                                }
                                handler.close();

                                JSONObject obj = new JSONObject();
                                obj.put("id",id);
                                obj.put("ago_posted",returnTimePassed(type,date_start));
                                obj.put("title",title);
                                obj.put("type",type);
                                obj.put("description",description);
                                obj.put("image",image);
                                obj.put("date_start",date_start);
                                obj.put("date_end",date_end);
                                arrayPromotions.put(obj);

                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        displayPromotions();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR",error.toString());
                        arrayErrorResponse = utilities.errorHandling(error);
                        swipeRefresh.setRefreshing(false);
                        getOffline();
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    String returnTimePassed(String type,String date_start){
        String ago = "";
        if(type.equals("promo")){
            ago = utilities.returnTimeAgo(date_start);
        }
        else{
            ago = "Sponsored";
        }
        return ago;
    }


    private void displayPromotions() {
        recycler_promotions_adapter  = new RecyclerPromotions(this,arrayPromotions);
        recycler_promotions_manager = new LinearLayoutManager(getApplicationContext());
        recycler_promotions.setAdapter(recycler_promotions_adapter);
        recycler_promotions.setLayoutManager(recycler_promotions_manager);
        recycler_promotions.setItemAnimator(new DefaultItemAnimator());
        recycler_promotions.setNestedScrollingEnabled(false);
        swipeRefresh.setRefreshing(false);
    }


    public void setToolbar(){
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
        forTitle.setText("New Promotions");
    }

    private void swipeConfiguration() {

        swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                getPromotions();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
