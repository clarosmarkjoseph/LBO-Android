package com.system.mobile.lay_bare.Utilities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.system.mobile.lay_bare.Classes.ProfileClass;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.NewLogin;

import com.system.mobile.lay_bare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mark on 21/09/2017.
 */

public class Utilities {

    Context context;
    NetworkResponse networkResponse;
    int statusCode = 0;
    String errorData;
    ArrayList arrayList;
    String SERVER_URL = "";
    DataHandler handler;
    JSONArray jsonArray;

    Dialog popup_loading;


    public Utilities(Context ctx){


        this.context = ctx;
        this.arrayList = new ArrayList<String>();
        this.handler   = new DataHandler(context);
        this.jsonArray = new JSONArray();

        popup_loading   = new Dialog(context);
        popup_loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_loading.setContentView(R.layout.popup_loading);
        popup_loading.setCancelable(false);
        popup_loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup_loading.setCanceledOnTouchOutside(false);

    }

    //register to Firebase topics (Push notification receipients)
    public void registerPushNotificationsByTopic(){

        FirebaseMessaging.getInstance().subscribeToTopic("tags-campaign-manager,tags-announcement,tags-PLC,tags-promotions,tags-appointments");
        //FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+clientID);
    }

    public void registerPushNotificationsBySingleDevice(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token", "Refreshed token: " + refreshedToken);
//        FirebaseMessaging.getInstance().sendRegistrationToServer(refreshedToken);
    }

    public void registerTestPushNotification(){
        FirebaseMessaging.getInstance().subscribeToTopic("test_laybare");
        //FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+clientID);
    }



    //dialog box(without action
    public void showDialogMessage(String title,String message,String type) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_dialog);
        TextView lbldialog_title            = (TextView) dialog.findViewById(R.id.lbldialog_title);
        TextView lbldialog_message          = (TextView) dialog.findViewById(R.id.lbldialog_message);
        Button btndialog_cancel             = (Button) dialog.findViewById(R.id.btndialog_cancel);
        Button btndialog_confirm            = (Button) dialog.findViewById(R.id.btndialog_confirm);
        ImageButton imgBtnClose             = (ImageButton) dialog.findViewById(R.id.imgBtn_dialog_close);
        RelativeLayout relativeToolbar      = (RelativeLayout) dialog.findViewById(R.id.relativeToolbar);

        btndialog_cancel.setVisibility(View.GONE);

        if(type.equals("error")){
            relativeToolbar.setBackgroundColor(context.getResources().getColor(R.color.themeRed));
            btndialog_confirm.setBackgroundColor(context.getResources().getColor(R.color.themeRed));
        }
        else if(type.equals("info")){
            relativeToolbar.setBackgroundColor(context.getResources().getColor(R.color.laybareInfo));
            btndialog_confirm.setBackgroundColor(context.getResources().getColor(R.color.laybareInfo));
        }
        else{
            relativeToolbar.setBackgroundColor(context.getResources().getColor(R.color.laybareGreen));
            btndialog_confirm.setBackgroundColor(context.getResources().getColor(R.color.laybareGreen));
        }

        lbldialog_title.setText(title);
        lbldialog_message.setText( message);

        final Dialog myDialog = dialog;
        btndialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showProgressDialog(String message){
        TextView lblTitle            = (TextView) popup_loading.findViewById(R.id.lbldialog_title);
        final TextView lblMessage    = (TextView) popup_loading.findViewById(R.id.lbldialog_message);
        lblTitle.setText("Loading...");
        if (message == null){
            lblMessage.setText("Please wait to finish....");
        }
        else{
            lblMessage.setText(message);
        }
        popup_loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup_loading.show();
    }

    public void hideProgressDialog(){
        if(popup_loading.isShowing()){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    popup_loading.dismiss();
                }
            }, 200);
        }
    }







    //general - Get Device name
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    //general - Capitalize words
    public String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    //handling all errors in request (String,JSONObject,JSONArray)
    public ArrayList<String> errorHandling(VolleyError error){
        networkResponse = error.networkResponse;
        if(networkResponse != null){
            arrayList.clear();
            statusCode = networkResponse.statusCode;
            if(statusCode == 400){
                try {
                    errorData = new String(error.networkResponse.data,"UTF-8");
                    try {
                        JSONObject jsonObject = new JSONObject(errorData);
                        String result         = jsonObject.getString("result");
                        String value          = jsonObject.getString("error");
                        Object json           = new JSONTokener(value).nextValue();

                        String passRequest    = "";
                        if (json instanceof JSONObject){
                            jsonObject            = new JSONObject(value);
                            jsonArray             = jsonObject.getJSONArray("");
                            passRequest+="An error occurred. Please fix the following: \n";
                            for(int x = 0; x < jsonArray.length();x++){
                                passRequest += "* "+jsonArray.get(x).toString()+"\n";
                            }
                            arrayList.add("Message Alert!");
                            arrayList.add(passRequest);
                        }
                        else if (json instanceof JSONArray){
                            jsonArray             = new JSONArray(value);
                            passRequest+="An error occurred. Please fix the following: \n";
                            for(int x = 0; x < jsonArray.length();x++){
                                passRequest += "* "+jsonArray.get(x).toString();
                                if(jsonArray.length() - 1 > x){
                                    passRequest += "\n";
                                }
                            }
                            arrayList.add("Message Alert!");
                            arrayList.add(passRequest);
                        }
                        else{
                            passRequest = value;
                            Log.e("JSON TYPE","none - String");
                            arrayList.add("Message Alert!");
                            arrayList.add(passRequest);
                        }
                        return arrayList;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            else if(statusCode == 401){
                ProfileClass profileClass = new ProfileClass(context);
                if(profileClass.logoutClient() == true){
                    showDialogMessage("Session Expire","Sorry, your app session is expired or invalid. Please Login again","error");
                    Toast.makeText(context,"Session Expire",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, NewLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
            else{
                try {
                    errorData = new String(error.networkResponse.data,"UTF-8");
                    Log.e("JSON TYPE",errorData);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                arrayList.add("Info1");
                arrayList.add("There was a problem connecting to Lay Bare App. Please check your connection and try again");
                return arrayList;
            }
        }
        else{
            arrayList.add("Info2");
            arrayList.add("There was a problem connecting to Lay Bare App. Please check your connection and try again");
            return arrayList;
        }

        arrayList.add("Info3");
        arrayList.add("There was a problem connecting to Lay Bare App. Please check your connection and try again");
        return arrayList;
    }

    //returns IP Address
    public String returnIpAddress(){
        handler = new DataHandler(context);
        handler.open();
        Cursor queryServer = handler.returnIPAddress();
        if(queryServer.getCount() > 0){
            queryServer.moveToFirst();
            SERVER_URL = queryServer.getString(0);
            return SERVER_URL;
        }
        handler.close();
        return null;
    }

    //returns Token
    public String getToken(){
        handler = new DataHandler(context);
        handler.open();
        Cursor queryServer = handler.returnToken();
        if(queryServer.getCount() > 0){
            queryServer.moveToFirst();
            String token = queryServer.getString(0);
            handler.close();
            return token;
        }
        handler.close();
        return null;
    }

    //returns client ID
    public String getClientID(){

        String clientID = null;
        handler = new DataHandler(context);
        handler.open();
        Cursor query = handler.returnUserAccount();
        if(query.getCount() > 0){
           query.moveToFirst();
            try {
                JSONObject objectClient = new JSONObject(query.getString(1));
                clientID         = objectClient.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            handler.close();
            return clientID;
        }
        handler.close();
        return null;
    }

    //get terms and condition
    public int getConsent(){

        int consent = 0;
        handler = new DataHandler(context);
        handler.open();
        Cursor query = handler.returnUserAccount();
        if(query.getCount() > 0){
            query.moveToFirst();
            try {
                JSONObject objectClient = new JSONObject(query.getString(1));
                consent                 = Integer.parseInt(objectClient.getString("is_agreed"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            handler.close();
            return consent;
        }
        handler.close();
        return consent;
    }

    //returns client email
    public String getClientEmail(){
        ProfileClass profileClass = new ProfileClass(context);
        String email = null;
        JSONObject objectClient = profileClass.returnClientObject();
        try {
            email         = objectClient.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return email;
    }

    //returns client Contact no
    public String getClientContactNo(){
        ProfileClass profileClass = new ProfileClass(context);
        String email = null;
        JSONObject objectClient = profileClass.returnClientObject();
        try {
            email         = objectClient.getString("user_mobile");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return email;
    }

    //returns client Contact no
    public String getHomeBranchID(){
        ProfileClass profileClass = new ProfileClass(context);
        String branch_id = null;
        JSONObject objectClient = profileClass.returnClientObject();
        try {
            JSONObject object = new JSONObject(objectClient.getString("user_data"));
            if(object.has("home_branch")){
                branch_id         = object.getString("home_branch");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return branch_id;
    }

    //returns client fullName
    public String getClientName(){

        ProfileClass profileClass = new ProfileClass(context);
        String name = null;
        JSONObject objectClient = profileClass.returnClientObject();
        try {
            if(objectClient.length() > 0){
                name         = capitalize(objectClient.getString("first_name"))+" "+objectClient.getString("last_name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    //returns client Birth date
    public String getClientBday(){

        ProfileClass profileClass = new ProfileClass(context);
        String bday = null;
        JSONObject objectClient = profileClass.returnClientObject();
        try {
            if(objectClient.length() > 0){
                bday                = objectClient.getString("birth_date");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bday;
    }

    //get user's gender
    public String getGender(){
        ProfileClass profileClass = new ProfileClass(context);
        String gender = null;
        JSONObject objectClient  = profileClass.returnClientObject();
        try {
            if(objectClient.length() > 0){
                gender                = objectClient.getString("gender");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gender;
    }

    //returns client Image
    public String getClientPicture(){

        ProfileClass profileClass = new ProfileClass(context);
        String user_picture = null;
        JSONObject objectClient  = profileClass.returnClientObject();
        try {
            if(objectClient.length() > 0){
                user_picture                = objectClient.getString("user_picture");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_picture.replace(" ","%20");
    }

    //count user
    public int CountUser(){
        handler = new DataHandler(context);
        handler.open();
        Cursor c = handler.returnUserAccount();
        int count = c.getCount();
        handler.close();
        return count;
    }

    //get age
    public int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        Integer ageInt = new Integer(age);
        return ageInt;
    }

    //convert int or string into currenct with zero at the end
    public String convertToCurrency(String num){
        double number = Double.parseDouble(num);
        double amount = 2000000;
        String str = String.format("%,.2f", number);

        return str;
    }

    //get duration time compute (time + duration)
    public String getEndTimeByDuration(String start_time, int duration){
        String time = "";
        Calendar schedCalendar = Calendar.getInstance();
        String[] strip     = start_time.split(":");
        int hour   = Integer.parseInt(strip[0]);
        int minute = Integer.parseInt(strip[1]) + duration;
        if(minute >= 60){
            hour+=1;
            minute-=60;
        }
        schedCalendar.set(Calendar.HOUR_OF_DAY,hour);
        schedCalendar.set(Calendar.MINUTE,minute);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        time = timeFormat.format(schedCalendar.getTime());
        return time;
    }

    //get duration time compute (time + duration)
    public String addDurationToDateTime(String start_time, int duration){

        String newTime = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(start_time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MINUTE, duration);
            newTime = df.format(cal.getTime());
            return newTime;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime;
    }

    //get duration time compute (time + duration)
    public String addMonthsToDateTime(String dateTime, int months){

        String newTime = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(dateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, months);
            newTime = df.format(cal.getTime());
            return newTime;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime;
    }



    public boolean compareTwoTimeRangeIfAvailable(String timeSelected,String condition,ArrayList<String> techOrBranchSched){

        boolean ifdateIsAvailable    = false;
        String pickedDateString      = timeSelected;
        String startTime             = techOrBranchSched.get(0);
        String endTime               = techOrBranchSched.get(1);

        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date pickedDateTime     = sdf.parse(pickedDateString);
            Date startDateTime      = sdf.parse(startTime);
            Date endDateTime        = sdf.parse(endTime);
            if(condition.equals("and")){
                if(pickedDateTime.getTime() >= startDateTime.getTime() && pickedDateTime.getTime() <= endDateTime.getTime()) {
                    ifdateIsAvailable = true;
                }
            }
            else if(condition.equals("or")){
                if(pickedDateTime.getTime() >= startDateTime.getTime() || pickedDateTime.getTime() <= endDateTime.getTime()) {
                    ifdateIsAvailable = true;
                }
            }
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return ifdateIsAvailable;
    }

    public boolean compareTime(String timeSelected,String timeSchedule){

        boolean ifdateIsAvailable = false;
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date pickedDateTime     = sdf.parse(timeSelected);
            Date scheduleDateTime   = sdf.parse(timeSchedule);
            if(pickedDateTime.getTime() >= scheduleDateTime.getTime()) {
                ifdateIsAvailable = true;
                Log.e("compare two time AND","true");
            }
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return ifdateIsAvailable;
    }

    public String getStartTime(String dateTime){

        String timeStart = "";
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = formatter1.format(calendar1.getTime());

//        int initStartHour       =
//        int initStartMin        = calendar1.get(Calendar.MINUTE);

        if(currentDate.compareTo(dateTime)>=0) {
            Log.e("inside","true");
            calendar1.set(Calendar.HOUR_OF_DAY,Calendar.HOUR_OF_DAY+ 2);
            currentDate = formatter1.format(calendar1.getTime());
            timeStart   = removeDateFromDateTime(currentDate);
            return timeStart;
        }

        Log.e("inside","false");
        timeStart = removeDateFromDateTime(dateTime);
        return timeStart;
    }


    //get current date -
    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = sdf.format(c.getTime());
        return todayDate;
    }

    //get current datetime
    public String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String todayDate = sdf.format(c.getTime());
        return todayDate.toString();
    }


    //convert 10/09/2017 - 2017-10-09
    public String returnDateFormat(Calendar myDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate     = sdf.format(myDate.getTime());
        return todayDate;
    }

    public String convert12Hours(String time){
        DateFormat f1 = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
        Date d = null;
        DateFormat f2 = null;
        String returnTime = "";
        try {
            d = f1.parse(time);
            f2 = new SimpleDateFormat("hh:mm a");
            returnTime =  f2.format(d).toLowerCase();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return returnTime;
    }

    public String convert24Hours(String time){
        DateFormat f1 = new SimpleDateFormat("hh:mm a"); //HH for hour of the day (0 - 23)
        Date d = null;
        DateFormat f2 = null;
        String returnTime = "";
        try {
            d = f1.parse(time);
            f2 = new SimpleDateFormat("HH:mm");
            returnTime =  f2.format(d).toLowerCase();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnTime;
    }

    public String convertStandardToDisplayDate(String date){
        String returndate = "";
        DateFormat f1 = new SimpleDateFormat("yyyy-mm-dd"); //HH for hour of the day (0 - 23)
        DateFormat f2;
        Date d = null;
        try {
            d           = f1.parse(date);
            f2          = new SimpleDateFormat("mm/dd/yyyy");
            returndate  =  f2.format(d).toLowerCase();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returndate;
    }

    public String convertDisplayDateToStandardDate(String date){
        String returndate = "";
        DateFormat f1 = new SimpleDateFormat("mm/dd/yyyy"); //HH for hour of the day (0 - 23)
        DateFormat f2;
        Date d = null;
        try {
            d           = f1.parse(date);
            f2          = new SimpleDateFormat("yyyy-mm-dd");
            returndate  =  f2.format(d).toLowerCase();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returndate;
    }

    //get weeks string
    public String getWeekString(int index){
        String[] weeks = new String[]{"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        return weeks[index];
    }

    //strip characters
    public int[] getStripValue(String charat,String sched){

        String[] strip      = sched.split(charat);
        int[] value;
        int value1          = Integer.parseInt(strip[0]);
        int value2          = Integer.parseInt(strip[1]);
        if(charat.equals("-")){
            int value3          = Integer.parseInt(strip[2]);
            value               = new int[]{value1,value2,value3};
        }
        else{
            value               = new int[]{value1,value2};
        }
        return value;
    }

    //generate transaction id
    public String generateTransactionID(){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( 10 );
        for( int i = 0; i < 10; i++ ) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public boolean checkDateRange(String start,String end){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date_start = null;
        Date date_end   = null;
        try {
            date_start  = sdf.parse(start);
            date_end    = sdf.parse(end);
            long start_mil  = date_start.getTime();
            long end_mil    = date_end.getTime();
            long today_mil  = System.currentTimeMillis();
            if (today_mil >= start_mil && today_mil <= end_mil) {
                return true;
            }
            else{
                return false;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkIfAppointmentIsValid(String selectedDateString,String toDateString){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date selectedDate   = null;
        Date toDate         = null;
        try {
            selectedDate    = simpleDateFormat.parse(selectedDateString);
            toDate          = simpleDateFormat.parse(toDateString);
            long longStart  = selectedDate.getTime();
            long longToday  = toDate.getTime();
            if (longStart >= longToday) {
                return true;
            }
            else{
                return false;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    //get day of the week
    public int getDayOfWeek(String date){

        String date_reg = "";
        if(date== null || date.equals("") || date.isEmpty()){
            date_reg = getCurrentDate();
        }
        else{
            date_reg = date;
        }
        String[] dateStrip = date_reg.split("-");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(dateStrip[0]), Integer.parseInt(dateStrip[1]) - 1, Integer.parseInt(dateStrip[2]));
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int weekDay = 0;

        switch (day) {
            case Calendar.SUNDAY: weekDay       = 0; break;
            case Calendar.MONDAY: weekDay       = 1; break;
            case Calendar.TUESDAY: weekDay      = 2; break;
            case Calendar.WEDNESDAY: weekDay    = 3; break;
            case Calendar.THURSDAY: weekDay     = 4; break;
            case Calendar.FRIDAY: weekDay       = 5; break;
            case Calendar.SATURDAY: weekDay     = 6; break;
        }
        return weekDay;
    }

    //converting date time without seconds to miliseconds
    public long convertDateTimeToMilliSeconds(String stringDate){

        Date date = null;
        long milliseconds = 0;
        try {
            date         = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(stringDate);
            milliseconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }

    public double convertDateTimeSecondToMilliSeconds(String stringDate){

        Date date = null;
        long milliseconds = 0;
        try {
            date         = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stringDate);
            milliseconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }

    public Date convertStringToDateTime(String stringDate){

        Date date = new Date();
        long milliseconds = 0;
        try {
            date         = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stringDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public Date convertStringToDate(String stringDate){

        Date date = new Date();
        long milliseconds = 0;
        try {
            date         = new SimpleDateFormat("yyyy-MM-dd").parse(stringDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String getMonthAsWord(String dates,boolean isComplete){
        String month_name = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dates);
            SimpleDateFormat month_date;
            if(isComplete==true){
                month_date= new SimpleDateFormat("MMMM");
            }
            else{
                month_date= new SimpleDateFormat("MMM");
            }
            month_name = month_date.format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return month_name;
    }



    public String getBirthdayAsWord(String dates){
        String month_name = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dates);
            SimpleDateFormat month_date;
            month_date= new SimpleDateFormat("MMMM dd, yyyy");
            month_name = month_date.format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return month_name;
    }

    public String getDayOfWeeks(String dates){
        String month_name       = "";
        String date_reg = "";
        if(dates== null || dates.equals("") || dates.isEmpty()){
            date_reg = getCurrentDate();
        }
        else{
            date_reg = dates;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(date_reg);
            SimpleDateFormat month_date = new SimpleDateFormat("EEEE");
            month_name = month_date.format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return month_name;
    }


    public String getCompleteDateMonth(String date){
        String returnDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dates = format.parse(date);
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM dd yyyy");
            returnDate = month_date.format(dates);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    public String getCompleteDateTime(String date){
        String returnDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dates = format.parse(date);
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM dd yyyy hh:mm a");
            returnDate = month_date.format(dates);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    public String getSpecificInDate(int yearOrDayOrMonth,String date1){
        String data = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            SimpleDateFormat date_request;
            Date date = format.parse(date1);
            if(yearOrDayOrMonth==0){
                date_request = new SimpleDateFormat("yyyy");
            }
            else if(yearOrDayOrMonth==1){
                date_request = new SimpleDateFormat("MM");
            }
            else{
                date_request = new SimpleDateFormat("dd");
            }
            data = date_request.format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }


    public String formatDatetimeAnyThing(String dateTime,String datePatern){
        String returnDate = "";
        try {
            SimpleDateFormat formatDate         = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatDateOnly     = new SimpleDateFormat("yyyy-MM-dd");
            Date dateTimeFormatted              = formatDate.parse(dateTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatDate.parse(dateTime));
            String dateOnlyString  = formatDateOnly.format(calendar.getTime());
            Date nowTime  = new Date();
            long dateDiff = nowTime.getTime() - dateTimeFormatted.getTime();
            long days    = TimeUnit.MILLISECONDS.toDays(dateDiff);
            if (getCurrentDate().equals(dateOnlyString)){
                SimpleDateFormat newDateFormat  = new SimpleDateFormat("hh:mm a");
                returnDate                      = newDateFormat.format(dateTimeFormatted);
            }
            else if(days >= 365){
                SimpleDateFormat newDateFormat  = new SimpleDateFormat(datePatern);
                returnDate                      = newDateFormat.format(dateTimeFormatted);
            }
            else{
                SimpleDateFormat newDateFormat  = new SimpleDateFormat("MMM dd");
                returnDate                      = newDateFormat.format(dateTimeFormatted);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }


    public long getDateTimeDifference(String date1,String date2,String returnWhat){
        long returnDate = 0;
        try {
            SimpleDateFormat formatDate     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date prevDate       = formatDate.parse(date1);
            Date currentDate    = formatDate.parse(date2);
            long dateDiff       = prevDate.getTime() - currentDate.getTime();
            long minutes        = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hours          = TimeUnit.MILLISECONDS.toHours(dateDiff);
            if (returnWhat.equals("minutes")){
                return minutes;
            }
            if (returnWhat.equals("hours")){
                return hours;
            }
            else{
                return minutes;
            }

        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }



    public String getScheduleTime(String time){
        String month_name       = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(time);
            SimpleDateFormat month_date = new SimpleDateFormat("HH:mm");
            month_name = month_date.format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return month_name;
    }

    public String getPLCCaption(int index){
        String[] data_caption = new String[]{
                "Sorry, based on your record, your current total transaction doesn't reach the limit of application. To qualify for a Premier Loyalty Card, you must have accumulated a minimum of Php 5,000.00 of services availed. Feel free to email us or message us below to review your previous transaction",
                "With your current transaction, you are now qualified to Premier Loyalty Card Application. Just Click the button below.",
                "Congratulations! Your application is approved! Please wait for the application to be process.\nExpect 2-3 weeks of waiting time before you can get your PLC Card",
                "Your application is on process. Expect 2-3 weeks of waiting time before you can get your PLC Card",
                "Your card is deployed and currently delivering to it's respective branch so you can pick it up",
                "Your card is ready and you may pick-up your card in the branch that you selected. \n Thank your for waiting.",
                "You already picked-up your card. Feel free to request a replacement if you lost your card",
                "Sorry! Your application is denied! \nReason:"
        };
        return data_caption[index];
    }

    public void returnRefreshColor(SwipeRefreshLayout swipeRefresh){
        swipeRefresh.setColorSchemeColors(
                context.getResources().getColor(R.color.laybareGreen),context.getResources().getColor(R.color.menu_promo),context.getResources().getColor(R.color.menu_appointment),context.getResources().getColor(R.color.brownLoading));
    }

    public static String changeNullString(String value) {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (value.equals("null")) {
            return "";
        }
        else{
            return value;
        }
    }

    //date only
    public String removeTimeFromDate(String dateTime) {

        String returnDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dateTime);
            SimpleDateFormat month_date = new SimpleDateFormat("yyyy-MM-dd");
            returnDate = month_date.format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    //Time only(24 hrs)
    public String removeDateFromDateTime(String dateTime) {

        String returnDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dateTime);
            SimpleDateFormat month_date = new SimpleDateFormat("HH:mm:ss");
            returnDate = month_date.format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    public String returnTimeAgo(String mydate){

        String returnDate = null;
        SimpleDateFormat firstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {

            Date date                   = firstFormat.parse(mydate);
            if(getCurrentDate().equals(removeTimeFromDate(mydate))){
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                returnDate                  = timeFormat.format(date);
                return returnDate;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
            returnDate                  = dateFormat.format(date);
            return returnDate;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    //get time ago
    public String getTimeAgo(String dateTime){

        String convTime = null;
        SimpleDateFormat firstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            SimpleDateFormat dateFormat         = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatToStandard   = new SimpleDateFormat("MMMM dd yyyy hh:mm a");
            Date pasTime                        = dateFormat.parse(dateTime);
            Date nowTime                        = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(dateTime));
            String dateString = formatToStandard.format(calendar.getTime());

            long dateDiff = nowTime.getTime() - pasTime.getTime();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour    = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long days    = TimeUnit.MILLISECONDS.toDays(dateDiff);

            Date date    = firstFormat.parse(dateTime);
            if (seconds < 60) {
                convTime = "a few seconds ago";
            }
            if(minutes == 1 ){
                convTime = "a minute ago";
            }
            if (minutes < 60 ) {
                convTime = minutes+" minutes ago";
            }
            if(hour == 1){
                convTime =" 1 hour ago";
            }
            if(hour > 1){
                convTime = hour+" hours ago";
            }
             if(days == 1 || hour == 24){
                convTime = " 1 day ago";
            }
            if(days > 1 && days <= 30){
                convTime = days+" days ago";
            }
            else{
                long countYear = 0;
                long countMonth = 0;

                if (days == 30){
                    convTime = " a month ago";
                }
                else if (days > 30) {
                    countMonth = days / 30;
                    if(countMonth > 1 && countMonth < 12){
                        convTime = (days / 30)+" months ago";
                    }
                    else if(countMonth > 12){
                        countYear = countMonth / 12;
                        if(countYear > 1){
                            convTime = countYear+" years ago";
                        }
                        else{
                            convTime = " a year ago";
                        }
                    }
                    else{
                        convTime = " a month ago";
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }
        return convTime;
    }

    public boolean ifHourIsRecent(String dateTime){
       boolean ifRecent = false;
        try {
            SimpleDateFormat dateFormat         = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatToStandard   = new SimpleDateFormat("MMMM dd yyyy hh:mm a");
            Date pasTime                        = dateFormat.parse(dateTime);
            Date nowTime                        = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(dateTime));
            String dateString = formatToStandard.format(calendar.getTime());

            long dateDiff = nowTime.getTime() - pasTime.getTime();
            long hour    = TimeUnit.MILLISECONDS.toHours(dateDiff);

            if(hour >= 3){
                ifRecent = true;
            }
            else{
                ifRecent = false;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return ifRecent;
    }


    public String getStringImage(Bitmap bitmap) {
        String imgString;
        if(bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageEncode = Base64.encodeToString(b,Base64.DEFAULT);
            imgString = imageEncode;
        }else{
            imgString = "";
        }
        return "data:image/jpeg;base64,"+imgString;
    }

    //resize bitmap(atleast 2mb max) Width
    public Bitmap resizeImageBitmap(Bitmap bitmap) {

        double getMemory = bitmap.getByteCount() / 1000000;
        Log.e("before memory",bitmap.getWidth()+" - "+bitmap.getHeight()+" - "+getMemory +" mb");
        if (getMemory > 4){
            do {
                bitmap      = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.5), (int)(bitmap.getHeight()*0.5), true);
                getMemory   = bitmap.getByteCount() / 1000000;
                Log.e("loop memory",bitmap.getWidth()+" - "+bitmap.getHeight()+" - "+getMemory +" mb");
            }
            while(getMemory > 4);
            Log.e("SOURCE after MEMORY ","WIDTH: "+ bitmap.getWidth()+" HEUIGHT: "+bitmap.getHeight()+" - " );
        }
        return bitmap;
    }


    public void showSimplePopUp(String title,String message) {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(context);
        helpBuilder.setTitle(title);
        helpBuilder.setMessage(message);
        if(title.equals("Success")){
            helpBuilder.setIcon(R.drawable.z_icon_success);
        }
        else if(title.equals("Info")){
            helpBuilder.setIcon(R.drawable.z_icon_info);
        }
        else{
            helpBuilder.setIcon(R.drawable.z_icon_error);
        }
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });
        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.setCancelable(false);
        helpDialog.show();
    }


    public void setUniversalImage(final ImageView imageView, final String image){
        Picasso.with(context)
            .load(image)
            .placeholder(R.drawable.no_image)
            .error(R.drawable.no_image)
            .fit()
            .noFade()
            .error(R.drawable.no_image)
            .into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError() {
                    Picasso.with(context)
                        .load(image)
                        .placeholder(R.drawable.no_image)
                        .fit()
                        .error(R.drawable.no_image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .noFade()
                        .error(R.drawable.no_image)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onError() {

                            }
                        });
                }
            });

    }


    public double getDistance(double prev_lat, double prev_lng, double current_lat, double current_lng) {

        double earthRadius = 6371; // in miles, change to 3958.75 for Miles output
        double dLat = Math.toRadians(current_lat-prev_lat);
        double dLng = Math.toRadians(current_lng-prev_lng);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(prev_lat)) * Math.cos(Math.toRadians(current_lat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return dist; // output distance, in MILES

    }

    //add dot to a text
    public String displayConcatDot(String message){

        String returnMessage = "";
        if (message.length() > 45){
            returnMessage = message.substring(0,45);
            return returnMessage+"....";
        }
        returnMessage = message;
        return returnMessage;
    }




    public void setUniversalSmallImage(final ImageView imageView, final String imageString){
        Picasso.with(context)
            .load(imageString.replace(" ","%20"))
            .placeholder(R.drawable.no_image)
            .noFade()
            .resize(200,0)
            .error(R.drawable.no_image)
            .into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(imageString.replace(" ","%20"))
                            .noFade()
                            .resize(200,0)
                            .error(R.drawable.no_image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                }
                            });
                }
            });
    }

    public void setUniversalBigImage(final ImageView imageView, final String imageString){
        Picasso.with(context)
                .load(imageString.replace(" ","%20"))
                .placeholder(R.drawable.no_image)
                .noFade().resize(1080,0)
                .error(R.drawable.no_image)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        Picasso.with(context)
                            .load(imageString.replace(" ","%20"))
                            .noFade().resize(1080,0)
                            .error(R.drawable.no_image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                }
                            });
                    }
                });
    }


    //get average review()
    public double getAverageTotalReviews(JSONArray arrayReviews){
        double review  = 0;
        double aveRate = 0;
        try {
            if (arrayReviews.length() > 0){
                for(int x = 0; x < arrayReviews.length(); x++){
                    JSONObject objectReview = arrayReviews.getJSONObject(x);
                    double rating           = objectReview.optDouble("rating",0);
                    aveRate+=rating;
                }
            }
            return review;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        review = aveRate / arrayReviews.length();
        return  review;
    }

    public ArrayList<Float> getArrayOfAverageForEachRating(JSONArray arrayReviews){

        ArrayList<Float> arraySummary = new ArrayList<>();
        float ave1stRate = 0;
        float ave2ndRate = 0;
        float ave3rdRate = 0;
        float ave4thRate = 0;
        float ave5thRate = 0;
        try {
            for(int x = 0; x < arrayReviews.length(); x++){
                double rating           = Double.parseDouble(String.valueOf(arrayReviews.get(x)));
                if(rating > 0 && rating == 1){
                    ave1stRate+=rating;
                }
                if(rating > 1 && rating == 2){
                    ave2ndRate+=rating;
                }
                if(rating > 2 && rating == 3){
                    ave3rdRate+=rating;
                }
                if(rating > 3 && rating == 4){
                    ave4thRate+=rating;
                }
                if(rating > 4 && rating == 5){
                    ave5thRate+=rating;
                }
            }
            arraySummary.add(ave1stRate);
            arraySummary.add(ave2ndRate);
            arraySummary.add(ave3rdRate);
            arraySummary.add(ave4thRate);
            arraySummary.add(ave5thRate);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return arraySummary;
    }







    //round off decimal
    public String roundOffDecimal(Double value){
        return String.format("%.1f",value);
    }


    //validate if appointment is acknowledge
    public boolean checkAppointmentIfAcknowledge(JSONObject objectAcknowledgement){
        boolean ifAcknowledgement = false;
        try {
            String signature = objectAcknowledgement.getString("signature");
            if(signature.equals("null")){
                ifAcknowledgement = false;
            }
            else{
                ifAcknowledgement = true;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return ifAcknowledgement;
    }

    //validate if there is a waiver
    public boolean checkWaiverIfExist(JSONObject objectWaiver){
        boolean ifWaiverExist = false;
        String stringparam = "{\"signature\":null}";
        if (stringparam.equals(objectWaiver.toString()) || stringparam == objectWaiver.toString()){
            ifWaiverExist = false;
        }
        else{
            ifWaiverExist = true;
        }
//        try {
//
////            String signature = objectWaiver.getString("signature");
////            if(signature.equals("null")){
////                ifWaiverExist = false;
////            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return ifWaiverExist;
    }

    public boolean ifClientIsPremier(){
        handler.open();
        Cursor queryUser = handler.returnUserAccount();
        if(queryUser.getCount() > 0){
            queryUser.moveToFirst();
            try {
                JSONObject objectUser       = new JSONObject(queryUser.getString(1));
                JSONObject objectUserData   = new JSONObject(objectUser.getString("user_data"));
                if(objectUserData.has("premier_status")){
                    int premier_status = objectUserData.getInt("premier_status");
                    if(premier_status == 1){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public int getlastNotificationID(){
        handler.open();
        Cursor cursorNotification = handler.returnAllNotification();
        if(cursorNotification.getCount() > 0){
            while (cursorNotification.moveToNext()){
                int id = Integer.parseInt(cursorNotification.getString(0));
                return id;
            }
            return 0;
        }
        return 0;
    }



    //Image Handling from local resources





}
