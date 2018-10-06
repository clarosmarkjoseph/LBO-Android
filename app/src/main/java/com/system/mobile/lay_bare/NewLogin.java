package com.system.mobile.lay_bare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by ITDevJr1 on 4/5/2017.
 */
import com.system.mobile.lay_bare.NavigationDrawer.NavigationDrawerActivity;
import com.system.mobile.lay_bare.Utilities.Utilities;

public class NewLogin extends AppCompatActivity {
    Button btnSubmit,btnForgotPassword,btnSignUp,btnBackLogin;
    DataHandler handler;
    String clientID = "",token="";
    String SERVER_URL = "";
    EditText username_obj;
    EditText password_obj;
    String username,password,first_name,middle_name,last_name,address,bday,mobile,email,gender,branch,imageURL,branch_id,total_transaction,total_discount = "";
    String fb_id;
    LoginButton loginButton;
    CallbackManager callBackManager;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    RelativeLayout register;
    String device     = "Android";
    String devicename = "";
    Utilities utilities;
    String serial_no   = Build.SERIAL;
    ArrayList<String> arrayErrorResponse = new ArrayList<>();
    Profile profile;
    int attempts = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlogin);
        utilities   = new Utilities(this);
        initElements();
    }

    private void initElements(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           getWindow().setStatusBarColor(this.getResources().getColor(R.color.themeBlack));
        }
        FacebookSdk.sdkInitialize(this);
        Log.e("AppLog", "key:" + FacebookSdk.getApplicationSignature(this));
        printHashKey();
        handler         = new DataHandler(getApplicationContext());
        callBackManager = CallbackManager.Factory.create();

        username_obj    = (EditText)findViewById(R.id.txtUsername);
        password_obj    = (EditText)findViewById(R.id.txtPassword);
        btnSubmit       = (Button)findViewById(R.id.btnLogin);

        btnForgotPassword = (Button)findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),ForgotPassword.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Register.class);
                intent.putExtra("type", "regular");
                startActivity(intent);
            }
        });

        SERVER_URL = utilities.returnIpAddress();
        if(SERVER_URL == null){
            Toast.makeText(getApplicationContext(),"Somethings went wrong, refreshing.....",Toast.LENGTH_SHORT).show();
            Intent in = new Intent(getApplicationContext(), SplashScreen.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
        profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            LoginManager.getInstance().logOut();
        }
        devicename  = utilities.getDeviceName();
        register    = (RelativeLayout)findViewById(R.id.fragment_container);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
            }
        };
        loginButton =(LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_birthday"));
        loginButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                if (loginResult.getAccessToken() != null) {
                    Set<String> deniedPermissions = loginResult.getRecentlyDeniedPermissions();
                    if (deniedPermissions.contains("user_birthday") && deniedPermissions.contains("email")) {
                        LoginManager.getInstance().logOut();
                        Toast.makeText(getApplicationContext(),"Failed to login with facebook. \n An error occured while authenticating facebook. Please try again.",Toast.LENGTH_LONG).show();
                    }
                    else{
                        GenerateProfileDetails(loginResult);
                    }
                }
            }
            @Override
            public void onCancel() {
                Log.v("LoginActivity", "cancel");
                LoginManager.getInstance().logOut();
                Toast.makeText(getApplicationContext(),"Facebook login cancelled",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                LoginManager.getInstance().logOut();
                Toast.makeText(getApplicationContext(),"Facebook login error, please try again",Toast.LENGTH_LONG).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username  = username_obj.getText().toString().trim();
                password  = password_obj.getText().toString().trim();
                if(username.matches("")){
                    utilities.showDialogMessage("Missing Information","Please enter your email address","error");
                    return;
                }
                if(utilities.isEmailValid(username) == false){
                    utilities.showDialogMessage("Missing Information","Please enter a valid email address","error");
                    return;
                }
                if(password.matches("")){
                    utilities.showDialogMessage("Missing Information","Please enter your password","error");
                    return;
                }
                else{
                    utilities.showProgressDialog("Logging in...");
                    if(!DetectionConnection.isNetworkAvailable(getApplicationContext())){
                        utilities.hideProgressDialog();
                        utilities.showDialogMessage("Can't Connect.","Sorry, can't connect to server right now. Please try again.","error");
                        return;
                    }
                    else {
                       submitLogin();
                    }
                }
            }
        });
    }

    public void printHashKey() {
        try {
            final PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
            }
        }
        catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }

    private void GenerateProfileDetails(LoginResult loginResult) {
        utilities.showProgressDialog("Fetching Data....");
        accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("MY DATA response", response.toString());
                        if(object.has("id")){
                            fb_id       = object.optString("id","");
                            imageURL   = "https://graph.facebook.com/"+ fb_id +"/picture?type=large";
                        }
                        else{
                            fb_id      = "";
                            imageURL   = "";
                        }
                        if(object.has("birthday")){
                            bday        = object.optString("birthday","0000-00-00");
                            bday        = utilities.convertDisplayDateToStandardDate(bday);
                        }
                        else{
                            bday       = "";
                        }
                        if(object.has("email")){
                            email      = object.optString("email","");
                        }
                        else{
                            email      = "";
                        }
                        if(object.has("last_name")){
                            last_name      = object.optString("last_name","");
                        }
                        else{
                            last_name      = "";
                        }
                        if(object.has("first_name")){
                            first_name      = object.optString("first_name","");
                        }
                        else{
                            first_name      = "";
                        }
                        if(object.has("gender")){
                            gender      = object.optString("gender","");
                        }
                        else{
                            gender      = "";
                        }
                        FacebookLogin(fb_id,email,gender,last_name,first_name,imageURL,bday);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,first_name,last_name,birthday");
        request.setParameters(parameters);
        request.executeAsync();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callBackManager.onActivityResult(requestCode,resultCode,data);
    }

    public void FacebookLogin(final String save_fb_id, final String save_email, final String save_gender, final String save_last_name, final String save_first_name, final String save_imageURL, final String save_bday) {

        Log.e("imageURL",imageURL);
        String log_url =  SERVER_URL+"/api/mobile/FacebookLogin";
        StringRequest postRequest = new StringRequest(Request.Method.POST, log_url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String result = "";
                Log.e("Result fbLogin: ",response);
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    boolean isAlready       = jsonObject.getBoolean("isAlready");
                    JSONObject objectResult = jsonObject.optJSONObject("objResult");

                    if(isAlready == true){
                        utilities.hideProgressDialog();
                        clientID                    = objectResult.getString("id");
                        String token                = jsonObject.getString("token");
                        String first_name           = objectResult.getString("first_name");
                        String image                = objectResult.getString("user_picture").replace(" ","%20");

                        handler.open();
                        handler.insertToken(token,utilities.getCurrentDate());
                        handler.insertUserAccount(clientID,objectResult.toString());
                        handler.close();
                        utilities.hideProgressDialog();
                        Toast.makeText(getApplicationContext(),"Logged-in as: "+first_name,Toast.LENGTH_LONG).show();
                        Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                    else{
                        utilities.hideProgressDialog();
                        Intent intent = new Intent(getApplicationContext(), Register.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("type", "facebook");
                        intent.putExtra("fname", save_first_name);
                        intent.putExtra("mname","");
                        intent.putExtra("lname", save_last_name);
                        intent.putExtra("address","");
                        intent.putExtra("bday",save_bday);
                        intent.putExtra("mobile","");
                        intent.putExtra("email", save_email);
                        intent.putExtra("gender", save_gender);
                        intent.putExtra("imageUrl",save_imageURL);
                        intent.putExtra("branch","");
                        intent.putExtra("terms",0);
                        intent.putExtra("fbID", save_fb_id);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    utilities.hideProgressDialog();
                    LoginManager.getInstance().logOut();
                    arrayErrorResponse = utilities.errorHandling(error);
                    utilities.showDialogMessage("Connection Error!",arrayErrorResponse.get(1).toString(),"error");

                }
            }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fb_id", save_fb_id);
                params.put("fb_email",save_email);
                params.put("fb_bday", save_bday);
                params.put("fb_fname", save_first_name);
                params.put("fb_lname",save_last_name);
                params.put("fb_gender",save_gender);
                params.put("fb_image",save_imageURL);
                params.put("device",device);
                params.put("device_info",devicename);
                params.put("unique_device_id",serial_no);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
    }



    public void submitLogin() {

        final String log_url =  SERVER_URL+"/api/mobile/loginUser";
        arrayErrorResponse.clear();
        StringRequest postRequest = new StringRequest(Request.Method.POST, log_url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response)  {
                try {
                    Log.e("response",response);
                    JSONObject jsonObjectResult  = new JSONObject(response);
                    token                        = jsonObjectResult.getString("token");
                    JSONObject objectUser        = jsonObjectResult.getJSONObject("users_data");
                    clientID                     = objectUser.getString("id");
                    handler.open();
                    handler.deleteUserAccount();
                    handler.deleteToken();
                    handler.deleteTotalTransactions();
                    handler.insertUserAccount(clientID,objectUser.toString());
                    handler.insertToken(token,utilities.getCurrentDate());
                    handler.close();
                    utilities.hideProgressDialog();

                    Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },
            new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    utilities.hideProgressDialog();
                    arrayErrorResponse = utilities.errorHandling(error);
                    if(attempts>=5){
                        utilities.showDialogMessage("Error","You have just logged-in "+attempts+" times. We sent you an email.","error");
                    }
                    else{
                        utilities.showDialogMessage("Error",arrayErrorResponse.get(1).toString(),"error");
                    }
                    attempts++;
                }
            })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", username);
                params.put("password", password);
                params.put("device",device);
                params.put("device_info",devicename);
                params.put("unique_device_id",serial_no);
                params.put("attempts", String.valueOf(attempts));
                return params;
            }
        };
        postRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        80000,
                        2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
    }



    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        LoginManager.getInstance().logOut();
    }



}

