package com.system.mobile.lay_bare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.Utilities.DateDialogUtilities;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ITDevJr1 on 4/11/2017.
 */

public class ForgotPassword extends AppCompatActivity{
    Button btnSendEmail;
    String email ="",url = "",bday = "";
    EditText txtEmailForgot,txtBday;
    String SERVER_URL = "";
    LinearLayout relative_forgotPassword;
    Utilities utilities;
    Calendar calendar = Calendar.getInstance();
    ArrayList<String> arrayErrorResponse = new ArrayList<>();
    InputMethodManager imm;
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;
    DatePickerDialog.OnDateSetListener dialogDate;
    DatePickerDialog dpDialog;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.themeBlack));
        }

        //keyboard input
        imm                 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        btnSendEmail        = (Button)findViewById(R.id.btnSendEmail);
        txtEmailForgot      = (EditText)findViewById(R.id.txtEmailForgot);
        txtBday             = (EditText)findViewById(R.id.txtBday);
        utilities           = new Utilities(this);
        SERVER_URL          = utilities.returnIpAddress();

        txtBday.setFocusable( true );
        txtBday.setFocusableInTouchMode( true );
        txtBday.requestFocus();
        txtBday.setKeyListener(null);
//        txtBday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View arg0, boolean hasfocus) {
//                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
//                showDateDialog();
//            }
//        });

        txtBday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                showDateDialog();
            }
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = txtEmailForgot.getText().toString().trim();
                bday  = txtBday.getText().toString().trim();
                if(email.matches("")) {
                    showSimplePopUp("Missing","Please enter your email address");
                }
                else {
                    if (!DetectionConnection.isNetworkAvailable(getApplicationContext())){
                        showSimplePopUp("Info","There was a problem connecting to Lay Bare App. Please check your connection and try again");
                    }
                    else {
                        sendEmailConfirmation();
                    }
                }
            }
        });
        relative_forgotPassword = (LinearLayout)findViewById(R.id.relative_forgotPassword);
        setToolbar();
        initDateDialog();

    }

    private void initDateDialog() {
        dialogDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                myCalendar.set(Calendar.YEAR,year);
//                myCalendar.set(Calendar.MONTH,month);
//                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                setDate(year,day,month+1);
            }
        };
    }

    private void setDate(int year, int day, int month) {
        String dateSelected = (year)+"-"+(month<10?("0"+month):(month))+"-"+(day<10?("0"+day):(day));
        txtBday.setText(dateSelected);
    }

    private void showDateDialog() {
        String[] getDate    = utilities.getCurrentDate().split("-");
        dpDialog            = new DatePickerDialog(this, R.style.DialogTheme,dialogDate,Integer.parseInt(getDate[0]),Integer.parseInt(getDate[1])-1,Integer.parseInt(getDate[2]));
        DatePicker datePicker = dpDialog.getDatePicker();
        datePicker.setMaxDate(myCalendar.getTimeInMillis());
        dpDialog.setTitle("");
        dpDialog.show();
    }


    private void sendEmailConfirmation() {

        utilities.showProgressDialog("Verifying account...");
        url   = SERVER_URL+"/api/mobile/verifyMyPassword";

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        utilities.hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String  result        = jsonObject.getString("result");
                            Toast.makeText(getApplicationContext(),"Email successfully sent. Please check your email address and reset your password",Toast.LENGTH_LONG).show();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utilities.hideProgressDialog();
                        utilities.errorHandling(error);
                        arrayErrorResponse = utilities.errorHandling(error);
                        utilities.showDialogMessage("Error",arrayErrorResponse.get(1).toString(),"info");
                    }
                })
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("verify_email",email);
                params.put("verify_birth_date",bday);
                return params;
            }
        };
        strRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        3,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(strRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation,menu);
        return true;
    }
    public void showSimplePopUp(String title,String Message) {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle(title);
        helpBuilder.setMessage(Message);
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });
        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }


    private void setToolbar() {
        myTypeface          = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle            = (TextView)findViewById(R.id.forTitle);
        imgBtnBack          = (ImageButton) findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forTitle.setTypeface(myTypeface);
        forTitle.setText("Forgot Password");
    }



}
