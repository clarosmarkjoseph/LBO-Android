package com.system.mobile.lay_bare.FirebaseNotification;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.system.mobile.lay_bare.Utilities.Utilities;

/**
 * Created by Mark on 24/08/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
//    private static final String REG_TOKEN = "REG_TOKEN";
    private static final String TAG = "MyInstanceIDService";
    String unique_no = Build.SERIAL;

    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
//        FirebaseMessaging.getInstance().subscribeToTopic("64a1dc2c");
//        utilities = new Utilities(getApplicationContext());
//        utilities.registerPushNotificationsByTopic();
        Log.e(TAG, "Refreshing GCM Registration Token");
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
