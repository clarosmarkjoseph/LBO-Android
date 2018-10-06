package com.system.mobile.lay_bare;

/**
 * Created by OrangeApps Zeus on 12/15/2015.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AppointmentReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

//        if(intent.hasExtra("RID")){
            Log.wtf("ALARM ", "APPOINTMENT RECEIVER RECEIVES AN ALARM");
            String id =  intent.getStringExtra("RID");
            String message    = "Your appointment will start after 15 minutes!";
            long[] v = {500,1000};
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (Build.VERSION.SDK_INT < 16) {
                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.putExtra("RID", String.valueOf(id));
                PendingIntent resultPendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

                Notification.Builder n  = new Notification.Builder(context);
                n.setContentIntent(resultPendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Lay Bare")
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setTicker(message)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(message))
                        .setOnlyAlertOnce(true)
                        .setVibrate(v)
                        .setContentText(message).getNotification();
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1254, n.getNotification());
            }else{
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Lay Bare")
                                .setAutoCancel(true)
                                .setSound(soundUri)
                                .setTicker(message)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(message))
                                .setOnlyAlertOnce(true)
                                .setVibrate(v)
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setContentText(message);
                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.putExtra("RID", String.valueOf(id));
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1254, mBuilder.build());
            }
//        }
    }
}