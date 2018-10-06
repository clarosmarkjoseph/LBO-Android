package com.system.mobile.lay_bare.ClientTransactions;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.system.mobile.lay_bare.Location.RecyclerBranchDetails;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Mark on 19/12/2017.
 */

public class ClientTransactionDetails extends AppCompatActivity {

    private TextView forTitle;
    private Typeface myTypeface;
    Utilities utilities;

    TextView lblReferenceNo,lblType,lblBranch,lblDate,lblSubtotal,lblDiscount,lblTotalPrice,lblTransactionNo;
    RecyclerView recycler_transaction_details;
    RecyclerView.LayoutManager recycler_transaction_layoutManager;
    RecyclerView.Adapter recycler_transaction_adapter;
    JSONObject jsonObject;
    private ImageButton imgBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_transaction_details);
        setToolbar();
        utilities = new Utilities(this);
        initElements();
    }

    private void initElements() {
        recycler_transaction_details = (RecyclerView)findViewById(R.id.recycler_transaction_details);
        lblReferenceNo               = (TextView)findViewById(R.id.lblReferenceNo);
        lblType                      = (TextView)findViewById(R.id.lblType);
        lblBranch                    = (TextView)findViewById(R.id.lblBranch);
        lblDate                      = (TextView)findViewById(R.id.lblDate);
        lblSubtotal                  = (TextView)findViewById(R.id.lblSubtotal);
        lblDiscount                  = (TextView)findViewById(R.id.lblDiscount);
        lblTotalPrice                = (TextView)findViewById(R.id.lblTotalPrice);
        lblTransactionNo             = (TextView)findViewById(R.id.lblTransactionNo);
        getExtra();
    }

    private void getExtra() {
        Bundle bundle = getIntent().getExtras();
        try {
            if(bundle != null){
                jsonObject = new JSONObject(bundle.getString("object_services"));
                iterateTransactions();
            }
            else{
                finish();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void iterateTransactions() {
        try {
            String transaction_id   = jsonObject.getString("transaction_id");
            String inv              = jsonObject.getString("inv");
            String date             = jsonObject.getString("date");
            String branch           = jsonObject.getString("branch");
            String gross_price      = jsonObject.getString("gross_price");
            String price_discount   = jsonObject.getString("price_discount");
            String net_amount       = jsonObject.getString("net_amount");
            String remarks          = jsonObject.getString("remarks");
            JSONArray arrayService  = jsonObject.getJSONArray("services");

            lblBranch.setText(branch);
            lblDate.setText(utilities.getCompleteDateMonth(date));
            lblTransactionNo.setText(transaction_id);
            lblType.setText("Branch Booking");
            lblReferenceNo.setText("INV-"+inv);
            lblSubtotal.setText("Php "+gross_price);
            lblDiscount.setText("Php "+price_discount);
            lblTotalPrice.setText("Php "+utilities.convertToCurrency(net_amount));
            setUpAdapter(arrayService);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setUpAdapter(JSONArray arrayService){
        recycler_transaction_adapter = new RecyclerTransactionDetails(this,arrayService);
        recycler_transaction_details.setNestedScrollingEnabled(false);
        recycler_transaction_layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_transaction_details.setLayoutManager(recycler_transaction_layoutManager);

        recycler_transaction_details.setAdapter(recycler_transaction_adapter);
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
        forTitle.setText("Transaction Details");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
