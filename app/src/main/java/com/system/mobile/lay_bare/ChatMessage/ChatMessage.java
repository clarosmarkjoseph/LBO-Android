package com.system.mobile.lay_bare.ChatMessage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.DetectionConnection;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.ObservableChat;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.SingletonGlobal;
import com.system.mobile.lay_bare.Sockets.SocketApplication;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

/**
 * Created by paolohilario on 3/22/18.
 */

public class ChatMessage extends AppCompatActivity implements InterfaceForChat,Observer {

    private TextView forTitle;
    private ImageButton imgBtnBack;
    RecyclerView recyclerChat;
    RecyclerView.LayoutManager recyclerLayoutmanager;
    RecyclerView.Adapter recyclerAdapter;
    EditText txtBodyChat;
    ImageButton imgBtnSendChat;
    Utilities utilities;
    DataHandler handler;
    private ArrayList<String> arrayErrorResponse;
    RelativeLayout relativeLoading;
    RelativeLayout relativeConnection;
    TextView lblConnectionStatus;
    ProgressBar progressBar;
    private Socket mSocket;
    TextView forCaption,lblGreeting;
    Handler activityHandler;
    int recipientID         = 0;
    int thread_id           = 0;
    int clientID            = 0;
    int lastChatID          = 0;
    int firstChatID         = 0;
    String token                    = "";
    String SERVER_URL               = "";
    String userName                 = "";
    String lastReceiverTimeActive   = "";
    SingletonGlobal singletonGlobal;
    SocketApplication socketApplication;
    ArrayList<JSONObject> arrayListChat;
    boolean hasMore                 = true;
    boolean isLoading               = false;
    static boolean isAppRunning     = false;
    static int countLimit   = 20;
    int offset              = 0;
    InputMethodManager imm;
    String type = "";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_message);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        ObservableChat.getInstance().addObserver(this);
        setToolbar();
        initElements();
    }

    private void initElements() {

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            arrayListChat       = new ArrayList<>();
            recipientID         = bundle.getInt("recipient_id");
            thread_id           = bundle.getInt("thread_id");
            userName            = bundle.getString("userName");
            type                = bundle.getString("chat_type");
            handler             = new DataHandler(getApplicationContext());
            utilities           = new Utilities(this);
            clientID            = Integer.parseInt(utilities.getClientID());
            SERVER_URL          = utilities.returnIpAddress();
            token               = utilities.getToken();
            lblGreeting         = (TextView) findViewById(R.id.lblGreeting);
            lblConnectionStatus = (TextView) findViewById(R.id.lblConnectionStatus);
            txtBodyChat         = (EditText)findViewById(R.id.txtBodyChat);
            imgBtnSendChat      = (ImageButton)findViewById(R.id.imgBtnSendChat);
            recyclerChat        = (RecyclerView)findViewById(R.id.recyclerChat);
            relativeLoading     = (RelativeLayout)findViewById(R.id.relativeLoading);
            relativeConnection  = (RelativeLayout) findViewById(R.id.relativeConnection);
            progressBar         = (ProgressBar) findViewById(R.id.progressBar);

            configureListener();
            checkIfRecipientIsOnline();
            forTitle.setText(userName);
            singletonGlobal                 = new SingletonGlobal();
            boolean ifSocketIsSet           = singletonGlobal.Instance().checkIfSocketSet();
            activityHandler                 = new Handler();
            activityHandler.postDelayed(timerActiveStatusRunnable,0);

            if (!ifSocketIsSet){
                socketApplication               = new SocketApplication();
                mSocket                         = socketApplication.getSocket();
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeOut);
                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Transport transport = (Transport) args[0];
                        transport.on(Transport.EVENT_ERROR, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Exception e = (Exception) args[0];
                                e.printStackTrace();
                                e.getCause().printStackTrace();
                            }
                        });
                    }
                });
                mSocket.connect();
                mSocket.open();
            }
            else{
                SocketApplication mySocketApp   = singletonGlobal.Instance().getMySocket();
                mSocket                         = mySocketApp.getSocket();
                socketApplication               = mySocketApp;
                mSocket                         = socketApplication.getSocket();
            }
            setAdapter();

            if (type.equals("message")){
                getMessage();
            }
            if (type.equals("inbox")){
                iterateMessageInDB("load");
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"No Receipient ID",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //method will call when there is no message
    private void getMessage(){

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
                                            handler.close();
                                        }
                                    }
                                }
                                handler.close();
                                iterateMessageInDB("load");
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            arrayErrorResponse = utilities.errorHandling(error);
                            Log.e("ERROR chat",arrayErrorResponse.get(1).toString());
                            iterateMessageInDB("load");
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


    private void iterateMessageInDB(String status) {

        handler.open();
        Cursor queryChat;
        if(status.equals("load") || status.equals("previous")){
            queryChat = handler.returnChatMessageWithOffset(thread_id,offset);
            offset += queryChat.getCount();
            if(queryChat.getCount() <= 0){
                if(hasMore == true){
                    getMessageOnCurrentThread();
                }
                else{
                    relativeLoading.setVisibility(View.GONE);
                }
            }
            else if(queryChat.getCount() >= countLimit){
                displayCursor(queryChat,status);
            }
            else{
                displayCursor(queryChat,status);
            }
        }
        else{
            queryChat = handler.returnLatestChatMessage(thread_id);
            displayCursor(queryChat,status);
        }
        handler.close();
    }

    private void displayCursor(Cursor cursorObject, String status) {

        int index        = 0;
        int dbCount      = cursorObject.getCount();
        int arrayListCount = ((arrayListChat.size() <= 0) ? 0 : arrayListChat.size());
        if(dbCount > 0){

            while(cursorObject.moveToNext()){
                JSONObject objectParams = new JSONObject();
                int chatID              = Integer.parseInt(cursorObject.getString(0));
                int senderID            = Integer.parseInt(cursorObject.getString(1));
                int resID               = Integer.parseInt(cursorObject.getString(2));
                String title            = cursorObject.getString(4);
                String body             = cursorObject.getString(5);
                String message_data     = cursorObject.getString(6);
                String dateTime         = cursorObject.getString(7);
                String isRead           = cursorObject.getString(8);
                String msgStatus        = cursorObject.getString(9);
                try {
                    objectParams.put("chatID", chatID);
                    objectParams.put("senderID", senderID);
                    objectParams.put("recipientID", resID);
                    objectParams.put("title", title);
                    objectParams.put("body", body);
                    objectParams.put("message_data", message_data);
                    objectParams.put("read_at", isRead);
                    objectParams.put("dateTime", dateTime);
                    objectParams.put("status", msgStatus);

                    if (status.equals("load") || status.equals("latest")) {
                        arrayListChat.add(objectParams);
                        if (index == 0) {
                            lastChatID = chatID;
                        }
                    }
                    else {
                        arrayListChat.add(0, objectParams);
                    }
                    if (index >= dbCount - 1) {
                        if (isRead.equals("0000-00-00 00:00:00") || isRead.equals("null") || isRead.equals(null) || isRead == null || isRead.equals("")) {
                            markMessageAsRead();
                        }
                        firstChatID = chatID;
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                index++;
            }
            finalDisplay(status);
        }
        else{
            relativeLoading.setVisibility(View.GONE);
            if(arrayListCount <= 0){
                lblGreeting.setVisibility(View.VISIBLE);
            }
        }
    }

    private void finalDisplay(String status) {

        Collections.sort(arrayListChat, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    double date1 = utilities.convertDateTimeSecondToMilliSeconds(o1.getString("dateTime"));
                    double date2 = utilities.convertDateTimeSecondToMilliSeconds(o2.getString("dateTime"));
                    return Double.compare(date1, date2);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        recyclerAdapter.notifyDataSetChanged();
        if(arrayListChat.size() <= 0){
            lblGreeting.setVisibility(View.VISIBLE);
        }
        else{
            lblGreeting.setVisibility(View.GONE);
        }

        if(status.equals("latest") || status.equals("load")){
            recyclerChat.scrollToPosition(arrayListChat.size() - 1);
            relativeLoading.setVisibility(View.GONE);
            isLoading = false;
        }
        else{
            Handler runningHandler = new Handler();
            runningHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    relativeLoading.setVisibility(View.GONE);
                    isLoading = false;
                }
            },1000);
        }
    }


    private void getMessageOnCurrentThread(){

        String url  = SERVER_URL+"/api/mobile/getMessageOnCurrentThread/"+thread_id+"/"+offset+"?token="+token;
//        Log.e("Offset", String.valueOf(offset)+" - "+url);
        StringRequest objectRequest  = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arrayResult = new JSONArray(response);
                            if(arrayResult.length() >=countLimit){
                                hasMore = true;
                            }
                            else{
                                hasMore = false;
                            }
                            for (int y = 0; y < arrayResult.length(); y++) {
                                JSONObject objectMessage    = arrayResult.getJSONObject(y);
                                int message_id              = objectMessage.optInt("id", 0);
                                int sender_id               = objectMessage.optInt("sender_id", 0);
                                int recipient_id            = objectMessage.optInt("recipient_id", 0);
                                int deleted_to_id           = objectMessage.optInt("deleted_to_id", 0);
                                int message_thread_id       = objectMessage.optInt("message_thread_id", 0);
                                String title                = objectMessage.optString("title", null);
                                String body                 = objectMessage.optString("body", "");
                                String message_data         = objectMessage.optString("message_data", "");
                                String read_at              = objectMessage.optString("read_at", "");
                                String message_created      = objectMessage.optString("created_at", "0000-00-00 00:00:00");
                                String status               = "";
                                String thread_status        = "";
                                if (read_at.equals("0000-00-00 00:00:00") || read_at.equals("null") || read_at.equals("")) {
                                    status = "sent";
                                }
                                else {
                                    status = "seen";
                                    thread_status = "seen";
                                }
                                handler.open();
                                Cursor cursorMessage = handler.returnChatMessageByID(message_id);
                                if (cursorMessage.getCount() > 0) {
                                    handler.updateChatMessage(message_id, sender_id, recipient_id, message_thread_id, title, body, message_data, message_created, read_at, status);
                                } else {
                                    handler.insertChatMessage(message_id, sender_id, recipient_id, message_thread_id, title, body, message_data, message_created, read_at, status);
                                }
                                handler.updateThreadTime(thread_id,thread_status, message_created);
                                handler.close();
                            }
                            iterateMessageInDB("load");
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        updateMessageAsSeen(thread_id);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {

                    }
                })
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("sender_id", String.valueOf(recipientID));
                params.put("thread_id", String.valueOf(thread_id));
                return params;
            }
        };
        objectRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
    }

//    private void requestForPreviousMessage() {
//
//        JSONObject objectParams         = new JSONObject();
//        String plc_url                  = SERVER_URL+"/api/mobile/getPreviousMessage?token="+token;
//        try {
//            objectParams.put("message_thread_id",thread_id);
//            objectParams.put("last_chat_id",firstChatID);
//            StringRequest stringRequest  = new StringRequest
//                    (Request.Method.POST, plc_url, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String result) {
//
//                            Log.e("response",result);
//                            try {
//
//                                JSONArray arrayMessages = new JSONArray(result);
//                                if (arrayMessages.length() > 0){
//                                    if (arrayMessages.length() >= countLimit){
//                                        hasMore = true;
//                                    }
//                                    else{
//                                        hasMore = false;
//                                    }
//                                    for (int x = 0; x < arrayMessages.length(); x++){
//                                        JSONObject objectMessage = arrayMessages.getJSONObject(x);
//                                        int message_id           = objectMessage.optInt("id",0);
//                                        int sender_id            = objectMessage.optInt("sender_id",0);
//                                        int recipient_id         = objectMessage.optInt("recipient_id",0);
//                                        int deleted_to_id        = objectMessage.optInt("deleted_to_id",0);
//                                        int message_thread_id    = objectMessage.optInt("message_thread_id",0);
//                                        String title             = objectMessage.optString("title","No Title");
//                                        String body              = objectMessage.optString("body","");
//                                        String message_data      = objectMessage.optString("message_data","");
//                                        String read_at           = objectMessage.optString("read_at","");
//                                        String message_created   = objectMessage.optString("created_at","0000-00-00 00:00:00");
//                                        String status            = "";
//                                        String thread_status     = "";
//                                        if(read_at.equals("0000-00-00 00:00:00") || read_at.equals("null") || read_at.equals("") ){
//                                            status = "sent";
//                                        }
//                                        else{
//                                            status = "seen";
//                                            thread_status = "seen";
//                                        }
//                                        handler.open();
//                                        Cursor cursorMessage = handler.returnChatMessageByID(message_id);
//                                        if(cursorMessage.getCount() > 0){
//                                            handler.updateChatMessage(message_id,sender_id,recipient_id,message_thread_id,title,body,message_data,message_created,read_at,status);
//                                        }
//                                        else{
//                                            handler.insertChatMessage(message_id,sender_id,recipient_id,message_thread_id,title,body,message_data,message_created,read_at,status);
//                                        }
//                                        handler.updateThreadTime(thread_id,thread_status,message_created);
//                                        handler.close();
//                                    }
//                                    iterateMessageInDB("previous");
//                                }
//                            }
//                            catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            arrayErrorResponse = utilities.errorHandling(error);
//                            Log.e("arrayErrorResponse",arrayErrorResponse.toString());
//                            iterateMessageInDB("previous");
//                        }
//                    })
//                {
//                    @Override
//                    public Map<String, String> getParams() throws AuthFailureError {
//                        Map<String,String> params = new HashMap<String, String>();
//                        params.put("message_thread_id", String.valueOf(thread_id));
//                        params.put("last_chat_id", String.valueOf(firstChatID));
//                        return params;
//                    }
//                };;
//            stringRequest.setRetryPolicy(
//                    new DefaultRetryPolicy(
//                            15000,
//                            3,
//                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                    )
//            );
//            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
//
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void requestAction(int positionRetry, String action) {
        if (action.equals("retry")){
            try {
                JSONObject objectFetch  =  arrayListChat.get(positionRetry);
                int chatID              =  objectFetch.getInt("chatID");
                String message          =  objectFetch.getString("body");
                sendMessage(message,false,positionRetry);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void setAdapter() {
        recyclerLayoutmanager   = new LinearLayoutManager(this);
        recyclerAdapter         = new RecyclerMessage(this,arrayListChat,clientID);
        recyclerChat.setLayoutManager(recyclerLayoutmanager);
        recyclerChat.setNestedScrollingEnabled(false);
        recyclerChat.setHasFixedSize(false);
        recyclerChat.setAdapter(recyclerAdapter);
        recyclerChat.setItemAnimator(new DefaultItemAnimator());
        recyclerChat.invalidate();
    }


    private void validateMessage(){
        final String textMessage            = txtBodyChat.getText().toString();
        sendMessage(textMessage,true, 0);
    }

    private void sendMessage(final String textMessage, final boolean ifNewMessage, int positionRetry){

        int initChatID;
        try {
            JSONObject objectMessageParams      = new JSONObject();
            final JSONObject objectInitSending  = new JSONObject();
            String currentDate                  = utilities.getCurrentDateTime();
            txtBodyChat.setText("");
            imgBtnSendChat.setAlpha((float) 0.5);
            imgBtnSendChat.setClickable(false);
            lblGreeting.setVisibility(View.GONE);

            if(ifNewMessage == false){
                JSONObject objectPrevious   = arrayListChat.get(positionRetry);
                initChatID                  = objectPrevious.optInt("chatID",0);
                objectPrevious.put("dateTime",currentDate);
                objectPrevious.put("status","sending");
                arrayListChat.set(positionRetry,objectPrevious);
                handler.open();
                handler.updateChatMessage(initChatID,clientID,recipientID,thread_id,"null",textMessage,"{}",utilities.getCurrentDateTime(),"0000-00-00 00:00:00","sending");
                handler.updateThreadTime(thread_id,"",currentDate);
                handler.close();
            }
            else{
                initChatID      = lastChatID + 1;
                positionRetry   = ((arrayListChat.size() <= 0) ? 0 : arrayListChat.size() - 1);
                objectInitSending.put("chatID",initChatID);
                objectInitSending.put("senderID",clientID);
                objectInitSending.put("recipientID",recipientID);
                objectInitSending.put("title",null);
                objectInitSending.put("body",textMessage);
                objectInitSending.put("message_data","{}");
                objectInitSending.put("read_at","0000-00-00 00:00:00");
                objectInitSending.put("dateTime",currentDate);
                objectInitSending.put("status","sending");

                arrayListChat.add(objectInitSending);
                handler.open();
                handler.insertChatMessage(initChatID,clientID,recipientID,thread_id,"null",textMessage,"{}",utilities.getCurrentDateTime(),"0000-00-00 00:00:00","sending");
                handler.updateThreadTime(thread_id,"",currentDate);
                handler.close();
            }

            recyclerAdapter.notifyDataSetChanged();
            objectMessageParams.put("body",textMessage);
            objectMessageParams.put("recipient_id",recipientID);
            objectMessageParams.put("thread_id",thread_id);

            final int finalChatPosition     = ((arrayListChat.size() <= 0) ? 0 : arrayListChat.size() - 1);
            final int finalChatID           = initChatID;
            recyclerChat.smoothScrollToPosition(positionRetry);

            Log.e("send message",String.valueOf(objectInitSending));
            String url  = SERVER_URL+"/api/mobile/sendChatMessage?token="+token;
            JsonObjectRequest objectRequest  = new JsonObjectRequest
                    (Request.Method.POST, url, objectMessageParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String result = response.getString("result");
                                if(result.equals("success")){
                                    int chat_id             = response.getInt("latestChatID");
                                    JSONObject object_sent  = response.getJSONObject("object_sent");
                                    int sender_id           = object_sent.getInt("sender_id");
                                    int recipient_id        = object_sent.getInt("recipient_id");
                                    String title            = object_sent.getString("title");
                                    String body             = object_sent.getString("body");
                                    String read_at          = object_sent.getString("read_at");
                                    String created_at       = object_sent.getString("created_at");
                                    String message_data     = object_sent.getString("message_data");
                                    String thread_status    = "";
                                    objectInitSending.put("chatID",chat_id);
                                    objectInitSending.put("senderID",sender_id);
                                    objectInitSending.put("recipientID",recipient_id);
                                    objectInitSending.put("title",title);
                                    objectInitSending.put("body",body);
                                    objectInitSending.put("message_data",message_data);
                                    objectInitSending.put("read_at","0000-00-00 00:00:00");
                                    objectInitSending.put("dateTime",created_at);
                                    objectInitSending.put("status","sent");
                                    if(!read_at.equals("0000-00-00 00:00:00") || !read_at.equals("null") || !read_at.equals("") ){
                                        thread_status = "sent";
                                    }
                                    handler.open();
                                    handler.deleteSpecificChatMessage(finalChatID);
                                    handler.insertChatMessage(chat_id,sender_id,recipient_id,thread_id,title,body,message_data,created_at,read_at,"sent");
                                    handler.updateThreadTime(thread_id,thread_status,created_at);
                                    handler.close();

                                    arrayListChat.set(finalChatPosition,objectInitSending);
                                    recyclerAdapter.notifyDataSetChanged();
                                    recyclerChat.smoothScrollToPosition(finalChatPosition);
                                    mSocket.emit("newMessage",recipient_id);
                                    getMessageOnCurrentThread();
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError error) {
                            arrayErrorResponse  = utilities.errorHandling(error);
                            try {
                                handler.open();
                                handler.updateChatMessage(finalChatID,clientID,recipientID,thread_id,"null",textMessage,"{}",utilities.getCurrentDateTime(),"0000-00-00 00:00:00","failed");
                                handler.updateThreadTime(thread_id,"",utilities.getCurrentDateTime());
                                handler.close();
                                objectInitSending.put("status","failed");
                                arrayListChat.set(finalChatPosition,objectInitSending);
                                recyclerAdapter.notifyDataSetChanged();
                                recyclerChat.smoothScrollToPosition(finalChatPosition);
                                Toast.makeText(getApplicationContext(),"Message sending failed. Please try again",Toast.LENGTH_SHORT).show();
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            objectRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            20000,
                            2,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }





    private void markMessageAsRead(){

        String url  = SERVER_URL+"/api/message/seenMessages?token="+token;
        StringRequest objectRequest  = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateMessageAsSeen(thread_id);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {

                    }
                })
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("sender_id", String.valueOf(recipientID));
                params.put("thread_id", String.valueOf(thread_id));
                return params;
            }
        };
        objectRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        7000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
    }


    private void updateMessageAsSeen(int thread_id) {
        handler.open();
        handler.setChatAsRead(thread_id,utilities.getCurrentDateTime());
        handler.setThreadAsSeen(thread_id);
        handler.close();
        recyclerAdapter.notifyDataSetChanged();
    }

    private String getActivityCaption(){
        String convTime = null;
        try {
            handler.open();
            Cursor cursorStatus = handler.returnUserOnlineLastActivity(recipientID);
            if (cursorStatus.getCount() > 0){
                cursorStatus.moveToFirst();
                String initDate             = cursorStatus.getString(2);
                Log.e("initDate",initDate);
                if(!initDate.equals("false") ){
                    lastReceiverTimeActive      = initDate;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date pasTime                = dateFormat.parse(lastReceiverTimeActive);
                    Date nowTime                = new Date();
                    long dateDiff               = nowTime.getTime() - pasTime.getTime();
                    long minutes                = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
                    // 300
                    if (minutes <= 1) {
                        convTime = "Active now!";
                    }
                    else{
                        convTime = "Active "+ utilities.getTimeAgo(lastReceiverTimeActive);
                    }
                }
            }
            else{
                return "";
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return convTime;
    }


    private void setConnectingAction(final String connection) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(connection.equals("connecting")){
                    relativeConnection.setBackgroundColor(getResources().getColor(R.color.caution));
                    progressBar.setVisibility(View.VISIBLE);
                    relativeConnection.setVisibility(View.VISIBLE);
                    lblConnectionStatus.setText("Connecting....");
                    activityHandler.postDelayed(noInternetConnection,10000);

                }
                else if(connection.equals("connected")){

                    activityHandler.removeCallbacks(noInternetConnection);
                    relativeConnection.setBackgroundColor(getResources().getColor(R.color.laybareGreen));
                    relativeConnection.setVisibility(View.VISIBLE);
                    lblConnectionStatus.setText("Connected");
                    progressBar.setVisibility(View.GONE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            relativeConnection.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
                else if(connection.equals("agent_offline")){
                    relativeConnection.setBackgroundColor(getResources().getColor(R.color.menu_testimonials));
                    relativeConnection.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    lblConnectionStatus.setText("User / Agent is offline right now. Please leave a message and we will send it thru our ticketing system...");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            relativeConnection.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
                else{
                    relativeConnection.setBackgroundColor(getResources().getColor(R.color.themeRed));
                    relativeConnection.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    lblConnectionStatus.setText("No Internet Connection");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            relativeConnection.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
            }
        });

    }


    private void setToolbar() {
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle            = (TextView)findViewById(R.id.forTitle);
        forCaption          = (TextView)findViewById(R.id.forCaption);
        imgBtnBack          = (ImageButton) findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forCaption.setVisibility(View.VISIBLE);
        forTitle.setTypeface(myTypeface);
    }


    private void checkIfRecipientIsOnline() {

        String url  = SERVER_URL+"/api/mobile/getLastTimeActivity/"+recipientID+"?token="+token;
        final StringRequest objectRequest  = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("false")){
                            forCaption.setText("Currently Offline");
                        }
                        if (response.equals("null") || response.equals("") ){
                            forCaption.setText("Currently Offline");
                        }
                        else{
                            lastReceiverTimeActive  = response;
                            handler.open();
                            Cursor cursorUser = handler.returnUserOnlineLastActivity(recipientID);
                            if(cursorUser.getCount() > 0){
                                handler.updateUserOnlineLastActivity(recipientID,lastReceiverTimeActive);
                            }
                            else{
                                handler.insertUserOnline(recipientID,userName,lastReceiverTimeActive);
                            }
                            handler.close();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        forCaption.setText("");
                    }
                });
        objectRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        20000,
                        2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);

    }


    private Runnable timerActiveStatusRunnable = new Runnable() {
        public void run() {
            forCaption.setText(getActivityCaption());
            activityHandler.postDelayed(timerActiveStatusRunnable,5000);
        }
    };

    private Runnable noInternetConnection = new Runnable() {
        public void run() {
//            setConnectingAction("offline");
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        txtBodyChat.clearFocus();
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        txtBodyChat.clearFocus();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAppRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppRunning = false;
    }


    private void configureListener() {
        imgBtnSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateMessage();
            }
        });

        txtBodyChat.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
                if(s.toString().trim().isEmpty()){

                }
                else{
                    imgBtnSendChat.setAlpha(1f);
                    imgBtnSendChat.setClickable(true);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        recyclerChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(-1)) {
                    Log.e("POSITION","This is at the TOP"+ isLoading+" - "+hasMore);
                    if(isLoading == false){
                        isLoading = true;
                        if(hasMore == true){
                            relativeLoading.setVisibility(View.VISIBLE);
                            iterateMessageInDB("previous");
                        }
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerChat.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
        txtBodyChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(txtBodyChat.getWindowToken(), 0);
                }
            }
        });

    }

    //all web sockets is here
    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("connection","connected");
//                    setConnectingAction("connected");
                }
            });

        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("connection","connection error");
//                    setConnectingAction("error");
                }
            });
        }
    };

    private Emitter.Listener onConnectTimeOut = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("connection","connection timeout");
//                    setConnectingAction("connecting");
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("connection","disconnected");
//                    setConnectingAction("error");
                }
            });
        }
    };

    private Emitter.Listener pongUsers = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = String.valueOf(args);
                    Log.e("PONG RECEIVER",data.toString());
                    JSONObject objectPongParams = new JSONObject();
                    try {
                        objectPongParams.put("id",clientID);
                        objectPongParams.put("is_client",1);
                        objectPongParams.put("platform","APP");
                        mSocket.emit("pongUsers",objectPongParams);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public void update(Observable o, Object arg) {
        iterateMessageInDB("latest");
    }
}
