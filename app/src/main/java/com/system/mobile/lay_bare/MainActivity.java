package com.system.mobile.lay_bare;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.system.mobile.lay_bare.Utilities.Utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

/**
 * Created by OrangeApps Zeus on 12/15/2015.
 */
public class MainActivity extends AppCompatActivity {
//    public static final String SERVER_URL = "https://system.lay-bare.com/dev/laybare_mobile/";
    public  String SERVER_URL = "";
    public  String SERVER_URL_TEMP = "";
    WebView home,header;
    SwipeRefreshLayout swipeRefresh;
    Boolean isconnected = false;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mfilePath;
    private final static int FILECHOOSER_RESULTCODE=1;
    private static final int REQUEST_FILE_PICKER              = 2;
    private static final int REQUEST_ACCESS_FINE_LOCATION     = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION   = 2;
    private static final int REQUEST_ACCESS_CAMERA            = 3;
    private static final int REQUEST_ACCESS_EXTERNAL_STORAGE  = 4;
    private Uri mCapturedImageURI = null;
    DataHandler handler = new DataHandler(getBaseContext());
    Double latitude = 0.0, longitude = 0.0;
    LocationManager mlocManager;
    MyLocationListener mlocListener;
    int k = 0;
    private PendingIntent pendingIntent;
    ProgressDialog adialog;
    SwipeRefreshLayout swipeLayout;
    ProgressBar load,loading_wait;
    private LinearLayout mlLayoutRequestError = null;
    RelativeLayout layout_loading;
    TextView txtLoading;
    private Handler mhErrorLayoutHide = null;
    private boolean mbErrorOccured = false;
    private boolean mbReloadPressed = false;
    ImageButton no_internet;
    Boolean isBack = false;
    ACProgressFlower dialog;
    ImageButton refresh;
    String pass_url;
    String device = "Android";
    String devicename = "";
    Utilities utilities;
    String clientID = "";
    Animation fadeOut;
    Animation fadeIn;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        fadeOut = AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in);
//        fadeIn = AnimationUtils.loadAnimation(MainActivity.this,android.R.anim.fade_out);
//        initToolbars();
//        utilities = new Utilities(getApplicationContext());
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(this.getResources().getColor(R.color.themeBlack));
//        }
//        txtLoading = (TextView)findViewById(R.id.txtLoading);
//        initLoading();
//        devicename = utilities.getDeviceName();
//        Log.d("DEVICE NAME: ",devicename);
//
//
//
//        //GET SERVER URL
//        handler = new DataHandler(getBaseContext());
//        handler.open();
//        Cursor cursor_ip = handler.returnIPAddress();
//        try
//        {
//            if (cursor_ip.getCount()>0){
//                SERVER_URL   = cursor_ip.getString(0);
//                if(SERVER_URL.equalsIgnoreCase("")){
//                    Intent intent = new Intent(MainActivity.this, IPSettings.class);
//                    startActivity(intent);
//                }else{
//                    //BUILDING ALARM AND SERVICE TO CHECK FOR NOTIFICATION EVERY 10 SECONDS
////                    Calendar c = Calendar.getInstance();
////                    c.add(Calendar.SECOND, 10);
////                    long firstTime = c.getTimeInMillis();
////                    // Schedule the alarm!
////                    Intent myIntent = new Intent(getApplicationContext(), MyReceiver.class);
////                    boolean alarmRunning = (PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_NO_CREATE) != null);
////                    if(alarmRunning == false) {
////                        Log.wtf("Service ", " Service not Running");
////                        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);
////                        AlarmManager alarmManager = (AlarmManager)getSystemService(getApplicationContext().ALARM_SERVICE);
////                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
////                                10 * 1000, pendingIntent);
////                    }else{
////                        Log.wtf("Service ", " Service is Running");
////                        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);
////                        AlarmManager alarmManager = (AlarmManager)getSystemService(getApplicationContext().ALARM_SERVICE);
////                        if(alarmManager == null){
////                            Log.wtf("Alarm ", " Setting alarm");
////                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
////                                    10 * 1000, pendingIntent);
////                        }else{
////                            Log.wtf("Alarm ", " Alarm was already set");
////                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
////                                    10 * 1000, pendingIntent);
////                        }
////                    }
//                }
//            }
//            else {
//                Intent i = new Intent(getApplicationContext(), IPSettings.class);
//                this.startActivity(i);
//                LogoutDetected();
//            }
//        } catch (Exception e) {
//
//        }
//        SERVER_URL_TEMP = SERVER_URL;
//        //END GET SERVER URL
//
//
//        home = (WebView)findViewById(R.id.webview);
//        mlLayoutRequestError = (LinearLayout) findViewById(R.id.layout_error);
//        no_internet = (ImageButton)findViewById(R.id.ref_button);
//        load = (ProgressBar)findViewById(R.id.loading_bar);
//        refresh = ((ImageButton) findViewById(R.id.ref_button));
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new refreshWebview().execute();
//            }
//        });
//        layout_loading = (RelativeLayout) findViewById(R.id.layout_loading);
//
//        WebSettings webSettings = home.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setLoadsImagesAutomatically(true);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webSettings.setEnableSmoothTransition(true);
//        webSettings.setGeolocationEnabled(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setBuiltInZoomControls(false);
//        webSettings.setSupportZoom(false);
//        webSettings.setUseWideViewPort(false);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        webSettings.setEnableSmoothTransition(true);
//
//        webSettings.setAppCacheMaxSize( 10 * 1024 * 1024 ); // 5MB
//        webSettings.setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
//        webSettings.setAllowFileAccess( true );
//        webSettings.setAppCacheEnabled( true );
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK ); // load online by default
//
//        if(Build.VERSION.SDK_INT >16){
//            webSettings.setMediaPlaybackRequiresUserGesture(false);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            CookieManager.getInstance().setAcceptThirdPartyCookies(home, true);
//        }
//        if (Build.VERSION.SDK_INT >= 19) {
//            home.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        }else {
//            home.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
//        home.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return true;
//            }
//        });
//        home.setLongClickable(false);
//        home.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        home.setWebViewClient(new HomeClient());
//
//        if(savedInstanceState == null){
//            String value;
//            Bundle bundle = getIntent().getExtras();
//            if (bundle != null) {
//                value = bundle.getString("web_url");
//                pass_url = SERVER_URL+"/"+value;
//                if(value.equals("gift"))
//                {
//                    home.loadUrl("https://giftaway.ph/laybare?utm_source=laybare&utm_medium=mobile&utm_campaign=direct");
//                }
//                else
//                {
//                    handler = new DataHandler(getApplicationContext());
//                    handler.open();
//                    Cursor query = handler.returnUserAccount();
//                    if(query.getCount() > 0){
//                       query.moveToFirst();
//                        clientID = query.getString(0);
//                        new refreshWebview().execute();
//                    }
//                    else{
//                        home.loadUrl(SERVER_URL+"/"+value);
//                    }
//                    handler.close();
//                }
//            }
//            else {
//                Intent in = new Intent(getApplicationContext(), NewLogin.class);
//                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(in);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            }
//        }
//
//
//        home.setWebChromeClient(new WebViewChromeClient());
//
//
//        //END WEB CHROME CLIENT
//        home.setDownloadListener(new android.webkit.DownloadListener() {
//
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//                DownloadManager.Request request = new DownloadManager.Request(
//                        Uri.parse(url));
//                Uri source = Uri.parse(url);
//                String[] tempkoText = url.split("/");
//                String filename = tempkoText[tempkoText.length - 1];
//                request.allowScanningByMediaScanner();
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
//                request.setMimeType(mimetype);
//                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                dm.enqueue(request);
//                Toast.makeText(getApplicationContext(), "Downloading File : " + filename, //To notify the Client that the file is being downloaded
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }
//
//
//
//    private void initToolbars() {
//
//        Toolbar toolbar1 = (Toolbar)findViewById(R.id.myToolbar1);
//        setSupportActionBar(toolbar1);
//        TextView title1 = (TextView)findViewById(R.id.forTitle1);
//        android.support.v7.app.ActionBar actionBar1 = getSupportActionBar();
//        Typeface myTypeface1 = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
//        title1.setTypeface(myTypeface1,Typeface.BOLD);
//    }
//
//    class refreshWebview extends AsyncTask<Object, Object, Boolean>
//    {
//        String passURL = pass_url;
//
//        @Override
//        protected void onPreExecute() {
//            no_internet.setVisibility(View.GONE);
//            load.setVisibility(View.VISIBLE);
//        }
//        @Override
//        protected Boolean doInBackground(Object... strings) {
//
//            if (!DetectionConnection.checkInternetConnection(MainActivity.this)) {
//                isconnected = false;
//            }
//            else{
//                handler = new DataHandler(getApplicationContext());
//                handler.open();
//                Cursor c = handler.returnUserAccount();
//                if(c.getCount()>0){
//
//                    try {
//                        String passDevice = devicename.replace(" ", "%20");
//                        URL url = new URL("https://system.lay-bare.com/laybare_mobile/JSONgetUserAccount.php?uid=" + clientID + "&device=" + device +"&devicename="+passDevice);
//                        HttpURLConnection urlConnection = null;
//                        try {
//                            urlConnection = (HttpURLConnection) url.openConnection();
//                            // Set cookies in requests
//                            CookieManager cookieManager = CookieManager.getInstance();
//                            String cookie = cookieManager.getCookie(urlConnection.getURL().toString());
//                            if (cookie != null) {
//                                urlConnection.setRequestProperty("Cookie", cookie);
//                            }
//                            urlConnection.setConnectTimeout(3 * 1000);
//                            urlConnection.connect();
//                            isconnected = urlConnection.getResponseCode() == 200;
//                            // Get cookies from responses and save into the cookie manager
//                            List cookieList = urlConnection.getHeaderFields().get("Set-Cookie");
//                            if (cookieList != null) {
//                                for (Object cookieTemp : cookieList) {
//                                    cookieManager.setCookie(urlConnection.getURL().toString(), String.valueOf(cookieTemp));
//                                }
//                            }
//
//                            InputStream in = new BufferedInputStream (urlConnection.getInputStream());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } finally {
//                            if (urlConnection != null) {
//                                urlConnection.disconnect();
//                            }
//                        }
//
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//                }
//                else{
//                    return true;
//                }
//                handler.close();
//                Log.e("ISCONNECTED: ",isconnected.toString());
//
//            }
//            return isconnected;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean s) {
//          Log.e("POST : ",s.toString()+" || "+pass_url);
//            if (isconnected == false){
//                mbErrorOccured = true;
//                showErrorLayout();
//            }else{
//                hideErrorLayout();
////                ShowLoadingHide();
//                home.loadUrl(pass_url);
//                home.setVisibility(View.VISIBLE);
//            }
//
////            Log.d("RESULTS",s);
//////            conn.setDoOutput(true);
//////            conn.setRequestProperty("Cookie","PHPSESSID=1lpfs9p29dd0a7tg1geotqq040");
////
////            Log.e("HAA",conn.getHeaderFields().toString());
////            if(s.equals("connected"))
////            {
//////                startActivity(getIntent());
////
////            }
////            else
////            {
////               runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        CountDownTimer timer = new CountDownTimer(1000, 1000) {
////                            @Override
////                            public void onTick(long millisUntilFinished) {
////
////                            }
////                            @Override
////                            public void onFinish() {
////                                mbReloadPressed = false;
////                                mbErrorOccured = true;
////                                showErrorLayout();
////                            }
////                        }.start();
////                    }
////                });
////            }
//        }
//    }
//
//    private void showErrorLayout()
//    {
//        mlLayoutRequestError.setVisibility(View.VISIBLE);
//        no_internet.setVisibility(View.VISIBLE);
//        load.setVisibility(View.VISIBLE);
//    }
//
//    private void hideErrorLayout()
//    {
//        mlLayoutRequestError.setVisibility(View.GONE);
//        no_internet.setVisibility(View.GONE);
//        load.setVisibility(View.GONE);
//    }
//
//
//
//
//
//    private class WebViewChromeClient extends WebChromeClient{
//        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//            if(!checkPermissionForCamera()){
//                requestPermissionForCamera();
//            }else{
//                mUploadMessage = uploadMsg;
//                if(!checkPermissionForExternalStorage()){
//                    requestPermissionForExternalStorage();
//                }else {
//                    try {
//                        File imageStorageDir = new File(
//                                Environment.getExternalStoragePublicDirectory(
//                                        Environment.DIRECTORY_PICTURES)
//                                , "LayBare");
//
//                        if (!imageStorageDir.exists()) {
//                            imageStorageDir.mkdirs();
//                        }
//
//                        File file = new File(
//                                imageStorageDir + File.separator + "IMG_"
//                                        + String.valueOf(System.currentTimeMillis())
//                                        + ".jpg");
//
//                        mCapturedImageURI = Uri.fromFile(file);
//
//                        final Intent captureIntent = new Intent(
//                                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//
//                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                        i.addCategory(Intent.CATEGORY_OPENABLE);
//                        i.setType("image/*");
//
//                        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//
//                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
//                                , new Parcelable[]{captureIntent});
//
//                        MainActivity.this.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
//
//                    } catch (Exception e) {
//                        Toast.makeText(getApplication().getBaseContext(), "Exception:" + e,
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        }
//
//        // openFileChooser for Android < 3.0
//        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//            if(!checkPermissionForCamera()){
//                requestPermissionForCamera();
//            }else {
//                if(!checkPermissionForExternalStorage()){
//                    requestPermissionForExternalStorage();
//                }else {
//                    openFileChooser(uploadMsg, "");
//                }
//            }
//        }
//        public void onProgressChanged(WebView view, int progress) {
//            Log.e("PROGRESS", String.valueOf(progress));
//            txtLoading.setText(progress+"%");
//            if(progress>=60){
//                ShowLoadingHide();
//                LoadingHideFlower();
//            }
//        }
//        //openFileChooser for other Android versions
//        public void openFileChooser(ValueCallback<Uri> uploadMsg,
//                                    String acceptType,
//                                    String capture) {
//            if(!checkPermissionForCamera()){
//                requestPermissionForCamera();
//            }else {
//                if(!checkPermissionForExternalStorage()){
//                    requestPermissionForExternalStorage();
//                }else {
//                    openFileChooser(uploadMsg, acceptType);
//                }
//            }
//        }
//
//        @Override
//        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, android.webkit.WebChromeClient.FileChooserParams fileChooserParams) {
//            if(!checkPermissionForCamera()){
//                requestPermissionForCamera();
//                return false;
//            }else {
//                if(!checkPermissionForExternalStorage()){
//                    requestPermissionForExternalStorage();
//                    return false;
//                }else {
//                    mfilePath = filePathCallback;
//                    // Create AndroidExampleFolder at sdcard
//                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Laybare_Images");
//
//                    if (!imageStorageDir.exists()) {
//                        // Create AndroidExampleFolder at sdcard
//                        imageStorageDir.mkdirs();
//                    }
//                    // Create camera captured image file path and name
//                    File file = new File(
//                            imageStorageDir + File.separator + "IMG_"
//                                    + String.valueOf(System.currentTimeMillis())
//                                    + ".jpg");
//                    mCapturedImageURI = Uri.fromFile(file);
//                    // Camera capture image intent
//                    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                    i.addCategory(Intent.CATEGORY_OPENABLE);
//                    i.setType("image/*");
//                    Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
//                            , new Parcelable[]{captureIntent});
//                    MainActivity.this.startActivityForResult(chooserIntent, REQUEST_FILE_PICKER);
//                    return true;
//                }
//            }
//        }
//
//        // The webPage has 2 filechoosers and will send a
//        // console message informing what action to perform,
//        // taking a photo or updating the file
//        public boolean onConsoleMessage(ConsoleMessage cm) {
//
//            onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
//            return true;
//        }
//
//        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
//
//        }
//    }
//
//
//    private class HomeClient extends WebViewClient {
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView home, String url) {
//            pass_url = url;
//            Log.e("STring url",url);
//            if(url.startsWith(SERVER_URL_TEMP)) {
//
//                if (!DetectionConnection.checkInternetConnection(MainActivity.this)) {
////                    Toast.makeText(MainActivity.this, "No Internet Connection1!", Toast.LENGTH_SHORT).show();
//                    mbErrorOccured = true;
//                    showErrorLayout();
//
//                    return true;
//                }else{
//                    mbErrorOccured = false;
//                    hideErrorLayout();
////                    home.setVisibility(View.VISIBLE);
//
//                    if (url.equalsIgnoreCase(SERVER_URL + "/branch_location.php")) {
//                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                                != PackageManager.PERMISSION_GRANTED) {
//
//                            requestGPSPermission();
//                            return false;
//                        }
//                        else {
//                            if(!checkPermissionForCoarseLocation()){
//                                requestPermissionForCoarseLocation();
//                                return false;
//                            }else{
//                                LocationTracker tracker = new LocationTracker(MainActivity.this);
//                                if (!tracker.canGetLocation()) {
////                                    tracker.showSettingsAlert();
//                                    return false;
//                                } else {
////                                    DetectGPSStatus();
//                                    return true;
//                                }
//                            }
//                        }
//                    }else{
//                        if (url.startsWith("tel:")) {
//                            Intent intent = new Intent(Intent.ACTION_DIAL,
//                                    Uri.parse(url));
//                            startActivity(intent);
//                            return true;
//                        }
//                        if (url.startsWith("sms:")) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                            intent.addCategory(Intent.CATEGORY_DEFAULT);
//                            startActivity(intent);
//                            return true;
//                        }
//
////                        if(url.startsWith(SERVER_URL + "/queuing.php")){
//////                            ShowLoading();
////                            home.loadUrl(url);
////                            return true;
////                        }
////                        if(url.startsWith(SERVER_URL + "/appointmentbranches.php")){
//////                            ShowLoading();
////                            LoadingFlower();
////                            home.loadUrl(url);
////                            return true;
////                        }
////
////                        if(url.startsWith(SERVER_URL + "/ClientProfile.php")){
//////                            ShowLoading();
////                            home.loadUrl(url);
////                            return true;
////                        }
////
////                        if(url.startsWith(SERVER_URL + "/plcreg.php")){
//////                            ShowLoading();
////                            home.loadUrl(url);
////                            return true;
////                        }
////                        if(url.startsWith(SERVER_URL + "/promotions.php")){
//////                            ShowLoading();
////                            home.loadUrl(url);
////                            return true;
////                        }
////                        if(url.startsWith(SERVER_URL + "/testimonials.php")){
////                            ShowLoading();
////                            home.loadUrl(url);
////                            return true;
////                        }
////                        if(url.startsWith(SERVER_URL + "/faq_landing.php")){
//////                            ShowLoading();
////                            home.loadUrl(url);
////                            return true;
////                        }
////                        if(url.startsWith(SERVER_URL + "/viewEvents.php")){
//////                            ShowLoading();
////                        }
////                        if(url.startsWith(SERVER_URL + "/logout.php?")){
////                            LogoutDetected();
////                            finish();
////                            return true;
////                        }
////                        if(url.startsWith(SERVER_URL + "/booking_form.php")){
////                            LoadingFlower();
////                            home.loadUrl(url);
////                            return true;
////                        }
//                        if(url.startsWith(SERVER_URL + "/navigation_recycler_button.php")){
//                            Intent intent = new Intent(MainActivity.this, NewLogin.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                            finish();
//                            return true;
//                        }
//
//                        if(url.startsWith(SERVER_URL + "/logout.php")){
//                            LogoutDetected();
//                            return true;
//                        }
//
//                        if(url.startsWith(SERVER_URL + "/indexandroid.php")){
//                            home.setVisibility(View.INVISIBLE);
//                            LogoutDetected();
//                            Intent intent = new Intent(MainActivity.this, NewLogin.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                            finish();
//                            return true;
//                        }
//                        else{
//                            LoadingFlower();
//                            home.loadUrl(url);
//                            return true;
//                        }
//                    }
//                }
//            }
//            else
//            {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                startActivity(intent);
//            }
//            return true;
//        }
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//            super.onPageStarted(view, url, favicon);
//        }
//        @Override
//        public void onPageFinished(WebView view, String url) {
//
//            ShowLoadingHide();
//            LoadingHideFlower();
//            pass_url = url;
//            String webUrl = view.getUrl();
//
////            if(url.startsWith(SERVER_URL + "/branch_location.php")){
//////                ShowLoadingHide();
////            }
////            else if(url.startsWith(SERVER_URL + "/queuing.php")){
//////                ShowLoadingHide();
////            }
////            else if(url.startsWith(SERVER_URL + "/ClientProfile.php"))
////            {
//////                ShowLoadingHide();
////            }
////            else if(url.startsWith(SERVER_URL + "/plcreg.php")){
//////                ShowLoadingHide();
////            }
////            else if(url.startsWith(SERVER_URL + "/navigation_recycler_button.php")){
////
////                finish();
////            }
////            else{
////                LoadingHideFlower();
////            }
////            else if(url.startsWith(SERVER_URL + "/promotions.php")){
//////                ShowLoadingHide();
////            }
////            else if(url.startsWith(SERVER_URL + "/promotions.php")){
//////                ShowLoadingHide();
////            }
////            else if(url.startsWith(SERVER_URL + "/testimonials.php")){
////                ShowLoadingHide();
////            }
////            else if(url.startsWith(SERVER_URL + "/faq_landing.php")){
//////                ShowLoadingHide();
////            }
////            else if(url.startsWith(SERVER_URL + "/appointmentbranches.php")){
//////                ShowLoadingHide();
////                LoadingHideFlower();
////            }
////            else if(url.startsWith(SERVER_URL + "/viewEvents.php")){
//////                ShowLoadingHide();
////            }
////            else if(url.startsWith(SERVER_URL + "/booking_form.php")){
////                LoadingHideFlower();
////            }
//
//
//        }
//
//        @Override
//        public void onReceivedHttpError(
//                WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//            mbErrorOccured = true;
//            showErrorLayout();
//        }
//
//        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler,
//                                       SslError error) {
//            mbErrorOccured = true;
//            showErrorLayout();
//        }
//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            if (!DetectionConnection.checkInternetConnection(MainActivity.this)) {
//                mbErrorOccured = true;
//                showErrorLayout();
//            }else{
//                home.loadUrl(failingUrl);
//                home.setVisibility(View.INVISIBLE);
//            }
////            ShowLoadingHide();
//        }
//
//    }
//
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(getApplicationContext(), IPSettings.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            finish();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        //getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode,Intent intent) {
//        if(requestCode==FILECHOOSER_RESULTCODE){
//            if (null == this.mUploadMessage) {
//                return;
//            }
//            Uri result=null;
//            try{
//                if (resultCode != Activity.RESULT_OK) {
//                    result = null;
//                } else {
//                    result = intent == null ? mCapturedImageURI : intent.getData();
//                    MediaUtility mediaUtility = new MediaUtility();
//                    String path = MediaUtility.getPath(getApplication(), result);
//                    result = Uri.fromFile(new File(path));
//                }
//            }
//            catch(Exception e)
//            {
//                Toast.makeText(getApplication().getBaseContext(), "Error : " + e.getMessage() + result , Toast.LENGTH_LONG).show();
//            }
//            mUploadMessage.onReceiveValue(result);
//            mUploadMessage = null;
//        }else if(requestCode==REQUEST_FILE_PICKER){
//            if(mfilePath!=null)
//            {
//                Uri result = intent==null || resultCode!=Activity.RESULT_OK ? null : intent.getData();
//                if(result!=null)
//                {
//                    MediaUtility mediaUtility = new MediaUtility();
//                    String path = MediaUtility.getPath(getApplication(), result);
//                    Uri uri = Uri.fromFile(new File(path));
//                    mfilePath.onReceiveValue(new Uri[]{ uri });
//                }
//                else
//                {
//                    result = intent == null ? mCapturedImageURI : intent.getData();
//                    MediaUtility mediaUtility = new MediaUtility();
//                    String path = MediaUtility.getPath(getApplication(), result);
//                    Uri uri = Uri.fromFile(new File(path));
//                    mfilePath.onReceiveValue(new Uri[]{ uri });
//                }
//            }
//            mfilePath = null;
//        }else if(requestCode == 100){
////            DetectGPSStatus();
//        }
//    }
//
//
//    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
//        String strAdd = "";
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder
//                    .getFromLocation(LATITUDE, LONGITUDE, 1);
//            if (addresses != null) {
//                android.location.Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("");
//
//                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress
//                            .append(returnedAddress.getAddressLine(i)).append(
//                            " ");
//                }
//                strAdd = strReturnedAddress.toString();
//            } else {
//                strAdd = "No address returned!";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            strAdd = "Cannot determine branch_location!";
//        }
//        return strAdd;
//    }
//
//
//    @Override
//    public void onBackPressed() {
////        LoadingFlower();
//        if (!DetectionConnection.checkInternetConnection(MainActivity.this)){
////            LoadingHideFlower();
//            if(k==1){
//                Toast.makeText(getBaseContext(), "No Internet Connection! Press again to exit", Toast.LENGTH_LONG).show();
//                ++k;
//                return;
//            }
//            else{
//                finish();
//            }
//        }
//        else{
//            if(home.canGoBack()){
//                String curl = home.getUrl();
//                if(curl.startsWith(SERVER_URL + "/navigation_recycler_button.php")){
////                    LoadingHideFlower();
//                    ++k;
//                    finish();
//                }
//                else{
//                    k=0;
//                    home.goBack();
//                    return;
//                }
//            }
//            else{
//               ++k;
//            }
//            if(k==1){
//                finish();
//            }
//        }
//        return;
//    }
//
//
//
//
//    public void LogoutDetected(){
//
////        ShowLoading();
//        CookieSyncManager.createInstance(MainActivity.this);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.removeAllCookie();
//        home.clearCache(true);
//        home.clearFormData();
//        handler = new DataHandler(getBaseContext());
//        handler.open();
//        handler.deletePromotion();
//        handler.deleteUserAccount();
//        handler.deleteMsg();
////        handler.deleteLogs();
////        handler.deleteCommercial();
//        handler.close();
//        LoginManager.getInstance().logOut();
//
//        Intent intent = new Intent(MainActivity.this, NewLogin.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
////        ShowLoadingHide();
//        finish();
//    }
//
//    public void initLoading()
//    {
//        dialog = new ACProgressFlower.Builder(this)
//                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
//                .themeColor(Color.WHITE)
//                .fadeColor(Color.GRAY).build();
//    }
//
//    public void LoadingFlower(){
//        if(dialog.isShowing()){
//
//        }
//        else{
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setCancelable(false);
//            dialog.show();
//        }
//
//    }
//
//    public void LoadingHideFlower(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                CountDownTimer timer = new CountDownTimer(1000, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//
//                    }
//                    @Override
//                    public void onFinish() {
//                        if(dialog.isShowing()){
//                            dialog.dismiss();
//                        }
//                    }
//                }.start();
//            }
//        });
//    }
//
//    private void ShowLoading() {
////        dialog.setCanceledOnTouchOutside(false);
////        dialog.setCancelable(false);
////        dialog.show();
//        layout_loading.setVisibility(View.VISIBLE);
//    }
//
//    public void ShowLoadingHide(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                CountDownTimer timer = new CountDownTimer(1000, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//
//                    }
//                    @Override
//                    public void onFinish() {
//                        layout_loading.setVisibility(View.GONE);
//                    }
//                }.start();
//            }
//        });
////        layout_loading.setVisibility(View.GONE);
//    }
//
//    //CHECK CAMERA PERMISSION
//    private boolean checkPermissionForCamera(){
//        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
//        return result == PackageManager.PERMISSION_GRANTED;
//    }
//
//    //CHECK COARSE LOCATION PERMISSION
//    private boolean checkPermissionForCoarseLocation(){
//        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
//        return result == PackageManager.PERMISSION_GRANTED;
//    }
//
//    //REQUEST PERMISSION FOR GPS
//    private void requestGPSPermission() {
//        Log.wtf("GPS", "GPS permission has NOT been granted. Requesting permission.");
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_ACCESS_FINE_LOCATION);
//
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//
//    //CHECK EXTERNAL STORAGE PERMISSION
//    public boolean checkPermissionForExternalStorage(){
//        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        return result == PackageManager.PERMISSION_GRANTED;
//    }
//
//    //REQUEST PERMISSION FOR CAMERA
//    private void requestPermissionForCoarseLocation(){
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
//        }
//    }
//
//    //REQUEST PERMISSION FOR CAMERA
//    private void requestPermissionForCamera(){
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_ACCESS_CAMERA);
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_ACCESS_CAMERA);
//        }
//    }
//
//    //REQUEST PERMISSION FOR EXTERNAL STORAGE
//    public void requestPermissionForExternalStorage(){
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//            Toast.makeText(this, "External Storage permission needed.", Toast.LENGTH_LONG).show();
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_ACCESS_EXTERNAL_STORAGE);
//        } else {
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_ACCESS_EXTERNAL_STORAGE);
//        }
//    }
//
//
//    //ON REQUEST PERMISSION RESULT
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
//            Log.wtf("GPS", "Received response for Camera permission request.");
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.wtf("GPS", "GPS permission has now been granted. Showing preview.");
//                //LocationTracker tracker = new LocationTracker(MainActivity.this);
//                //if (!tracker.canGetLocation()) {
//                    //tracker.showSettingsAlert();
//                //} else {
//                    //DetectGPSStatus();
//                //}
//            } else {
//                Toast.makeText(getApplicationContext(), "GPS permission was not granted.", Toast.LENGTH_SHORT).show();
//                home.loadUrl(SERVER_URL + "/navigation_recycler_button.php");
//            }
//
//        } else if (requestCode == REQUEST_ACCESS_CAMERA) {
//            Log.wtf("CAMERA", "Received response for CAMERA permissions request.");
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                Toast.makeText(getApplicationContext(),  "Camera permission needed.", Toast.LENGTH_SHORT).show();
//            }
//
//        } else if (requestCode == REQUEST_ACCESS_EXTERNAL_STORAGE) {
//            Log.wtf("EXTERNAL STORAGE", "Received response for EXTERNAL STORAGE permissions request.");
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                Toast.makeText(getApplicationContext(),  "External Storage permission needed.", Toast.LENGTH_SHORT).show();
//            }
//
//        }else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
    }
}
