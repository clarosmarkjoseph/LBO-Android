package com.system.mobile.lay_bare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.FAQs.RecyclerFAQ;
import com.system.mobile.lay_bare.NavigationDrawer.NavigationDrawerActivity;
import com.system.mobile.lay_bare.Utilities.DateDialogUtilities;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import cc.cloudist.acplibrary.ACProgressFlower;


public class Register extends AppCompatActivity implements OnSetBOSSArrayIndex {

    EditText regBday;

    Network network;
    Button btnReg;
    DataHandler handler;
    String token = "";
    String  lname,fname,mname,address,birthday,gender,email,password,confirm,phone,branch,clientID,image;
    int terms = 0;
    String fbID = "";
    EditText regBossID,reglname,regfname,regmname,regaddress,regbirthday,regemail,regpassword,regconfirm,regphone;
    Spinner reggender,regBranch;
    ArrayAdapter<CharSequence> adapter;
    Boolean isTermsIsChecked = false;
    LinearLayout linear_agree;
    Button btnTerms;
    String SERVER_URL,branch_id="";
    Utilities utilities;
    int branch_index;
    ArrayList<String> arrayBranch;
    ArrayList<String> arrayBranchID;
    ArrayList<String> arrayErrorResponse = new ArrayList<>();
    InputMethodManager imm;
    DatePickerDialog.OnDateSetListener dialogDate;
    DatePickerDialog dpDialog;
    Calendar myCalendar = Calendar.getInstance();
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;
    LinearLayout linearContent;
    String stringTerms = "";
    LinearLayout linearBossIDValue,linearBossID,linearBossLoading;
    RecyclerView recyclerBossRecord;
    RecyclerView.LayoutManager recyclerLayout;
    RecyclerView.Adapter recyclerAdapter;
    JSONArray arrayBossRecords = new JSONArray();
    int bossIndex = 0;
    ImageButton btnRefreshBoss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        network = new BasicNetwork(new HurlStack());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.themeBlack));
        }

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        setToolbar();

        utilities  = new Utilities(this);
        token      = utilities.getToken();
        SERVER_URL = utilities.returnIpAddress();
        linear_agree   = (LinearLayout) findViewById(R.id.linear_agree);
        btnTerms   = (Button) findViewById(R.id.btnTerms);
        linearContent   = (LinearLayout) findViewById(R.id.linearContent);
        btnReg          = (Button)findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveRegistration();
            }
        });
        reglname    = (EditText)findViewById(R.id.regLname);
        regfname    = (EditText)findViewById(R.id.regFname);
        regmname    = (EditText)findViewById(R.id.regMname);
        regaddress  = (EditText)findViewById(R.id.regAddress);
        regphone    = (EditText)findViewById(R.id.regPhone);
        regBday         = (EditText)findViewById(R.id.regBday);
        reggender   = (Spinner)findViewById(R.id.regGender);
        regemail    = (EditText)findViewById(R.id.regEmail);
        regpassword = (EditText)findViewById(R.id.regPassword);
        regconfirm  = (EditText)findViewById(R.id.regConfirmPassword);
        regBranch       = (Spinner)findViewById(R.id.regBranch);

        regBossID   = (EditText)findViewById(R.id.regBossID);
        btnRefreshBoss  = (ImageButton) findViewById(R.id.btnRefreshBoss);

        linearBossIDValue   =  (LinearLayout) findViewById(R.id.linearBossIDValue);
        linearBossID        =  (LinearLayout) findViewById(R.id.linearBossID);
        linearBossLoading   =  (LinearLayout) findViewById(R.id.linearBossLoading);
        recyclerBossRecord  =  (RecyclerView) findViewById(R.id.recyclerBossRecord);

        arrayBranch   = new ArrayList<String>();
        arrayBranchID = new ArrayList<>();

        regBday.setFocusable( true );
        regBday.setFocusableInTouchMode( true );
        regBday.requestFocus();
        regBday.setKeyListener(null);
        regBday.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                showDateDialog();
            }
        });
        btnTerms.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                showDialogTerms("Terms And Condition");
            }
        });

        btnRefreshBoss.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bossIndex = 0;
                regBossID.setText("");
                linearBossIDValue.setVisibility(View.GONE);
                linearBossID.setVisibility(View.VISIBLE);
            }
        });

        setGender();
        retrieveBranches();
        getTerms();
        retrieveExtra();
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

    private void getTerms(){
        utilities.showProgressDialog("Loading...");
        String url = SERVER_URL+"/api/config/getTerms";
        StringRequest stringRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        utilities.hideProgressDialog();
                        stringTerms = response;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utilities.hideProgressDialog();
                        arrayErrorResponse = utilities.errorHandling(error);
                        Toast.makeText(getApplicationContext(),arrayErrorResponse.get(1),Toast.LENGTH_LONG).show();
                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void setDate(int year, int day, int month) {
        String dateSelected = (year)+"-"+(month<10?("0"+month):(month))+"-"+(day<10?("0"+day):(day));
        regBday.setText(dateSelected);
        String fname = regfname.getText().toString();
        String lname = reglname.getText().toString();
        if(!fname.isEmpty() && !lname.isEmpty()){
            getLastTransaction();
        }
    }

    private void showDateDialog() {
        String[] getDate;
        if (regBday.getText().toString().isEmpty()){
            getDate    = utilities.getCurrentDate().split("-");
        }
        else{
            getDate    = regBday.getText().toString().split("-");
        }
        dpDialog            = new DatePickerDialog(this,R.style.myCustomDialogPicker,dialogDate,Integer.parseInt(getDate[0]),Integer.parseInt(getDate[1])-1,Integer.parseInt(getDate[2]));

        DatePicker datePicker = dpDialog.getDatePicker();
        datePicker.setMaxDate(myCalendar.getTimeInMillis());
        dpDialog.setTitle("");
        dpDialog.show();
    }

    public void retrieveBranches(){
        handler = new DataHandler(getApplicationContext());
        handler.open();
        Cursor cursorBranch = handler.returnBranches();
        if(cursorBranch.getCount() > 0){
            cursorBranch.moveToFirst();
            try {
                JSONArray arrayJSONBranch = new JSONArray(cursorBranch.getString(1));
                for (int x = 0; x < arrayJSONBranch.length(); x++){
                    JSONObject objectJSONBranch = arrayJSONBranch.getJSONObject(x);
                    String myid                 = objectJSONBranch.getString("id");
                    String branch_name          = objectJSONBranch.getString("branch_name");
                    arrayBranch.add(branch_name);
                    arrayBranchID.add(myid);
                }
                regBranch.setAdapter(new ArrayAdapter<String>(Register.this, android.R.layout.simple_spinner_dropdown_item, arrayBranch));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        handler.close();
    }

    private void displayRecyclerView(){

        if (arrayBossRecords.length() > 0){
            linearBossLoading.setVisibility(View.GONE);
            linearBossID.setVisibility(View.VISIBLE);
        }
        else{
            linearBossLoading.setVisibility(View.GONE);
            linearBossID.setVisibility(View.GONE);
        }
        //setBoss Record
        recyclerAdapter  = new RecyclerBossRecord(this,arrayBossRecords,true);
        recyclerLayout = new LinearLayoutManager(getApplicationContext());
        recyclerBossRecord.setAdapter(recyclerAdapter);
        recyclerBossRecord.setLayoutManager(recyclerLayout);
        recyclerBossRecord.setItemAnimator(new DefaultItemAnimator());
        recyclerBossRecord.setNestedScrollingEnabled(false);
    }

    private void retrieveExtra() {
        final Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String type = bundle.getString("type");
            if(type.equals("facebook")){
                fbID = bundle.getString("fbID");
                regfname.setText(bundle.getString("fname"));
                regmname.setText(bundle.getString("mname"));
                reglname.setText(bundle.getString("lname"));
                regaddress.setText(bundle.getString("address"));
                regBday.setText(bundle.getString("bday"));
                regphone.setText(bundle.getString("mobile"));
                regemail.setText(bundle.getString("email"));
                terms = (bundle.getInt("terms"));
                image = bundle.getString("imageUrl");
                isTermsIsChecked = terms != 0;
                reggender.setSelection(adapter.getPosition(bundle.getString("gender")));
                if(bundle.getString("gender") != null || !bundle.getString("gender").equals("") ){
                    CountDownTimer timer = new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }
                        @Override
                        public void onFinish() {
                            regBranch.setSelection(arrayBranch.indexOf(bundle.getString("branch")));
                        }
                    }.start();
                }
                if(fbID != null || (fbID != "")){
                    registeredVia(0,"Congratulations!","You will be signed-up to Lay Bare Waxing Salon through your Facebook account. Please fill-up all necessary fields and continue.");
                }
                else{
                    registeredVia(2,"Clarifications","Hello! Please agree to our terms and conditions to continue using Lay Bare App");
                }
            }
            else{
                registeredVia(1,"Welcome to Lay Bare!","Please enter your personal details to create your own Lay Bare Online Account. You may use this account in any platforms that we have (Online and Mobile Application)");
            }
        }
        else{
            registeredVia(1,"Welcome to Lay Bare!","Please enter your personal details to create your own Lay Bare Online Account. You may use this account in any platforms that we have (Online and Mobile Application)");
        }
    }

    private void registeredVia(final int isRegisteredVia,String title, String message){
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle(title);
        helpBuilder.setMessage(message);
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Log.e("IS :", String.valueOf(isRegisteredVia));
                        if(isRegisteredVia == 2){
                            popUpOptions();
                        }

                    }
                });
        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }


    private void popUpOptions() {

        final AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.modal_terms,null);
        myBuilder.setView(mView);
        myBuilder.setIcon(R.drawable.app_logo);
        final AlertDialog myDialog = myBuilder.create();
        CheckBox chkAgree = (CheckBox)mView.findViewById(R.id.chkAgree);
        final Button btnAgree = (Button)mView.findViewById(R.id.btnAgree);
        Button btnCancel = (Button)mView.findViewById(R.id.btnCancel);
        btnAgree.setEnabled(false);
        btnAgree.setAlpha(Float.parseFloat("0.5"));
        chkAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    btnAgree.setEnabled(true);
                    btnAgree.setAlpha(Float.parseFloat("1.0"));
                }
                else{
                    btnAgree.setEnabled(false);
                    btnAgree.setAlpha(Float.parseFloat("0.5"));
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTermsIsChecked = false;
                myDialog.dismiss();
            }
        });

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTermsIsChecked = true;
                myDialog.dismiss();
            }
        });
        myDialog.setCancelable(false);
        myDialog.show();
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

    private void showExit() {

        String message = "";
       if(fbID.isEmpty()){
           message = "You have successfully registered your account. To activate your account, kindly check your email";
           AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
           helpBuilder.setTitle("Succesfully Registered!");
           helpBuilder.setMessage(message);
           helpBuilder.setPositiveButton("Ok",
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           Intent intent = new Intent(Register.this,NewLogin.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           startActivity(intent);
                           overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                           finish();
                       }
                   });
           utilities.hideProgressDialog();
           AlertDialog helpDialog = helpBuilder.create();
           helpDialog.show();
       }
       else{
           utilities.hideProgressDialog();
           message = "You have successfully registered your account. You may book your appointments on this account via Lay Bare Online And Mobile App.";
           AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
           helpBuilder.setTitle("Welcome to Lay Bare!");
           helpBuilder.setMessage(message);
           helpBuilder.setPositiveButton("Ok",
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           Intent intent = new Intent(Register.this,NewLogin.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           startActivity(intent);
                           overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                           finish();
                       }
                   });

           AlertDialog helpDialog = helpBuilder.create();
           helpDialog.show();
       }
    }

    public void getLastTransaction(){

        utilities.showProgressDialog("Loading Account....");
        linearBossLoading.setVisibility(View.VISIBLE);

        String reg_bday  = regBday.getText().toString();
        String reg_fname = regfname.getText().toString().replace(" ","%20");
        String reg_lname = reglname.getText().toString().replace(" ","%20");
        String myURL = "https://api.lay-bare.com/transactions/getLastTransaction?birth_date="+reg_bday+"&first_name="+reg_fname+"&last_name="+reg_lname;
        JsonArrayRequest objectRequest  = new JsonArrayRequest
                (Request.Method.GET, myURL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response reg",response.toString());
                        utilities.hideProgressDialog();
                        arrayBossRecords = response;
                        displayRecyclerView();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        arrayErrorResponse = utilities.errorHandling(error);
                        btnReg.setEnabled(true);
                        btnReg.setAlpha(Float.parseFloat("1.0"));
                        utilities.hideProgressDialog();
                        Log.e("arrayErrorResponse",arrayErrorResponse.get(1).toString());
                        utilities.showDialogMessage("Connection Error",arrayErrorResponse.get(1).toString(),"error");
                    }
                });
        objectRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        60000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
//        objectRequest.cancel();
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
    }



    public void saveRegistration(){

        try{
            email           = regemail.getText().toString().trim();
            lname           = reglname.getText().toString().trim();
            fname           = regfname.getText().toString().trim();
            mname           = regmname.getText().toString().trim();
            address         = regaddress.getText().toString().trim();
            phone           = regphone.getText().toString().trim();
            birthday        = regBday.getText().toString().trim();
            gender          = reggender.getSelectedItem().toString();
            password        = regpassword.getText().toString().trim();
            confirm         = regconfirm.getText().toString().trim();
            branch          = regBranch.getSelectedItem().toString();
            branch_index    = regBranch.getSelectedItemPosition();
            branch_id       = arrayBranchID.get(branch_index);

            if(fname.matches("")) {
                utilities.showDialogMessage("Missing information","Please indicate your first name","error");
            }
            else if(lname.matches("")) {
                utilities.showDialogMessage("Missing information","Please indicate your last name","error");
            }
            else if(address.matches("")) {
                utilities.showDialogMessage("Missing information","Please indicate your address","error");
            }
            else if(phone.matches("")) {
                utilities.showDialogMessage("Missing information","Please indicate your phone number","error");
            }
            else if(phone.length() < 10) {
                utilities.showDialogMessage("Invalid Information","Your phone number is invalid. Please try again.","error");
            }
            else if(birthday.matches("")) {
                utilities.showDialogMessage("Missing information","Please indicate your birthday","error");
            }
            else if(email.matches("")) {
                utilities.showDialogMessage("Missing information","Please indicate your email address","error");
            }
            else if(utilities.isEmailValid(email) == false){
                utilities.showDialogMessage("Invalid Email Address","Please enter a valid email address","error");
            }
            else if(password.matches("")) {
                utilities.showDialogMessage("Missing information","Please indicate your password","error");
            }
            else if(confirm.equals("")) {
                utilities.showDialogMessage("Missing information","Please indicate your confirmed password","error");
            }
            else if(password.length()<8) {
                utilities.showDialogMessage("Incomplete","Password must greater than 8 letters,characters or numbers","error");
            }
            else if(!(confirm.equals(password))) {
                utilities.showDialogMessage("Doesn't Match!","Your password doesn't match","error");
            }
            else {
                String[] split_bday =  birthday.split("-");
                int year  = Integer.parseInt(split_bday[0]);
                int day   = Integer.parseInt(split_bday[2]);
                int month = Integer.parseInt(split_bday[1]);
                int age   = utilities.getAge(year,day,month);
                if(age < 13) {
                    utilities.showDialogMessage("Under age","Age must 13 years old above","error");
                }
                else {
                    addAccount();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Snackbar bar = Snackbar.make(linearContent,"Connection Lost", Snackbar.LENGTH_LONG)
                    .setAction("Try again", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            bar.show();
        }
    }

    // save url to server
    private void addAccount() {

        btnReg.setEnabled(false);
        btnReg.setAlpha(Float.parseFloat("0.5"));
        utilities.showProgressDialog("Saving....");
        String myURL    = SERVER_URL+"/api/mobile/registerUser";
        String boss_id  = regBossID.getText().toString();
        if (boss_id.equals("")){
            boss_id = "0";
        }
        JSONObject objectParams = new JSONObject();
        try {
            objectParams.put("addEmail", email);
            objectParams.put("addLname", lname);
            objectParams.put("addMname", mname);
            objectParams.put("addFname", fname);
            objectParams.put("addAddress",address);
            objectParams.put("addMobile", phone);
            objectParams.put("addBday",birthday);
            objectParams.put("addGender", gender);
            objectParams.put("addPassword", password);
            objectParams.put("addBranch",branch_id);
            objectParams.put("addBossID",boss_id);
            objectParams.put("addBossArray",arrayBossRecords);
            objectParams.put("addFBID",fbID);
            objectParams.put("addDevice","Android");
            objectParams.put("addDeviceName",utilities.getDeviceName());
            objectParams.put("addImageUrl",image);
            objectParams.put("addUniqueID",Build.SERIAL);

            JsonObjectRequest objectRequest  = new JsonObjectRequest
                    (Request.Method.POST, myURL, objectParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            btnReg.setEnabled(true);
                            btnReg.setAlpha(Float.parseFloat("1.0"));
                            boolean isFacebook    = false;
                            try {
                                isFacebook = response.getBoolean("isFacebook");
                                utilities.hideProgressDialog();
                                if(isFacebook == true){
                                    image                   = response.getString("image");
                                    token                   = response.getString("token");
                                    clientID                = response.getString("client_id");
                                    JSONObject objectClient = response.getJSONObject("client_data");
                                    handler                 = new DataHandler(getApplicationContext());
                                    handler.open();
                                    handler.deleteToken();
                                    handler.deleteUserAccount();
                                    handler.insertToken(token,utilities.getCurrentDate());
                                    branch_index    = regBranch.getSelectedItemPosition();
                                    branch_id       = arrayBranchID.get(branch_index);
                                    handler.insertUserAccount(clientID,objectClient.toString());
                                    handler.close();
                                    Toast.makeText(getApplicationContext(),"Registration complete!.",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this,NavigationDrawerActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                }
                                else{
                                    showExit();
                                    btnReg.setEnabled(true);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            arrayErrorResponse = utilities.errorHandling(error);
                            btnReg.setEnabled(true);
                            btnReg.setAlpha(Float.parseFloat("1.0"));
                            utilities.hideProgressDialog();
                            utilities.showDialogMessage("Connection Error",arrayErrorResponse.get(1).toString(),"error");
                        }
                    });
            objectRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            8000,
                            3,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }




    //set gender
    private void setGender() {
        adapter = ArrayAdapter.createFromResource(this,R.array.gender_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Log.d("array_gender: ", String.valueOf(adapter));
        reggender.setAdapter(adapter);
    }









    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
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

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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
        forTitle.setText("Registration");
    }

    //dialog box(without action
    public void showDialogTerms(String title) {

        final Dialog dialog                 = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_terms);
        TextView lbldialog_title            = (TextView) dialog.findViewById(R.id.lbldialog_title);
        TextView lblContent                 = (TextView) dialog.findViewById(R.id.lblContent);
        Button btndialog_cancel             = (Button) dialog.findViewById(R.id.btndialog_cancel);
        Button btndialog_confirm            = (Button) dialog.findViewById(R.id.btndialog_confirm);
        ImageButton imgBtnClose             = (ImageButton) dialog.findViewById(R.id.imgBtn_dialog_close);
        RelativeLayout relativeToolbar      = (RelativeLayout) dialog.findViewById(R.id.relativeToolbar);

        btndialog_cancel.setVisibility(View.VISIBLE);

        relativeToolbar.setBackgroundColor(getResources().getColor(R.color.laybareInfo));
        btndialog_confirm.setBackgroundColor(getResources().getColor(R.color.laybareInfo));

        lbldialog_title.setText(title);
        Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(stringTerms, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(stringTerms);
        }
//        Spanned sp = Html.fromHtml(stringTerms);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            lblContent.setText(Html.fromHtml(stringTerms, Html.FROM_HTML_MODE_LEGACY));
//        }
//        else {
//            lblContent.setText(stringTerms);
//        }
        lblContent.setText(spanned);

        final Dialog myDialog = dialog;
        btndialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                Handler myHandler = new Handler();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnTerms.setVisibility(View.GONE);
                        linear_agree.setVisibility(View.VISIBLE);
                        btnReg.setEnabled(true);
                        btnReg.setAlpha((float) 1.0);
                    }
                },500);
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        btndialog_cancel.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void OnSetBOSSArrayIndex(int index) {
        bossIndex = index;
        linearBossID.setVisibility(View.GONE);
        linearBossIDValue.setVisibility(View.VISIBLE);
        try {
            JSONObject objectBossRecord = arrayBossRecords.getJSONObject(index);
            String boss_id              = objectBossRecord.optString("custom_client_id","");
            String email_address        = objectBossRecord.optString("email_address","");
            String contact_number       = objectBossRecord.optString("contact_number","");
            String middlename           = objectBossRecord.optString("middlename","");
            String gender              = objectBossRecord.optString("gender","");
            if (gender.equals("f") || gender.equals("female") || gender.equals("Female") || gender.equals("FEMALE")){
                reggender.setSelection(0);
            }
            else{
                reggender.setSelection(1);
            }
            regmname.setText(middlename);
            regemail.setText(email_address);
            regphone.setText(contact_number);
            regBossID.setText(boss_id);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
