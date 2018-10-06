package com.system.mobile.lay_bare.PLC;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.RecyclerBossRecord;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by paolohilario on 12/29/17.
 */

public class PLCPreviewLogs extends AppCompatActivity {

    Toolbar toolbar;
    TextView forTitle;
    TextView lbl1,lbl2,lbl3,lbl4,lbl5,lbl6,lbl7,lbl8;// topic - date request/apply - application/request Status - Message/branch -  date processed - Remarks
    TextView lblAnswer1,lblAnswer2,lblAnswer3,lblAnswer4,lblAnswer5,lblAnswer6,lblAnswer7,lblAnswer8,lblAttachment;
    Button btnAction;
    ImageView imageView;
    LinearLayout linear_for_review_only,linear_for_plc_only,linear_date;
    JSONObject objectResult;
    String type;
    Utilities utilities;
    String SERVER_URL;
    JSONArray arrayMerging;
    RecyclerView recyclerMerging;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    LinearLayout linear_for_merging;
    private ImageButton imgBtnBack;
    private Typeface myTypeface;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plc_preview_logs);
        setToolbar();
        initElements();
    }



    private void initElements() {
        utilities               = new Utilities(this);
        SERVER_URL              = utilities.returnIpAddress();
        lbl1                    = (TextView)findViewById(R.id.lbl1);
        lbl2                    = (TextView)findViewById(R.id.lbl2);
        lbl3                    = (TextView)findViewById(R.id.lbl3);
        lbl4                    = (TextView)findViewById(R.id.lbl4);
        lbl5                    = (TextView)findViewById(R.id.lbl5);
        lbl6                    = (TextView)findViewById(R.id.lbl6);
        lbl7                    = (TextView)findViewById(R.id.lbl7);
        lbl8                    = (TextView)findViewById(R.id.lbl8);
        lblAnswer1              = (TextView)findViewById(R.id.lblAnswer1);
        lblAnswer2              = (TextView)findViewById(R.id.lblAnswer2);
        lblAnswer3              = (TextView)findViewById(R.id.lblAnswer3);
        lblAnswer4              = (TextView)findViewById(R.id.lblAnswer4);
        lblAnswer5              = (TextView)findViewById(R.id.lblAnswer5);
        lblAnswer6              = (TextView)findViewById(R.id.lblAnswer6);
        lblAnswer7              = (TextView)findViewById(R.id.lblAnswer7);
        lblAnswer8              = (TextView)findViewById(R.id.lblAnswer8);
        lblAttachment           = (TextView)findViewById(R.id.lblAttachment);
        imageView               = (ImageView)findViewById(R.id.imageView);
        recyclerMerging         = (RecyclerView) findViewById(R.id.recyclerMerging);
        btnAction               = (Button)findViewById(R.id.btnAction);
        linear_date             = (LinearLayout)findViewById(R.id.linear_date);
        linear_for_review_only  = (LinearLayout)findViewById(R.id.linear_for_review_only);
        linear_for_plc_only     = (LinearLayout)findViewById(R.id.linear_for_plc_only);
        linear_for_merging      = (LinearLayout)findViewById(R.id.linear_for_merging);
        arrayMerging            = new JSONArray();
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PreviewPLCCard.class);
                intent.putExtra("details",objectResult.toString());
                startActivity(intent);
            }
        });
        getExtraData();

    }

    private void getExtraData(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            try {
                objectResult = new JSONObject(bundle.getString("result"));
                Log.e("objectResult",objectResult.toString());
                type         = bundle.getString("type");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Error previewing Data, Please try again",Toast.LENGTH_SHORT).show();
            finish();
        }
        iterateResults();
    }

    private void iterateResults() {

        if(type.equals("plc_application")){
            hidePLCApplicationElements();
            try{
                String application_type = objectResult.optString("application_type","N/A");
                String platform         = objectResult.optString("platform","N/A");
                String status           = objectResult.optString("status","N/A");
                String reference_no     = objectResult.optString("reference_no","N/A");
                String branch_name      = objectResult.optString("branch_name","N/A");
                String dateIfPLC        = objectResult.optString("created_at","N/A");
                String remarks          = objectResult.optString("remarks","N/A");
                String updated_date     = objectResult.optString("updated_at","N/A");
                String date_applied     = objectResult.optString("date_applied","--/--/----");
                String reason_remarks   = "";
                JSONObject plc_data     = objectResult.getJSONObject("plc_data");

                if(!dateIfPLC.equals("null")){
                    dateIfPLC = utilities.getCompleteDateTime(dateIfPLC);
                }
                else{
                    dateIfPLC = "------";
                }

                Log.e("branch_name",branch_name);
                lbl1.setText("Reference No: ");
                lbl2.setText("Date of Application: ");
                lbl3.setText("Home Branch: ");
                lbl4.setText("PLC Status: ");
                lbl5.setText("Application Type: ");
                lbl6.setText("Platform: ");

                lblAnswer1.setText(reference_no);
                lblAnswer2.setText(dateIfPLC);
                lblAnswer3.setText(branch_name);
                lblAnswer4.setText(status.toUpperCase());
                lblAnswer5.setText(application_type);
                lblAnswer6.setText(platform);

                //see reasons AND Date process or date delivered in JSONObject(plc_data) put if not null
                lbl7.setText("Date Process: ");
                lbl8.setText("Remarks: ");


                if(status.equals("denied")){
                    lbl7.setText("Reason:");
                    lblAnswer7.setText(remarks);
                    btnAction.setAlpha((float) 0.5);
                    btnAction.setClickable(false);
                    btnAction.setText("Card preview not available");
                    if(plc_data.has("reason")){
                        String reason = plc_data.optString("reason","None");
                        reason_remarks = "\nReason: "+utilities.capitalize(reason);
                    }
                    lblAnswer4.setBackground(getResources().getDrawable(R.drawable.circle_red));
                    getRemarks(0,reason_remarks);
                }
                else if(status.equals("approved")){
                    lbl7.setText("Date Approved: ");
                    lblAnswer7.setText(utilities.getCompleteDateTime(updated_date));
                    lblAnswer4.setBackground(getResources().getDrawable(R.drawable.circle_blue));
                    getRemarks(1, reason_remarks);
                }
                else if(status.equals("processing")){
                    linear_date.setVisibility(View.GONE);
                    lbl7.setText("Date Process: ");
                    lblAnswer7.setText(utilities.getCompleteDateTime(updated_date));
                    lblAnswer4.setBackground(getResources().getDrawable(R.drawable.circle_yellow));
                    getRemarks(2, reason_remarks);
                }
                else if(status.equals("delivery")){

                    lblAnswer4.setBackground(getResources().getDrawable(R.drawable.circle_green));
                    lbl7.setText("Date Delivered: ");
                    lblAnswer7.setText(utilities.getCompleteDateTime(updated_date));
                    getRemarks(3, reason_remarks);
                }
                else if(status.equals("ready")){
                    lbl7.setText("Date Delivered: ");
                    lblAnswer4.setBackground(getResources().getDrawable(R.drawable.circle_green));
                    lblAnswer7.setText(utilities.getCompleteDateTime(updated_date));
                    getRemarks(4, reason_remarks);
                }
                else if(status.equals("picked_up")){
                    lblAnswer4.setBackground(getResources().getDrawable(R.drawable.circle_green));
                    lbl7.setText("Date Picked-Up: ");
                    lblAnswer7.setText(utilities.getCompleteDateTime(updated_date));
                    getRemarks(5, reason_remarks);
                }
                else if(status.equals("deleted")){
                    lbl7.setText("Date Deleted: ");
                    lblAnswer7.setText(utilities.getCompleteDateTime(updated_date));
                    btnAction.setAlpha((float) 0.5);
                    btnAction.setClickable(false);
                    btnAction.setText("Card preview not available");
                    lblAnswer4.setBackground(getResources().getDrawable(R.drawable.circle_green));
                    lblAnswer8.setText("N/A");
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        else{
            hideNonPLCApplicationElements();
            try {

                String dateRequest          = utilities.removeTimeFromDate(objectResult.getString("created_at"));
                String date_process         = objectResult.getString("processed_date");
                String message              = objectResult.optString("message","None");
                String remarks              = objectResult.optString("remarks","None");
                String status               = objectResult.getString("status");
                String imgUrl               = objectResult.getString("valid_id_url");
                JSONObject objectMerge      = new JSONObject(objectResult.optString("plc_review_request_data","{}"));
                if (objectMerge.has("transactions")){
                    JSONArray arrayTransactions = objectMerge.optJSONArray("transactions");
                    arrayMerging                = arrayTransactions;
                }
                lblAnswer4.setTextColor(getResources().getColor(R.color.brownLoading));
                lbl1.setText("Topic: ");
                lblAnswer1.setText("Transaction Review Request");
                lbl2.setText("Date Requested: ");
                lblAnswer2.setText(utilities.getCompleteDateMonth(dateRequest));
                lbl3.setText("Current Status: ");
                lblAnswer3.setText(status.toUpperCase());
                lbl4.setText("Your Message: ");
                lblAnswer4.setText(message);
                lbl5.setText("Date Processed: ");

                if(status.equals("pending")){
                    lblAnswer3.setBackground(getResources().getDrawable(R.drawable.circle_yellow));
                    lblAnswer3.setTextColor(getResources().getColor(R.color.themeWhite));
                }
                else{
                    lblAnswer3.setBackground(getResources().getDrawable(R.drawable.circle_green));
                    lblAnswer3.setTextColor(getResources().getColor(R.color.themeWhite));
                }
                if(date_process.equals("null") || date_process.equals("") || date_process.equals(null)){
                    lblAnswer5.setText("-----");
                }
                else{
                    String process = utilities.removeTimeFromDate(date_process);
                    lblAnswer5.setText(utilities.getCompleteDateMonth(process));
                }
                lbl6.setText("Remarks: ");
                if(remarks.equals("null")){
                    lblAnswer6.setText("None");
                }
                else{
                    lblAnswer6.setText(remarks);
                }

                if (!imgUrl.equals("null")){
                    imgUrl = SERVER_URL+"/images/ids/"+imgUrl;
                    utilities.setUniversalImage(imageView,imgUrl);
                }
                else{
                    lblAttachment.setText("No Attachment(s)");
                    lblAttachment.setGravity(Gravity.CENTER);
                    imageView.setVisibility(View.GONE);
                }
                displayAdapter();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    void hideNonPLCApplicationElements(){
        forTitle.setText("Transaction Request");
        linear_for_review_only.setVisibility(View.VISIBLE);
        lbl7.setVisibility(View.GONE);
        lbl8.setVisibility(View.GONE);
        lblAnswer7.setVisibility(View.GONE);
        lblAnswer8.setVisibility(View.GONE);
    }
    void hidePLCApplicationElements(){
        linear_for_plc_only.setVisibility(View.VISIBLE);
        forTitle.setText("PLC Application");
    }



    private void getRemarks(int position, String reason_remarks){
        String message = "";
        if(position == 0){
            message = "Your application is denied. Please see reason below. "+reason_remarks;
        }
        if(position == 1){
            message = utilities.getPLCCaption(2);
        }
        if(position == 2){
            message = utilities.getPLCCaption(3);
        }
        if(position == 3){
            message = utilities.getPLCCaption(4);
        }
        if(position == 4){
            message = utilities.getPLCCaption(5);
        }
        if (position == 5){
            message = utilities.getPLCCaption(6);
        }
        lblAnswer8.setText(message);
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
    }

    private void displayAdapter(){
        if (arrayMerging.length() > 0){
            linear_for_merging.setVisibility(View.VISIBLE);
        }
        recyclerMerging.setAdapter( new RecyclerBossRecord(this,arrayMerging,false));
        recyclerMerging.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerMerging.setItemAnimator(new DefaultItemAnimator());
        recyclerMerging.setNestedScrollingEnabled(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
