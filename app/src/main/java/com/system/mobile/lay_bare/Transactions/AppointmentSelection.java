package com.system.mobile.lay_bare.Transactions;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ListView;

import com.system.mobile.lay_bare.Transactions.Adapter.OptionSelectBaseAdapter;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Profile.BaseAdapter.BaseAdapterHomeNavigation;
import com.system.mobile.lay_bare.Utilities.Utilities;

import java.util.ArrayList;

/**
 * Created by Mark on 03/10/2017.
 */

public class AppointmentSelection extends AppCompatActivity {

    Utilities utilities;
    OptionSelectBaseAdapter optionSelectBaseAdapter;
    BaseAdapterHomeNavigation navHomeBaseAdapter;
    RelativeLayout relative_optionSelect;
    ArrayList<Integer> arrayImage    = new ArrayList<Integer>();
    ArrayList<String>  arrayTitle    = new ArrayList<>();
    ArrayList<String>  arrayDesc     = new ArrayList<>();

    ListView listView;
    String date_selected = "";
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_select);
        setToolbarFonts();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            date_selected = bundle.getString("date");
        }

        utilities = new Utilities(this);
        relative_optionSelect = (RelativeLayout)findViewById(R.id.relative_optionSelect);

        if(arrayImage.size() <= 0) {
            arrayTitle.add("Branch Booking");
            arrayDesc.add("Book an appointment to our branches");
            arrayImage.add(R.drawable.a_appointment);
            arrayTitle.add("Home Service");
            arrayDesc.add("Home waxing service is now available");
            arrayImage.add(R.drawable.a_home);
        }

        optionSelectBaseAdapter = new OptionSelectBaseAdapter(this,arrayTitle,arrayDesc,arrayImage);
        listView = (ListView) findViewById(R.id.list_opt);
        listView.setAdapter(optionSelectBaseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(position==0){
                    Intent intent = new Intent(getApplicationContext(), AppointmentForm.class);
                    intent.putExtra("date",date_selected);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
                else if(position == 1){

                }
                else{

                }
            }
        });
    }

    private void setToolbarFonts() {
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
        forTitle.setText("Select Type");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
