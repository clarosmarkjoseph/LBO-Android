package com.system.mobile.lay_bare.FAQs;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 10/11/2017.
 */

public class FAQ extends AppCompatActivity {

    RecyclerView recycler_faq;
    RecyclerView.LayoutManager recycler_faq_manager;
    RecyclerView.Adapter recycler_faq_adapter;

    Utilities utilities;
    DataHandler handler;
    String SERVER_URL   = "";
    private JSONArray arrayFAQ      = new JSONArray();
    private JSONArray arrayFAQCat   = new JSONArray();
    private ArrayList<String> arrayErrorResponse = new ArrayList<>();
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq);
        setToolbar();
        utilities  = new Utilities(this);
        handler    = new DataHandler(getApplicationContext());
        SERVER_URL = utilities.returnIpAddress();
        recycler_faq = (RecyclerView)findViewById(R.id.recycler_faq);
        getFAQs();
    }

    private void getFAQs() {

        utilities.showProgressDialog("Loading...");
        String url = SERVER_URL+"/api/faq/getFAQs";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arrayCategory     = response.getJSONArray("category");
                            arrayFAQCat                 = arrayCategory;
                            handler.open();
                            handler.deleteFAQCategory();
                            handler.deleteFAQ();
                            handler.insertFAQCategory(arrayFAQCat.toString());
                            handler.close();
                            JSONArray arrayQuestions    = response.getJSONArray("questions");
                            for (int x = 0; x < arrayQuestions.length(); x++){
                                JSONObject objResult = arrayQuestions.getJSONObject(x);
                                String id            = objResult.getString("id");
                                String question      = objResult.getString("question");
                                String answer        = objResult.getString("answer");
                                String cat_id        = objResult.getString("category");
                                handler.open();
                                handler.insertFAQ(id,question,answer,cat_id);
                                handler.close();
                                JSONObject obj = new JSONObject();
                                obj.put("question",question);
                                obj.put("answer",answer);
                                obj.put("category_id",answer);
                                arrayFAQ.put(obj);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        displayFAQs();
                        utilities.hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR",error.toString());
                        arrayErrorResponse = utilities.errorHandling(error);
                        Toast.makeText(getApplicationContext(),arrayErrorResponse.get(1),Toast.LENGTH_SHORT).show();
                        utilities.hideProgressDialog();
                        setUpOffLine();
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    private void setUpOffLine(){
        if(arrayFAQCat.length() <= 0 || arrayFAQ.length() <= 0){
            handler.open();
            Cursor queryCat = handler.returnFAQCategory();
            while(queryCat.moveToNext()){
                try {
                    arrayFAQCat = new JSONArray(queryCat.getString(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        handler.close();
        displayFAQs();
    }

    private void displayFAQs() {
        Log.e("wew",arrayFAQCat.toString());
        recycler_faq_adapter  = new RecyclerFAQMenu(this,arrayFAQCat);
        recycler_faq_manager = new LinearLayoutManager(getApplicationContext());
        recycler_faq.setAdapter(recycler_faq_adapter);
        recycler_faq.setLayoutManager(recycler_faq_manager);
        recycler_faq.setItemAnimator(new DefaultItemAnimator());
        recycler_faq.setNestedScrollingEnabled(false);

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
        forTitle.setText("Frequently Asked Question");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);
    }


}
