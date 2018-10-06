package com.system.mobile.lay_bare.Queuing;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.GeneralActivity.BranchSelection;
import com.system.mobile.lay_bare.MainActivity;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Sockets.SocketApplication;
import com.system.mobile.lay_bare.Transactions.AppointmentForm;
import com.system.mobile.lay_bare.Transactions.AppointmentSingleton;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by paolohilario on 3/15/18.
 */

public class Queuing extends AppCompatActivity {

    Utilities utilities;
    String SERVER_URL;
    TextView forTitle;
    CardView cardviewBranch;
    static final int BRANCH_RESULT_CODE         = 0;
    RecyclerView recyclerQueuing;
    TextView lblCountServing,lblCountAvailable,lblEmptyQueuing,lblQueueCount,lblBranch;
    RecyclerView.LayoutManager recyclerLayoutManager;
    RecyclerView.Adapter recyclerQueuingAdapter;
    JSONArray arrayQueuing = new JSONArray(),arrayServing = new JSONArray(),arrayCalling = new JSONArray(),arrayMyAppointment = new JSONArray();
    Socket mSocket;
    int roomCount       = 0;
    int branch_id       = 0;
    int remainingCount  = 0;
    String branch_name  = "No Branch Selected";
    Button btnAddAppointment;
    ImageButton imgBtnBack;
    private Typeface myTypeface;
    boolean ifFirstLoad = true;
    ArrayList<String > arrayResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queuing);
        setToolbar();
        initElements();

    }

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
        forTitle.setText("Queuing System");

    }

    private void initElements() {

        utilities               = new Utilities(Queuing.this);
        lblCountServing         = (TextView)findViewById(R.id.lblCountServing);
        lblCountAvailable       = (TextView)findViewById(R.id.lblCountAvailable);
        lblEmptyQueuing         = (TextView)findViewById(R.id.lblEmptyQueuing);
        lblQueueCount           = (TextView)findViewById(R.id.lblQueueCount);
        lblBranch               = (TextView)findViewById(R.id.lblBranch);
        SERVER_URL              = utilities.returnIpAddress();
        cardviewBranch          = (CardView) findViewById(R.id.cardviewSelectBranch);
        recyclerQueuing         = (RecyclerView) findViewById(R.id.recyclerQueuing);
        btnAddAppointment       = (Button)findViewById(R.id.btnAddAppointment);

        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utilities.getClientID() == null){
                    utilities.showDialogMessage("Aunthentication Required","Sorry, you must logged-in before you proceed in Online Appointment","info");
                    return;
                }
                else {
                    AppointmentSingleton appointmentSingleton = new AppointmentSingleton();
                    appointmentSingleton.Instance().setAppReserved(utilities.getCurrentDate());
                    Intent intent = new Intent(Queuing.this, AppointmentForm.class);
                    startActivity(intent);
                }
            }
        });
        cardviewBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Queuing.this, BranchSelection.class);
                intent.putExtra("type","Appointments");
                startActivityForResult(intent, BRANCH_RESULT_CODE);
            }
        });

//        Collections.sort(arrayQueuing, new Comparator<JSONObject>() {
//            @Override
//            public int compare(JSONObject o1, JSONObject o2) {
//                try {
//                    double s1 = o1.getDouble("distance");
//                    double s2 = o2.getDouble("distance");
//                    return Double.compare(s1, s2);
//                }
//                catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return 0;
//            }
//        });

        recyclerQueuingAdapter         = new RecyclerQueuing(this,arrayQueuing,arrayServing,arrayCalling);
        recyclerLayoutManager   = new LinearLayoutManager(this);
        recyclerQueuing.setHasFixedSize(true);
        recyclerQueuing.setAdapter(recyclerQueuingAdapter);
        recyclerQueuing.setLayoutManager(recyclerLayoutManager);
        recyclerQueuing.setItemAnimator(new DefaultItemAnimator());
        recyclerQueuing.setNestedScrollingEnabled(false);

        registerSockets();

    }

    private void registerSockets() {

        SocketApplication socketApplication = new SocketApplication();
        mSocket                             = socketApplication.getSocket();
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on("callClient", callClient);
        mSocket.on("refreshAppointments", refreshAppointments);
        mSocket.connect();
        mSocket.open();

    }


    private void loadQueuedData() {

        if(ifFirstLoad == true){
            utilities.showProgressDialog("Loading Queue....");
        }
        String booking_url = SERVER_URL+"/api/appointment/getAppointments/queue/"+branch_id+"/queue";
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, booking_url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        arrayQueuing = new JSONArray();

                        try{
                            for (int x = 0; x < response.length(); x++){
                                JSONObject objectResponse = response.getJSONObject(x);
                                String status             = objectResponse.getString("transaction_status");
                                if(status.equals("reserved")){
                                    arrayQueuing.put(objectResponse);
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        lblQueueCount.setText("Queue Count: "+arrayQueuing.length());
                        if(arrayQueuing.length() > 0){
                            recyclerQueuing.setVisibility(View.VISIBLE);
                            lblEmptyQueuing.setVisibility(View.GONE);
                            recyclerQueuingAdapter.notifyDataSetChanged();
                            getDataChanged();
                        }
                        else{
                            lblEmptyQueuing.setVisibility(View.VISIBLE);
                            recyclerQueuing.setVisibility(View.GONE);
                            if(ifFirstLoad == true){
                                ifFirstLoad = false;
                                utilities.hideProgressDialog();
                            }
                            lblCountServing.setText("0");
                            lblCountAvailable.setText(String.valueOf(roomCount));
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        arrayResponse = utilities.errorHandling(error);
                        if(ifFirstLoad == true){
                            ifFirstLoad = false;
                            utilities.hideProgressDialog();
                        }
                        utilities.showDialogMessage("Connection Error",arrayResponse.get(1).toString(),"error");
                    }
                });
            jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }


    private void getDataChanged() {

        String booking_url = "http://lbo-express.azurewebsites.net/api/queuing/"+branch_id;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, booking_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("Object result queuing",response.toString()+" queue: "+ arrayQueuing);
                        try {
                            arrayServing            = response.getJSONArray("serving");
                            arrayCalling            = response.getJSONArray("calling");
                            recyclerQueuingAdapter = new RecyclerQueuing(Queuing.this,arrayQueuing,arrayServing,arrayCalling);
                            recyclerQueuing.setAdapter(recyclerQueuingAdapter);

                            remainingCount = roomCount - arrayServing.length();
                            lblCountServing.setText(String.valueOf(arrayServing.length()));
                            if(remainingCount < 0){
                                lblCountAvailable.setText("0");
                            }
                            else{
                                lblCountAvailable.setText(String.valueOf(remainingCount));
                            }
                            if(ifFirstLoad == true){
                                ifFirstLoad = false;
                                utilities.hideProgressDialog();
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }
                });
        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }


    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("WEW", "Socket Connected!");
//            loadQueuedData();
        }
    };


    public Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = String.valueOf(args[0]);
                    Log.e("onConnectError", "Socket error!" + data);
                }
            });
        }
    };

    public Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = String.valueOf(args[0]);
                    Log.e("WEW", "Socket Disconnected!" + data);
                }
            });
        }
    };

    public Emitter.Listener callClient = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONObject objectCall = new JSONObject(String.valueOf(args[0]));
                        Log.e("data callClient",objectCall.toString());
                        int callClientID = objectCall.getInt("client_id");
                        int callBranchID = objectCall.getInt("branch_id");
                        String action    = objectCall.getString("action");
                        if(branch_id == callBranchID){
                            getDataChanged();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    public Emitter.Listener refreshAppointments = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject objectCall = new JSONObject(String.valueOf(args[0]));
                        Log.e("data refreshAppoinments",  objectCall.toString());
                        int refreshBranchID = objectCall.getInt("branch_id");
                        loadQueuedData();
                        if(branch_id == refreshBranchID){

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        mSocket.disconnect();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mSocket.disconnect();
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BRANCH_RESULT_CODE){
            if (resultCode == RESULT_OK) {
                try {
                    JSONObject objectBranch = new JSONObject(data.getStringExtra("branch_object"));
                    Log.e("branch object",objectBranch.toString());
                    branch_id                   = objectBranch.getInt("id");
                    branch_name                 = objectBranch.getString("branch_name");
                    roomCount                   = objectBranch.getInt("rooms_count");
                    lblBranch.setText(branch_name);
                    ifFirstLoad = true;
                    loadQueuedData();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
