package com.system.mobile.lay_bare.PLC;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.GeneralActivity.BranchSelection;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Profile.ClientProfile;
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
import java.util.Locale;

/**
 * Created by paolohilario on 12/26/17.
 */

public class PLCRequests extends AppCompatActivity {

    Utilities utilities;
    CardView cardview_is_applied,cardview_request_review,cardview_applying;
    String SERVER_URL,clientID;
    TextView lblRemarks,lblReferenceNo,lblDate,lblTitleDate,lblTitleDate2,lblDate2,lblDateApplied,lblBranch;
    TextView lblFullName,lblGender,lblBirthday,lblEmail,lblProfileCaption,lblImageStatus;
    EditText txtComment,txtBranch;
    Button btnRequestForTransaction,btnApply;
    String client_email = "";
    ImageButton imgDots;
    String imageClientIDString = "";
    private String clientName,clientBday,clientGender;
    private int REQUEST_CAMERA  = 1 ,BRANCH_RESULT_CODE  = 0,PERMISSION_CAMERA = 2;
    int branch_id       = 0;
    String branch_name  = "";
    RadioGroup radioGroup;
    private ArrayList<String> arrayErrorResponse = new ArrayList<>();
    String stringParam = "";
    ImageView imgPreview;
    ImageButton imgBtnBack;
    Button btnUploadImage;
    TextView forTitle;
    Typeface myTypeface;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plc_request);
        setToolbars();
        initElement();


    }

    //initialization of objects
    void initElement(){

        utilities               = new Utilities(this);
        SERVER_URL              = utilities.returnIpAddress();
        clientID                = utilities.getClientID();
        clientName              = utilities.getClientName();
        clientBday              = utilities.getClientBday();
        clientGender            = utilities.getGender();
        client_email            = utilities.getClientEmail();
        cardview_is_applied     = (CardView)findViewById(R.id.cardview_is_applied);
        cardview_request_review = (CardView)findViewById(R.id.cardview_request_review);
        cardview_applying       = (CardView)findViewById(R.id.cardview_applying);
        txtBranch               = (EditText)findViewById(R.id.txtBranch);

        //get profile details(when transaction is less than 5k)
        btnUploadImage          = (Button) findViewById(R.id.btnUploadImage);
        imgPreview              = (ImageView)findViewById(R.id.imgPreview);
        imgDots                 = (ImageButton)findViewById(R.id.imgDots);
        lblRemarks              = (TextView)findViewById(R.id.lblRemarks);
        lblReferenceNo          = (TextView)findViewById(R.id.lblReferenceNo);
        lblTitleDate            = (TextView)findViewById(R.id.lblTitleDate);
        lblDate                 = (TextView)findViewById(R.id.lblDate);
        lblDateApplied          = (TextView)findViewById(R.id.lblDateApplied);
        lblBranch               = (TextView)findViewById(R.id.lblBranch);
        lblFullName             = (TextView)findViewById(R.id.lblFullName);
        lblEmail                = (TextView)findViewById(R.id.lblEmail);
        lblGender               = (TextView)findViewById(R.id.lblGender);
        lblBirthday             = (TextView)findViewById(R.id.lblBirthday);
        lblProfileCaption       = (TextView)findViewById(R.id.lblProfileCaption);
        txtComment              = (EditText)findViewById(R.id.txtComment);
        btnRequestForTransaction= (Button)findViewById(R.id.btnRequestForTransaction);
        radioGroup              = (RadioGroup)findViewById(R.id.radioGroup);
        lblImageStatus          = (TextView)findViewById(R.id.lblImageStatus);
        btnApply                = (Button)findViewById(R.id.btnApply);

        if(clientName != null || client_email != null || client_email != null || clientGender != null){
            lblProfileCaption.setVisibility(View.GONE);
            btnRequestForTransaction.setEnabled(true);
            btnRequestForTransaction.setAlpha(1);
            txtComment.setEnabled(true);
            txtComment.setAlpha(1);
            btnUploadImage.setEnabled(true);
        }
        else{
            lblProfileCaption.setVisibility(View.VISIBLE);
        }

        //request transactions
        btnRequestForTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForTransaction();
            }
        });
        //apply plC
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyForPLCApplication();
            }
        });

        txtBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBranches();
            }
        });
        imgDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(imgDots);
            }
        });
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        getExtra();
        imgPreview.setVisibility(View.GONE);
    }

    private void getExtra() {

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            stringParam = bundle.getString("category");
            if(stringParam.equals("TransactionRequest")){
                cardview_request_review.setVisibility(View.VISIBLE);
                forTitle.setText("Transaction Review");
                lblBirthday.setText(clientBday);
                lblGender.setText(clientGender);
                lblFullName.setText(clientName);
                lblEmail.setText(client_email);
            }
            if(stringParam.equals("MarkedAsReceived")){
                radioGroup.check(R.id.radioReplacement);
                forTitle.setText("PLC Lost");
            }
            if(stringParam.equals("MarkAsLost") || stringParam.equals("PLCApplication")){
                cardview_applying.setVisibility(View.VISIBLE);
                forTitle.setText("PLC Application");
                radioGroup.check(R.id.radioNew);
            }
            else{

            }
        }

    }


    //send request
    private void requestForTransaction() {

        utilities.showProgressDialog("Requesting for transaction.....");
        String token            = utilities.getToken();
        String transaction_url  = SERVER_URL+"/api/premier/sendReviewRequest?token="+token;
        String message          = txtComment.getText().toString();
        if(imageClientIDString.equals("") || imageClientIDString.equals(null)){
            utilities.hideProgressDialog();
            utilities.showDialogMessage("Missing Valid ID","Please provide a valid ID / Image","error");
        }
        else{
            JSONObject objRequest     = new JSONObject();
            try {
                objRequest.put("message",message);
                objRequest.put("valid_id_url",imageClientIDString);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, transaction_url,objRequest, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                utilities.hideProgressDialog();
                                cardview_request_review.setVisibility(View.GONE);
                                customMessagePrompt("Successfully Requested!","success","You have successfully request for transaction review. We will contact you once we review it.");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                utilities.hideProgressDialog();
                                arrayErrorResponse = utilities.errorHandling(error);
                                customMessagePrompt("Connection Error!","error",arrayErrorResponse.get(1).toString());
                            }
                        });
                jsObjRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                10000,
                                3,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    //show branches with results
    private void showBranches() {
        Intent intent = new Intent(getApplicationContext(),BranchSelection.class);
        startActivityForResult(intent,BRANCH_RESULT_CODE);
        overridePendingTransition(R.anim.enter,R.anim.exit);
    }

    private void applyForPLCApplication(){

        utilities.showProgressDialog("Saving....");
        int id = radioGroup.getCheckedRadioButtonId();
        String platform             = "Android";
        JSONObject obj              = new JSONObject();
        JSONObject plcSubmitObj     = new JSONObject();
        String token = utilities.getToken();
        String type = "";
        if(id == 0){
            utilities.hideProgressDialog();
            customMessagePrompt("Missing Info!","error","Please select Application type.");
        }
        if(branch_id == 0){
            utilities.hideProgressDialog();
            customMessagePrompt("Missing Info!","error","Please select the Branch where you will get the card");
        }
        else{
            type = id == R.id.radioNew ? "New" : "Replacement";
            try {
                obj.put("label",branch_name);
                obj.put("value",branch_id);
                plcSubmitObj.put("branch",obj);
                plcSubmitObj.put("type",type);
                plcSubmitObj.put("platform",platform);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            String plc_url = SERVER_URL+"/api/premier/applyPremier?token="+token;
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, plc_url, plcSubmitObj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            utilities.hideProgressDialog();
                            customMessagePrompt("Success!","success","You have successfully applied for Premiere Loyalty Card. The process will take 2-3 weeks. We will notify you once the card is ready and you may claim it to your selected branch. \nThank you!");

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            utilities.hideProgressDialog();
                            arrayErrorResponse = utilities.errorHandling(error);
                            Log.e("WEW plc request er",arrayErrorResponse.get(1).toString());
                            customMessagePrompt("Connection Error!","error",arrayErrorResponse.get(1).toString());
                        }
                    });
            jsObjRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            30000,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CAMERA ){
            if (resultCode == RESULT_OK) {

                bitmap           = (Bitmap) data.getExtras().get("data");
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
                imageClientIDString     = utilities.getStringImage(bitmap);
                imgPreview.setImageBitmap(bitmap);
                ifUserUpload();
            }
        }
        else if(requestCode == BRANCH_RESULT_CODE){
            if (resultCode == RESULT_OK) {
                JSONObject objectBranch = null;
                try {
                    objectBranch                    = new JSONObject(data.getStringExtra("branch_object"));
                    String id                       = String.valueOf(objectBranch.getInt("id"));
                    String technician               = objectBranch.getString("branch_name");
                    branch_id                       = Integer.parseInt(id);
                    branch_name                     = technician;
                    txtBranch.setText(technician);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //right side of card when requesting a transaction Review
    private void showPopupMenu(View view) {

        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.plc_edit_profile, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.myProfile){
                    Intent intent = new Intent(getApplicationContext(), ClientProfile.class);
                    startActivity(intent);
                    return false;
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), PremierClient.class);
                    startActivity(intent);
                    finish();
                    return false;
                }

            }
        });

        popup.show();
    }

    public void openCamera(){

        if (ContextCompat.checkSelfPermission(PLCRequests.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(PLCRequests.this, new String[] {Manifest.permission.CAMERA},PERMISSION_CAMERA );
        }
        else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,REQUEST_CAMERA);
        }
    }

    private  void ifUserUpload(){
        if(!imageClientIDString.equals("")){
            lblImageStatus.setText("Image is Ready");
            lblImageStatus.setTextColor(getResources().getColor(R.color.laybareGreen));
            imgPreview.setVisibility(View.VISIBLE);
        }
        else{
            imageClientIDString = "";
            lblImageStatus.setText("No image Scanned / upload");
            lblImageStatus.setTextColor(getResources().getColor(R.color.themeRed));
            imgPreview.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
            imgPreview.setVisibility(View.GONE);
        }
    }

    private void customMessagePrompt(String title,String msgStatus,String message) {

        final Dialog dialog                 = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_dialog);
        TextView lbldialog_title            = (TextView) dialog.findViewById(R.id.lbldialog_title);
        TextView lbldialog_message          = (TextView) dialog.findViewById(R.id.lbldialog_message);
        Button btndialog_cancel             = (Button) dialog.findViewById(R.id.btndialog_cancel);
        Button btndialog_confirm            = (Button) dialog.findViewById(R.id.btndialog_confirm);
        ImageButton imgBtnClose             = (ImageButton) dialog.findViewById(R.id.imgBtn_dialog_close);
        RelativeLayout relativeToolbar      = (RelativeLayout) dialog.findViewById(R.id.relativeToolbar);

        btndialog_cancel.setVisibility(View.GONE);

        lbldialog_title.setText(title);
        lbldialog_message.setText(message);

        if(msgStatus.equals("success")){
            imgBtnClose.setVisibility(View.GONE);
            relativeToolbar.setBackgroundColor(this.getResources().getColor(R.color.laybareGreen));
            btndialog_confirm.setBackgroundColor(this.getResources().getColor(R.color.laybareGreen));
            btndialog_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
        else{
            relativeToolbar.setBackgroundColor(this.getResources().getColor(R.color.themeRed));
            btndialog_confirm.setBackgroundColor(this.getResources().getColor(R.color.themeRed));
            btndialog_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void setToolbars() {

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
        forTitle.setText("Request Transaction Review");

    }



}
