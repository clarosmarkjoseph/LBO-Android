package com.system.mobile.lay_bare.FAQs;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 16/11/2017.
 */

public class FAQuestions extends AppCompatActivity {

    View layout;
    Utilities utilities;
    String category_id   = "";
    String category_name = "";
    DataHandler handler;
    JSONArray arrayQuestion;
    JSONArray arrayHolder;

    RecyclerView recycler_faqDetails;
    RecyclerView.LayoutManager recycler_faq_manager;
    RecyclerView.Adapter recycler_faq_adapter;

    TextView title;
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_question);
        title = (TextView)findViewById(R.id.forTitle);
        setToolbar();
        arrayHolder         = new JSONArray();
        handler             = new DataHandler(getApplicationContext());
        recycler_faqDetails = (RecyclerView)findViewById(R.id.recycler_faqDetails);
        getExtra();
    }

    private void getExtra() {
        arrayQuestion = new JSONArray();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            category_id   = bundle.getString("category_id");
            category_name = bundle.getString("category_name");
            if(category_name.contains("About Waxing")){
                title.setText("About Waxing");
            }
            else{
                title.setText(category_name);
            }
        }

        handler.open();
        Cursor faq = handler.returnFAQ(category_id);
        if(faq.getCount() > 0){
            while(faq.moveToNext()){
                try {
                    String id           = faq.getString(0);
                    String question     = faq.getString(1);
                    String answer       = faq.getString(2);
                    JSONObject obj = new JSONObject();
                    obj.put("category_id",id);
                    obj.put("question",question);
                    obj.put("answer",answer);
                    arrayQuestion.put(obj);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            arrayHolder = arrayQuestion;
        }
        handler.close();
        displayFAQs();
    }

    private void displayFAQs() {

        recycler_faq_adapter  = new RecyclerFAQ(this,arrayHolder);
        recycler_faq_manager = new LinearLayoutManager(getApplicationContext());
        recycler_faqDetails.setAdapter(recycler_faq_adapter);
        recycler_faqDetails.setLayoutManager(recycler_faq_manager);
        recycler_faqDetails.setItemAnimator(new DefaultItemAnimator());
        recycler_faqDetails.setNestedScrollingEnabled(false);
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
    }




}
