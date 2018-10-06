package com.system.mobile.lay_bare.Classes;

import android.content.Context;
import android.util.Log;

import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by paolohilario on 1/19/18.
 */

public class AppointmentValidationClass {
    Context context;
    Calendar schedCalendar          = Calendar.getInstance();
    Calendar mcurrentTime           = Calendar.getInstance();
    Utilities utilities;


    public AppointmentValidationClass(Context ctx){
        this.context    = ctx;
        this.utilities  = new Utilities(ctx);
    }


    public ArrayList<ArrayList> returnAppointmentTime(String start_time,String end_time,String app_reserved){

        ArrayList<String> arrayStandardTime          = new ArrayList<>();
        ArrayList<String> arrayMilitaryTime          = new ArrayList<>();
        ArrayList<ArrayList> arrayReturnTime         = new ArrayList<>();

        String[] date_today = app_reserved.split("-");
        int month = Integer.parseInt(date_today[0]);
        int day   = Integer.parseInt(date_today[1]);
        int year  = Integer.parseInt(date_today[2]);

        schedCalendar.set(Calendar.MONTH,month - 1);
        schedCalendar.set(Calendar.DAY_OF_MONTH,day);
        schedCalendar.set(Calendar.YEAR,year);

        int currentStart_hour   = 0;
        int currentStart_minute = 0;
        int currentEnd_hour     = 0;
        int currentEnd_minute   = 0;

        if(utilities.getCurrentDate().equals(app_reserved)){

            String myDateTimeStart       = app_reserved+" "+currentStart_hour+":"+currentStart_minute;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatter.setLenient(false);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String mycurrentDate = df.format(c.getTime());

            try {
                Date currentDate    = formatter.parse(mycurrentDate);
                Date oldDate        = formatter.parse(myDateTimeStart);

                if (oldDate.compareTo(currentDate) > 0) {

                    Log.e("Status date","app date is before currentDate");
                    int[] time_start_values  = utilities.getStripValue(":",start_time);
                    currentStart_hour      = time_start_values[0];
                    currentStart_minute    = time_start_values[1];
                    Log.e("time start",currentStart_hour+":"+currentStart_minute);
                }
                else{
                    Log.e("Status date","app date is after currentDate");
                    currentStart_hour       = mcurrentTime.get(Calendar.HOUR_OF_DAY) + 2;
                    currentStart_minute     = mcurrentTime.get(Calendar.MINUTE);

                }
                int remainder           = currentStart_minute % 10;
                int inc                 = 10 - remainder;
                currentStart_minute     = currentStart_minute + inc;
                int[] time_end_values   = utilities.getStripValue(":",end_time);
                currentEnd_hour         = time_end_values[0];
                currentEnd_minute       = time_end_values[1];

            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            int[] time_start_values  = utilities.getStripValue(":",start_time);
            currentStart_hour      = time_start_values[0];
            currentStart_minute    = time_start_values[1];
            int remainder          = currentStart_minute % 10;
            int inc                = 10 - remainder;
            currentStart_minute    = currentStart_minute + inc;
            int[] time_end_values  = utilities.getStripValue(":",end_time);
            currentEnd_hour        = time_end_values[0];
            currentEnd_minute      = time_end_values[1];
        }

        String military_format = "";
        String standard_format = "";
        for(int x = currentStart_hour; x <= currentEnd_hour; x++){
            int correspondingHour = x;
            for(int y = 0; y < 60; y++){
                int mins   = y;
                int remain = (mins % 10);
                if(correspondingHour == currentStart_hour  && mins < currentStart_minute){
                    continue;
                }
                if((remain % 10) == 0){
                    Log.e("correspondingHour", correspondingHour+":"+mins);
                    military_format = String.valueOf(correspondingHour > 9? correspondingHour : "0"+correspondingHour)+":"+String.valueOf(mins > 9? mins : "0"+mins);
                    standard_format = utilities.convert12Hours(String.valueOf(correspondingHour)+":"+String.valueOf(mins));
                    if(x == currentEnd_hour){
                        if(mins <= currentEnd_minute){
                            standard_format = utilities.convert12Hours(String.valueOf(correspondingHour)+":"+String.valueOf(mins));
                            military_format = String.valueOf(correspondingHour > 9? correspondingHour : "0"+correspondingHour)+":"+String.valueOf(mins > 9? mins : "0"+mins);
                        }
                        else{
                            break;
                        }
                    }
                    arrayMilitaryTime.add(military_format);
                    arrayStandardTime.add(standard_format);
                }
            }
        }
        arrayReturnTime.add(0,arrayMilitaryTime);
        arrayReturnTime.add(1,arrayStandardTime);
        Log.e("arrayReturnTime",arrayReturnTime.toString());
        return arrayReturnTime;
    }

    public boolean ifTechnicianScheduleIsConflict(JSONArray arrayTechnicianSchedule){

//        if(arrayTechnicianAppointment.length() > 0){
//            for (int arr = 0; arr<arrayTechnicianAppointment.length(); arr++){
//                try {
//                    JSONObject jsonObj      = arrayTechnicianAppointment.getJSONObject(arr);
//                    String existingStart    = jsonObj.getString("sched_appointment_start");
//                    String existingEnd      = jsonObj.getString("sched_appointment_end");
//                    double militaryTimeDb   = utilities.convertTimeToMilliSeconds(military_format);
//                    double existingStartDb  = utilities.convertTimeToMilliSeconds(existingStart);
//                    double existingEndDb    = utilities.convertTimeToMilliSeconds(existingEnd);
//                    if(militaryTimeDb >= existingStartDb && militaryTimeDb <= existingEndDb) {
//                        arraySchedNotAvailable.add(standard_format);
//                    }
//                    else{
//                        arraySchedTech.add(standard_format);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        else{
//            arraySchedTech.add(standard_format);
//        }
        return false;
    }

}
