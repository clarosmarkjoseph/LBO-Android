package com.system.mobile.lay_bare.FirebaseNotification;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.Notifications.BroadcastReceiverNotification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Mark on 24/08/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    DataHandler handler;
    String SERVER_URL   = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        handler     = new DataHandler(getApplicationContext());
        handler.open();
        Cursor cursorUser           = handler.returnUserAccount();
        Cursor cursorIP             = handler.returnIPAddress();
        cursorIP.moveToFirst();
        SERVER_URL                  = cursorIP.getString(0);
        JSONObject jsonObject       = new JSONObject(remoteMessage.getData());
        Calendar c                  = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String todayDate            = dateFormat.format(c.getTime());

        Log.e("remote message",String.valueOf(remoteMessage.getData()));

        if(jsonObject.has("notification_type")){
            try {
                Calendar calendar           = Calendar.getInstance();
                long timeInMiliseconds      = 0;
                try {
                    calendar.setTime(dateFormat.parse(todayDate));
                    timeInMiliseconds = calendar.getTimeInMillis();
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                String notification_type   = jsonObject.optString("notification_type","");
                String title               = jsonObject.getString("title");
                JSONObject objectData      = new JSONObject(jsonObject.optString("object","{}"));
                String body                = objectData.getString("body");
                int unique_id              = objectData.optInt("unique_id",0);
                if(notification_type.equals("appointment") || notification_type.equals("PLC") ||  notification_type.equals("chat")){
                    if(cursorUser.getCount() > 0){
                        if(notification_type.equals("chat")){
                            getMessage(unique_id,title,body,notification_type,objectData,timeInMiliseconds);
                        }
                        else{
                            setNotificationIntent(unique_id,title,body,notification_type,objectData,timeInMiliseconds);
                        }
                    }
                    else{
                        return;
                    }
                }
                else{
                    setNotificationIntent(unique_id,title,body,notification_type,objectData,timeInMiliseconds);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void setNotificationIntent(int unique_id,String title,String body,String notification_type,JSONObject objectData,long timeInMiliseconds){

        Intent intent = new Intent(getApplicationContext(), BroadcastReceiverNotification.class);
        intent.putExtra("id",unique_id);
        intent.putExtra("title",title);
        intent.putExtra("body",body);
        intent.putExtra("notification_type",notification_type);
        intent.putExtra("object", String.valueOf(objectData));
        intent.putExtra("isRunning",isRunning(getApplicationContext()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,timeInMiliseconds,AlarmManager.RTC_WAKEUP,pendingIntent);
    }

    private void getMessage(final int unique_id, final String title, final String body, final String notification_type, final JSONObject objectData, final long timeInMiliseconds){

        handler.open();
        JSONObject objectParams = new JSONObject();
        JSONArray arrayThreadID = new JSONArray();
        JSONArray arrayLastID   = new JSONArray();
        Cursor cursorThread     = handler.returnAllThread();
        Cursor cursorToken      = handler.returnToken();
        cursorToken.moveToFirst();
        String token            = cursorToken.getString(0);
        String url              = SERVER_URL+"/api/mobile/getAllChatMessage?token="+token;

        try {
            if(cursorThread.getCount() > 0) {
                while (cursorThread.moveToNext()) {
                    int thread_id = cursorThread.getInt(0);
                    int last_msg_id = handler.returnLastChatMessage(thread_id);
                    arrayThreadID.put(thread_id);
                    arrayLastID.put(last_msg_id);
                }

                objectParams.put("arrayThreadID", String.valueOf(arrayThreadID));
                objectParams.put("arrayLastID", String.valueOf(arrayLastID));
            }
            else {
                objectParams.put("arrayThreadID", "{}");
                objectParams.put("arrayLastID", "{}");

            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url,objectParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject res) {
                            try {
                                Log.e("results",String.valueOf(res));
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
                                            if(y >= arrayMessages.length()){
                                                if(!read_at.equals("null") || !read_at.equals("")){
                                                    handler.setThreadAsSeen(thread_id);
                                                }
                                            }
                                            handler.close();
                                        }
                                    }
                                }
                                handler.close();
                                setNotificationIntent(unique_id,title,body,notification_type,objectData,timeInMiliseconds);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("THIS IS A ERROR ","ERROR: "+error);
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


    public static boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }



}
