package com.system.mobile.lay_bare.Location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.system.mobile.lay_bare.BuildConfig;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mark on 13/12/2017.
 */

public class LocationIndex extends AppCompatActivity {

    private static final int REQUEST_FOR_LOCATION_PERMISSION = 1, REQUEST_GPS = 2;

    static final String TAG = LocationIndex.class.getSimpleName();
    TextView forTitle;
    Utilities utilities;
    boolean isGPSTurnedON = false;
    double latitude = 0.0;
    double longitude = 0.0;
    String locationName = "";
    Location location;
    LinearLayout linear_content, linear_loading;
    FusedLocationProviderClient mfFusedLocationClient;
    TextView lblTitle, lblDesc;
    Button btnGPS;
    private Typeface myTypeface;
    private ImageButton imgBtnBack;
    LocationTracker locationTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_index);
        utilities = new Utilities(this);
        setToolbar();
        mfFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        linear_content = (LinearLayout) findViewById(R.id.linear_content);
        linear_loading = (LinearLayout) findViewById(R.id.linear_loading);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblDesc = (TextView) findViewById(R.id.lblDesc);
        btnGPS = (Button) findViewById(R.id.btnGPS);
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonActionClicked();
            }
        });
        showLoadingLocation();
        requestPermissions();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void showPermissionDialog() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FOR_LOCATION_PERMISSION);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission( LocationIndex.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( LocationIndex.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showPermissionDialog();
        }
        else {
            Log.e(TAG,"get last Location");
            getLastLocation();
        }
    }

    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("ANYARE", "WTF IS THIS permission");
            showPermissionDialog();
//            return;
        }
        final Task locationResult = mfFusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {

            @Override
            public void onComplete(@NonNull final Task<Location> task) {
                showLoadingLocation();
                if (task.isSuccessful() && task.getResult() != null){

                    Log.e(TAG, "YES location");
                    location        = task.getResult();
                    longitude       = location.getLongitude();
                    latitude        = location.getLatitude();
                    locationName    = getCompleteAddressString(latitude,longitude);
                    Intent intent   = new Intent(getApplicationContext(),LocationMap.class);
                    intent.putExtra("lat",latitude);
                    intent.putExtra("long",longitude);
                    intent.putExtra("name",locationName);
                    Log.e("LAT LONG",latitude+" - "+longitude+" - "+locationName+" YEAH");
                    startActivity(intent);
                    finish();
                }
                else{

                    Log.e(TAG, "GetLastLocation:exception",task.getException());
                    Log.e("ANYARE locationn", "locationn:"+ String.valueOf(location));
                    locationTracker =   new LocationTracker(LocationIndex.this);
                    if (!locationTracker.canGetLocation()) {
                        Log.e(TAG, "we cannot locate your current location");
                        hideLoadingLocation();
                        Toast.makeText(getApplicationContext(),"We cannot locate your current location. Please wait for a while and try again.",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Log.e(TAG, "Alternative location");
                        Handler myHandler = new Handler();
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                location        = locationTracker.getLocation();
                                if (location != null){
                                    longitude       = location.getLongitude();
                                    latitude        = location.getLatitude();
                                    locationName    = getCompleteAddressString(latitude,longitude);
                                    Intent intent   = new Intent(getApplicationContext(),LocationMap.class);
                                    intent.putExtra("lat",String.valueOf(latitude));
                                    intent.putExtra("long",String.valueOf(longitude));
                                    intent.putExtra("name",locationName);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"We cannot locate your current location. Please wait for a while and try again.",Toast.LENGTH_LONG).show();
                                    Handler myHandler = new Handler();
                                    myHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    },1500);
                                }
                            }
                        },500);

                    }


                }
            }
        });


    }

//    private class loadAlternativeLocation extends AsyncTask<Void,Void,Void>{
//
//        double longitude    = 0;
//        double latitude     = 0;
//        String locationName = "";
//        Location location;
//        LocationTracker locationTracker;
//
//        public interface LocationResponse {
//            void processFinish(Boolean output);
//        }
//
//
//        LocationResponse asynResponse = null;
//
//        public loadAlternativeLocation(LocationResponse asynResponse,Location location,LocationTracker locationTracker) {
//            this.asynResponse   = asynResponse;
//            this.location           = location;
//            this.locationTracker    = locationTracker;
//        }
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showLoadingLocation();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Log.e(TAG, "Alternative location");
//
//
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==REQUEST_GPS) {
            showLoadingLocation();
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            Log.e("Providers",provider);
            if(provider != null){
//                    Log.e("ANYARE", " Location providers: "+provider);
                getLastLocation();
            }
            else{
                hideLoadingLocation();
            }
        }

    }

    //ON REQUEST PERMISSION RESULT
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_FOR_LOCATION_PERMISSION){
            if(grantResults.length <= 0 ){
                Log.e("ANYARE","User interaction was cancelled");
            }
            else if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.e("ANYARE","PERMISSION_GRANTED");
                getLastLocation();
            }
            else{
                Log.e("ANYARE","Permission denied.");
                Toast.makeText(getApplicationContext(),"Sorry, Branch Location will not work if you deny the 'LOCATION PERMISSION'. Allow Location Permission in Settings to continue ",Toast.LENGTH_LONG).show();
                Handler myHandler = new Handler();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1500);
            }
        }
    }

    void buttonActionClicked(){
        if(isGPSTurnedON == false){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_GPS);
        }
        else {
            getLastLocation();
        }
    }



    public String getCompleteAddressString(double lat, double longi) {
        String strAdd = "";
        Geocoder geocoder;
        List<Address> yourAddresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            yourAddresses= geocoder.getFromLocation(lat, longi, 1);
            if (yourAddresses.size() > 0) {
                String yourAddress = yourAddresses.get(0).getAddressLine(0);
                strAdd = yourAddress;
            }
            else{
                strAdd = "Cannot determine location";
            }
        }
        catch (IOException e) {
            strAdd = "Cannot determine location";
            e.printStackTrace();
        }
        return strAdd;
    }

    void showLoadingLocation(){
        linear_content.setVisibility(View.GONE);
        linear_loading.setVisibility(View.VISIBLE);
    }

    void hideLoadingLocation(){
        linear_content.setVisibility(View.VISIBLE);
        linear_loading.setVisibility(View.GONE);
    }





    private void setToolbar() {
        myTypeface          = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle                = (TextView)findViewById(R.id.forTitle);
        imgBtnBack              = (ImageButton) findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forTitle.setTypeface(myTypeface);
        forTitle.setText("Branch Location");
    }



}
