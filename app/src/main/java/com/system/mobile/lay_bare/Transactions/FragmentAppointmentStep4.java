package com.system.mobile.lay_bare.Transactions;


import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Sockets.SocketApplication;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;

/**
 * Created by paolohilario on 1/8/18.
 */

public class FragmentAppointmentStep4 extends Fragment {

    View layout;
    TextView lblAgreement;
    CheckBox checkAgree;
    String SERVER_URL = "";
    Utilities utilities;
    JSONObject objectSubmitBooking;
    AppointmentSingleton stepperSingleton;
    Socket mSocket;
    ArrayList<String> arrayErrorResponse;
    Button btnNext;
    ImageButton btnPrev;
    private TextView forTitle;
    private Typeface myTypeface;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(layout == null){
            layout            = inflater.inflate(R.layout.fragment_appointment_step4, container, false);
        }
        else {
            container.removeAllViews();
        }
        initElements();
        return layout;
    }

    private void initElements() {

        utilities           = new Utilities(getActivity());
        SERVER_URL          = utilities.returnIpAddress();
        stepperSingleton    = new AppointmentSingleton();
        objectSubmitBooking = new JSONObject();
        lblAgreement        = (TextView)layout.findViewById(R.id.lblAgreement);
        checkAgree          = (CheckBox) layout.findViewById(R.id.checkAgree);
        btnPrev             = (ImageButton)layout.findViewById(R.id.btnPrev);
        btnNext             = ( Button) layout.findViewById(R.id.btnNext);
        objectSubmitBooking = stepperSingleton.Instance().getAppointmentObject();
        forTitle            = (TextView) layout.findViewById(R.id.forTitle);
        myTypeface          = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle.setTypeface(myTypeface);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAppointment("submit");
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        connectServer();
    }

    private void generateDataMessage() {
        String message = "I ";
        message+= " "+utilities.getClientName()+", ";
        message+="of legal age, fully understood that the procedure's involves certain risks to my body, " +
                " which includes scratches, pain, soreness, injury, sickness, irittation, or rash, etc., which may be present and/or after the procedure and I fully accept and assume such risk and responsibility for losses, cost, and damages I may occur. I hereby release and discharge LAY BARE WAXING SALON, its stockholders, directors, franchisees, officers and technicians from all liability, claims, damages, losses, arising from the services they have rendered into me." +
                " I acknowledge that I have read this Agreement and fully understand its terms and conditions.";
        lblAgreement.setText(message);
    }

    private void connectServer() {

        SocketApplication socketApplication = new SocketApplication();
        mSocket = socketApplication.getSocket();
        generateDataMessage();
    }

    //send data / emit
    private void refreshAppointment(int branch_id){
        mSocket.emit("refreshAppointments",branch_id);
    }





    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    void validateAppointment(final String action){


        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_dialog);
        final TextView lbldialog_title              = (TextView) dialog.findViewById(R.id.lbldialog_title);
        final TextView lbldialog_message            = (TextView) dialog.findViewById(R.id.lbldialog_message);
        Button btndialog_cancel                     = (Button) dialog.findViewById(R.id.btndialog_cancel);
        Button btndialog_confirm                    = (Button) dialog.findViewById(R.id.btndialog_confirm);
        ImageButton imgBtnClose                     = (ImageButton) dialog.findViewById(R.id.imgBtn_dialog_close);
        RelativeLayout relativeToolbar              = (RelativeLayout) dialog.findViewById(R.id.relativeToolbar);

        btndialog_cancel.setVisibility(View.GONE);

        if(action.equals("submit")){
            relativeToolbar.setBackgroundColor(getActivity().getResources().getColor(R.color.laybareInfo));
            btndialog_confirm.setBackgroundColor(getActivity().getResources().getColor(R.color.laybareInfo));

            if(checkAgree.isChecked()){
                btndialog_cancel.setVisibility(View.VISIBLE);
                btndialog_cancel.setBackgroundColor(getActivity().getResources().getColor(R.color.themeRed));
                lbldialog_title.setText("Confirmation");
                lbldialog_message.setText("Are you sure you want to do continue this appointment?");
            }
            else{
                lbldialog_title.setText("Verification");
                lbldialog_message.setText("Please agree to terms and conditions to continue");
            }
        }
        else{
            relativeToolbar.setBackgroundColor(getActivity().getResources().getColor(R.color.laybareGreen));
            btndialog_confirm.setBackgroundColor(getActivity().getResources().getColor(R.color.laybareGreen));
            lbldialog_title.setText("Successfully Booked");
            lbldialog_message.setText("You have successfully booked your appointment!");
        }

        btndialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final Dialog myDialog = dialog;
        btndialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                if(action.equals("submit")){
                    if(checkAgree.isChecked()){
                        myDialog.dismiss();
                        submitAppointment();
                    }
                    else{
                       myDialog.dismiss();
                    }
                }
                else{
                    myDialog.dismiss();
                    getActivity().finish();
                }
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

    }


    private void submitAppointment(){

        utilities.showProgressDialog("Saving Appointment....");
        String token       = utilities.getToken();
        String booking_url = SERVER_URL+"/api/appointment/addAppointment?token="+token;
        Log.e("url",booking_url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, booking_url, objectSubmitBooking, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("response",response.toString());
                        try {
                            JSONObject objectbranch     = objectSubmitBooking.getJSONObject("branch");
                            int branch_id               = Integer.parseInt(objectbranch.getString("value"));
                            String result               = response.getString("result");
                            utilities.hideProgressDialog();
                            if(result.equals("success")){
                                refreshAppointment(branch_id);
                                validateAppointment("success");
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utilities.hideProgressDialog();
                        arrayErrorResponse = utilities.errorHandling(error);
                        utilities.showDialogMessage("Connection Error",arrayErrorResponse.get(1).toString(),"error");
                    }
                })
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=utf-8");
                    return params;
                }
            };
        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        40000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    private String getAppointmentTime(){
        String transaction_datetime = null;
        try {
            transaction_datetime = objectSubmitBooking.getString("transaction_date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transaction_datetime;
    }


}
