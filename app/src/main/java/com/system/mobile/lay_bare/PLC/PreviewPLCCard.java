package com.system.mobile.lay_bare.PLC;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.Location.LocationTabOptionsAdapter;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by paolohilario on 12/29/17.
 */

public class PreviewPLCCard extends AppCompatActivity {
    Toolbar toolbar;
    Typeface myTypeface;
    TextView forTitle;
    ViewPager viewPager_card;
    LinearLayout linear_content_no_internet,linear_loading;
    PreviewPLCCardAdapter previewPLCCardAdapter;
    Utilities utilities;
    String gender = "";
    String SERVER_URL;
    JSONObject objectData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plc_preview_card);
        initElements();
    }

    private void initElements() {
        utilities                   = new Utilities(this);
        linear_content_no_internet  = (LinearLayout) findViewById(R.id.linear_content_no_internet);
        linear_loading              = (LinearLayout) findViewById(R.id.linear_loading);
        viewPager_card              = (ViewPager) findViewById(R.id.viewPager_card);
        gender                      = utilities.getGender();
        SERVER_URL                  = utilities.returnIpAddress();
        getCardDetails();
    }


    private void getCardDetails() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            try{
                objectData = new JSONObject(bundle.getString("details"));
                JSONObject objectParams  = new JSONObject();
                objectParams.put("full_name",utilities.getClientName());
                objectParams.put("branch",objectData.getString("branch_name"));
                objectParams.put("reference_no",objectData.getString("reference_no"));
                objectParams.put("bday",utilities.getClientBday());
                objectParams.put("gender",gender);
                previewPLCCardAdapter    = new PreviewPLCCardAdapter(PreviewPLCCard.this,objectParams);
                int limit                = (previewPLCCardAdapter.getCount() > 1 ? previewPLCCardAdapter.getCount() - 1 : 1);
                viewPager_card.setAdapter(previewPLCCardAdapter);
                viewPager_card.setOffscreenPageLimit(limit);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
