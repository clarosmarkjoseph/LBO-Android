package com.system.mobile.lay_bare.Transactions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.Classes.AppointmentValidationClass;
import com.system.mobile.lay_bare.Classes.BranchClass;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.GeneralActivity.BranchSelection;
import com.system.mobile.lay_bare.GeneralActivity.TechnicianSelection;
import com.system.mobile.lay_bare.GeneralActivity.TimeSelection;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.android.volley.toolbox.ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT;


/**
 * Created by Mark on 17/10/2017.
 */

public class FragmentAppointmentStep1 extends Fragment {

    private String SERVER_URL                   = "";
    ArrayList<String> arrayErrorResponse        = new ArrayList<>();
    static final int BRANCH_RESULT_CODE         = 0;
    static final int TECHNICIAN_RESULT_CODE     = 1;
    static final int TIME_RESULT_CODE           = 2;
    Utilities utilities;
    NestedScrollView nestedScrollView;
    Calendar appointmentCalendar                = Calendar.getInstance();
    BranchClass branchClass;
    ArrayList<String> arrayStandardTime          = new ArrayList<>();
    ArrayList<String> arrayMilitaryTime          = new ArrayList<>();
    Button btnNext;
    ImageButton btnPrev;
    AppointmentSingleton appointmentSingleton;
    CardView cardviewDate,cardviewBranch,cardviewTechnician,cardviewTime,cardviewPromo;
    TextView lblDate,lblBranch,lblTechnician,lblPromoCode,lblTime,forTitle;
    String branch_id = "",branch_name= "",technician_id= "0",technician_name= "",app_reserved= "",appointment_time= "";
    DatePickerDialog.OnDateSetListener dialogDate;
    DatePickerDialog dpDialog;
    View layout;
    InputMethodManager imm;
    String transaction_id = "";
    JSONObject objectBranchSchedule;
    JSONObject objectTechSchedule;
    JSONArray arrayBranchSchedules;
    JSONArray arrayQueuedAppointment;
    JSONArray arrayServiceAvailable;
    JSONArray arrayProductAvailable;
    boolean ifAlreadyLoaded = false;
    DataHandler handler;
    int roomCount                   = 0;
    int selectedIndex               = 0;
    Typeface myTypeface;
    String currentDate              = "";
    String branch_classification    = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(layout == null){
            layout            = inflater.inflate(R.layout.fragment_appointment_step1, container, false);
        }
        else {
            container.removeAllViews();
        }
        imm               = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        initElements();
        return layout;
    }


    private void initElements() {

        handler                 = new DataHandler(getActivity());
        utilities               = new Utilities(getActivity());
        branchClass             = new BranchClass(getActivity());
        appointmentSingleton    = new AppointmentSingleton();
        app_reserved            = appointmentSingleton.Instance().getAppReserved();
        SERVER_URL              = utilities.returnIpAddress();
        currentDate             = utilities.getCurrentDateTime();
        btnPrev                 = ( ImageButton) layout.findViewById(R.id.btnPrev);
        btnNext                 = ( Button) layout.findViewById(R.id.btnNext);
        cardviewDate            = (CardView) layout.findViewById(R.id.cardviewDate);
        cardviewBranch          = (CardView) layout.findViewById(R.id.cardviewBranch);
        cardviewTechnician      = (CardView) layout.findViewById(R.id.cardviewTechnician);
        cardviewTime            = (CardView) layout.findViewById(R.id.cardviewTime);
        cardviewPromo           = (CardView) layout.findViewById(R.id.cardviewPromo);
        lblDate                 = (TextView) layout.findViewById(R.id.lblDate);
        lblBranch               = (TextView) layout.findViewById(R.id.lblBranch);
        lblTechnician           = (TextView) layout.findViewById(R.id.lblTechnician);
        lblPromoCode            = (TextView) layout.findViewById(R.id.lblPromoCode);
        lblTime                 = (TextView) layout.findViewById(R.id.lblTime);
        nestedScrollView        = (NestedScrollView)layout.findViewById(R.id.nestedScrollView);
        transaction_id          = utilities.generateTransactionID();
        forTitle                = (TextView) layout.findViewById(R.id.forTitle);
        myTypeface              = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LobsterTwo-Regular.ttf");

        forTitle.setTypeface(myTypeface);
        lblDate.setText(utilities.getCompleteDateMonth(app_reserved));

        cardviewBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BranchSelection.class);
                intent.putExtra("type","Appointments");
                startActivityForResult(intent, BRANCH_RESULT_CODE);
            }
        });

        cardviewDate.setFocusable( true );
        cardviewDate.setFocusableInTouchMode( true );
        cardviewDate.requestFocus();
        cardviewDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        cardviewTechnician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(branch_id==null || branch_id.equals("") || branch_id.equals(null) || branch_id.equals("null") || branch_id.equals("0")){
                   utilities.showDialogMessage("Branch is required","Please choose your branch first","info");
                   return;
               }
               else{
                   Intent intent = new Intent(getActivity(), TechnicianSelection.class);
                   intent.putExtra("app_reserved",app_reserved);
                   intent.putExtra("branch_id",branch_id);
                   startActivityForResult(intent, TECHNICIAN_RESULT_CODE);
               }
            }
        });

        cardviewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(branch_id==null || branch_id.equals("") || branch_id.equals(null) || branch_id.equals("null")|| branch_id.equals("0")){
                    utilities.showDialogMessage("Branch is required","Please choose your branch first","info");
                    return;
                }
                else{
                    displayTime();
                }
            }
        });
        cardviewPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilities.showDialogMessage("Maintenance Mode","Sorry, this setup is not yet available.","info");
                return;
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ifAlreadyLoaded == false){
                    utilities.showDialogMessage("Please complete all details", "Please complete the following:\n\n *Date of Appointment\n *Branch Selection\n *Time of Appointment", "error");
                    return;
                }
                if(technician_id.equals("0")){
                    customDialog("You didn't select a technician on this appointment and you might end up serviced by other waxers or wait in the queue if no waxer is available. Continue?");
                    return;
                }
                else{
                    validateData();
                }
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentSingleton.Instance().resetAppointment();
                getActivity().finish();
            }
        });
        initDateDialog();
    }


    private void displayTime() {

        try {
            AppointmentValidationClass appointmentValidationClass = new AppointmentValidationClass(getActivity());
            ArrayList<ArrayList> arrayTime;
            if(objectTechSchedule.length() > 0){
                arrayTime               = appointmentValidationClass.returnAppointmentTime(objectTechSchedule.getString("start").toString(),objectTechSchedule.getString("end").toString(),app_reserved);
            }
            else{
                arrayTime               = appointmentValidationClass.returnAppointmentTime(objectBranchSchedule.getString("start").toString(),objectBranchSchedule.getString("end").toString(),app_reserved);
            }
            arrayMilitaryTime       = arrayTime.get(0);
            arrayStandardTime       = arrayTime.get(1);
            Intent intent = new Intent(getActivity(), TimeSelection.class);
            intent.putExtra("app_reserved",app_reserved);
            intent.putExtra("branch_id",branch_id);
            intent.putExtra("array_schedule",arrayStandardTime);
            intent.putExtra("selectedIndex",selectedIndex);
            startActivityForResult(intent, TIME_RESULT_CODE);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initDateDialog() {
        dialogDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                appointmentCalendar.set(Calendar.YEAR,year);
                appointmentCalendar.set(Calendar.MONTH,month);
                appointmentCalendar.set(Calendar.DAY_OF_MONTH,day);
                setDate(year,day,month+1);
            }
        };
    }

    private void setDate(int year, int day, int month) {
        clearContent("date");
        String dateSelected = (year)+"-"+(month<10?("0"+month):(month))+"-"+(day<10?("0"+day):(day));
        app_reserved        = dateSelected;
        appointmentSingleton.Instance().setAppReserved(app_reserved);
        lblDate.setText(utilities.getCompleteDateMonth(dateSelected));
    }

    private void showDateDialog() {
        String[] getDate    = utilities.getCurrentDate().split("-");
        long minimumDate    = utilities.convertDateTimeToMilliSeconds(currentDate);
        long maxDate        = utilities.convertDateTimeToMilliSeconds(utilities.addMonthsToDateTime(utilities.getCurrentDateTime(),6));
        dpDialog            = new DatePickerDialog(getActivity(), R.style.DialogTheme,dialogDate,Integer.parseInt(getDate[0]),Integer.parseInt(getDate[1])-1,Integer.parseInt(getDate[2]));
        DatePicker datePicker = dpDialog.getDatePicker();
        datePicker.setMinDate(minimumDate);
        datePicker.setMaxDate(maxDate);
        dpDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BRANCH_RESULT_CODE){
            if (resultCode == getActivity().RESULT_OK) {
                selectedIndex = 0;
                clearContent("branch");
                try {
                    JSONObject objectBranch = new JSONObject(data.getStringExtra("branch_object"));
                    branch_id                   = String.valueOf(objectBranch.getInt("id"));
                    branch_name                 = objectBranch.getString("branch_name");
                    roomCount                   = objectBranch.getInt("rooms_count");
                    branch_classification       = objectBranch.optString("branch_classification","company-owned");
                    JSONObject objectBranchData = objectBranch.getJSONObject("branch_data");

                    if(objectBranchData.has("type")){
                        if (objectBranchData.has("extension_minutes")){
                            int extended_minute = Integer.parseInt(objectBranchData.getString("extension_minutes"));
                            appointmentSingleton.Instance().setTimeExtension(extended_minute);
                        }
                    }
                    if(!objectBranch.isNull("services")){
                        arrayServiceAvailable       = objectBranch.optJSONArray("services");
                        arrayProductAvailable       = objectBranch.optJSONArray("products");
                    }
                    else{
                        arrayServiceAvailable       = new JSONArray();
                        arrayProductAvailable       = new JSONArray();
                    }
                    appointmentSingleton.Instance().setRoomCount(roomCount);
                    getSchedules();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == TECHNICIAN_RESULT_CODE){
            if (resultCode == getActivity().RESULT_OK) {
                selectedIndex = 0;
                try {
                    clearContent("tech");
                    JSONObject objectTechnician  = new JSONObject(data.getStringExtra("technician_object"));
                    validateTechnicianSchedule(objectTechnician);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == TIME_RESULT_CODE){
            if (resultCode == getActivity().RESULT_OK) {
                selectedIndex                   = data.getIntExtra("selectedIndex",0);
                appointment_time                = arrayMilitaryTime.get(selectedIndex);
                String display_appointment_time = arrayStandardTime.get(selectedIndex);
                lblTime.setText(display_appointment_time);
                ifAlreadyLoaded = true;
            }
        }
    }




    private void getSchedules(){

        utilities.showProgressDialog("Loading Schedules.. Please wait...");
        String branch_url = SERVER_URL+"/api/mobile/getBranchSchedules/"+branch_id+"/"+app_reserved;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, branch_url,null,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            utilities.hideProgressDialog();
                            JSONArray arrayBranchSchedule = response.getJSONArray("branch");
                            JSONArray arrayTechSchedule   = response.getJSONArray("technician");
                            arrayQueuedAppointment        = response.getJSONArray("transactions");
                            handler.open();
                            Cursor queryBranch = handler.returnBranchSchedule(branch_id);
                            if(queryBranch.getCount() > 0){
                                handler.updateBranchSchedule(branch_id,arrayBranchSchedule.toString(),arrayTechSchedule.toString(),utilities.getCurrentDate());
                            }
                            else{
                                handler.insertBranchSchedule(branch_id,arrayBranchSchedule.toString(),arrayTechSchedule.toString(),utilities.getCurrentDate());
                            }
                            handler.close();
                            appointmentSingleton.Instance().setArrayQueueing(arrayQueuedAppointment);
                            setScheduleAction();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ifAlreadyLoaded = false;
                        roomCount       = 0;
                        branch_id       = "0";
                        branch_name     = "No Branch Selected";
                        technician_id   = "0";
                        technician_name = "No Technician Selected";
                        appointment_time = "";
                        lblBranch.setText(branch_name);
                        lblTechnician.setText(technician_name);
                        lblTime.setText("No Time Selected");

                        utilities.hideProgressDialog();
                        arrayErrorResponse = utilities.errorHandling(error);
                        utilities.showDialogMessage("Check the Connection", "There was a problem with your connection. Please check it and try again", "error");

                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DEFAULT_IMAGE_BACKOFF_MULT));

        MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    private void setScheduleAction() {

        handler.open();
        Cursor queryBranch = handler.returnBranchSchedule(branch_id);
        if(queryBranch.getCount() > 0){

            while(queryBranch.moveToNext()){
                try {
                    JSONArray arrayTechnicianSchedule   = new JSONArray(queryBranch.getString(2));
                    JSONArray arrayBranch               = new JSONArray(queryBranch.getString(1));

                    for (int x = 0; x < arrayBranch.length(); x++){
                        JSONObject objectBranch       = arrayBranch.getJSONObject(x);
                        JSONArray arrayBranchSchedule = new JSONArray(objectBranch.getString("schedule_data"));
                        String schedule_type          = objectBranch.getString("schedule_type");
                        if(schedule_type.equals("closed")){
                            handler.close();
                            utilities.showDialogMessage("Date is not available","Sorry, the branch is closed on this date / time. Please choose other day","error");
                            break;
                        }
                        else{
                            int weekIndex                       = utilities.getDayOfWeek(app_reserved);
                            JSONObject objectInitBranchSched    = arrayBranchSchedule.getJSONObject(weekIndex);
                            String initStart                    = objectInitBranchSched.getString("start");
                            String initEnd                      = objectInitBranchSched.getString("end");
                            int extended_minute                 = appointmentSingleton.Instance().getTimeExtension();

                            objectBranchSchedule.put("start",initStart);
                            objectBranchSchedule.put("end",utilities.getEndTimeByDuration(initEnd,extended_minute));
                            handler.close();
                            lblBranch.setText(branch_name);

                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            handler.close();
            utilities.showDialogMessage("Error Connection!","There was a problem withing your internet connection. Please check it and try again","error");
        }
    }


    private void validateTechnicianSchedule(final JSONObject objectTechnician){

        String employee_id = objectTechnician.optString("employee_id","0");
        utilities.showProgressDialog("Validating.. Please wait...");
        String url  = "";
        if(branch_classification.equals("franchised")){
            url = "https://emsf.lay-bare.com/api/getTechnicianAttendance/"+employee_id+"/"+app_reserved;
        }
        else{
            url = "https://ems.lay-bare.com/api/getTechnicianAttendance/"+employee_id+"/"+app_reserved;
        }
        Log.e("url",url);
        StringRequest jsObjRequest = new StringRequest
                (Request.Method.GET, url,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        utilities.hideProgressDialog();
                        generateTechnicianSchedule(objectTechnician,response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onErrorResponse", String.valueOf(utilities.errorHandling(error)));
                        generateTechnicianSchedule(objectTechnician,"false");
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DEFAULT_IMAGE_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);

    }

    private void generateTechnicianSchedule(JSONObject objectTechnician,String response){
        try {
            String id               =  objectTechnician.getString("id");
            String name             =  objectTechnician.getString("name");
            JSONObject objectSched  =  objectTechnician.getJSONObject("schedule");

            if(!response.equals("false")){
                JSONObject objectTechSchedule   = new JSONObject(response);
                JSONArray arrayLeave            = objectTechSchedule.getJSONArray("leave");

                for(int x = 0; x < arrayLeave.length(); x++){
                    JSONObject objectLeave          = arrayLeave.getJSONObject(x);
                    JSONObject objectRequestData    = objectLeave.getJSONObject("request_data");
                    String status                   = objectRequestData.getString("status");
                    String mode                     = objectRequestData.getString("mode");

                    if(status.equals("approved")){
                        utilities.showDialogMessage("Technician not available!","Sorry, the technician that you've selected is not available. Please choose other technician","error");
                        return;
                    }
                }
                setTechnicianSchedule(id,name,objectSched);
            }
            else{
                setTechnicianSchedule(id,name,objectSched);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTechnicianSchedule(String id,String name,JSONObject objectSched){
        technician_id                   = id;
        technician_name                 = name;
        objectTechSchedule              = objectSched;
        lblTechnician.setText(technician_name);
    }


    private void validateData() {

        String stringDate       = utilities.getCurrentDate();
        Date appointmentDate    = utilities.convertStringToDate(app_reserved);
        Date currentDate        = utilities.convertStringToDate(stringDate);

        if(branch_id == null || branch_id.equals("") || branch_id.equals("0")){
            utilities.showDialogMessage("Branch is Required!", "Please select appointment branch!", "error");
            return;
        }
        if(appointment_time == null || appointment_time.equals("")){
            utilities.showDialogMessage("Incomplete Details", "Please specify the time of your apppointment", "error");
            return;
        }
        if(currentDate.compareTo(appointmentDate) > 0){
            utilities.showDialogMessage("Invalid Date", "Sorry, you can not select the previous date.", "error");
            return;
        }
        else{
            try {
                String datePicked       = app_reserved + " " + appointment_time;
                arrayQueuedAppointment  = appointmentSingleton.Instance().getArrayQueueing();
                if(arrayQueuedAppointment.length() > 0){

                    int countQueueIfConflict = 0;
                    for (int x = 0;  x < arrayQueuedAppointment.length(); x++){

                        JSONObject objectQueue = arrayQueuedAppointment.getJSONObject(x);
                        String dateTimeStart   = objectQueue.getString("transaction_datetime");
                        int duration           = objectQueue.getInt("duration");
                        int queuedTechID       = objectQueue.getInt("technician_id");
                        String dateTimeEnd     = utilities.addDurationToDateTime(dateTimeStart,duration);

                        ArrayList<String> arrayLists = new ArrayList<>();
                        arrayLists.add(dateTimeStart);
                        arrayLists.add(dateTimeEnd);

                        if (utilities.compareTwoTimeRangeIfAvailable(datePicked, "and", arrayLists) == true ) {
                            countQueueIfConflict++;
                            Log.e("count query",String.valueOf(countQueueIfConflict)+" - COUNT ROOM:"+roomCount);
                            if(Integer.parseInt(technician_id) == 0){
                                if(roomCount <= countQueueIfConflict){
                                    utilities.showDialogMessage("Not available", "Sorry, no more cubicle available on this time slot.", "error");
                                    return;
                                }
                            }
                            else{
                                if(queuedTechID == Integer.parseInt(technician_id) && Integer.parseInt(technician_id) != 0){
                                    utilities.showDialogMessage("Not available", "Sorry, the selected technician ("+technician_name+") is not available this time", "error");
                                    return;
                                }
                            }
                        }
                    }
                    nextTransaction();
                }
                else if(!technician_id.equals("0")){

                    String techStart   = objectTechSchedule.getString("start");
                    String techEnd     = objectTechSchedule.getString("end");
                    ArrayList<String> arrayLists    = new ArrayList<>();

                    arrayLists.add(app_reserved + " " + techStart+":00");
                    arrayLists.add(app_reserved + " " + techEnd+":00");

                    if (utilities.compareTwoTimeRangeIfAvailable(datePicked, "and", arrayLists) == false) {
                        String statement = "Sorry,the technician " + technician_name + " is only available between " + utilities.convert12Hours(techStart) + " to " + utilities.convert12Hours(techEnd);
                        utilities.showDialogMessage("Not available", statement, "error");
                        return;
                    }
                    else{
                        nextTransaction();
                    }
                }
                else if(appointment_time.equals("")){
                    utilities.showDialogMessage("Incomplete Details", "Please specify the time of your apppointment", "error");
                    return;
                }
                else{
                    nextTransaction();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void nextTransaction(){

        JSONObject objectAppointments  = new JSONObject();
        JSONObject objectClient        = new JSONObject();
        JSONObject objectBranch        = new JSONObject();
        JSONObject objectTechnician    = new JSONObject();
        JSONObject objectUserInfo      = new JSONObject();
        JSONArray arrayItems           = new JSONArray();
        JSONArray arrayServices        = new JSONArray();
        JSONArray arrayProducts        = new JSONArray();
        String transaction_type        = "branch_booking";
        String transaction_date        = app_reserved;
        String platform                = "APP - ANDROID";
        appointmentSingleton.Instance().setIfAppointment(true);
        appointmentSingleton.Instance().setBranchID(Integer.parseInt(branch_id));
        appointmentSingleton.Instance().setTechnicianSchedule(objectTechSchedule);
        try {

            objectUserInfo.put("pick_start_time",appointment_time);
            objectUserInfo.put("branch_start_time",objectBranchSchedule.getString("start"));
            objectUserInfo.put("branch_end_time",objectBranchSchedule.getString("end"));
            appointmentSingleton.Instance().setUserInformation(objectUserInfo);

            objectClient.put("value",utilities.getClientID());
            objectClient.put("label",utilities.getClientName());
            objectBranch.put("value",branch_id);
            objectBranch.put("label",branch_name);
            objectTechnician.put("value",technician_id);
            objectTechnician.put("label",technician_name);

            objectAppointments.put("branch",objectBranch);
            objectAppointments.put("technician",objectTechnician);
            objectAppointments.put("client",objectClient);
            objectAppointments.put("services",arrayServices);
            objectAppointments.put("products",arrayProducts);
            objectAppointments.put("platform",platform);

            objectAppointments.put("transaction_date",transaction_date);
            objectAppointments.put("transaction_type",transaction_type);

            appointmentSingleton.Instance().setAppointmentObject(objectAppointments);
            appointmentSingleton.Instance().setArrayItems(arrayItems);

            appointmentSingleton.Instance().setArrayOnlyAvailableService(arrayServiceAvailable);
            appointmentSingleton.Instance().setArrayOnlyAvailableProduct(arrayProductAvailable);

            FragmentAppointmentStep2 fragmentAppointmentStep2   = new FragmentAppointmentStep2();
            FragmentManager fragmentManager                     = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction             = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameAppointment, fragmentAppointmentStep2,"FragmentAppointmentStep2");
            fragmentTransaction.addToBackStack("FragmentAppointmentStep1");
            fragmentTransaction.commit();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void customDialog(String message) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_dialog);
        TextView lbldialog_title            = (TextView) dialog.findViewById(R.id.lbldialog_title);
        TextView lbldialog_message          = (TextView) dialog.findViewById(R.id.lbldialog_message);
        Button btndialog_cancel             = (Button) dialog.findViewById(R.id.btndialog_cancel);
        Button btndialog_confirm            = (Button) dialog.findViewById(R.id.btndialog_confirm);
        ImageButton imgBtnClose             = (ImageButton) dialog.findViewById(R.id.imgBtn_dialog_close);
        RelativeLayout relativeToolbar      = (RelativeLayout) dialog.findViewById(R.id.relativeToolbar);

        btndialog_cancel.setVisibility(View.GONE);

        relativeToolbar.setBackgroundColor(getActivity().getResources().getColor(R.color.laybareInfo));
        btndialog_confirm.setBackgroundColor(getActivity().getResources().getColor(R.color.laybareInfo));

        lbldialog_title.setText("Confirmation");
        lbldialog_message.setText(message);

        final Dialog myDialog = dialog;
        btndialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
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
        dialog.show();

    }

    void clearContent(String module){

        if(module.equals("date")){

            roomCount       = 0;
            branch_id       = "0";
            branch_name     = "No Branch Selected";
            lblBranch.setText(branch_name);

            technician_id           = "0";
            technician_name         = "No Technician Selected";
            branch_classification   = "";
            appointment_time        = "";

            lblBranch.setText(branch_name);
            lblTechnician.setText(technician_name);
            lblTime.setText("No Time Selected");

            arrayProductAvailable       = new JSONArray();
            arrayServiceAvailable       = new JSONArray();
            arrayBranchSchedules        = new JSONArray();
            arrayQueuedAppointment      = new JSONArray();
            objectBranchSchedule        = new JSONObject();
            objectTechSchedule          = new JSONObject();
            ifAlreadyLoaded = false;

        }
        else if(module.equals("branch")){
            technician_id   = "0";
            technician_name = "No Technician Selected";
            lblTechnician.setText(technician_name);
            objectBranchSchedule        = new JSONObject();
            arrayQueuedAppointment      = new JSONArray();
            objectTechSchedule          = new JSONObject();
            arrayProductAvailable       = new JSONArray();
            arrayServiceAvailable       = new JSONArray();
            appointment_time = "";
            lblTime.setText("No Time Selected");
            ifAlreadyLoaded = false;
        }
        else{
            technician_id           = "0";
            technician_name         = "No Technician Selected";
            objectTechSchedule      = new JSONObject();
            appointment_time        = "";
            lblTime.setText("No Time Selected");
            lblTechnician.setText(technician_name);
            ifAlreadyLoaded = false;
        }
    }







}
