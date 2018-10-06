package com.system.mobile.lay_bare.GeneralActivity;

import android.content.Intent;
import android.database.Cursor;
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

public class FragmentProduct extends Fragment {

    DataHandler handler;
    int PRODUCT_CODE = 2;
    ListView listview_products;
    TextView NoService;
    View layout;
    RelativeLayout progressLoading;

    boolean isLoaded;
    String SERVER_URL = "";
    Utilities utilities;
    JSONArray jArrayProducts  = new JSONArray();
    AppointmentSingleton appointmentSingleton;
    EditText search;
    JSONArray arrayHolder   = new JSONArray();
    boolean ifAppointment;

    JSONArray arrayAvailableProduct = new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        layout                  = inflater.inflate(R.layout.fragment_service,container,false);
        utilities               = new Utilities(getActivity());
        NoService               = (TextView)layout.findViewById(R.id.NoService);
        listview_products       = (ListView) layout.findViewById(R.id.listview_service);
        progressLoading         = (RelativeLayout)layout.findViewById(R.id.progressLoading);
        appointmentSingleton    = new AppointmentSingleton();
        search                  = (EditText) layout.findViewById(R.id.search_service);
        ifAppointment           = appointmentSingleton.Instance().checkIfAppointment();
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

        listview_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            try {
                JSONObject jObject = arrayHolder.getJSONObject(position);
                if (ifAppointment == false) {
                    Intent intent = new Intent(getActivity(), ItemDetails.class);
                    intent.putExtra("objectItem",jObject.toString());
                    intent.putExtra("item_type", "Products");
                    getActivity().startActivity(intent);
                }
                else {
                    Intent intent = new Intent();
                    String item_type = "Products";
                    String item_id = jObject.getString("id");
                    String name = jObject.getString("product_group_name");
                    String price = jObject.getString("product_price");
                    String item_size = jObject.getString("product_size");
                    String item_variant = jObject.getString("product_variant");
                    String image = jObject.getString("product_picture");
                    String desc = jObject.getString("product_description");
                    String quantity = "1";

                    JSONObject objParam = new JSONObject();
                    objParam.put("item_id", item_id);
                    objParam.put("item_type", item_type);
                    objParam.put("item_name", name);
                    objParam.put("item_quantity", quantity);
                    objParam.put("item_image", image);
                    objParam.put("item_description", desc);
                    objParam.put("item_price", price);
                    objParam.put("item_variant", item_variant);
                    objParam.put("item_type_id", "0");
                    objParam.put("item_size", item_size);
                    intent.putExtra("objProducts", String.valueOf(objParam));
                    intent.putExtra("item_type", item_type);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PRODUCT_CODE){
            if(resultCode == getActivity().RESULT_OK){
                String arrayObjResult = data.getStringExtra("objService");
                String type           = data.getStringExtra("type");
                Intent intent = new Intent();
                intent.putExtra("objService", arrayObjResult);
                intent.putExtra("type",type);
                getActivity().setResult(getActivity().RESULT_OK, intent);
                getActivity().finish();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("Product is visible?", String.valueOf(isVisibleToUser));
        if(isVisibleToUser && !isLoaded){
            loadProducts();
            isLoaded = true;
        }
    }

    private void loadProducts() {

        SERVER_URL              = utilities.returnIpAddress();
        //for service
        arrayAvailableProduct   = appointmentSingleton.Instance().getArrayOnlyAvailableProduct();
        handler                 = new DataHandler(getActivity());
        handler.open();
        Cursor query_product = handler.returnProducts();
        if(query_product.getCount() > 0){
            try {
                query_product.moveToNext();
                JSONArray arrayResult   = new JSONArray(query_product.getString(1));
                for(int x = 0; x < arrayResult.length(); x++){
                    JSONObject objectResult =   arrayResult.getJSONObject(x);
                    int productID = objectResult.getInt("id");
                    if(arrayAvailableProduct ==  null ||  arrayAvailableProduct.length() <= 0){
                        jArrayProducts.put(objectResult);
                    }
                    else{
                        for (int y = 0; y < arrayAvailableProduct.length(); y++){
                            int productAvailableID = arrayAvailableProduct.getInt(y);
                            if(productAvailableID == productID){
                                jArrayProducts.put(objectResult);
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
        arrayHolder = jArrayProducts;
        displayAdapter();
    }

    private void displayAdapter() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                listview_products.setAdapter(new FragmentProductAdapter(getActivity(),arrayHolder));
                if(jArrayProducts.length() <= 0 ){
                    NoService.setVisibility(View.VISIBLE);
                    NoService.setText("No Product(s) available");
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
            arrayHolder = jArrayProducts;
            displayAdapter();
        }
        else{
            arrayHolder = new JSONArray();
            for (int y = 0; y < jArrayProducts.length(); y++){
                try {
                    String name = jArrayProducts.getJSONObject(y).getString("product_group_name");
                    String desc = jArrayProducts.getJSONObject(y).getString("product_description");
                    if(name.toLowerCase().startsWith(s.toLowerCase()) == true || desc.toLowerCase().startsWith(s.toLowerCase())){
                        try {
                            arrayHolder.put(jArrayProducts.get(y));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        NoService.setVisibility(View.VISIBLE);
                        NoService.setText("No Products To display");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            displayAdapter();
        }
    }

    public void loadProgressBar(){
        listview_products.setVisibility(View.GONE);
        progressLoading.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar(){
        listview_products.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.GONE);
    }

}
