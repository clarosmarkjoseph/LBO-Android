package com.system.mobile.lay_bare.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.system.mobile.lay_bare.ChatMessage.ChatInbox;
import com.system.mobile.lay_bare.ChatMessage.ChatMessage;
import com.system.mobile.lay_bare.ObservableChat;
import com.system.mobile.lay_bare.PLC.PremierClient;
import com.system.mobile.lay_bare.Promotions;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.SplashScreen;
import com.system.mobile.lay_bare.Transactions.AppointmentForm;
import com.system.mobile.lay_bare.Transactions.AppointmentHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by paolohilario on 8/28/18.
 */

public class BroadcastReceiverNotification  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int id                      = intent.getIntExtra("id",0);
        String title                = intent.getStringExtra("title");
        String body                 = intent.getStringExtra("body");
        String notification_type    = intent.getStringExtra("notification_type");
        String objectIntent         = intent.getStringExtra("object");
        boolean isRunning           = intent.getBooleanExtra("isRunning",false);
        int unique_id               = intent.getIntExtra("id",0);
        String content              = "";
        Intent repeating_intent     = null;
        PendingIntent pendingIntent = null;
        long[] pattern = {500,500,500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(notification_type.equals("promotion")) {
            content = "New Promotion: " + Html.fromHtml(body);
            repeating_intent = new Intent(context,Promotions.class);
            repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(context,unique_id,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);

        }
        else if(notification_type.equals("PLC")){
            content = body;
            repeating_intent = new Intent(context, PremierClient.class);
            repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(context,unique_id,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else if(notification_type.equals("appointment")){
            try {
                JSONObject objectAppointment    = new JSONObject(objectIntent);
                String appointment_type         = objectAppointment.getString("appointment_type");
                content                         = body;
                if(appointment_type.equals("expired") || appointment_type.equals("cancelled") || appointment_type.equals("completed")){
                    repeating_intent = new Intent(context, AppointmentHistory.class);
                    repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                else{
                     repeating_intent = new Intent(context, SplashScreen.class);
                     repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                pendingIntent = PendingIntent.getActivity(context,unique_id,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(notification_type.equals("chat")){
            try {
                JSONObject objectChat       = new JSONObject(objectIntent);
                int sender_id               = objectChat.optInt("sender_id");
                int thread_id               = objectChat.getInt("thread_id");
                String messageContent       = objectChat.getString("userName");
                content                     = body;
                repeating_intent            = new Intent(context, ChatMessage.class);
                repeating_intent.putExtra("recipient_id",sender_id);
                repeating_intent.putExtra("thread_id",thread_id);
                repeating_intent.putExtra("chat_type","message");
                repeating_intent.putExtra("userName",messageContent);
                repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(context,unique_id,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);
                ObservableChat.getInstance().reloadValue();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
          }

        else if(notification_type.equals("campaign_manager")){
            content = ""+Html.fromHtml(title);
            title   = "Lay Bare: Announcement";
            repeating_intent = new Intent(context,NotificationDetails.class);
            repeating_intent.putExtra("id",id);
            repeating_intent.putExtra("unique_id",unique_id);
            repeating_intent.putExtra("type",notification_type);
            repeating_intent.putExtra("object",String.valueOf(objectIntent));
            repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(context,unique_id,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if(notification_type.equals("chat")){
            if(isRunning == false){
                NotificationCompat.Builder mBuilder  = new NotificationCompat.Builder(context)
                        .setTicker(title)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(0)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setVibrate(pattern)
                        .setSound(alarmSound)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_HIGH);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(randomNumber(),mBuilder.build());
            }
          }
          else{
              NotificationCompat.Builder mBuilder  = new NotificationCompat.Builder(context)
                      .setTicker(title)
                      .setSmallIcon(R.mipmap.ic_launcher)
                      .setWhen(0)
                      .setContentTitle(title)
                      .setContentText(content)
                      .setVibrate(pattern)
                      .setSound(alarmSound)
                      .setDefaults(Notification.DEFAULT_SOUND)
                      .setAutoCancel(true)
                      .setContentIntent(pendingIntent)
                      .setPriority(Notification.PRIORITY_HIGH);
              NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
              notificationManager.notify(randomNumber(),mBuilder.build());
          }

    }

    public static int randomNumber() {
        Random rand = new Random();
        int pickedNumber = rand.nextInt(100);
        return pickedNumber;
    }

}
