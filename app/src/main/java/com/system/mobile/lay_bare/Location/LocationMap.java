package com.system.mobile.lay_bare.Location;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class LocationMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private static final int REQUEST_SEARCH = 3;
    LatLng latLng;
    double latitude         = 0.0;
    double longitude        = 0.0;
    String locationName     = "";
    Utilities utilities;
    String SERVER_URL = "";
    MarkerOptions markerOptionPosition;
    int height = 100;
    int width  = 100;
    BitmapDrawable bitmapdraw;
    Bitmap b;
    Bitmap smallMarker;
    TextView forTitle;
    EditText txtSearchBranch;
    EditText txtFilterBy;
    FloatingActionButton fabTargetLocation;
    String matrixUrlEnd      ="";
    private Typeface myTypeface;
    private ImageButton imgBtnBack;
    DataHandler handler;
    int offset = 0;
    int branch_id = 0;
    LocationClassSingleton locationClassSingleton;
    boolean ifAllBranches = true;

    WeakHashMap<Marker, Integer> allBranchHash;
    WeakHashMap<Marker, Integer> nearestBranchHash;

    ArrayList<MarkerOptions> arrayOptionAll;
    ArrayList<MarkerOptions> arrayOptionNear;

    ArrayList<Marker> arrayMarkerAll;
    ArrayList<Marker> arrayMarkerNear;

    private JSONArray arrayAllBranch                = new JSONArray();
    private JSONArray arrayNearestBranch            = new JSONArray();
    private JSONArray arrayParamsAllBranch          = new JSONArray();
    private JSONArray arrayParamsNearestBranch      = new JSONArray();

//    static boolean isBottomsheetShow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_maps);
        setToolbar();

        utilities           = new Utilities(this);
        SERVER_URL          = utilities.returnIpAddress();
        matrixUrlEnd        = "&key="+getResources().getString(R.string.google_maps_key);

        allBranchHash       = new WeakHashMap();
        nearestBranchHash   = new WeakHashMap();
        arrayOptionAll      = new ArrayList<>();
        arrayOptionNear     = new ArrayList<>();
        arrayMarkerAll      = new ArrayList<>();
        arrayMarkerNear     = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        handler             = new DataHandler(getApplicationContext());
        locationClassSingleton  = new LocationClassSingleton();
        txtSearchBranch     = (EditText)findViewById(R.id.txtSearchBranch);
        txtFilterBy         = (EditText)findViewById(R.id.txtFilterBy);
        fabTargetLocation   = (FloatingActionButton)findViewById(R.id.fabTargetLocation);
        fabTargetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusCurrentLocation();
            }
        });
        txtFilterBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });

        txtSearchBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray array;
                if (ifAllBranches == true){
                    array       = arrayParamsAllBranch;
                }
                else{
                    array       = arrayParamsNearestBranch;
                }
                Intent intent = new Intent(getApplicationContext(),LocationSearch.class);
                intent.putExtra("branches",array.toString());
                intent.putExtra("ifAll",String.valueOf(ifAllBranches));
                startActivityForResult(intent,REQUEST_SEARCH);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap1) {

        googleMap            = googleMap1;
        bitmapdraw           = (BitmapDrawable)getResources().getDrawable(R.drawable.a_pointer);
        b                    = bitmapdraw.getBitmap();
        smallMarker          = Bitmap.createScaledBitmap(b, width, height, false);
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                fabTargetLocation.setColorFilter(getResources().getColor(R.color.themeWhite));
            }
        });

        googleMap.setTrafficEnabled(true);
        googleMap.setBuildingsEnabled(true);
        arrayParamsNearestBranch    = new JSONArray();
        Bundle bundle               = getIntent().getExtras();
        if(bundle != null){
            latitude        = bundle.getDouble("lat");
            longitude       = bundle.getDouble("long");
            locationName    = bundle.getString("name");
            latLng          = new LatLng(latitude,longitude);

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.hideInfoWindow();
                    if(marker.getTitle() != null){
                       if (marker.getTitle().equals("You are here")  ){
                           return false;
                       }
                       else{
                           int index;
                           if (ifAllBranches == true){
                               index = allBranchHash.get(marker);
                           }
                           else{
                               index = nearestBranchHash.get(marker);
                           }
                           configureFetchedJSONObject(index);
                           return true;
                       }
                    }
                    return false;
                }
            });
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    marker.hideInfoWindow();
                }
            });
            setCurrentCoordinates(latLng);
            getBranches();

        }
        else{
            Toast.makeText(getApplicationContext(),"There's an error occuring at your current location. Please check your connection and try again",Toast.LENGTH_LONG).show();
            finish();
        }

    }


    public void setCurrentCoordinates(LatLng myLatLng) {

        markerOptionPosition = new MarkerOptions().position(myLatLng).draggable(false);
        Marker marker =   googleMap.addMarker(markerOptionPosition);
        marker.setTag("0");
        marker.setTitle("You are here");
        focusCurrentLocation();

    }

    private void focusCurrentLocation() {

        LatLng loc                   = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate    = CameraUpdateFactory.newLatLngZoom(loc,13);
        googleMap.moveCamera(cameraUpdate);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.animateCamera(cameraUpdate, 400,  new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.e("ALready FINISHED","true");
            }

            @Override
            public void onCancel() {
                Log.e("ALready cancel","true");
            }
        });
    }


    private void getBranches(){

        handler.open();
        Cursor queryBranch = handler.returnBranches();
        if(queryBranch.getCount() > 0){

            queryBranch.moveToFirst();
            try {
                arrayAllBranch = new JSONArray(queryBranch.getString(1));
                Log.e("arrayBranch",arrayAllBranch.toString());
                calculateBranch();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        handler.close();
    }

    private void calculateBranch() {

        LatLng loc;
        int posNear = 0;
        for(int x = 0; x< arrayAllBranch.length(); x++){
            try {
                JSONObject jsonObject       = arrayAllBranch.getJSONObject(x);
                JSONObject objCoordinates   = jsonObject.getJSONObject("map_coordinates");
                String branch_name          = jsonObject.getString("branch_name");
                String branch_id            = jsonObject.getString("id");
                String branch_address       = jsonObject.getString("branch_address");
                double branch_lat           = objCoordinates.getDouble("lat");
                double branch_lng           = objCoordinates.getDouble("long");

                double distance             = utilities.getDistance(latitude,longitude,branch_lat,branch_lng);
                distance                    = Double.parseDouble(utilities.roundOffDecimal(distance));
                double kilometer            = 5.0;
                loc                         = new LatLng(branch_lat, branch_lng);
                JSONObject objFiltered      = new JSONObject();

                objFiltered.put("id",branch_id);
                objFiltered.put("branch_name",branch_name);
                objFiltered.put("branch_address",branch_address);
                objFiltered.put("branch_lat",branch_lat);
                objFiltered.put("branch_lng",branch_lng);
                objFiltered.put("distance",String.valueOf(distance));
                objFiltered.put("duration","N/A");

                arrayParamsAllBranch.put(objFiltered);
                if(distance <= kilometer) {
                    //markers
                    arrayParamsNearestBranch.put(objFiltered);
                    arrayNearestBranch.put(jsonObject);
                    MarkerOptions markerOptions = new MarkerOptions().position(loc).title(branch_name).snippet(branch_address).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).draggable(false);
                    arrayOptionNear.add(posNear,markerOptions);
                    posNear++;
                }
                //markers
                MarkerOptions markerOptions = new MarkerOptions().position(loc).title(branch_name).snippet(branch_address).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).draggable(false);
                arrayOptionAll.add(x,markerOptions);

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        generateMarkers(ifAllBranches);

        new Runnable() {
            @Override
            public void run() {
                utilities.hideProgressDialog();
            }
        };

    }


    public void generateMarkers(boolean isAll){

        googleMap.clear();
        setCurrentCoordinates(latLng);
        arrayMarkerNear.clear();
        arrayMarkerAll.clear();

        if (isAll == true){
            for (int x = 0; x < arrayOptionAll.size(); x++){
                Marker myMarker = googleMap.addMarker(arrayOptionAll.get(x));
                arrayMarkerAll.add(myMarker);
                allBranchHash.put(myMarker,x);
            }
        }
        else{
            for (int x = 0; x < arrayOptionNear.size(); x++){
                Marker myMarker = googleMap.addMarker(arrayOptionNear.get(x));
                arrayMarkerNear.add(myMarker);
                nearestBranchHash.put(myMarker,x);
            }
        }
    }


    private void configureFetchedJSONObject(final int position) {

        Marker selectedMarker;
        if (ifAllBranches == true){
            selectedMarker =  arrayMarkerAll.get(position);
        }
        else{
            selectedMarker =  arrayMarkerNear.get(position);
        }

        selectedMarker.showInfoWindow();
        CameraUpdate center = CameraUpdateFactory.newLatLng(selectedMarker.getPosition());
        googleMap.animateCamera(center, 400, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                setBottomDialog(position);
            }

            @Override
            public void onCancel() {

            }
        });

    }




    private void setBottomDialog(final int position) {

        JSONObject objectResult = null;
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        final View sheetView = getLayoutInflater().inflate(R.layout.location_dialog, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);

        try {
            if (ifAllBranches == true){
                objectResult = arrayParamsAllBranch.getJSONObject(position);
            }
            else{
                objectResult = arrayParamsNearestBranch.getJSONObject(position);
            }

            branch_id                                   = objectResult.optInt("id",0);
            final LinearLayout linearLoading            = (LinearLayout)sheetView.findViewById(R.id.linearLoading);
            final LinearLayout linearLoadingContent     = (LinearLayout)sheetView.findViewById(R.id.linearLoadingContent);
            TextView lblBranchName                      = (TextView)sheetView.findViewById(R.id.lblBranchName);
            TextView lblBranchAddress                   = (TextView)sheetView.findViewById(R.id.lblBranchAddress);
            final Button btnViewDetails                 = (Button)sheetView.findViewById(R.id.btnViewDetails);

            linearLoading.setVisibility(View.VISIBLE);
            linearLoadingContent.setVisibility(View.GONE);


            String branch_name          = objectResult.getString("branch_name");
            String branch_address       = objectResult.getString("branch_address");
            final double branch_lat     = objectResult.getDouble("branch_lat");
            final double branch_lng     = objectResult.getDouble("branch_lng");

            //display elements
            lblBranchName.setText(branch_name);
            lblBranchAddress.setText(branch_address);
            txtSearchBranch.setText(branch_name);

            if (!objectResult.has("objectReviews")){

                final JSONObject finalObjectResult = objectResult;
                requestForBranchReview(new RequestForReviewResult(){
                    @Override
                    public void onFinishGetReview(String result){

                        try {
                            JSONObject objectParamsReview   = new JSONObject();
                            JSONObject objectReview         = new JSONObject(result);

                            JSONArray arrayReview           = objectReview.optJSONArray("arrayReview");
                            offset                          = objectReview.optInt("offset",0);
                            int totalReviews                = objectReview.optInt("totalReviews",0);
                            double totalRatings             = objectReview.optDouble("totalRatings",0);
                            double resDistance              = objectReview.optDouble("distance",0);
                            JSONArray arrayListRatings      = objectReview.optJSONArray("arrayRatings");
                            resDistance                     = resDistance / 1000;
                            String resDuration              = objectReview.optString("duration","N/A");

                            objectParamsReview.put("offset",offset);
                            objectParamsReview.put("totalReviews",totalReviews);
                            objectParamsReview.put("totalRatings",totalRatings);
                            objectParamsReview.put("arrayReviews",arrayReview);
                            objectParamsReview.put("arrayListRatings",arrayListRatings);

                            finalObjectResult.put("objectReviews",objectParamsReview);
                            finalObjectResult.put("distance",resDistance);
                            finalObjectResult.put("duration",resDuration);

                            locationClassSingleton.Instance().setOffset(offset);
                            locationClassSingleton.Instance().setTotalReview(totalReviews);
                            locationClassSingleton.Instance().setTotalRatings(totalRatings);
                            locationClassSingleton.Instance().setArrayReviews(arrayReview);
                            locationClassSingleton.Instance().setArrayListRatings(arrayListRatings);

                            if (ifAllBranches == true){
                                arrayParamsAllBranch.put(position,finalObjectResult);
                            }
                            else{
                                arrayParamsNearestBranch.put(position,finalObjectResult);
                            }
                            Log.e("arrReview location inside",arrayReview.toString());
                            displayReviewDetails(sheetView,position,true);

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse() {
                        displayReviewDetails(sheetView,position,false);
                    }
                },position,branch_lat,branch_lng);

            }
            else{

                JSONObject objectBranch;
                JSONObject objectReviews;
                JSONArray arrReview;
                JSONArray arrayListRatings;
                int offset                  = 0;
                int totalReviews            = 0;
                Double totalRatings         = 0.0;

                if (ifAllBranches == true){
                    objectBranch     = arrayParamsAllBranch.getJSONObject(position);
                    objectReviews    = objectBranch.getJSONObject("objectReviews");
                    arrReview        = objectReviews.getJSONArray("arrayReviews");
                    offset           = objectReviews.getInt("offset");
                    totalReviews     = objectReviews.getInt("totalReviews");
                    totalRatings     = objectReviews.getDouble("totalRatings");
                    arrayListRatings = objectReviews.optJSONArray("arrayListRatings");
                }
                else{
                    objectBranch = arrayParamsNearestBranch.getJSONObject(position);
                    objectReviews   = objectBranch.getJSONObject("objectReviews");
                    arrReview       = objectReviews.getJSONArray("arrayReviews");
                    offset          = objectReviews.getInt("offset");
                    totalReviews    = objectReviews.getInt("totalReviews");
                    totalRatings    = objectReviews.getDouble("totalRatings");
                    arrayListRatings = objectReviews.optJSONArray("arrayListRatings");
                }
                Log.e("arrReview location",arrReview.toString());
                locationClassSingleton.Instance().setOffset(offset);
                locationClassSingleton.Instance().setTotalReview(totalReviews);
                locationClassSingleton.Instance().setTotalRatings(totalRatings);
                locationClassSingleton.Instance().setArrayReviews(arrReview);
                locationClassSingleton.Instance().setArrayListRatings(arrayListRatings);
                displayReviewDetails(sheetView,position,true);
            }

            //Button for View details
            btnViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject object;
                        if (ifAllBranches == true){
                            object = arrayAllBranch.getJSONObject(position);
                        }
                        else{
                            object = arrayNearestBranch.getJSONObject(position);
                        }
                        Intent intent = new Intent(LocationMap.this,LocationDetails.class);
                        intent.putExtra("object_branch",object.toString());
                        startActivity(intent);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            mBottomSheetDialog.show();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayReviewDetails(View sheetView, int position,boolean IfSuccess) {

        JSONObject objectBranch;
        double distances;
        String  myDuration;
        final LinearLayout linearRating             = (LinearLayout)sheetView.findViewById(R.id.linearRating);
        final TextView lblBranchDistance            = (TextView)sheetView.findViewById(R.id.lblBranchDistance);
        final TextView lblBranchDuration            = (TextView)sheetView.findViewById(R.id.lblBranchDuration);
        final TextView lblAverageReview             = (TextView)sheetView.findViewById(R.id.lblAverageReview);
        final TextView lblCountReview               = (TextView)sheetView.findViewById(R.id.lblCountReview);
        final RatingBar ratingBarReview             = (RatingBar)sheetView.findViewById(R.id.ratingBarReview);
        final LinearLayout linearLoading            = (LinearLayout)sheetView.findViewById(R.id.linearLoading);
        final LinearLayout linearLoadingContent     = (LinearLayout)sheetView.findViewById(R.id.linearLoadingContent);
        final LinearLayout linearNoInternet         = (LinearLayout)sheetView.findViewById(R.id.linearNoInternet);


        try {
            if (ifAllBranches == true){
                objectBranch = arrayParamsAllBranch.getJSONObject(position);
            }
            else{
                objectBranch = arrayParamsNearestBranch.getJSONObject(position);
            }

            distances                = objectBranch.getDouble("distance");
            myDuration               = objectBranch.getString("duration");

            if (objectBranch.has("objectReviews")){

                JSONObject objectReviews = objectBranch.getJSONObject("objectReviews");
                offset                   = objectReviews.getInt("offset");
                int totalReviews         = objectReviews.optInt("totalReviews",0);
                double totalRatings      = objectReviews.optDouble("totalRatings",0);

                if(totalReviews > 1){
                    lblCountReview.setText(String.valueOf(totalReviews)+" reviews");
                }
                else{
                    lblCountReview.setText(String.valueOf(totalReviews)+" review");
                }

                ratingBarReview.setRating((float) totalRatings);
                lblAverageReview.setText(utilities.roundOffDecimal(totalRatings));
                lblBranchDistance.setText(utilities.roundOffDecimal(distances)+ " km");
                lblBranchDuration.setText(myDuration);
                linearLoading.setVisibility(View.GONE);
                linearNoInternet.setVisibility(View.GONE);
                linearLoadingContent.setVisibility(View.VISIBLE);

            }
            else{
                if (IfSuccess == true){
                    linearLoading.setVisibility(View.GONE);
                    linearNoInternet.setVisibility(View.GONE);
                    linearLoadingContent.setVisibility(View.VISIBLE);
                    lblBranchDistance.setText(utilities.roundOffDecimal(distances)+ " km");
                    lblBranchDuration.setText(myDuration);
                }
                else{
                    linearLoading.setVisibility(View.GONE);
                    linearLoadingContent.setVisibility(View.GONE);
                    linearNoInternet.setVisibility(View.VISIBLE);
                }
            }


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public interface RequestForReviewResult{
        void onFinishGetReview(String result);
        void onErrorResponse();
    }

    private void requestForBranchReview(final RequestForReviewResult callback, int position, final double branch_lat, final double branch_lng) {

        JSONObject objectBranch;
        try {
            if (ifAllBranches == true){
                objectBranch = arrayParamsAllBranch.getJSONObject(position);
            }
            else{
                objectBranch = arrayParamsNearestBranch.getJSONObject(position);
            }

            final double default_distance = objectBranch.optDouble("distance",0);
            final String default_duration = objectBranch.optString("duration","N/A");
            String url = SERVER_URL+"/api/mobile/getBranchRatings";
            StringRequest arrayRequest = new StringRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("RESPONSE : ",response.toString());
                            callback.onFinishGetReview(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("ERROR getREVIEWS","ERROR");
                            callback.onErrorResponse();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("branch_id", String.valueOf(branch_id));
                    params.put("offset",String.valueOf(0));
                    params.put("long", String.valueOf(longitude));
                    params.put("lat", String.valueOf(latitude));
                    params.put("default_distance", String.valueOf(default_distance));
                    params.put("default_duration", default_duration);
                    params.put("destination_long",String.valueOf(branch_lng));
                    params.put("destination_lat",String.valueOf(branch_lat));
                    params.put("getAllDetails",String.valueOf(true));
                    return params;
                }
            };
            arrayRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            20000,
                            2,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    ));
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("REQUEST CODE: ", String.valueOf(resultCode)+"|");
        if(requestCode == REQUEST_SEARCH){
            if(resultCode == RESULT_OK){
                int index               = data.getIntExtra("position",0);
                configureFetchedJSONObject(index);
            }
        }
    }

    private void filterDialog(){
        final CharSequence[] items = { "All Branches", "Nearest Branches",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtered By: ");
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    ifAllBranches = true;
                    txtFilterBy.setText("Filtered by: All Branches");
                    generateMarkers(true);

                }
                else if (item == 1) {
                    ifAllBranches = false;
                    txtFilterBy.setText("Filtered by: Nearest Branches");
                    generateMarkers(false);

                }
                else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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
        forTitle.setText("Branch Location");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        locationClassSingleton.Instance().clearSingleton();
    }

}
