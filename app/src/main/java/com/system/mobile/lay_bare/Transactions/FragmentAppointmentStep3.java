package com.system.mobile.lay_bare.Transactions;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 17/10/2017.
 */

public class FragmentAppointmentStep3 extends Fragment{

    private View layout;
    RecyclerView recyclerWaiver;
    RecyclerView.LayoutManager recyclerWaiver_layoutManager;
    RecyclerView.Adapter recyclerWaiver_adapter;
    String SERVER_URL = "";
    Utilities utilities;
    AppointmentSingleton stepperSingleton;
    private ArrayList<String> arrayErrorResponse = new ArrayList();
    JSONArray arrayWaiver;
    String gender = "";
    JSONArray arrayWholeQuestion;
    JSONArray arrayWaiverAnswer;
    JSONObject jsonObjectAnswer   = new JSONObject();

    Button btnNext;
    ImageButton btnPrev;

    JSONArray arrayDisallowedService = new JSONArray();


    DataHandler handler;
    private TextView forTitle;
    private Typeface myTypeface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(layout == null){
            layout            = inflater.inflate(R.layout.fragment_appointment_step3, container, false);
        }
        else {
            container.removeAllViews();
        }
        utilities        = new Utilities(getActivity());
        gender           = utilities.getGender();
        SERVER_URL       = utilities.returnIpAddress();
        initElements();
        return layout;
    }

    private void initElements() {
        recyclerWaiver          = (RecyclerView)layout.findViewById(R.id.recyclerWaiver);
        btnPrev                 = ( ImageButton) layout.findViewById(R.id.btnPrev);
        btnNext                 = ( Button) layout.findViewById(R.id.btnNext);

        arrayWaiver         = new JSONArray();
        arrayWholeQuestion  = new JSONArray();
        arrayWaiverAnswer   = new JSONArray();
        handler             = new DataHandler(getActivity());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compileData();
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        forTitle                = (TextView) layout.findViewById(R.id.forTitle);
        myTypeface              = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LobsterTwo-Regular.ttf");

        forTitle.setTypeface(myTypeface);
        
        generateWaiver();
    }

    private void generateWaiver() {

        handler.open();
        Cursor queryWaiver = handler.returnWaiver();
        if(queryWaiver.getCount() > 0){
            queryWaiver.moveToFirst();
            String waiver = queryWaiver.getString(0);
            try {
                JSONArray arrayWaiver = new JSONArray(waiver);
                handler.close();
                iterateWaiver(arrayWaiver);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            String waiver_url = SERVER_URL+"/api/waiver/getWaiverQuestions";
            final JsonArrayRequest jsObjRequest = new JsonArrayRequest
                    (Request.Method.GET, waiver_url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            handler.insertWaiver(response.toString());
                            handler.close();
                            iterateWaiver(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.close();
                            arrayErrorResponse = utilities.errorHandling(error);
                            utilities.showDialogMessage("Error Connection",arrayErrorResponse.get(1).toString(),"error");
                        }
                    });
            MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
        }
    }

    private void iterateWaiver(JSONArray arrayWaivers) {

        for (int x = 0; x<arrayWaivers.length(); x++){
            try {

                JSONArray  arrayOption;
                JSONObject jObj                     = arrayWaivers.getJSONObject(x);

                String question                     = utilities.changeNullString(jObj.getString("question"));
                String target_gender                = jObj.getString("target_gender");
                String question_type                = jObj.getString("question_type");
                JSONObject jObjectArrayQuestionData = jObj.getJSONObject("question_data");
                String default_selected             = jObjectArrayQuestionData.getString("default_selected");
                String placeholder                  = jObjectArrayQuestionData.getString("placeholder");
                String waiver_message               = "";
                String waiver_option                = "";

                if(target_gender.equals("female") && gender.equals("male")){
                    continue;
                }

                else{
                    JSONObject objQuestion      = new JSONObject();
                    JSONObject objQuestionData  = new JSONObject();
                    boolean isSelected;
                    if(default_selected.equals("YES")){
                        isSelected = true;
                    }
                    else{
                        isSelected = false;
                    }
                    if (jObjectArrayQuestionData.has("message")) {
                        waiver_message = jObjectArrayQuestionData.getString("message");
                        objQuestionData.put("message",waiver_message);
                        objQuestionData.put("answer","");
                    }
                    if (jObjectArrayQuestionData.has("options")) {
                        waiver_option = jObjectArrayQuestionData.getString("options");
                        arrayOption     = new JSONArray(waiver_option);
                        for (int z = 0; z < arrayOption.length(); z++){
                            JSONObject objOption = arrayOption.getJSONObject(z);
                            objOption.put("answer","");
                        }
                        objQuestionData.put("options",arrayOption);
                        objQuestionData.put("selected_option",0);
                    }
                    if (jObjectArrayQuestionData.has("disallowed_services")) {
                        arrayDisallowedService = jObjectArrayQuestionData.getJSONArray("disallowed_services");
                        objQuestionData.put("disallowed",arrayDisallowedService);
                        objQuestionData.put("answer","");
                    }
                    else{
                        objQuestionData.put("answer","");
                    }
                    objQuestion.put("question",question);
                    objQuestion.put("selected",isSelected);
                    objQuestion.put("data",objQuestionData);
                    objQuestion.put("question_type",question_type);
                    objQuestion.put("placeholder",placeholder);
                    arrayWaiverAnswer.put(objQuestion);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonObjectAnswer.put("signature","");
            jsonObjectAnswer.put("questions",arrayWaiverAnswer);
            jsonObjectAnswer.put("strokes",0);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        stepperSingleton.Instance().setWaiverAnswer(jsonObjectAnswer);
        arrayWholeQuestion = arrayWaivers;
        startDisplayingWaiver();
    }

    private void startDisplayingWaiver() {
        recyclerWaiver_adapter       = new RecyclerWaiverForm(getActivity());
        recyclerWaiver_layoutManager = new LinearLayoutManager(getActivity());
        recyclerWaiver.setHasFixedSize(true);
        recyclerWaiver.setAdapter(recyclerWaiver_adapter);
        recyclerWaiver.setLayoutManager(recyclerWaiver_layoutManager);
        recyclerWaiver.setItemAnimator(new DefaultItemAnimator());
    }


    private void compileData() {

       if(arrayDisallowedService.length() > 0){

       }

           JSONObject objectWaiver         = stepperSingleton.Instance().getWaiverAnswer();
        JSONObject objectAppointment    = stepperSingleton.Instance().getAppointmentObject();
        try {
            objectAppointment.put("waiver_data",objectWaiver);
            Log.e("OBject Appointment",objectAppointment.toString());
            stepperSingleton.Instance().setAppointmentObject(objectAppointment);
            FragmentAppointmentStep4 fragmentAppointmentStep4   = new FragmentAppointmentStep4();
            FragmentManager fragmentManager                     = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction             = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameAppointment, fragmentAppointmentStep4,"FragmentAppointmentStep4");
            fragmentTransaction.addToBackStack("FragmentAppointmentStep3");
            fragmentTransaction.commit();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //    private void customMessagePrompt(String status,String message) {
//
//        android.app.AlertDialog.Builder myBuilder = new android.app.AlertDialog.Builder(getActivity());
//        View mView                      =  LayoutInflater.from(getActivity()).inflate(R.layout.a_popup,null);
//        myBuilder.setView(mView);
//        LinearLayout linear_header      = (LinearLayout)mView.findViewById(R.id.linear_header);
//        ImageView imgHeader             = (ImageView) mView.findViewById(R.id.imgHeader);;
//        Button btnFinish                = (Button)mView.findViewById(R.id.btnFinish);
//        ImageButton btnClose            = (ImageButton)mView.findViewById(R.id.btnClose);
//        TextView lblTitle               = (TextView)mView.findViewById(R.id.lblTitle);
//        TextView lblMessage             = (TextView)mView.findViewById(R.id.lblMessage);
//        final AlertDialog helpDialog    = myBuilder.create();
//
//        if(status.equals("success")){
//            linear_header.setBackgroundColor(getActivity().getResources().getColor(R.color.laybareGreen));
//            imgHeader.setImageDrawable(getResources().getDrawable(R.drawable.z_icon_success));
//            lblTitle.setText("Successfully Booked!");
//            lblMessage.setText(message);
//            btnFinish.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    helpDialog.dismiss();
//                }
//            });
//        }
//        else if(status.equals("info")){
////            submitAppointment();
//            linear_header.setBackgroundColor(getActivity().getResources().getColor(R.color.menu_plc));
//            imgHeader.setImageDrawable(getResources().getDrawable(R.drawable.z_icon_info));
//            lblTitle.setText("Terms And Condition");
//            lblMessage.setText(message);
//            btnClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    helpDialog.dismiss();
//                }
//            });
//            btnFinish.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    helpDialog.dismiss();
//                }
//            });
//        }
//        else{
//            linear_header.setBackgroundColor(getActivity().getResources().getColor(R.color.themeRed));
//            imgHeader.setImageDrawable(getResources().getDrawable(R.drawable.z_icon_error));
//            lblTitle.setText("Error");
//            lblMessage.setText(message);
//            btnFinish.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    helpDialog.dismiss();
//                }
//            });
//        }
//        helpDialog.show();
//    }



}
