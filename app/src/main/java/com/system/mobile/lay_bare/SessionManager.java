package com.system.mobile.lay_bare;

/**
 * Created by Mark on 07/07/2017.
 */
//This is my SharedPreferences Class
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "PHP";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String SESSION_ID      = "id";
    public static final String SESSION_FNAME   = "fname";
    public static final String SESSION_GENDER  = "gender";
    public static final String SESSION_TERMS   = "terms";
    public static final String SESSION_LEVEL   = "level";
    public static final String SESSION_SURVEY  = "survey";
    public static final String SESSION_DEVICE  = "device_type";


    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String id,String fname,String gender,String terms,String level,String survey,String device_type) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(SESSION_ID, id);
        editor.putString(SESSION_FNAME, fname);
        editor.putString(SESSION_GENDER, gender);
        editor.putString(SESSION_TERMS, terms);
        editor.putString(SESSION_LEVEL, level);
        editor.putString(SESSION_SURVEY, survey);
        editor.putString(SESSION_DEVICE, device_type);
        editor.commit();
    }

    /**
     * Check login method wil check user login status If false it will redirect
     * user to login page Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, NewLogin.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SESSION_ID, pref.getString(SESSION_ID, null));
        user.put(SESSION_FNAME, pref.getString(SESSION_FNAME, null));
        user.put(SESSION_GENDER, pref.getString(SESSION_GENDER, null));
        user.put(SESSION_TERMS, pref.getString(SESSION_TERMS, null));
        user.put(SESSION_LEVEL, pref.getString(SESSION_LEVEL, null));
        user.put(SESSION_SURVEY, pref.getString(SESSION_SURVEY, null));
        user.put(SESSION_DEVICE, pref.getString(SESSION_DEVICE, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
