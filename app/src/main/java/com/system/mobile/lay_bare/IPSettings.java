package com.system.mobile.lay_bare;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by OrangeApps Zeus on 12/15/2015.
 */
public class IPSettings extends Activity{
    EditText txtIPAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_settings);
        txtIPAddress = (EditText) findViewById(R.id.txtIPAddress);
        Button btnSave        = (Button) findViewById(R.id.btnSaveIP);
        Button btnBack        = (Button) findViewById(R.id.btnBack);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipaddress  = txtIPAddress.getText().toString();
                if(ipaddress.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Please enter IP Address!", Toast.LENGTH_SHORT).show();
                }else{
                    DataHandler handler = new DataHandler(getBaseContext());
                    handler.open();
                    handler.deleteIPAddress();
                    handler.insertIPAddress(ipaddress);
                    handler.close();
                    Toast.makeText(getApplicationContext(), "IP Address successfully saved!", Toast.LENGTH_SHORT).show();
                    getIPAddress();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        DataHandler handler = new DataHandler(getBaseContext());
        handler.open();
        Cursor cursor_ip = handler.returnIPAddress();
        String ipaddress = "";
        try {
            if (cursor_ip.getCount()>0){
                ipaddress   = cursor_ip.getString(0);
            }
        } catch (Exception e) {
            Log.v("Error : ", e.getLocalizedMessage());
        }
        handler.close();
        txtIPAddress.setText(ipaddress);
    }

    public  String getIPAddress(){
        DataHandler handler = new DataHandler(getBaseContext());
        handler.open();
        Cursor cursor_ip = handler.returnIPAddress();
        String ipaddress = "";
        try {
            if (cursor_ip.getCount()>0){
                ipaddress   = cursor_ip.getString(0);
            }
            else {
                handler.deleteUserAccount();
                handler.deletePromotion();
                handler.deleteIPAddress();
                Intent intent = new Intent(getApplicationContext(), IPSettings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        } catch (Exception e) {
            Log.v("Error : ", e.getLocalizedMessage());
        }
        handler.close();
//        txtIPAddress.setText(ipaddress);
        txtIPAddress.setText(ipaddress);
        return ipaddress;
    }
}
