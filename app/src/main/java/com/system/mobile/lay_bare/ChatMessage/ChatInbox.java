package com.system.mobile.lay_bare.ChatMessage;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.ObservableChat;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.SingletonGlobal;
import com.system.mobile.lay_bare.Sockets.SocketApplication;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

/**
 * Created by paolohilario on 4/12/18.
 */

public class ChatInbox extends AppCompatActivity implements Observer {

    Utilities utilities;
    DataHandler handler;
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListner;
    LinearLayout linear_content_no_internet,linear_loading;
    TextView lblCaption;
    RecyclerView recyclerInbox;
    RecyclerView.Adapter recyclerAdapter;
    RecyclerView.LayoutManager recyclerManager;
    String SERVER_URL = "";
    private ArrayList<String> arrayErrorResponse;
    private SocketApplication socketApplication;
    private Socket mSocket;
    String token;
    int clientID            = 0;
    static boolean isAppRunning     = false;
    boolean isLatestLoading         = false;
    ArrayList<JSONObject> arrayListInbox;
    JSONArray arrayMessage  = new JSONArray();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_inbox);
        ObservableChat.getInstance().addObserver(this);
        setToolbar();
        initElement();
    }

    private void initElement() {
        utilities                   = new Utilities(this);
        handler                     = new DataHandler(getApplicationContext());

        swipeRefreshLayout          = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        recyclerInbox               = (RecyclerView)findViewById(R.id.recyclerInbox);
        linear_content_no_internet  = (LinearLayout)findViewById(R.id.linear_content_no_internet);
        linear_loading              = (LinearLayout)findViewById(R.id.linear_loading);
        lblCaption                  = (TextView)findViewById(R.id.lblCaption);

        SERVER_URL                  = utilities.returnIpAddress();
        clientID                    = Integer.parseInt(utilities.getClientID());
        token                       = utilities.getToken();

        socketApplication           = new SocketApplication();
        mSocket                     = socketApplication.getSocket();
        mSocket                     = socketApplication.getSocket();
        showLoading();
        utilities.returnRefreshColor(swipeRefreshLayout);
        swipeConfiguration();
        swipeRefreshListner.onRefresh();

    }

    private void swipeConfiguration() {

        swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getMessage();
            }
        };

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshListner.onRefresh();
            }
        });
    }


    private void getMessage(){

        isLatestLoading = true;
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
            objectParams.put("arrayThreadID",String.valueOf(arrayThreadID));
            objectParams.put("arrayLastID",String.valueOf(arrayLastID));
            String url   = SERVER_URL+"/api/mobile/getAllChatMessage?token="+token;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url,objectParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject res) {

                            try {
                                swipeRefreshLayout.setRefreshing(false);
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
                                            String title             = objectMessage.optString("title",null);
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
                                            if(y >= arrayMessages.length() - 1){
                                                if(!read_at.equals("null") || !read_at.equals("")){
                                                    handler.setChatAsRead(thread_id,utilities.getCurrentDateTime());
                                                    handler.setThreadAsSeen(thread_id);
                                                }
                                            }
                                            handler.close();
                                        }
                                    }
                                }
                                handler.close();
                                displayInbox();
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            arrayErrorResponse = utilities.errorHandling(error);
                            swipeRefreshLayout.setRefreshing(false);
                            Log.e("ERROR chat",arrayErrorResponse.get(1).toString());
                            displayInbox();
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

    private void displayInbox() {

        showContent();
        handler.open();
        arrayListInbox = new ArrayList<>();
        Cursor cursorThread = handler.returnAllThread();
        if(cursorThread.getCount() > 0){

            recyclerInbox.setVisibility(View.VISIBLE);
            lblCaption.setVisibility(View.GONE);
            while (cursorThread.moveToNext()){

                int thread_id           = cursorThread.getInt(0);
                String participants     = cursorThread.getString(1);
                String thread_name      = cursorThread.getString(2);
                String created_by_id    = cursorThread.getString(3);
                String dateTime         = cursorThread.getString(4);
                JSONObject objectParams = new JSONObject();
                try {
                    Cursor cursorMessage    = handler.returnLastChatMessageByThreadID(thread_id);
                    if (cursorMessage.getCount() > 0){

                        while (cursorMessage.moveToNext()){

                            arrayMessage        = new JSONArray();
                            int message_id      = cursorMessage.getInt(0);
                            int sender_id       = cursorMessage.getInt(1);
                            int recipient_id    = cursorMessage.getInt(2);
                            String title        = cursorMessage.getString(4);
                            String body         = cursorMessage.getString(5);
                            String message_data = cursorMessage.getString(6);
                            String message_time = cursorMessage.getString(7);
                            String isRead       = cursorMessage.getString(8);
                            String status       = cursorMessage.getString(9);

                            JSONObject objectMessage = new JSONObject();
                            objectMessage.put("message_id",message_id);
                            objectMessage.put("sender_id",sender_id);
                            objectMessage.put("recipient_id",recipient_id);
                            objectMessage.put("title",title);
                            objectMessage.put("body",body);
                            objectMessage.put("message_data",message_data);
                            objectMessage.put("read_at",isRead);
                            objectMessage.put("status",status);
                            arrayMessage.put(objectMessage);
                        }
                        objectParams.put("id",thread_id);
                        objectParams.put("participants",participants);
                        objectParams.put("thread_name",thread_name);
                        objectParams.put("created_by_id",created_by_id);
                        objectParams.put("dateTime",dateTime);
                        objectParams.put("arrayMessage",arrayMessage);
                        arrayListInbox.add(objectParams);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setAdapter();
        }
        else{
            recyclerInbox.setVisibility(View.GONE);
            lblCaption.setVisibility(View.VISIBLE);
            setAdapter();
        }
        handler.close();

    }


    private void setAdapter() {
        swipeRefreshLayout.setRefreshing(false);
        recyclerAdapter         = new RecyclerInbox(ChatInbox.this,arrayListInbox);
        recyclerManager         = new LinearLayoutManager(ChatInbox.this);
        recyclerInbox.setHasFixedSize(true);
        recyclerInbox.setLayoutManager(recyclerManager);
        recyclerInbox.setAdapter(recyclerAdapter);
        recyclerInbox.setItemAnimator(new DefaultItemAnimator());
        isLatestLoading = false;
    }

    private void showLoading(){
        linear_content_no_internet.setVisibility(View.GONE);
        recyclerInbox.setVisibility(View.GONE);
        linear_loading.setVisibility(View.VISIBLE);
    }

    private void showContent(){
        linear_loading.setVisibility(View.GONE);
        linear_content_no_internet.setVisibility(View.GONE);
        recyclerInbox.setVisibility(View.VISIBLE);
    }

    private void setToolbar() {
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
        forTitle.setTypeface(myTypeface);
        forTitle.setText("Message Inbox");
    }



    @Override
    protected void onStart() {
        super.onStart();
        isAppRunning = true;
        if (arrayListInbox != null){
            displayInbox();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppRunning = false;
    }

    @Override
    public void update(Observable o, Object arg) {
        displayInbox();
    }


}
