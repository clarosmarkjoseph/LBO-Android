package com.system.mobile.lay_bare.PLC;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.system.mobile.lay_bare.ChatMessage.ChatMessage;
import com.system.mobile.lay_bare.ClientTransactions.FragmentTotalTransaction;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

/**
 * Created by paolohilario on 12/21/17.
 */

public class PremierClient extends AppCompatActivity {

    Typeface myTypeface;
    TextView forTitle;
    Utilities utilities;
    private ImageButton imgBtnBack;
    SwipeRefreshLayout swipRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListner;
    NestedScrollView nestedScrollview;
    Button btnPerks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.premier_client);
        setToolbar();
        initElements();
    }

    private void initElements() {
        utilities           = new Utilities(this);
        swipRefreshLayout   = (SwipeRefreshLayout)findViewById(R.id.swipRefreshLayout);
        nestedScrollview    = (NestedScrollView)findViewById(R.id.nestedScrollview);
        btnPerks            = (Button)findViewById(R.id.btnPerks);
        utilities.returnRefreshColor(swipRefreshLayout);
        swipeConfiguration();
        swipRefreshLayout.setRefreshing(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipRefreshLayout.setRefreshing(false);
            }
        },4000);
        loadTotalTransactions(false);
        btnPerks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PLCLogHistory.class);
                startActivity(intent);
            }
        });
    }

    private void swipeConfiguration() {

        swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipRefreshLayout.setRefreshing(true);
                loadTotalTransactions(true);
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


    private void loadTotalTransactions(boolean ifRestarted) {

        if (ifRestarted == true){
            Fragment fragment = null;
            fragment = getSupportFragmentManager().findFragmentById(R.id.frameTransaction);
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(fragment);
            ft.attach(fragment);
            ft.commit();
        }
        else{
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameTransaction, new FragmentTotalTransaction());
            fragmentTransaction.commit();
        }
    }

//    private void loadPremiere() {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.frameDetails, new PLCLogHistory());
//        fragmentTransaction.commit();
//    }

//    private void loadFrequency() {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.frameDetails, new PLCLogHistory());
//        fragmentTransaction.commit();
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
        forTitle.setText("Premiere Loyalty Card");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MySingleton.getInstance(getApplicationContext()).cancelAllRequests("requests");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
