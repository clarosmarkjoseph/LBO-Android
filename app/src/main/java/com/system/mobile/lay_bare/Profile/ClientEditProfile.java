package com.system.mobile.lay_bare.Profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.system.mobile.lay_bare.Classes.BranchClass;
import com.system.mobile.lay_bare.Classes.ProfileClass;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark on 14/09/2017.
 */

public class ClientEditProfile extends AppCompatActivity {

    EditText txtFname,txtMname,txtLname,txtAddress,txtContact,txtBday,txtGender,txtEmail,txtOldPassword,txtNewPassword,txtConfirmPassword;
    Spinner spinner_profile_gender,spinner_profile_branch;
    ArrayAdapter gender_adapter;
    String gender = "";
    String fname,mname,lname,address,contact,bday="",email,branch,image,token,oldPassword,newPassword,confirmPassword,SERVER_URL = "",options = "",clientID = "";
    Calendar currentCalendarInstance    = Calendar.getInstance();
    Calendar calendarInstance           = Calendar.getInstance();
    ArrayList<String> arrayBranch   = new ArrayList<>();
    ArrayList<String> arrayBranchID = new ArrayList<>();
    CardView cardview_homebranch,cardview_profile_details,cardview_profile_account;
    ImageButton btnDoAction;
    Utilities utilities;
    ArrayList<String> arrayErrorResponse = new ArrayList<>();
    Map<String,String> params = new HashMap<String, String>();
    String birthdayFormat = "";
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_edit_profile);
        initElements();
        setToolbar();
    }

    private void initElements() {
        txtFname                = (EditText)findViewById(R.id.txtFname);
        txtMname                = (EditText)findViewById(R.id.txtMname);
        txtLname                = (EditText)findViewById(R.id.txtLname);
        txtAddress              = (EditText)findViewById(R.id.txtAddress);
        txtContact              = (EditText)findViewById(R.id.txtContact);
        txtEmail                = (EditText)findViewById(R.id.txtEmail);
        txtOldPassword          = (EditText)findViewById(R.id.txtOldPassword);
        txtNewPassword          = (EditText)findViewById(R.id.txtNewPassword);
        txtConfirmPassword      = (EditText)findViewById(R.id.txtConfirmPassword);
        spinner_profile_branch  = (Spinner)findViewById(R.id.spinner_profile_branch);
        spinner_profile_gender  = (Spinner)findViewById(R.id.spinner_profile_gender);
        btnDoAction             = (ImageButton)findViewById(R.id.btnDoAction);
        txtBday                 = (EditText)findViewById(R.id.txtBday);

        btnDoAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser(options);
            }
        });

        utilities           = new Utilities(this);
        SERVER_URL          = utilities.returnIpAddress();
        token               = utilities.getToken();
        clientID            = utilities.getClientID();

        Bundle bundle = getIntent().getExtras();
        options = bundle.getString("Options");
        initProfileOffline();

        if(options.equals("Account")){
            cardview_profile_account = (CardView)findViewById(R.id.cardview_profile_account);
            cardview_profile_account.setVisibility(View.VISIBLE);
            txtEmail.setText(email);
        }
        else if(options.equals("Branch")){
            cardview_homebranch = (CardView)findViewById(R.id.cardview_homebranch);
            cardview_homebranch.setVisibility(View.VISIBLE);
            getBranches();
        }
        else{
            cardview_profile_details = (CardView)findViewById(R.id.cardview_profile_details);
            cardview_profile_details.setVisibility(View.VISIBLE);
            setGender();
            txtBday.setFocusable( true );
            txtBday.setFocusableInTouchMode( true );
            txtBday.requestFocus();
            txtBday.setKeyListener(null);
            if(!bday.equals("")){
                txtBday.setText(utilities.convertStandardToDisplayDate(bday));
            }
            txtBday.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    setBirthday();
                }
            });
            txtFname.setText(fname);
            txtMname.setText(mname);
            txtLname.setText(lname);
            txtAddress.setText(address);
            txtContact.setText(contact);
        }



    }

    private void saveUser(String options) {
        final String branch_id;
        utilities.showProgressDialog("Saving....");
        String url;
        final String branch_name;

        if(options.equals("Branch")){
            url         = SERVER_URL+"/api/mobile/updateHomeBranch?token="+token;
            branch_name = arrayBranch.get(spinner_profile_branch.getSelectedItemPosition());
            branch_id   = arrayBranchID.get(spinner_profile_branch.getSelectedItemPosition());
            params.put("edit_home_branch_id", branch_id);
            params.put("edit_home_branch", branch_name);
            params.put("edit_client_id", clientID);
        }
        else if(options.equals("Account")){
            url             = SERVER_URL+"/api/mobile/updateAccount?token="+token;
            email           = txtEmail.getText().toString();
            oldPassword     = txtOldPassword.getText().toString();
            newPassword     = txtNewPassword.getText().toString();
            confirmPassword = txtConfirmPassword.getText().toString();
            if(oldPassword.equals("") || email.equals("") || newPassword.equals("") || confirmPassword.equals("")){
                utilities.hideProgressDialog();
                utilities.showDialogMessage("Required field is missing","Please fill-up all the fields","info");
                return;
            }
            else{
                if(newPassword.equals(confirmPassword)){
                    if (newPassword.length() <= 9) {
                        utilities.hideProgressDialog();
                        utilities.showDialogMessage("Secure your password!","Password too short.Your password must atleast 10 characters or numbers","info");
                        return;
                    }
                    else {
                        params.put("edit_email", email);
                        params.put("edit_old_password", oldPassword);
                        params.put("edit_new_password", newPassword);
                        params.put("edit_confirm_password", confirmPassword);
                        params.put("edit_client_id", clientID);
                    }
                }
                else{
                    utilities.hideProgressDialog();
                    utilities.showDialogMessage("Password not matched!","Password is not match. Please try again","info");
                    return;
                }
            }
        }
        else{
            url     = SERVER_URL+"/api/mobile/updatePersonalInfo?token="+token;
            fname   = txtFname.getText().toString();
            mname   = txtMname.getText().toString();
            lname   = txtLname.getText().toString();
            address = txtAddress.getText().toString();
            contact = txtContact.getText().toString();
            bday    = txtBday.getText().toString();
            String[] split_bday =  bday.split("/");
            int year  = Integer.parseInt(split_bday[2]);
            int day   = Integer.parseInt(split_bday[0]);
            int month = Integer.parseInt(split_bday[1]);
            int age = utilities.getAge(year,day,month);

            gender  = spinner_profile_gender.getSelectedItem().toString();
            if(fname.equals("") || lname.equals("") || address.equals("") || contact.equals("") || bday.equals("") || gender.equals("") ){
                utilities.hideProgressDialog();
                utilities.showDialogMessage("Field(s) missing","Please provide all your information in the field","info");
                return;
            }
            else if(bday.equals("00/00/0000")){
                utilities.hideProgressDialog();
                utilities.showDialogMessage("Birthday is missing","Please provide  your birthdate in the field","info");
                return;
            }

            if(age < 13) {
                utilities.hideProgressDialog();
                utilities.showDialogMessage("User is under age","Only 13 years old and above are allowed to register in this app","info");
                return;
            }
            else{
                bday    = utilities.convertDisplayDateToStandardDate(bday)+" 00:00:00";
                params.put("edit_client_id", clientID);
                params.put("edit_fname", fname);
                params.put("edit_mname", mname);
                params.put("edit_lname", lname);
                params.put("edit_address", address);
                params.put("edit_contact", contact);
                params.put("edit_bday", bday);
                params.put("edit_gender", gender.toLowerCase());
            }
        }

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        utilities.hideProgressDialog();
                        JSONObject jsonObject = new JSONObject(response);
                        String result   = jsonObject.getString("result");
                        Log.e("result: ",result);

                        if(result.equals("success")){
                            Toast.makeText(getApplicationContext(),"Successfully updated!",Toast.LENGTH_LONG).show();
                            saveJSONObjectData();
                            finish();
                        }
                        else if(result.equals("incorrect")){
                            Toast.makeText(getApplicationContext(),"Your old password is incorrect. Please try again",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Sorry, we had trouble saving your home branch this time.",Toast.LENGTH_LONG).show();
                            finish();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    utilities.hideProgressDialog();
                    utilities.errorHandling(error);
                    arrayErrorResponse = utilities.errorHandling(error);
                    utilities.showDialogMessage("Connection Error!",arrayErrorResponse.get(1).toString(),"error");
                }
            })
            {
                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
        strRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(strRequest);
}

    private void saveJSONObjectData() {
        ProfileClass profileClass = new ProfileClass(getApplicationContext());
        try {
            JSONObject objectParams = profileClass.returnClientObject();
            String user_data        = objectParams.getString("user_data");
            if(options.equals("Branch")){
                JSONObject objectUserData = new JSONObject(user_data);
                String branch_id          = arrayBranchID.get(spinner_profile_branch.getSelectedItemPosition());
                objectUserData.put("home_branch",branch_id);
                objectParams.put("user_data",objectUserData);
            }
            if(options.equals("Personal")){
                objectParams.put("user_address",address);
                objectParams.put("first_name",fname);
                objectParams.put("middle_name",mname);
                objectParams.put("last_name",lname);
                objectParams.put("gender",gender);
                objectParams.put("last_name",lname);
                objectParams.put("birth_date",bday);
                objectParams.put("user_mobile",contact);
            }
            profileClass.updateClientProfile(objectParams);
            Log.e("WEW",profileClass.returnClientObject().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initProfileOffline() {

        ProfileClass profileClass = new ProfileClass(getApplicationContext());
        JSONObject objectResponse =  profileClass.returnClientObject();
        try {
            clientID                    = objectResponse.getString("id");
            fname                       = utilities.changeNullString(objectResponse.getString("first_name"));
            mname                       = utilities.changeNullString(objectResponse.getString("middle_name"));
            lname                       = utilities.changeNullString(objectResponse.getString("last_name"));
            address                     = utilities.changeNullString(objectResponse.getString("user_address"));
            contact                     = utilities.changeNullString(objectResponse.getString("user_mobile"));
            bday                        = objectResponse.optString("birth_date","0000-00-00");
            gender                      = utilities.changeNullString(objectResponse.getString("gender"));
            email                       = utilities.changeNullString(objectResponse.getString("email"));
            JSONObject objClient        = new JSONObject(objectResponse.getString("user_data"));
            String branch_id            = utilities.changeNullString(objClient.getString("home_branch"));
            branch                      = profileClass.getHomeBranch(branch_id);
            image                       = utilities.changeNullString(objectResponse.getString("user_picture"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setGender() {
        gender_adapter = ArrayAdapter.createFromResource(this,R.array.gender_array,android.R.layout.simple_spinner_item);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_profile_gender.setAdapter(gender_adapter);
        spinner_profile_gender.setSelection(gender_adapter.getPosition(gender));
        spinner_profile_branch.setEnabled(false);
        spinner_profile_branch.setClickable(false);
    }

    private void setBirthday() {
        String myBday = txtBday.getText().toString();
        DatePickerDialog dpDialog;
        String[] dateStrip = myBday.split("/");
        int year   = Integer.parseInt(dateStrip[2]);
        int month  = Integer.parseInt(dateStrip[0]);
        int day    = Integer.parseInt(dateStrip[1]);
        calendarInstance.set(Calendar.YEAR, year);
        calendarInstance.set(Calendar.MONTH, month);
        calendarInstance.set(Calendar.DAY_OF_MONTH, day);


//        dpDialog            = new DatePickerDialog(this,R.style.myCustomDialogPicker,dialogDate,Integer.parseInt(getDate[0]),Integer.parseInt(getDate[1])-1,Integer.parseInt(getDate[2]));
//        DatePicker datePicker = dpDialog.getDatePicker();
//        datePicker.setMaxDate(myCalendar.getTimeInMillis());
//        dpDialog.show();

        dpDialog = new DatePickerDialog(this,R.style.myCustomDialogPicker,dialogDate,year,month-1,day);
        DatePicker datePicker     = dpDialog.getDatePicker();
        datePicker.setMaxDate(currentCalendarInstance.getTimeInMillis());
        dpDialog.setTitle("Select Date");
        dpDialog.show();
    }

    //initiate DialogDate and get the current date
    DatePickerDialog.OnDateSetListener dialogDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            int equalMonth = month;
            updateTextLabel(year,day,month+1);
        }
    };

    private void updateTextLabel(int year, int day, int month) {
        txtBday.setText((month<10?("0"+month):(month)) +"/"+(day<10?("0"+day):(day))+"/"+year);
    }

    public void getBranches() {

        BranchClass branchClass = new BranchClass(getApplicationContext());
        JSONArray branchArray   = branchClass.arrayReturnBranch();
        for(int x = 0; x < branchArray.length();x++) {
            try {
                JSONObject json = branchArray.getJSONObject(x);
                arrayBranch.add(json.getString("branch_name"));
                arrayBranchID.add(json.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        spinner_profile_branch.setAdapter(new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, arrayBranch));
        spinner_profile_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_profile_branch.setSelection(arrayBranch.indexOf(branch));

    }

    public void setToolbar(){

        btnDoAction.setVisibility(View.VISIBLE);
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
        forTitle.setText("Edit Profile");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
