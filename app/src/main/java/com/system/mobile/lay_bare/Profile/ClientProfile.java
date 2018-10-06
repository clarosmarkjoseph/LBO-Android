package com.system.mobile.lay_bare.Profile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.system.mobile.lay_bare.Classes.ProfileClass;
import com.system.mobile.lay_bare.ClientTransactions.FragmentTotalTransaction;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Mark on 11/09/2017.
 */


public class ClientProfile extends AppCompatActivity {

    ImageView imgview_profile_edit;
    ImageButton btnUploadImage;

    Utilities utilities;
    String token;
    ArrayList<String> arrayErrorResponse = new ArrayList<>();
    String SERVER_URL,clientID,email,bday,contact,fullname,branchName,image;
    TextView lblFullName,lblHomeBranch,lblBday,lblMobile,lblEmail;
    String imageDataString = "";
    boolean ifPaused = false;
    private Uri mCapturedImageURI = null;
    private Typeface myTypeface;
    private TextView forTitle;
    private ImageButton imgBtnBack;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private int REQUEST_CAMERA = 1,REQUEST_IMAGE = 2,PERMISSION_CAMERA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_profile);
        setToolbar();
        initElement();
    }

    private void initElement() {

        utilities               = new Utilities(this);
        SERVER_URL              = utilities.returnIpAddress();
        token                   = utilities.getToken();

        lblFullName             = (TextView)findViewById(R.id.lblFullName);
        lblBday                 = (TextView)findViewById(R.id.lblBday);
        lblHomeBranch           = (TextView)findViewById(R.id.lblHomeBranch);
        lblMobile               = (TextView)findViewById(R.id.lblMobile);
        lblEmail                = (TextView)findViewById(R.id.lblEmail);
        imgview_profile_edit    = (ImageView)findViewById(R.id.imgview_profile_edit);


//        tabLayout.addTab(tabLayout.newTab().setText("Reviews"));


        btnUploadImage          = (ImageButton)findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
        loadTransaction();
        loadProfile();
        displayData();
    }

    private void loadTransaction() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameTransaction, new FragmentTotalTransaction());
        fragmentTransaction.commit();
    }

    private void loadProfile(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new FragmentProfile());
        fragmentTransaction.commit();
    }


    private void displayData(){

        ProfileClass profileClass = new ProfileClass(getApplicationContext());
        fullname        = utilities.getClientName();
        clientID        = utilities.getClientID();
        email           = utilities.getClientEmail();
        bday            = utilities.getBirthdayAsWord(utilities.getClientBday());
        contact         = utilities.getClientContactNo();
        branchName      = profileClass.getHomeBranch(utilities.getHomeBranchID());
        image           = SERVER_URL+"/images/users/"+utilities.getClientPicture();

        lblBday.setText(bday);
        lblFullName.setText(fullname);
        lblEmail.setText(email);
        lblHomeBranch.setText(branchName);
        lblMobile.setText(contact);
        if(!image.equals("")){
            utilities.setUniversalBigImage(imgview_profile_edit,image);
        }
    }



    public void openOptions() {


        Intent captureIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
         Intent chooserIntent = Intent.createChooser(i, "Select Image Options");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                , new Parcelable[]{captureIntent});
        startActivityForResult(chooserIntent,0);

    }

    public void checkCameraPermission() {

        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ClientProfile.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }
                        else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, REQUEST_IMAGE);
                        }
                        else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
            else{
                ActivityCompat.requestPermissions(ClientProfile.this, new String[] {Manifest.permission.CAMERA},PERMISSION_CAMERA );
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode ==REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                bitmap = utilities.resizeImageBitmap(bitmap);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                imgPath = destination.getAbsolutePath();
                uploadToServer(bitmap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode ==  REQUEST_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            if(selectedImage != null){
                try {
                    Bitmap bitmap       = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    bitmap      = utilities.resizeImageBitmap(bitmap);
                    imgPath     = getRealPathFromURI(selectedImage);
                    destination = new File(imgPath.toString());
                    bitmap      = fromGallery(bitmap);
                    uploadToServer(bitmap);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{

            }
        }
        else if(requestCode == PERMISSION_CAMERA){
            openOptions();
        }
    }

    private Bitmap fromGallery(Bitmap bm) {
        Matrix mat = new Matrix();
        mat.postRotate(0);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mat, true);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void uploadToServer(final Bitmap imageBitmap) {

        final Handler handler   = new Handler();
        Toast.makeText(getApplicationContext(),"Uploading image, please wait...",Toast.LENGTH_LONG).show();
        imageDataString         = utilities.getStringImage(imageBitmap);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = SERVER_URL+ "/api/mobile/uploadUserImage?token="+token;
                StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.e("Image response",response.toString());
                                    JSONObject jsonObject       = new JSONObject(response);
                                    String imageDirectory       = jsonObject.getString("imageDirectory");
                                    ProfileClass profileClass   = new ProfileClass(getApplicationContext());
                                    JSONObject paramObj         = profileClass.returnClientObject();
                                    paramObj.put("user_picture",imageDirectory);
                                    profileClass.updateClientProfile(paramObj);
                                    Toast.makeText(getApplicationContext(),"Successfully uploaded!",Toast.LENGTH_SHORT).show();
                                    imgview_profile_edit.setImageBitmap(imageBitmap);
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                utilities.hideProgressDialog();
                                arrayErrorResponse = utilities.errorHandling(error);
                                Toast.makeText(getApplicationContext(),arrayErrorResponse.get(1),Toast.LENGTH_SHORT).show();
                            }
                        })
                {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("upload_image",imageDataString);
                        params.put("upload_client_id",clientID);
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
        }, 500);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ifPaused == true){
            ifPaused = false;
            displayData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ifPaused = true;
    }

    public void setToolbar(){
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
        forTitle.setText("My Profile");
    }


}
