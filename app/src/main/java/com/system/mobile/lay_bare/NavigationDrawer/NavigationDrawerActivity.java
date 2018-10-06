package com.system.mobile.lay_bare.NavigationDrawer;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.FirebaseNotification.FirebaseHandler;
import com.system.mobile.lay_bare.FirebaseNotification.FirebaseNotificationSettings;
import com.system.mobile.lay_bare.FirebaseNotification.RegistrationIntentService;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.NewLogin;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Profile.ClientProfile;
import com.system.mobile.lay_bare.Sockets.SocketApplication;
import com.system.mobile.lay_bare.Utilities.Utilities;
import com.system.mobile.lay_bare.Classes.VersionClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.emitter.Emitter;

/**
 * Created by Mark on 17/11/2017.
 */

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    TextView toolbar_title;
    protected View view;
    GridView grid;
    Utilities utilities;
    String clientID = "";
    DataHandler handler;
    LinearLayout linear_header;
    TextView lblName;
    TextView lblEmail;
    ImageView imgProfile;
    private String full_name = "",email="",imageUrl="";
    String SERVER_URL = "";
    boolean isLoggedIn;
    RecyclerView recyclerButton,recycler_navigation;
    RecyclerView.LayoutManager recyclerButton_layoutManager;
    RecyclerView.Adapter recyclerButton_adapter,recyclerNavigation_adapter;
    int dotsCount = 0;
    ImageView[] dots;
    LinearLayout pagerIndicator;
    ImageButton btnNext, btnFinish;
    ViewPager ads_pager;
    private JSONArray arrayCarousel = new JSONArray();
    ViewPagerAdapter viewPagerAdapter;
    Timer timer;
    LinearLayout linear_content_no_internet,linear_loading,linear_content,linearLayout2;
    ImageButton imgNoInternet;
    private Typeface myTypeface;
    boolean ifLoaded = false;
    private ArrayList<String> arrayErrorResponse;
    public static NavigationDrawerActivity navigationDrawerActivity;
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    int lastNotifID = 0;
    String token = "";
    int notifCount = 0;
    Context context;
    String PRIVACY_CONSENT = "";
    boolean ifTerms         = false;
    public static boolean isAppRunning      = false;
    static boolean isMessageLoading         = false;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_activity);
        setToolbar();
        initElement();
    }

    void initElement(){
        context             = this;
        handler             = new DataHandler(getApplicationContext());
        utilities           = new Utilities(this);
        SERVER_URL          = utilities.returnIpAddress();
        token               = utilities.getToken();

        drawerLayout        = (DrawerLayout)findViewById(R.id.drawerLayout);
        drawerToggle        = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        clientID                    = utilities.getClientID();
        imgProfile                  = (ImageView)findViewById(R.id.imgProfile);
        lblName                     = (TextView)findViewById(R.id.lblName);
        lblEmail                    = (TextView)findViewById(R.id.lblEmail);
        linear_header               = (LinearLayout)findViewById(R.id.linear_header);
        recycler_navigation         = (RecyclerView)findViewById(R.id.recycler_navigation);
        linear_content_no_internet  = (LinearLayout)findViewById(R.id.linear_content_no_internet);
        linear_loading              = (LinearLayout)findViewById(R.id.linear_loading);
        linear_content              = (LinearLayout)findViewById(R.id.linear_content);
        linearLayout2               = (LinearLayout)findViewById(R.id.linearLayout2);
        pagerIndicator              = (LinearLayout)findViewById(R.id.viewPagerCountDots);
        ads_pager                   = (ViewPager)findViewById(R.id.ads_pager);
        btnNext                     = (ImageButton)findViewById(R.id.btn_next);
        btnFinish                   = (ImageButton)findViewById(R.id.btn_finish);
        recyclerButton              = (RecyclerView)findViewById(R.id.recycler_button);
        imgNoInternet               = (ImageButton)findViewById(R.id.imgNoInternet);
        grid                        = (GridView)findViewById(R.id.main_menu_grid);

        imgNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading();
                    }
                });
            }
        });


        linear_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerNav(isLoggedIn);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ads_pager.setCurrentItem((ads_pager.getCurrentItem() < dotsCount)
                        ? ads_pager.getCurrentItem() + 1 : 0);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ads_pager.setCurrentItem((ads_pager.getCurrentItem() < dotsCount)
                        ? ads_pager.getCurrentItem() - 1 : 0);
            }
        });

        loadData();
        showLoading();
    }




    private void showLoading(){

        linear_content_no_internet.setVisibility(View.GONE);
        linear_loading.setVisibility(View.VISIBLE);
        linear_content.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getFirstLoadOfTheDay();
            }
        }).start();
    }


    public void getFirstLoadOfTheDay() {

        final VersionClass versionClass = new VersionClass(this);
        String statement                = versionClass.returnBannerVersion()+"/"+versionClass.returnCommercialVersion()+"/"+versionClass.returnServiceVersion()+"/"+versionClass.returnPackagesVersion()+"/"+versionClass.returnProductVersion()+"/"+versionClass.returnBranchesVersion();
        String url = SERVER_URL+"/api/mobile/getFirstLoadDetails/"+statement+"?token="+token;
        Log.e("url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("response",response.toString());
                            ifLoaded = true;
                            JSONObject objectVersion  = response.getJSONObject("versions");
                            double version_banner     = objectVersion.getDouble("version_banner");
                            double version_branches   = objectVersion.getDouble("version_branches");
                            double version_commercial = objectVersion.getDouble("version_commercial");
                            double version_services   = objectVersion.getDouble("version_services");
                            double version_packages   = objectVersion.getDouble("version_packages");
                            double version_products   = objectVersion.getDouble("version_products");
                            handler.open();
                            if(version_banner > versionClass.returnBannerVersion()){
                                JSONArray arrayBanner     = response.getJSONArray("arrayBanner");
                                Cursor queryCount         = handler.returnBanner();
                                if(queryCount.getCount() > 0){
                                    handler.deleteBanner();
                                }
                                handler.insertBanner(String.valueOf(version_banner),arrayBanner.toString());
                            }
                            if(version_branches > versionClass.returnBranchesVersion()){
                                JSONArray arrayBranches     = response.getJSONArray("arrayBranch");
                                Cursor queryCount           = handler.returnBranches();
                                if(queryCount.getCount() > 0){
                                    handler.deleteBranches();
                                }
                                handler.insertBranches(String.valueOf(version_branches),arrayBranches.toString());
                            }

                            if(version_commercial > versionClass.returnCommercialVersion()){
                                JSONArray arrayCommercial   = response.getJSONArray("arrayCommercial");
                                Cursor queryCount           = handler.returnCommercial();
                                if(queryCount.getCount() > 0){
                                    handler.deleteCommercial();
                                }
                                handler.insertCommercial(String.valueOf(version_commercial),arrayCommercial.toString());
                            }
                            if(version_services > versionClass.returnServiceVersion()){
                                JSONArray arrayService    = response.getJSONArray("arrayServices");
                                Cursor queryCount         = handler.returnServices();
                                if(queryCount.getCount() > 0){
                                    handler.deleteService();
                                }
                                handler.insertServices(String.valueOf(version_services),arrayService.toString());
                            }
                            if(version_packages > versionClass.returnPackagesVersion()){
                                JSONArray arrayPackage    = response.getJSONArray("arrayPackage");
                                Cursor queryCount         = handler.returnPackage();
                                if(queryCount.getCount() > 0){
                                    handler.deletePackage();
                                }
                                handler.insertPackages(String.valueOf(version_services),arrayPackage.toString());
                            }
                            if(version_products > versionClass.returnProductVersion()){
                                JSONArray arrayProduct    = response.getJSONArray("arrayProducts");
                                Cursor queryCount         = handler.returnProducts();
                                if(queryCount.getCount() > 0){
                                    handler.deleteProducts();
                                }
                                handler.insertProducts(String.valueOf(version_services),arrayProduct.toString());
                            }
                            handler.close();
                            getImageList();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e("ERROR WEW","ERER");
                        handler.open();
                        Cursor query = handler.returnBanner();
                        if(query.getCount() > 0){
                            getImageList();
                        }
                        else{
                            showNoInternet();
                        }
                        handler.close();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        20000,
                        2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }


    //get image banner (ads)
    private void getImageList() {
        arrayCarousel = new JSONArray();
        handler = new DataHandler(getApplicationContext());
        handler.open();
        Cursor query_banner = handler.returnBanner();
        if(query_banner.getCount() > 0){
            query_banner.moveToFirst();
            try {
                arrayCarousel = new JSONArray(query_banner.getString(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        handler.close();
        showMore();
        getPagerAdapter();
    }


    //set carousel adapter
    private void getPagerAdapter(){

        viewPagerAdapter =  new ViewPagerAdapter(this,arrayCarousel,"ads");
        ads_pager.setAdapter(viewPagerAdapter);
        ads_pager.setCurrentItem(0);
        ads_pager.setOffscreenPageLimit(1);
        ads_pager.canScrollVertically(50);
        dotsCount   = viewPagerAdapter.getCount();
        dots        = new ImageView[dotsCount];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        if(dotsCount > 0){
            for (int x = 0; x<dotsCount;x++){
                dots[x] = new ImageView(getApplicationContext());
                dots[x].setImageDrawable(getResources().getDrawable(R.drawable.nonselectable));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dots[x].setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeGray)));
                }
                params.setMargins(4,0,4,0);
                pagerIndicator.addView(dots[x],params);
                if(x == 0){
                    dots[x].setImageDrawable(getResources().getDrawable(R.drawable.nonselectable));
                }
            }
        }
        ads_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                position = ads_pager.getCurrentItem();
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselectable));
                }
                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selectable));
                if (position + 1 == dotsCount) {
                    btnNext.setVisibility(View.GONE);
                    btnFinish.setVisibility(View.VISIBLE);
                }
                else if((position + 1 > 1) && (position < dotsCount)){
                    btnNext.setVisibility(View.VISIBLE);
                    btnFinish.setVisibility(View.VISIBLE);
                }
                else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnFinish.setVisibility(View.GONE);
                }
//                timer.cancel();
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        } );
//        pageSwitcher(5);
        setDrawerButtons();
        setNavigationButtons();
    }

    private void setDrawerButtons(){
        recycler_navigation.setNestedScrollingEnabled(false);
        recyclerButton_layoutManager = new LinearLayoutManager(this);
        recycler_navigation.setLayoutManager(recyclerButton_layoutManager);
        recyclerNavigation_adapter = new RecyclerNavigation(NavigationDrawerActivity.this);
        recycler_navigation.setAdapter(recyclerNavigation_adapter);
    }

    private void setNavigationButtons(){
        recyclerButton.setNestedScrollingEnabled(false);
        recyclerButton_layoutManager = new GridLayoutManager(this,3);
        recyclerButton.setLayoutManager(recyclerButton_layoutManager);
        recyclerButton_adapter = new RecyclerButton(NavigationDrawerActivity.this);
        recyclerButton.setAdapter(recyclerButton_adapter);
        showContent();
    }


    void showContent(){
        linear_content_no_internet.setVisibility(View.GONE);
        linear_loading.setVisibility(View.GONE);
        linear_content.setVisibility(View.VISIBLE);
    }


    private void showMore(){
        if(clientID != null){
            registerWithNotificationHubs();
            navigationDrawerActivity = this;
            NotificationsManager.handleNotifications(this, FirebaseNotificationSettings.SenderID, FirebaseHandler.class);
            if (utilities.getConsent() <= 0 && ifTerms == false){
                ifTerms = false;
                getConsent();
            }
            else{
                ifTerms = true;
            }
            getNotification();
        }
    }

    //load settings
    private void loadData() {

        if(clientID != null){
            handler.open();
            Cursor query = handler.returnUserAccount();
            if(query.getCount() > 0){
                query.moveToFirst();
                try {
                    JSONObject objectClient = new JSONObject(query.getString(1));
                    clientID       = objectClient.getString("id");
                    full_name      = objectClient.getString("first_name")+" "+objectClient.getString("last_name");
                    email          = objectClient.getString("email");
                    imageUrl       = objectClient.getString("user_picture");
                    String imgUrl  = SERVER_URL+"/images/users/"+imageUrl;
                    lblName.setText(full_name);
                    lblEmail.setText(email);
                    utilities.setUniversalSmallImage(imgProfile,imgUrl);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            handler.close();
            isLoggedIn = true;
        }
        else{
            isLoggedIn = false;
            lblName.setText("Hello, Guest");
            lblEmail.setText("Please login to continue");
        }
    }



    private void getConsent() {

        String url          = SERVER_URL+"/api/config/getConsent";
        StringRequest arrayRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PRIVACY_CONSENT = response;
                        showConsent();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        arrayErrorResponse = utilities.errorHandling(error);
                        Log.e("ERROR chat",arrayErrorResponse.get(1).toString());
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

    public void showConsent() {


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_terms);
        TextView lbldialog_title            = (TextView) dialog.findViewById(R.id.lbldialog_title);
        TextView lblContent                 = (TextView) dialog.findViewById(R.id.lblContent);
        Button btndialog_cancel             = (Button) dialog.findViewById(R.id.btndialog_cancel);
        Button btndialog_confirm            = (Button) dialog.findViewById(R.id.btndialog_confirm);
        ImageButton imgBtnClose             = (ImageButton) dialog.findViewById(R.id.imgBtn_dialog_close);
        RelativeLayout relativeToolbar      = (RelativeLayout) dialog.findViewById(R.id.relativeToolbar);

        btndialog_cancel.setVisibility(View.VISIBLE);

        relativeToolbar.setBackgroundColor(context.getResources().getColor(R.color.laybareInfo));
        btndialog_confirm.setBackgroundColor(context.getResources().getColor(R.color.laybareInfo));
        btndialog_cancel.setBackgroundColor(context.getResources().getColor(R.color.themeRed));

        lbldialog_title.setText("Acknowledgement & Consent");
        String content = PRIVACY_CONSENT.replace("{first_name} {last_name}",utilities.getClientName());

        lblContent.setText(Html.fromHtml(content));

        btndialog_confirm.setText("I Agree");
        final Dialog myDialog = dialog;
        btndialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                utilities.showProgressDialog("Please Wait....");
                submitConsent();
            }
        });
        btndialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submitConsent() {
        token               = utilities.getToken();
        String url          = SERVER_URL+"/api/user/approveConsent?token="+token;
        StringRequest arrayRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        utilities.hideProgressDialog();
                        ifTerms = true;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        arrayErrorResponse = utilities.errorHandling(error);
                        Log.e("ERROR chat",arrayErrorResponse.get(1).toString());
                        utilities.hideProgressDialog();
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




    private void getNotification(){

        lastNotifID = utilities.getlastNotificationID();
        String url  = SERVER_URL+"/api/mobile/getNotifications/"+lastNotifID+"?token="+token;
        JsonArrayRequest arrayRequest = new JsonArrayRequest
                (Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        notifCount = response.length();

                        if(response.length() > 0){
                            for (int x = 0; x < response.length(); x++){
                                try {
                                    JSONObject objectNotif               = response.getJSONObject(x);
                                    int id                               = objectNotif.optInt("id",0);
                                    String notification_type             = objectNotif.getString("notification_type");
                                    String created_at                    = objectNotif.getString("created_at");
                                    JSONObject objectNotificationData    = new JSONObject(objectNotif.getString("notification_data"));
                                    String title                         = objectNotificationData.getString("title");
                                    String body                          = objectNotificationData.getString("body");
                                    int isRead                           = objectNotif.optInt("isRead",1);

                                    handler.open();
                                    Cursor queryNotif = handler.returnNotification(id);
                                    if(queryNotif.getCount() > 0){
                                        handler.updateNotification(id,created_at,notification_type,objectNotif,0,isRead);
                                    }
                                    else{
                                        handler.insertNotification(id,created_at,notification_type,objectNotif,0,isRead);
                                    }
                                    handler.close();
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        setDrawerButtons();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error:",error.toString());
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

    private void getMessage(){

        isMessageLoading = true;
        handler.open();
        JSONObject objectParams = new JSONObject();
        JSONArray arrayThreadID = new JSONArray();
        JSONArray arrayLastID   = new JSONArray();
        Cursor cursorThread     = handler.returnAllThread();
        try {
            if(cursorThread.getCount() > 0){
                while (cursorThread.moveToNext()){
                    int thread_id    = cursorThread.getInt(0);
                    int last_msg_id  = handler.returnLastChatMessage(thread_id);
                    arrayThreadID.put(thread_id);
                    arrayLastID.put(last_msg_id);
                }
            }

            objectParams.put("arrayThreadID",arrayThreadID);
            objectParams.put("arrayLastID",arrayLastID);
            String url   = SERVER_URL+"/api/mobile/getAllChatMessage?token="+token;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url,objectParams, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject res) {
                            try {
                                JSONArray arrayResult = res.getJSONArray("allMessage");
                                for (int x = 0; x < arrayResult.length(); x++){
                                    JSONObject objectResponse   = arrayResult.getJSONObject(x);
                                    int thread_id               = objectResponse.optInt("id",0);
                                    int creator_id              = objectResponse.optInt("created_by_id",0);
                                    String thread_name          = objectResponse.optString("thread_name","");
                                    String participants         = objectResponse.optString("participant_ids","[]");
                                    String update_at            = objectResponse.optString("updated_at","");
                                    JSONArray arrayMessages     = objectResponse.getJSONArray("messages");

                                    if (arrayMessages.length() > 0){
                                        handler.open();
                                        Cursor cursorThread = handler.returnChatThread(thread_id);
                                        if(cursorThread.getCount() > 0){
                                            handler.updateChatThread(thread_id,thread_name,update_at, creator_id,participants);
                                        }
                                        else{
                                            handler.insertChatThread(thread_id,thread_name,update_at, creator_id,participants);
                                        }
                                        handler.close();
                                        for (int y = 0; y < arrayMessages.length(); y++){
                                            JSONObject objectMessage = arrayMessages.getJSONObject(y);
                                            int message_id           = objectMessage.optInt("id",0);
                                            int sender_id            = objectMessage.optInt("sender_id",0);
                                            int recipient_id         = objectMessage.optInt("recipient_id",0);
                                            int deleted_to_id        = objectMessage.optInt("deleted_to_id",0);
                                            int message_thread_id    = objectMessage.optInt("message_thread_id",0);
                                            String title             = objectMessage.optString("title","No Title");
                                            String body              = objectMessage.optString("body","");
                                            String message_data      = objectMessage.optString("message_data","");
                                            String read_at           = objectMessage.optString("read_at","");
                                            String message_created   = objectMessage.optString("created_at","0000-00-00 00:00:00");
                                            String status            = "";
                                            String thread_status     = "";
                                            if(read_at.equals("0000-00-00 00:00:00") || read_at.equals("null") || read_at.equals("") ){
                                                status = "sent";
                                            }
                                            else{
                                                status          = "seen";
                                                thread_status   = "seen";
                                            }
                                            handler.open();
                                            Cursor cursorMessage = handler.returnChatMessageByID(message_id);
                                            if(cursorMessage.getCount() > 0){
                                                handler.updateChatMessage(message_id,sender_id,recipient_id,message_thread_id,title,body,message_data,message_created,read_at,status);
                                            }
                                            else{
                                                handler.insertChatMessage(message_id,sender_id,recipient_id,message_thread_id,title,body,message_data,message_created,read_at,status);
                                            }
                                            handler.updateThreadTime(thread_id,thread_status,message_created);
                                            if(y >= arrayMessages.length()){
                                                if(!read_at.equals("null") || !read_at.equals("")){
                                                    handler.setThreadAsSeen(thread_id);
                                                }
                                            }
                                            handler.close();
                                        }
                                    }
                                }
                                handler.close();
                                isMessageLoading = false;
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            arrayErrorResponse = utilities.errorHandling(error);
                            isMessageLoading = false;
                        }
                    });

            jsonObjectRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            40000,
                            2,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    ));
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        handler.close();

    }

    private void setToolbar() {
        toolbar             = (Toolbar)findViewById(R.id.myToolbar);
        toolbar_title       = (TextView)findViewById(R.id.forTitle);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        myTypeface          = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        toolbar_title.setTypeface(myTypeface);
        toolbar_title.setText("Hair Free Morning!");
    }



    void showNoInternet(){
        linear_content_no_internet.setVisibility(View.VISIBLE);
        linear_loading.setVisibility(View.GONE);
    }




    public void pageSwitcher(int seconds) {

        timer = new Timer(); // At this line a new Thread will be created
//        timer.scheduleAtFixedRate(new RemindTask(), 2500, seconds * 1000); // delay
    }

    // this is an inner class...
    class RemindTask extends TimerTask {

        @Override
        public void run() {
            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {
                    int page = ads_pager.getCurrentItem();
                    page++;
                    if (page > viewPagerAdapter.getCount() - 1) { // In my case the number of pages are 5
                        page = 0;
//                        timer.cancel();
                    }
                    ads_pager.setCurrentItem(page,true);
                }
            });
        }
    }


    private void headerNav(final boolean b) {

        drawerLayout.closeDrawers();
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(b == true){
                    Intent intent = new Intent(getApplicationContext(),ClientProfile.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter,R.anim.exit);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),NewLogin.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter,R.anim.exit);
                    finish();
                }
            }
        },300);

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
//            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }



    public void registerWithNotificationHubs() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.e(TAG, "This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAppRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAppRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppRunning = true;
        if(isAppRunning == true){
            loadData();
        }
        if (recyclerButton_adapter != null){
            recyclerNavigation_adapter.notifyItemChanged(5);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppRunning = false;

    }


    private Emitter.Listener newMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAppRunning == true){
                        if (isMessageLoading == false){
                            isMessageLoading = true;
                            String data = String.valueOf(args[0]);
                            try {
                                JSONObject object = new JSONObject(data);
                                int receive_recipientID = object.getInt("recipient_id");
                                if(object.has("sender_id")){
                                    int receive_senderID    = object.getInt("sender_id");
                                    if (Integer.parseInt(clientID) == receive_recipientID && receive_senderID != Integer.parseInt(clientID)){
                                        getMessage();
                                    }
                                    else{
                                        isMessageLoading = false;
                                    }
                                }
                                else{
                                    isMessageLoading = false;
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    };




}
