package com.system.mobile.lay_bare.Transactions;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.Location.BranchReview;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.NavigationDrawer.NavigationDrawerActivity;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 06/12/2017.
 */

public class AppointmentHistory extends AppCompatActivity {

    TabLayout tabsHistory;
    ViewPager viewpager_appointments;
    TabAdapter tabAdapter;
    private TextView forTitle;
    private Typeface myTypeface;
    private ImageButton imgBtnBack;
    Utilities utilities;
    String SERVER_URL = "";
    DataHandler handler;
    private ArrayList<String> arrayErrorResponse;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_history);
        setToolbar();
        initElements();
    }


    private void initElements() {

        tabsHistory             = (TabLayout)findViewById(R.id.tabsHistory);
        viewpager_appointments  = (ViewPager)findViewById(R.id.viewpager_appointments);
        utilities               = new Utilities(this);
        SERVER_URL              = utilities.returnIpAddress();
        handler                 = new DataHandler(getApplicationContext());

        tabsHistory.addTab(tabsHistory.newTab().setIcon(R.drawable.a_ongoing).setText("My Appointment"));
        tabsHistory.addTab(tabsHistory.newTab().setIcon(R.drawable.a_history).setText("History"));

        tabsHistory.setSelectedTabIndicatorColor(getResources().getColor(R.color.brownLoading));
        tabsHistory.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.brownLoading), PorterDuff.Mode.SRC_IN);
        tabsHistory.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.themeLightGray), PorterDuff.Mode.SRC_IN);

        tabsHistory.setTabGravity(TabLayout.GRAVITY_FILL);
        tabAdapter              = new TabAdapter(getSupportFragmentManager(),getApplicationContext(),tabsHistory.getTabCount());
        viewpager_appointments.setAdapter(tabAdapter);
        viewpager_appointments.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabsHistory));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewpager_appointments.setNestedScrollingEnabled(false);
        }
        int limit = (tabAdapter.getCount() > 1 ? tabAdapter.getCount() - 1 : 1);
        viewpager_appointments.setOffscreenPageLimit(limit);

        tabsHistory.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(),R.color.brownLoading);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                viewpager_appointments.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(),R.color.themeLightGray);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            int position = bundle.getInt("position");
            viewpager_appointments.setCurrentItem(position);
        }
        else{
            viewpager_appointments.setCurrentItem(0);
        }
    }


    public void setToolbar(){
        myTypeface          = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle                = (TextView)findViewById(R.id.forTitle);
        imgBtnBack              = (ImageButton) findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forTitle.setTypeface(myTypeface);
        forTitle.setText("Appointment History");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




}
