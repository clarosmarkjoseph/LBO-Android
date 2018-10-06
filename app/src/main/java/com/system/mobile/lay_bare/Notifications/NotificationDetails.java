package com.system.mobile.lay_bare.Notifications;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paolohilario on 4/4/18.
 */

public class NotificationDetails extends AppCompatActivity {

    Utilities utilities;
    DataHandler handler;
    String type             = "";
    int notification_id     = 0;
    int unique_id           = 0;
    RelativeLayout relativeImage;
    ViewPager viewPager;
    ImageView imgDisplayNotif;
    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapse_toolbar;
    TextView lblTitle,lblTime,lblContent;
    private Toolbar toolbar;
    private Typeface myTypeface;
    String SERVER_URL = "";
    private ArrayList<String> arrayErrorResponse;
    CardView cardViewContent;
    LinearLayout linear_loading,linear_content_no_internet;
    ImageButton imgNoInternet;
    //this will show if the notification type is promo,promotions,campaign manager
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_details);
        setToolbar();
        initElements();
    }

    private void initElements() {

        utilities           = new Utilities(this);
        SERVER_URL          = utilities.returnIpAddress();
        handler             = new DataHandler(getApplicationContext());

        cardViewContent     = (CardView) findViewById(R.id.cardViewContent);
        lblTitle            = (TextView) findViewById(R.id.lblTitle);
        lblTime             = (TextView) findViewById(R.id.lblTime);
        lblContent          = (TextView) findViewById(R.id.lblContent);
        imgDisplayNotif     = (ImageView)findViewById(R.id.imgDisplayNotif);
        viewPager           = (ViewPager) findViewById(R.id.viewpager_branch_image);
        coordinatorLayout   = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        collapse_toolbar    = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        relativeImage       = (RelativeLayout)findViewById(R.id.relativeImage);

        //components of loading & no internet
        linear_loading                  = (LinearLayout) findViewById(R.id.linear_loading);
        linear_content_no_internet      = (LinearLayout) findViewById(R.id.linear_content_no_internet);
        imgNoInternet                   = (ImageButton) findViewById(R.id.imgNoInternet);

        collapse_toolbar.setContentScrimColor(ContextCompat.getColor(this,R.color.laybareGreen));
        collapse_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.themeWhite));
        collapse_toolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapse_toolbar.setExpandedTitleColor(getResources().getColor(R.color.themeWhite));
        collapse_toolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        collapse_toolbar.setTitle("");
        collapse_toolbar.setTitleEnabled(true);
        collapse_toolbar.setCollapsedTitleTypeface(myTypeface);


        Bundle bundle   = getIntent().getExtras();
        if(bundle != null){

            type            = bundle.getString("type");
            unique_id       = bundle.getInt("unique_id");
            notification_id = bundle.getInt("id");
            setNotificationAsMarked();
            if(type.equals("promotion")){
                collapse_toolbar.setTitle("New Promotions!");
                getPromotions();
            }
            if(type.equals("campaign_manager")){
                try {
                    collapse_toolbar.setTitle("Lay Bare Notification Center");
                    JSONObject objectDetails = new JSONObject(bundle.getString("object"));
                    getCampaignManager(objectDetails);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        imgNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPromotions();
            }
        });
    }




    private void getPromotions(){

        Log.e("unique_id", String.valueOf(unique_id));
        showLoading();
        handler.open();
        Cursor c = handler.returnSpecificPromotion(unique_id);
        if(c.getCount() > 0){

            c.moveToFirst();
            String title        = c.getString(1);
            String description  = c.getString(2);
            String date_start   = c.getString(3);
            String date_end     = c.getString(4);
            String type         = c.getString(5);
            String image        = c.getString(6);
            String promo_data   = c.getString(7);
            String imageURL         = SERVER_URL+"/images/promotions/"+image;
            if(!date_start.equals("null")){
                lblTime.setText(utilities.getCompleteDateMonth(date_start)+" - "+utilities.getCompleteDateMonth(date_end));
            }
            else {
                lblTime.setText("Regular Promo");
            }
            relativeImage.setVisibility(View.VISIBLE);
            lblTitle.setText(title);
            lblContent.setText(Html.fromHtml(description));
            utilities.setUniversalBigImage(imgDisplayNotif,imageURL);
            showContent();
        }
        else{
            getSpecificPromotions();
        }

        handler.close();


    }

    private void getSpecificPromotions(){

        String url = SERVER_URL+"/api/promotion/getSpecificPromo/"+unique_id;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            int id                  = response.optInt("id");
                            String title            = response.getString("title");
                            String type             = response.getString("type");
                            String description      = response.getString("description");
                            String image            = response.getString("promo_picture");
                            String date_start       = response.getString("date_start");
                            String date_end         = response.getString("date_end");
                            String promotions_data  = response.optString("promotions_data","{}");
                            handler.open();
                            Cursor c = handler.returnSpecificPromotion(id);
                            if(c.getCount() > 0){
                                handler.updatePromotions(id,title,description,date_start,date_end,type,image,promotions_data);
                            }
                            else{
                                handler.insertPromotions(id,title,description,date_start,date_end,type,image,promotions_data);
                            }
                            handler.close();
                            getPromotions();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR",error.toString());
                       showNoInternet();
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    private void getCampaignManager(JSONObject objectData){
        showLoading();
        try {

            String title            = objectData.getString("title");
            String body             = objectData.getString("body");
            lblTitle.setText(title);
            lblContent.setText(Html.fromHtml(body));
            relativeImage.setVisibility(View.GONE);
            showContent();
        }
        catch (JSONException e) {
            e.printStackTrace();
            showContent();
        }
    }


    private void getSurveys(){

    }

    private void getNews(){

    }


    void showLoading(){
        cardViewContent.setVisibility(View.GONE);
        linear_content_no_internet.setVisibility(View.GONE);
        linear_loading.setVisibility(View.VISIBLE);
    }

    void showNoInternet(){
        linear_loading.setVisibility(View.GONE);
        cardViewContent.setVisibility(View.GONE);
        linear_content_no_internet.setVisibility(View.VISIBLE);
    }

    void showContent(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                linear_loading.setVisibility(View.GONE);
                linear_content_no_internet.setVisibility(View.GONE);
                cardViewContent.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }


    private void setToolbar() {
        toolbar     = (Toolbar) findViewById(R.id.myToolbar);
        myTypeface  = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void setNotificationAsMarked() {
        String token = utilities.getToken();
        String url  = SERVER_URL+"/api/mobile/setNotificationAsSeen?token="+token;
        StringRequest arrayRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response seen", String.valueOf(response));
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
                        2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);
    }
}
