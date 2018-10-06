package com.system.mobile.lay_bare.GeneralActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.RecyclerView.RecyclerSelection;
import com.system.mobile.lay_bare.Transactions.TimeRecyclerPositionInterface;
import com.system.mobile.lay_bare.Utilities.Utilities;

import java.util.ArrayList;

/**
 * Created by paolohilario on 3/2/18.
 */

public class TimeSelection extends AppCompatActivity implements TimeRecyclerPositionInterface{

    TextView lblDate,lblHour,lblMinute;
    Button btnNext;
    RecyclerView recyclerSelection;
    RecyclerView.LayoutManager recyclerLayoutmanager;
    RecyclerView.Adapter recyclerAdapter;
    Utilities utilities;
    String app_reserved = "";
    Bundle bundle;
    ArrayList<String> arrayStandardTime          = new ArrayList<>();
    public int selectedIndex = 0;
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;
    TextView lblNoSchedule;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_selection);
        setToolbar();
        initElements();
    }
    private void setToolbar() {
        myTypeface          = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle                = (TextView)findViewById(R.id.forTitle);
        imgBtnBack              = (ImageButton) findViewById(R.id.imgBtnBack);
        lblNoSchedule           = (TextView)findViewById(R.id.lblNoSchedule);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forTitle.setTypeface(myTypeface);
        forTitle.setText("Appointment Schedule ");
    }

    private void initElements() {
        utilities           = new Utilities(this);
        lblDate             = (TextView)findViewById(R.id.lblDate);
        lblHour             = (TextView)findViewById(R.id.lblHour);
        lblMinute           = (TextView)findViewById(R.id.lblMinute);
        btnNext             = (Button)findViewById(R.id.btnNext);
        recyclerSelection   = (RecyclerView)findViewById(R.id.recyclerSelection);

        bundle              = getIntent().getExtras();
        if (bundle != null) {
            app_reserved        = bundle.getString("app_reserved");
            arrayStandardTime   = bundle.getStringArrayList("array_schedule");
            selectedIndex       = bundle.getInt("selectedIndex");
            lblDate.setText(utilities.getCompleteDateMonth(app_reserved));
            if (arrayStandardTime.size() > 0){
//                btnNext.setEnabled(true);
//                btnNext.setAlpha(1);
                String[] timeSel = arrayStandardTime.get(selectedIndex).split(":");
                lblHour.setText(timeSel[0]);
                lblMinute.setText(timeSel[1]);
                recyclerSelection.setVisibility(View.VISIBLE);
                lblNoSchedule.setVisibility(View.GONE);
                setAdapter();
            }
            else{
//                btnNext.setEnabled(false);
//                btnNext.setAlpha((float) 0.5);
//                btnNext.setText("No more available time.");
                lblNoSchedule.setVisibility(View.VISIBLE);
                recyclerSelection.setVisibility(View.GONE);
            }
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(arrayStandardTime.size() > 0){
                   Intent intent = new Intent();
                   intent.putExtra("selectedIndex",selectedIndex);
                   Log.e("selectedIndex", String.valueOf(selectedIndex));
                   setResult(RESULT_OK, intent);
                   finish();
               }
               else{
                   utilities.showDialogMessage("No time Selection","Sorry, there is no available time on this day. Please choose other date.","error");
               }
            }
        });


    }

    void setAdapter(){
        recyclerLayoutmanager   = new GridLayoutManager(this,4);
        recyclerAdapter         = new RecyclerSelection(this,arrayStandardTime,lblHour,lblMinute,selectedIndex,btnNext);
        recyclerSelection.setAdapter(recyclerAdapter);
        recyclerSelection.setLayoutManager(recyclerLayoutmanager);
        recyclerSelection.setItemAnimator(new DefaultItemAnimator());
        recyclerSelection.setNestedScrollingEnabled(false);
    }

    @Override
    public void setIndex(int position) {
        this.selectedIndex = position;
    }

}
