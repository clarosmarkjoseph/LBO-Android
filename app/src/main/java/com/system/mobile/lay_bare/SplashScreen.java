package com.system.mobile.lay_bare;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.Classes.ProfileClass;
import com.system.mobile.lay_bare.NavigationDrawer.NavigationDrawerActivity;

import com.system.mobile.lay_bare.Utilities.Utilities;
import com.system.mobile.lay_bare.Classes.VersionClass;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;


public class SplashScreen extends AppCompatActivity {

    DataHandler handler;
    ImageView imgsplash;
    Utilities utilities;
    String SERVER_URL           = "";
    String device               = "Android";
    String devicename           = "";
    double appVersion           = 0.0;
    String serial_no            = Build.SERIAL;
    VersionClass versionClass;
    private Handler mHandler;
    private Runnable mRunnable;
    private static final long SPLASH_DURATION = 1000L;
    LinearLayout linear_content,linear_content_no_internet,linear_loading,linear_splashscreen,linear_upgrade;
    Button btnUpgradeApp;
    ImageButton imgNoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        linear_content              = (LinearLayout)findViewById(R.id.linear_content);
        linear_content_no_internet  = (LinearLayout)findViewById(R.id.linear_content_no_internet);
        linear_loading              = (LinearLayout)findViewById(R.id.linear_loading);
        linear_splashscreen         = (LinearLayout)findViewById(R.id.linear_splashscreen);
        linear_upgrade              = (LinearLayout)findViewById(R.id.linear_upgrade);
        btnUpgradeApp               = (Button)findViewById(R.id.btnUpgradeApp);
        utilities                   = new Utilities(this);
        versionClass                = new VersionClass(this);
        appVersion                  = versionClass.returnAppVersion();
        devicename                  = utilities.getDeviceName();
        imgsplash                   = (ImageView)findViewById(R.id.imgsplash);
        imgNoInternet               = (ImageButton)findViewById(R.id.imgNoInternet);
        handler                     = new DataHandler(getBaseContext());
        linear_content_no_internet.setBackgroundColor(getResources().getColor(R.color.laybareGreen));
        imgNoInternet.setBackgroundColor(getResources().getColor(R.color.laybareGreen));
        imgNoInternet.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.themeWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
        handler.open();
        Cursor cursor_ip = handler.returnIPAddress();
        try {
            if (cursor_ip.getCount() > 0){
                SERVER_URL   = cursor_ip.getString(0);
                if(SERVER_URL.equalsIgnoreCase("")){
                    handler.insertIPAddress("https://lbo.lay-bare.com");
                    SERVER_URL = "https://lbo.lay-bare.com";
                }
                else{
                    handler.insertIPAddress("https://lbo.lay-bare.com");
                    SERVER_URL = "https://lbo.lay-bare.com";
                }
            }
            else {
                handler.insertIPAddress("https://lbo.lay-bare.com");
                SERVER_URL = "https://lbo.lay-bare.com";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        handler.close();
        btnUpgradeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse( "https://play.google.com/store/apps/details?id=" + SplashScreen.this.getPackageName()));
                    startActivity(viewIntent);
                }
                catch(Exception e) {
                    Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        imgNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
            }
        });
        showLoading();
    }



    private void checkAppVersion() {

       final ProfileClass profileClass = new ProfileClass(this);
       String token    = utilities.getToken();
       String url      = SERVER_URL+ "/api/mobile/getAppVersion/"+appVersion+"/"+device+"/"+devicename+"/"+serial_no+"?token="+token;
       JsonObjectRequest jsObjRequest = new JsonObjectRequest
               (Request.Method.GET, url.replace(" ","%20"),null, new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       try {
                           boolean isValidToken      = response.getBoolean("isValidToken");
                           boolean ifUpdated         = response.getBoolean("ifUpdated");

                           if(ifUpdated == true){

                               if(isValidToken == true){
                                   JSONObject objectProfile = response.getJSONObject("arrayProfile");
                                   profileClass.updateClientProfile(objectProfile);
                                   configureStartup();
                               }
                               else{
                                   if (utilities.CountUser() > 0){
                                       profileClass.logoutClient();
                                   }
                                   configureStartup();
                               }
                           }
                           else{
                               showAppUpgrade();
                           }
                       }
                       catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       configureStartup();
                   }
               });
       jsObjRequest.setRetryPolicy(
               new DefaultRetryPolicy(
                       20000,
                       2,
                       DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
               )
       );
       MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }


    void configureStartup(){

        mHandler  = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                dismissSplash();
            }
        };
        mHandler.postDelayed(mRunnable, SPLASH_DURATION);
    }

    private void dismissSplash() {
        Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        startActivity(in);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    void showLoading(){
        linear_content.setVisibility(View.VISIBLE);
        linear_content_no_internet.setVisibility(View.GONE);
        linear_loading.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //add your code here
                checkAppVersion();
            }
        }, 500);

    }
//    void showNoInternet(){
//        linear_content.setVisibility(View.GONE);
//        linear_content_no_internet.setVisibility(View.VISIBLE);
//        linear_loading.setVisibility(View.GONE);
//    }
//
//    void showContent(){
//        linear_content.setVisibility(View.VISIBLE);
//        linear_content_no_internet.setVisibility(View.GONE);
//        linear_loading.setVisibility(View.GONE);
//    }
    void showAppUpgrade(){
        linear_loading.setVisibility(View.GONE);
        linear_upgrade.setVisibility(View.VISIBLE);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
