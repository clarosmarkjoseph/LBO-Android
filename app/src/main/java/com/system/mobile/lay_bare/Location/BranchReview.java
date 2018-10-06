package com.system.mobile.lay_bare.Location;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paolohilario on 3/20/18.
 */

public class BranchReview extends AppCompatActivity {
    private Typeface myTypeface;
    private ImageButton imgBtnBack;
    Utilities utilities;
    private TextView forTitle;
    RatingBar ratingBar;
    TextView lblClientName,lblTransactionDate,lblRemarkStatus,lblBranch;
    Button btnSubmit;
    EditText txtRemarks;
    ImageView imgReviewClient;
    String SERVER_URL       = "";
    String branch_name      = "";
    int transaction_id      = 0;
    int review_id           = 0;
    String transaction_date = "";
    float rating            = 0;
    JSONObject objectReview;
    private ArrayList<String> arrayErrorResponse;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.branch_review);
        setToolbar();
        getIntentData();
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            try {
                objectReview     = new JSONObject(bundle.getString("appointment_review_object"));
                review_id        = objectReview.getInt("review_id");
                transaction_id   = objectReview.getInt("transaction_id");
                transaction_date = objectReview.getString("transaction_datetime");
                rating           = Float.parseFloat(objectReview.getString("review_rating"));
                branch_name      =objectReview.getString("branch_name");
                initElement();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void initElement() {

        utilities           = new Utilities(this);
        SERVER_URL          = utilities.returnIpAddress();
        lblClientName       = (TextView)findViewById(R.id.lblClientName);
        lblTransactionDate  = (TextView)findViewById(R.id.lblTransactionDate);
        lblRemarkStatus     = (TextView)findViewById(R.id.lblRemarkStatus);
        btnSubmit           = (Button) findViewById(R.id.btnSubmit);
        txtRemarks          = (EditText) findViewById(R.id.txtRemarks);
        ratingBar           = (RatingBar)findViewById(R.id.ratingBar);
        imgReviewClient     = (ImageView)findViewById(R.id.imgReviewClient);
        lblBranch           = (TextView)findViewById(R.id.lblBranch);
        String imageURL     = SERVER_URL+"/images/users/"+utilities.getClientPicture();

        lblClientName.setText(utilities.getClientName());
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                btnSubmit.setAlpha(1);
                btnSubmit.setEnabled(true);
                ratingAction(v);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReview();
            }
        });
        lblBranch.setText(branch_name);
        lblClientName.setText(utilities.getClientName());
        lblTransactionDate.setText(transaction_date);
        lblTransactionDate.setText(utilities.getCompleteDateMonth(utilities.removeTimeFromDate(transaction_date)));
        ratingBar.setRating(rating);
        ratingAction(rating);
        utilities.setUniversalSmallImage(imgReviewClient,imageURL);
    }

    private void submitReview() {
        utilities.showProgressDialog("Submitting review....");
        String feedback     = txtRemarks.getText().toString();
        float reviewRating  = ratingBar.getRating();
        if (reviewRating <= 0){
            utilities.showDialogMessage("Rating Required!","Please rate your review","info");
        }
        if(feedback.isEmpty()){
            feedback = null;
        }

        JSONObject objectParams = new JSONObject();
        try {
            objectParams.put("review_id",review_id);
            objectParams.put("transaction_id",transaction_id);
            objectParams.put("rating",reviewRating);
            objectParams.put("feedback",feedback);
            String token            = utilities.getToken();
            String transaction_url  = SERVER_URL+"/api/mobile/reviews/reviewTransaction?token="+token;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, transaction_url, objectParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            utilities.hideProgressDialog();
                            try {
                                Log.e("response",response.toString());
                                String resultData   = response.getString("data");
                                showPopup(resultData);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //on error response
                            utilities.hideProgressDialog();
                            arrayErrorResponse = utilities.errorHandling(error);
                            utilities.showDialogMessage("Connection Error",arrayErrorResponse.get(1).toString(),"error");
                        }
                    });
            jsObjRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            6000,
                            2,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showPopup(String resultData) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_dialog);
        TextView lbldialog_title            = (TextView) dialog.findViewById(R.id.lbldialog_title);
        TextView lbldialog_message          = (TextView) dialog.findViewById(R.id.lbldialog_message);
        Button btndialog_cancel             = (Button) dialog.findViewById(R.id.btndialog_cancel);
        Button btndialog_confirm            = (Button) dialog.findViewById(R.id.btndialog_confirm);
        ImageButton imgBtnClose             = (ImageButton) dialog.findViewById(R.id.imgBtn_dialog_close);
        RelativeLayout relativeToolbar      = (RelativeLayout) dialog.findViewById(R.id.relativeToolbar);

        btndialog_cancel.setVisibility(View.GONE);
        relativeToolbar.setBackgroundColor(getResources().getColor(R.color.laybareGreen));
        btndialog_confirm.setBackgroundColor(getResources().getColor(R.color.laybareGreen));

        lbldialog_title.setText("Review Success!");
        lbldialog_message.setText( resultData);

        final Dialog myDialog = dialog;
        btndialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                finish();
            }
        });
        imgBtnClose.setVisibility(View.GONE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void ratingAction(float value) {

        if(value >= 1){
            lblRemarkStatus.setText("Unsatisfied!");
        }
        if(value  >= 2){
            lblRemarkStatus.setText("Slightly Disliked!");
        }
        if(value >= 3){
            lblRemarkStatus.setText("It's Okay!");
        }
        if(value == 4){
            lblRemarkStatus.setText("Liked it!");
        }
        if(value == 5){
            lblRemarkStatus.setText("Loved it!");
        }

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
        forTitle.setText("Branch Review");
    }

}
