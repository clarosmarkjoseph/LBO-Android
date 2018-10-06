package com.system.mobile.lay_bare.Classes;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.FirebaseNotification.FirebaseNotificationSettings;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by paolohilario on 1/15/18.
 */

public class ProfileClass {

    Context activity;
    DataHandler handler;
    String SERVER_URL = "",clientID = "";
    Utilities utilities;
    boolean isLoggedOut;
    private ArrayList<String> arrayErrorResponse;

    public ProfileClass(Context activity){
        this.activity   = activity;
        this.utilities  = new Utilities(activity);
        this.SERVER_URL = utilities.returnIpAddress();
        this.clientID   = utilities.getClientID();
    }

    public void updateClientProfile(JSONObject objectClientData){

        try {
            String clientID             = objectClientData.getString("id");
            handler = new DataHandler(activity);
            handler.open();
            handler.updateUserAccount(clientID,objectClientData.toString());
            handler.close();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject returnClientObject(){

        JSONObject objectUser = new JSONObject();
        handler = new DataHandler(activity);
        handler.open();
        Cursor query = handler.returnUserAccount();
        if(query.getCount() > 0){
            query.moveToFirst();
            try {
                objectUser = new JSONObject(query.getString(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        handler.close();
        return objectUser;
    }

    public boolean logoutClient(){

        clientID    = utilities.getClientID();
        if(clientID == null || clientID.equals(null) || clientID.equals("")) {
            isLoggedOut = true;
        }
        else {
            String token            = utilities.getToken();
            JSONObject objParams    = new JSONObject();
            try {
                objParams.put("user_id",clientID);
                String url = SERVER_URL+"/api/user/destroyToken?token="+token;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url,objParams, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                isLoggedOut = true;
                                handler = new DataHandler(getApplicationContext());
                                handler.open();
                                handler.deleteUserAccount();
                                handler.deleteToken();
                                handler.deleteTotalTransactions();
                                handler.deleteAppointment();
                                handler.deletePromotion();
                                handler.deleteApplicationAndRequest();
                                handler.deleteBranchRating();
                                handler.deleteAllChatMessage();
                                handler.deleteAllNotification();
                                handler.deleteTransactionLogs();
                                handler.deleteAllChatThread();
                                handler.close();

//                                FirebaseMessaging.getInstance().unsubscribeFromTopic("");
                                NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.cancelAll();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                isLoggedOut = false;
                                arrayErrorResponse = utilities.errorHandling(error);
                            }
                        });
                jsObjRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                10000,
                                1,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
                isLoggedOut = true;
            }

            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isLoggedOut;
    }

    public String getHomeBranch(String branch_id){

        String id = "None";
        if(branch_id.equals("null") || branch_id.equals(null) || branch_id.isEmpty() || branch_id.equals("")){
            return id;
        }
        else{
            handler = new DataHandler(getApplicationContext());
            handler.open();
            Cursor query = handler.returnBranches();
            if(query.getCount()>0){
                query.moveToFirst();
                try {
                    JSONArray arrayBranches = new JSONArray(query.getString(1));
                    for(int x = 0; x < arrayBranches.length(); x++){
                        JSONObject objectBranch = arrayBranches.getJSONObject(x);
                        String resID            = objectBranch.getString("id");
                        String resName          = objectBranch.getString("branch_name");
                        if(resID.equals(branch_id)){
                            return resName;
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                handler.close();
                return id;
            }
        }
        handler.close();
        return id;
    }


}
