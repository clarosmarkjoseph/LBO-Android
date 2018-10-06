package com.system.mobile.lay_bare.GeneralActivity;

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

public class FragmentPackage extends Fragment {

    DataHandler handler;
    ListView listview_package;
    TextView NoService;
    View layout;
    Utilities utilities;
    String SERVER_URL = "";
    String gender = "";

    boolean isLoaded;
    RelativeLayout progressLoading;

    boolean ifAppointment;
    JSONArray arrayPackage  = new JSONArray();
    JSONArray arrayHolder   =  new JSONArray();
    EditText search;
    private JSONArray arrayAvailableService;
    AppointmentSingleton appointmentSingleton;

    public static FragmentPackage getInstance(int position) {
        FragmentPackage fragmentService = new FragmentPackage();
        Bundle args = new Bundle();
        args.putInt("position",position);
        fragmentService.setArguments(args);
        return fragmentService;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,Bundle savedInstanceState) {

        layout              = inflater.inflate(R.layout.fragment_service,container,false);
        progressLoading     = (RelativeLayout)layout.findViewById(R.id.progressLoading);
        utilities           = new Utilities(getActivity());
        appointmentSingleton= new AppointmentSingleton();
        SERVER_URL          = utilities.returnIpAddress();
        gender              = utilities.getGender();
        NoService           = (TextView)layout.findViewById(R.id.NoService);
        listview_package    = (ListView) layout.findViewById(R.id.listview_service);
        progressLoading     = (RelativeLayout) layout.findViewById(R.id.progressLoading);
        search              = (EditText) layout.findViewById(R.id.search_service);
        ifAppointment       = appointmentSingleton.Instance().checkIfAppointment();
        SERVER_URL          = utilities.returnIpAddress();
        handler             = new DataHandler(getActivity());
        arrayPackage        = new JSONArray();

        search.addTextChangedListener(new TextWatcher() {
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

        listview_package.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                JSONObject jObject      = arrayHolder.getJSONObject(position);
                    if(ifAppointment == false){

                        Intent intent = new Intent(getActivity(), ItemDetails.class);
                        intent.putExtra("objectItem",jObject.toString());
                        intent.putExtra("item_type","Packages");
                        getActivity().startActivity(intent);
                    }
                    else{

                        Intent intent = new Intent();
                        JSONObject objParams    = new JSONObject();
                        String item_type        = "Packages";
                        String item_id          = jObject.getString("id");
                        String name             = jObject.getString("package_name");
                        String price            = jObject.getString("package_price");
                        String package_service  = jObject.getString("package_services");
                        String duration         = jObject.getString("package_duration");
                        String image            = jObject.getString("package_image");
                        String desc             = jObject.getString("package_desc");
                        String quantity         = "1";
                        objParams.put("item_id",item_id);
                        objParams.put("item_type",item_type);
                        objParams.put("item_name",name);
                        objParams.put("item_quantity",quantity);
                        objParams.put("item_image",image);
                        objParams.put("item_description",desc);
                        objParams.put("item_duration",duration);
                        objParams.put("item_price",price);
                        objParams.put("item_type_id","0");
                        objParams.put("item_service_id",package_service);
                        intent.putExtra("objService", String.valueOf(objParams));
                        intent.putExtra("item_type",item_type);
                        getActivity().setResult(getActivity().RESULT_OK, intent);
                        getActivity().finish();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        loadProgressBar();
        return layout;
    }

    private void generatePackage() {

        loadProgressBar();
        arrayAvailableService       = appointmentSingleton.Instance().getArrayOnlyAvailableService();
        handler.open();
        Cursor queryPackage         = handler.returnPackage();
        if(queryPackage.getCount() > 0){
            try {
                queryPackage.moveToFirst();
                JSONArray arrayResult   = new JSONArray(queryPackage.getString(1));
                for(int x = 0; x < arrayResult.length(); x++){
                    JSONObject objectResult = arrayResult.getJSONObject(x);
                    int service_id          = objectResult.getInt("id");
                    String service_gender   = objectResult.getString("package_gender");

                    if(arrayAvailableService == null || arrayAvailableService.length() <= 0){
                        if(gender == null){
                            arrayPackage.put(objectResult);
                        }
                        else{
                            if(gender.toLowerCase().equals(service_gender.toLowerCase())) {
                                arrayPackage.put(objectResult);
                            }
                        }
                    }
                    else{
                        if(gender.toLowerCase().equals(service_gender.toLowerCase())){
                            for (int y = 0; y < arrayAvailableService.length(); y++){
                                int availableID = arrayAvailableService.getInt(y);
                                if(availableID == service_id){
                                    arrayPackage.put(objectResult);
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
        arrayHolder = arrayPackage;
        Log.e("arrayHolder",arrayHolder.toString());
        displayAdapter();
    }


    private void filterData(String s) {
        loadProgressBar();
        if(s.length() == 0){
            arrayHolder = arrayPackage;
            displayAdapter();
        }
        else{
            arrayHolder = new JSONArray();
            for (int y = 0; y < arrayPackage.length(); y++){
                try {
                    String name = arrayPackage.getJSONObject(y).getString("package_name");
                    String desc = arrayPackage.getJSONObject(y).getString("package_desc");
                    if(name.toLowerCase().startsWith(s.toLowerCase()) == true || desc.toLowerCase().startsWith(s.toLowerCase())){
                        try {
                            arrayHolder.put(arrayPackage.get(y));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        NoService.setVisibility(View.VISIBLE);
                        NoService.setText("No Package To display");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            displayAdapter();
        }
    }



    private void displayAdapter() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                listview_package.setAdapter(new FragmentPackageAdapter(getActivity(),arrayHolder,gender));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    listview_package.setNestedScrollingEnabled(true);
                }
                if(arrayPackage.length() <= 0){
                    NoService.setVisibility(View.VISIBLE);
                    NoService.setText("No Service(s) available");
                }
                else{
                    NoService.setVisibility(View.GONE);
                }

            }
        }, 500);
    }

    public void loadProgressBar(){
        listview_package.setVisibility(View.GONE);
        progressLoading.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar(){
        listview_package.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.GONE);

    }




    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && !isLoaded){
            generatePackage();
            isLoaded = true;
        }

    }
}
