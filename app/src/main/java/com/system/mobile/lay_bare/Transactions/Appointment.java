package com.system.mobile.lay_bare.Transactions;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.Location.LocationSearchAdapter;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Profile.CalendarDecorator.EventCurrentDecorator;
import com.system.mobile.lay_bare.Profile.CalendarDecorator.EventDecorator;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mark on 26/10/2017.
 */


public class Appointment extends AppCompatActivity {

    MaterialCalendarView materialCalendarView;
    String[] monthWord = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
    RecyclerView recyclerActivity;
    RecyclerView.LayoutManager recyclerActivity_layoutManager;
    RecyclerView.Adapter recyclerAdapter;

    JSONArray arrayAppointments;
    DataHandler handler;
    Calendar calendar,currentDateCalendar;
    TextView lblEmpty;
    Animation animFadeOut;
    Animation animFadeIn;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat monthFormat;
    String firstDay,lastDay,todayDate,currentMonth,currentYear;
    String date_selected = "";
    FloatingActionButton fabMain;
    Utilities utilities;
    String SERVER_URL;
    ArrayList<String> arrayErrorResponse = new ArrayList<>();
    ArrayList<CalendarDay> arrayDate     = new ArrayList<>();
    EventDecorator eventDecorator;
    EventCurrentDecorator currentDecorator;
    SwipeRefreshLayout swipeRefresh;
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListner;
    String clientID;
    CalendarDay calendarDay;
    private TextView lblCalendarMonth,lblCalendarYear;
    ImageButton imgBtnNext,imgBtnPrev;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment);
        initElements();
    }

    private void initElements() {

        currentDateCalendar     = Calendar.getInstance();
        simpleDateFormat        = new SimpleDateFormat("yyyy-MM-dd");
        monthFormat             = new SimpleDateFormat("MM");
        calendar                = Calendar.getInstance();
        todayDate               = simpleDateFormat.format(calendar.getTime());
        currentMonth            = monthFormat.format(calendar.getTime());
        currentYear             = String.valueOf(calendar.get(Calendar.YEAR));
        firstDay                = currentYear+"-"+currentMonth+"-01";
        lastDay                 = currentYear+"-"+currentMonth+"-"+String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        recyclerActivity        = (RecyclerView)findViewById(R.id.activity_recycler_view);
        lblEmpty                = (TextView)findViewById(R.id.lblEmpty);
        utilities               = new Utilities(this);
        SERVER_URL              = utilities.returnIpAddress();
        clientID                = utilities.getClientID();
        handler                 = new DataHandler(getApplicationContext());
        lblCalendarMonth        = (TextView)findViewById(R.id.lblCalendarMonth);
        lblCalendarYear         = (TextView)findViewById(R.id.lblCalendarYear);
        fabMain                 = (FloatingActionButton)findViewById(R.id.fabMain);
        materialCalendarView    = (MaterialCalendarView)findViewById(R.id.calendarView);
        imgBtnNext              = (ImageButton)findViewById(R.id.imgBtnNext);
        imgBtnPrev              = (ImageButton)findViewById(R.id.imgBtnPrev);

        calendarDay             = new CalendarDay();
        currentDecorator        = new EventCurrentDecorator(Appointment.this);
//

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)

                .commit();
        materialCalendarView.setSelectionColor(Color.parseColor("#92c740"));
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        materialCalendarView.setHeaderTextAppearance(R.style.ACPLDialog);
        materialCalendarView.addDecorator(currentDecorator);
        materialCalendarView.setTopbarVisible(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                materialCalendarView.setDateSelected(currentDateCalendar.getTime(), true);
                materialCalendarView.setCurrentDate(CalendarDay.from(currentDateCalendar.getTime()), true);
            }
        },1000);



        date_selected = simpleDateFormat.format(currentDateCalendar.getTime()).toString();

        //set Calendar Header
        lblCalendarMonth.setText(monthWord[currentDateCalendar.get(Calendar.MONTH)]);
        lblCalendarYear.setText(String.valueOf(currentDateCalendar.get(Calendar.YEAR)));

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(final MaterialCalendarView widget, final CalendarDay date, boolean selected) {
                String dateSelected = simpleDateFormat.format(date.getDate());
                date_selected       = dateSelected;
                getTransactionDetails(date_selected);
            }
        });


        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int dateMonth       = date.getMonth();
                int month           = dateMonth + 1;
                int yr              = date.getYear();
                int day             = date.getDay();
                lblCalendarMonth.setText(monthWord[dateMonth]);
                lblCalendarYear.setText(String.valueOf(yr));
                if(yr != calendar.get(Calendar.YEAR)){
                    getAllApppointmentsOrEvents(date);
                }
            }
        });


        imgBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialCalendarView.goToNext();
            }
        });

        imgBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialCalendarView.goToPrevious();
            }
        });
        arrayAppointments    = new JSONArray();
        displayAppointment();

        //swipe refresh
        swipeRefresh            = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        utilities.returnRefreshColor(swipeRefresh);
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOptions(0);
            }
        });
//        animationInit();
        swipeConfiguration();
        swipeRefreshListner.onRefresh();


    }

    private void swipeConfiguration() {
        swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                getAllApppointmentsOrEvents(calendarDay);
            }
        };
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshListner.onRefresh();
            }
        });
    }

    private void getAllApppointmentsOrEvents(final CalendarDay date){

        swipeRefresh.setRefreshing(true);
        final int dayStart    = date.getCalendar().getActualMinimum(calendar.DAY_OF_MONTH);
        final int dayEnd      = date.getCalendar().getActualMaximum(calendar.DAY_OF_MONTH);
        final int month       = date.getMonth() + 1;
        final String start    = date.getYear()+"-"+month +"-"+dayStart;
        final String end      = date.getYear()+"-"+month +"-"+dayEnd;
        String token          = utilities.getToken();

        String url = SERVER_URL+"/api/appointment/getAppointments/client/"+clientID+"/allData";
        StringRequest arrayRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handler.open();
                        handler.deleteAppointment();
                        handler.close();
                        iterateResults(response,start,end);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        arrayErrorResponse = utilities.errorHandling(error);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefresh.setRefreshing(false);
                            }
                        }, 1000);
                        arrayErrorResponse = utilities.errorHandling(error);
                        utilities.showDialogMessage("Connection Error",arrayErrorResponse.get(1).toString(),"errro");
                        setCalendarDotToTransactions(start,end);
                    }
                });

        arrayRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);

    }

    private void iterateResults(String response, final String start, final String end) {


        try {
            handler.open();
            JSONArray array = new JSONArray(response);
            for (int x = 0; x < array.length(); x++){

                JSONObject objectResult     = array.getJSONObject(x);
                final int transaction_id    = objectResult.getInt("id");
                String dateTime             = objectResult.getString("transaction_datetime");
                String transaction_status   = objectResult.getString("transaction_status");
                JSONArray arrayItems        = objectResult.getJSONArray("items");
                Cursor cursorAppointment    = handler.returnSpecificAppointments(String.valueOf(transaction_id));
                Cursor cursorNotification   = handler.returnNotification(transaction_id);

                if(cursorAppointment.getCount() > 0){
                    handler.updateAppointments(String.valueOf(transaction_id),utilities.removeTimeFromDate(dateTime),transaction_status,objectResult.toString(),arrayItems.toString());
                }
                else{
                    handler.insertAppointments(String.valueOf(transaction_id),utilities.removeTimeFromDate(dateTime),transaction_status,objectResult.toString(),arrayItems.toString());
                }

            }
            handler.close();
            setCalendarDotToTransactions(start,end);
            getTransactionDetails(date_selected);
        }
        catch (JSONException e){
            e.printStackTrace();
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);
            }
        }, 1000L);
    }


    // data of dots
    public void getTransactionDetails(final String eventDate){

        try{
            arrayAppointments = new JSONArray();
            handler.open();
            Cursor query = handler.returnDateAppointments(eventDate);
            if(query.getCount() > 0){

                lblEmpty.setVisibility(View.GONE);
                recyclerActivity.setVisibility(View.VISIBLE);
                while(query.moveToNext()){
                    JSONObject objectDetails    = new JSONObject(query.getString(3));
                    arrayAppointments.put(objectDetails);
                }
                recyclerAdapter = new RecyclerActivityAdapter(Appointment.this,arrayAppointments);
                recyclerActivity.setAdapter(recyclerAdapter);
            }
            else{
                recyclerActivity.setVisibility(View.GONE);
                lblEmpty.setVisibility(View.VISIBLE);
            }
            handler.close();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }




    //point dots
    private void setCalendarDotToTransactions(String start,String end) {

        String selectedDate;
        handler = new DataHandler(getApplicationContext());
        handler.open();
        Cursor query = handler.returnAllAppointments();
        if(query.getCount() > 0){
            while (query.moveToNext()){
                selectedDate = query.getString(1);
                try {
                    Date currentDate = simpleDateFormat.parse(selectedDate);
                    arrayDate.add(CalendarDay.from(currentDate));
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            materialCalendarView.removeDecorator(eventDecorator);
            eventDecorator = new EventDecorator(Color.parseColor("#cc292b"), arrayDate);
            materialCalendarView.addDecorator(eventDecorator);
        }
        handler.close();
    }


    private void displayAppointment() {
        recyclerAdapter                                = new RecyclerActivityAdapter(this,arrayAppointments);
        recyclerActivity_layoutManager                 = new LinearLayoutManager(this);
        recyclerActivity.setHasFixedSize(true);
        recyclerActivity.setLayoutManager(recyclerActivity_layoutManager);
        recyclerActivity.setAdapter(recyclerAdapter);
        recyclerActivity.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }



    private void clickOptions(int i) {

        if(i == 0){
            boolean ifDateIsValid = utilities.checkIfAppointmentIsValid(date_selected+" 00:00:00",utilities.getCurrentDate()+" 00:00:00");
            if(ifDateIsValid == true){
                AppointmentSingleton appointmentSingleton = new AppointmentSingleton();
                appointmentSingleton.Instance().setAppReserved(date_selected);
                Intent intent = new Intent(getApplicationContext(), AppointmentForm.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else{
                Toast.makeText(getApplicationContext(),"Sorry, you can no longer book appointment in this date",Toast.LENGTH_SHORT).show();
            }
        }
        else if(i == 2){

        }
        else if(i == 3){
            Intent intent = new Intent(getApplicationContext(),AppointmentHistory.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else{
            swipeRefreshListner.onRefresh();
        }

    }

    private void animationInit() {
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        animFadeIn  = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        animFadeOut.setDuration(100);
        animFadeIn.setDuration(100);
    }



    //comment for future use(get appointment by month)
//        String url            = SERVER_URL+"/api/mobile/getAppointmentsByMonth?token="+token;
//        StringRequest arrayRequest = new StringRequest
//                (Request.Method.POST, url, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            arrayTransactions = new JSONArray(response);
////                            utilities.setCustomDialog(true);
//                            iterateResults(start,end);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                swipeRefresh.setRefreshing(false);
//                            }
//                        }, 1000L);
//                        arrayErrorResponse = utilities.errorHandling(error);
//                        Toast.makeText(getApplicationContext(),arrayErrorResponse.get(1),Toast.LENGTH_SHORT).show();
//                        setCalendarDotToTransactions(start,end);
//                    }
//                })
//        {
//            @Override
//            public Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("start_date",start);
//                params.put("end_date",end);
//                return params;
//            }
//        };
//        arrayRequest.setRetryPolicy(
//                new DefaultRetryPolicy(
//                        10000,
//                        0,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                )
//        );
//        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);
//        String url = SERVER_URL+"/api/appointment/getAppointments/client/"+clientID+"/active";

}
