package com.system.mobile.lay_bare.FirebaseNotification;

/**
 * Created by paolohilario on 4/6/18.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.system.mobile.lay_bare.NavigationDrawer.NavigationDrawerActivity;

import static android.os.Build.SERIAL;


public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    String build_no = Build.SERIAL;
    private NotificationHub hub;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String resultString = null;
        String regID = null;
        String storedToken = null;

        try {

            String FCM_token = FirebaseInstanceId.getInstance().getToken();
            Log.e(TAG, "FCM Registration Token: " + FCM_token);
            // Storing the registration ID that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            if (((regID=sharedPreferences.getString("registrationID", null)) == null)){

                NotificationHub hub = new NotificationHub(FirebaseNotificationSettings.HubName,
                        FirebaseNotificationSettings.HubListenConnectionString, this);
                Log.e(TAG, "Attempting a new registration with NH using FCM token : " + FCM_token);
                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
//                regID = hub.register(FCM_token).getRegistrationId();
                Log.e("build_no",build_no);
                regID           = hub.register(FCM_token, build_no+",tags-campaign-manager,tags-announcement,tags-promotion").getRegistrationId();
                resultString    = "New NH Registration Successfully - RegId : " + regID;
                Log.e(TAG, resultString);
                sharedPreferences.edit().putString("registrationID", regID ).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token ).apply();
            }
            // Check if the token may have been compromised and needs refreshing.
            else if ((storedToken=sharedPreferences.getString("FCMtoken", "")) != FCM_token) {
                Log.e("build_no",build_no);
//                FirebaseMessaging.getInstance().subscribeToTopic("tags-campaign-manager");
                NotificationHub hub = new NotificationHub(FirebaseNotificationSettings.HubName,
                        FirebaseNotificationSettings.HubListenConnectionString, this);
                Log.e(TAG, "NH Registration refreshing with token : " + FCM_token);
                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
//                regID = hub.register(FCM_token).getRegistrationId();
                regID           = hub.register(FCM_token, build_no+",tags-campaign-manager,tags-announcement,tags-promotion").getRegistrationId();
                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.e(TAG, resultString);

                sharedPreferences.edit().putString("registrationID", regID ).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token ).apply();
            }

            else {
                resultString = "Previously Registered Successfully - RegId : " + regID;
                Log.e(TAG, resultString);
            }
        }
        catch (Exception e) {
            Log.e(TAG, resultString="Failed to complete registration", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }
        // Notify UI that registration has completed.
        if (NavigationDrawerActivity.isAppRunning) {
//            NavigationDrawerActivity.navigationDrawerActivity.ToastNotify(resultString);
        }

    }
}