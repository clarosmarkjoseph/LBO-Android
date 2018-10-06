package com.system.mobile.lay_bare.Transactions;

/**
 * Created by Mark on 04/10/2017.
 */
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.GeneralActivity.FragmentServiceAdapter;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Sockets.SocketApplication;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

import static com.android.volley.toolbox.ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT;


public class AppointmentForm extends AppCompatActivity {

    Socket mSocket;
    Utilities utilities;
    SocketApplication socketApplication;
    AppointmentSingleton appointmentSingleton;
    String SERVER_URL = "";
    DataHandler handler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_form);
        initElements();
    }

    private void initElements() {

        handler             = new DataHandler(getApplicationContext());
        utilities           = new Utilities(this);
        SERVER_URL          = utilities.returnIpAddress();
        socketApplication   = new SocketApplication();
        mSocket             = socketApplication.getSocket();

        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on("refreshAppointments",getQueuedAppointment);
        mSocket.connect();
        mSocket.open();
        mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = (Transport) args[0];
                transport.on(Transport.EVENT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Exception e = (Exception) args[0];
                        Log.e("WEW", "Transport error " + e);
                        e.printStackTrace();
                        e.getCause().printStackTrace();
                    }
                });
            }
        });
        loadFragment();
    }



    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.wtf("WEW", "Socket Connected!");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = String.valueOf(args[0]);
                    Log.wtf("WEW", "Socket error!"+ data);
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
                    String data = String.valueOf(args[0]);
                    Log.wtf("WEW", "Socket Disconnected!"+ data);
                }
            });
        }
    };

    private Emitter.Listener getQueuedAppointment = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject objectAppointments = new JSONObject(String.valueOf(args[0]));
                        int branch_id           = objectAppointments.getInt("branch_id");
                        int selectedBranchID    = appointmentSingleton.Instance().getBranchID();
                        if(branch_id == selectedBranchID){
                            getAppointmentQueuing(branch_id);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void getAppointmentQueuing(final int branch_id) {

        String app_reserved = appointmentSingleton.Instance().getAppReserved();
        String branch_url = SERVER_URL+"/api/mobile/getBranchSchedules/"+String.valueOf(branch_id)+"/"+app_reserved;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, branch_url,null,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("getAppointmentQueuing",response.toString());
                            JSONArray arrayBranchSchedule       = response.getJSONArray("branch");
                            JSONArray arrayTechSchedule         = response.getJSONArray("technician");
                            JSONArray arrayQueuedAppointment    = response.getJSONArray("transactions");

                            handler.open();
                            Cursor queryBranch = handler.returnBranchSchedule(String.valueOf(branch_id));
                            if(queryBranch.getCount() > 0){
                                handler.updateBranchSchedule(String.valueOf(branch_id),arrayBranchSchedule.toString(),arrayTechSchedule.toString(),utilities.getCurrentDate());
                            }
                            else{
                                handler.insertBranchSchedule(String.valueOf(branch_id),arrayBranchSchedule.toString(),arrayTechSchedule.toString(),utilities.getCurrentDate());
                            }
                            handler.close();
                            appointmentSingleton.Instance().setArrayQueueing(arrayQueuedAppointment);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                7000,
                3,
                DEFAULT_IMAGE_BACKOFF_MULT));

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);


    }


    private void loadFragment() {
        FragmentAppointmentStep1 fragmentAppointmentStep1   = new FragmentAppointmentStep1();
        FragmentManager fragmentManager                     = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction             = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameAppointment, fragmentAppointmentStep1,"FragmentAppointmentStep1");
        fragmentTransaction.commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

//        appointmentSingleton.resetAppointment();
        Runtime.getRuntime().gc();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameAppointment);
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}
