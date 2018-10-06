package com.system.mobile.lay_bare.GeneralActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.ItemDetails;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Transactions.AppointmentSingleton;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ITDevJr1 on 4/25/2017.
 */

public class FragmentService extends Fragment {

    DataHandler handler;
    ListView listview_service;
    TextView NoService;
    View layout;
    Utilities utilities;
    String SERVER_URL = "";

    RelativeLayout progressLoading;
    boolean isLoaded;
    AppointmentSingleton appointmentSingleton;
    boolean isAppointment;
    EditText search_service;
    String gender = "";

    JSONArray arrayService = new JSONArray();
    JSONArray arrayHolder   = new JSONArray();
    JSONArray arrayAvailableService = new JSONArray();


    public static FragmentService getInstance(int position) {
        FragmentService fragmentService = new FragmentService();
        Bundle args = new Bundle();
        args.putInt("position",position);
        fragmentService.setArguments(args);
        return fragmentService;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,Bundle savedInstanceState) {
        layout                  = inflater.inflate(R.layout.fragment_service,container,false);
        utilities               = new Utilities(getActivity());
        handler                 = new DataHandler(getActivity());
        NoService               = (TextView)layout.findViewById(R.id.NoService);
        listview_service        = (ListView) layout.findViewById(R.id.listview_service);
        progressLoading         = (RelativeLayout) layout.findViewById(R.id.progressLoading);
        appointmentSingleton    = new AppointmentSingleton();
        isAppointment           = appointmentSingleton.Instance().checkIfAppointment();
        search_service          = (EditText) layout.findViewById(R.id.search_service);
        gender                  = utilities.getGender();

        listview_service.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    JSONObject objectArray  = arrayHolder.getJSONObject(position);
                    if(isAppointment == false){
                        Intent intent = new Intent(getActivity(), ItemDetails.class);
                        intent.putExtra("objectItem",objectArray.toString());
                        intent.putExtra("item_type","Services");
                        getActivity().startActivity(intent);
                    }
                    else{

                        Intent intent = new Intent();
                        String item_id              = objectArray.getString("id");
                        String name                 = objectArray.getString("service_name");
                        String price                = objectArray.getString("service_price");
                        String duration             = objectArray.getString("service_minutes");
                        String image                = objectArray.getString("service_picture");
                        String service_type_data    = objectArray.getString("service_type_data");
                        String service_type_id      = objectArray.getString("service_type_id");
                        int quantity                = 1;
                        String item_type            = "Services";
                        String item_start_time      = "";
                        String item_end_time        = "";
                        JSONObject objectParams = new JSONObject();
                        objectParams.put("item_type",item_type);
                        objectParams.put("item_id",item_id);
                        objectParams.put("item_name",name);
                        objectParams.put("item_image",image);
                        objectParams.put("item_price",price);
                        objectParams.put("item_type_id",service_type_id);
                        objectParams.put("item_quantity",String.valueOf(quantity));
                        objectParams.put("item_duration",duration);
                        objectParams.put("item_start_time",item_start_time);
                        objectParams.put("item_end_time",item_end_time);
                        objectParams.put("service_type_data",service_type_data);
                        objectParams.put("item_size","");
                        intent.putExtra("objService",objectParams.toString());
                        intent.putExtra("item_type","Services");
                        getActivity().setResult(getActivity().RESULT_OK, intent);
                        getActivity().finish();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        search_service.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        generateServices();
        return layout;
    }


    private void generateServices() {

        loadProgressBar();
        arrayAvailableService       = appointmentSingleton.Instance().getArrayOnlyAvailableService();
        arrayService                = new JSONArray();
        gender                      = utilities.getGender();
        SERVER_URL                  = utilities.returnIpAddress();
        handler                     = new DataHandler(getActivity());
        handler.open();
        Cursor query_service        = handler.returnServices();
        if(query_service.getCount() > 0){
            query_service.moveToFirst();
            try {
                JSONArray arrayResult   = new JSONArray(query_service.getString(1));
                for(int x = 0; x < arrayResult.length(); x++){
                    JSONObject objectResult = arrayResult.getJSONObject(x);
                    int service_id          = objectResult.getInt("id");
                    String service_gender   = objectResult.getString("service_gender");
                    if(arrayAvailableService == null || arrayAvailableService.length() <= 0){
                        if(gender == null){
                            arrayService.put(objectResult);
                        }
                        else{
                            if(gender.toLowerCase().equals(service_gender.toLowerCase())) {
                                arrayService.put(objectResult);
                            }
                        }
                    }
                    else{
                        if(gender.toLowerCase().equals(service_gender.toLowerCase())){
                            for (int y = 0; y < arrayAvailableService.length(); y++){
                                int availableID = arrayAvailableService.getInt(y);
                                if(availableID == service_id){
                                    arrayService.put(objectResult);
                                }
                            }
                        }
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        handler.close();

        arrayHolder = arrayService;
        displayAdapter();

    }

    private void displayAdapter() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                listview_service.setAdapter(new FragmentServiceAdapter(getActivity(),arrayHolder,gender));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    listview_service.setNestedScrollingEnabled(true);
                }
                if(arrayService.length() <= 0){
                    NoService.setVisibility(View.VISIBLE);
                    NoService.setText("No Service(s) available");
                }
                else{
                    NoService.setVisibility(View.GONE);
                }
            }
        }, 500);
    }


    private void filterData(String s) {
        loadProgressBar();

        if(s.length() == 0){
            arrayHolder = arrayService;
            displayAdapter();
        }
        else{
            arrayHolder = new JSONArray();
            for (int y = 0; y < arrayService.length(); y++){
                try {
                    String name = arrayService.getJSONObject(y).getString("service_name");
                    String desc = arrayService.getJSONObject(y).getString("service_description");
                    if(name.toLowerCase().startsWith(s.toLowerCase()) == true || desc.toLowerCase().startsWith(s.toLowerCase())){
                        try {
                            arrayHolder.put(arrayService.get(y));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        NoService.setVisibility(View.VISIBLE);
                        NoService.setText("No Service To display");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            displayAdapter();
        }
    }


    private void chooseGender() {

        final AlertDialog.Builder myBuilder1 = new AlertDialog.Builder(getActivity());
        myBuilder1.setIcon(R.drawable.app_logo);
        myBuilder1.setTitle("Please choose a gender");
        final String[] items = {"Male", "Female"};
        myBuilder1.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gender = items[which].toLowerCase();
                generateServices();
            }
        });
        myBuilder1.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
        final AlertDialog myDialog1  = myBuilder1.create();
        myDialog1.show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && !isLoaded){
            isLoaded = true;
        }
    }

    public void loadProgressBar(){
        listview_service.setVisibility(View.GONE);
        progressLoading.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar(){
        listview_service.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.GONE);
    }

}
