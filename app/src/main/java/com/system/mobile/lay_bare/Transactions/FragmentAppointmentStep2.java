package com.system.mobile.lay_bare.Transactions;

import android.content.Intent;
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

import com.system.mobile.lay_bare.GeneralActivity.ServicePackageProduct;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 20/10/2017.
 */

public class FragmentAppointmentStep2 extends Fragment {

    static final int SERVICE_RESULT_CODE        = 2;
    static final int PRODUCT_RESULT_CODE        = 3;
    View layout;

    String start_time               = "";
    String branch_start_time        = "";
    String branch_end_time          = "";

    Utilities utilities;

    RecyclerView recyclerItems;
    RecyclerView.LayoutManager recyclerItems_layoutManager;
    RecyclerView.Adapter recyclerItems_adapter;

    Button btnNext;
    ImageButton btnPrev,btnAdd;

    public TextView lblEmpty;
    JSONObject objectUserInfo;
    AppointmentSingleton appointmentSingleton;
    JSONArray arrayItems = new JSONArray();
    JSONArray arrayQueue = new JSONArray();

    TextView lblTotalPrice,lblTotalQty;
    private TextView forTitle;
    private Typeface myTypeface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(layout == null){
            layout            = inflater.inflate(R.layout.fragment_appointment_step2, container, false);
        }
        else {
            container.removeAllViews();
        }
        utilities        = new Utilities(getActivity());
        initElements();

        return layout;

    }

    private void initElements() {

        btnPrev                 = ( ImageButton) layout.findViewById(R.id.btnPrev);
        btnNext                 = ( Button) layout.findViewById(R.id.btnNext);
        btnAdd                  = (ImageButton) layout.findViewById(R.id.btnAdd);
        lblEmpty                = (TextView)layout.findViewById(R.id.lblEmpty);
        lblTotalPrice           = (TextView)layout.findViewById(R.id.lblTotalPrice);
        lblTotalQty             = (TextView)layout.findViewById(R.id.lblTotalQty);
        recyclerItems           = (RecyclerView) layout.findViewById(R.id.recyclerItems);
        appointmentSingleton    = new AppointmentSingleton();
        forTitle                = (TextView) layout.findViewById(R.id.forTitle);
        myTypeface              = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LobsterTwo-Regular.ttf");

        forTitle.setTypeface(myTypeface);

        try {
            objectUserInfo      = appointmentSingleton.Instance().getUserInformation();
            start_time          = objectUserInfo.getString("pick_start_time");
            branch_start_time   = objectUserInfo.getString("branch_start_time");
            branch_end_time     = objectUserInfo.getString("branch_end_time");
            arrayQueue          = appointmentSingleton.Instance().getArrayQueueing();
            arrayItems          = appointmentSingleton.Instance().getArrayItems();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitItems();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().getFragmentManager().popBackStack("FragmentAppointmentStep1",0);
                getFragmentManager().popBackStack();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ServicePackageProduct.class);
                startActivityForResult(intent, SERVICE_RESULT_CODE);
            }
        });

        displayCardService();

    }


    public void displayCardService() {

        if(arrayItems.length()>0){
            lblEmpty.setVisibility(View.GONE);
        }
        else{
            lblEmpty.setVisibility(View.VISIBLE);
        }
        recyclerItems_adapter                       = new BookingInfoRecycler(getActivity(), start_time,lblEmpty,lblTotalQty,lblTotalPrice,recyclerItems);
        recyclerItems_layoutManager                 = new LinearLayoutManager(getActivity());
        recyclerItems.setHasFixedSize(true);
        recyclerItems.setAdapter(recyclerItems_adapter);
        recyclerItems.setLayoutManager(recyclerItems_layoutManager);
        recyclerItems.setItemAnimator(new DefaultItemAnimator());
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SERVICE_RESULT_CODE){
            if (resultCode == getActivity().RESULT_OK) {
                try {
                    JSONObject objService   = new JSONObject();
                    String type             = data.getStringExtra("item_type");
                    if (type.equals("Services") || type.equals("Packages")) {
                        JSONObject objectSelectedItem = new JSONObject(data.getStringExtra("objService"));
                        checkIfItemIsDuplicate(objectSelectedItem, type);
                    }
                    else {
                        JSONObject objectSelectedItem = new JSONObject(data.getStringExtra("objProducts"));
                        checkIfItemIsDuplicate(objectSelectedItem, type);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if(resultCode == PRODUCT_RESULT_CODE){
            if (resultCode == getActivity().RESULT_OK) {
                String stringObjItems = data.getStringExtra("objProducts");
                try {
                    JSONObject objectProduct = new JSONObject(stringObjItems);
                    checkIfItemIsDuplicate(objectProduct, "Products");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //validate time
    private void checkIfItemIsDuplicate(JSONObject objectSelectedItem, String selectedType){
        
        try {
            int selectedID      = Integer.parseInt(objectSelectedItem.getString("item_id"));
            int countItems      = arrayItems.length();

            if(countItems > 0) {

                if (countItems > 4) {
                    utilities.showDialogMessage("Maximum of 5","Sorry, you can only book atleast 5 Services or Products","error");
                    return;
                }

                if(checkIfPackage(objectSelectedItem) == true){

                    if(checkIfCartHasPackage(arrayItems) == true){
                        utilities.showDialogMessage("One package only","Sorry, you can only choose one(1) package only.","error");
                        return;
                    }
                    if(validatePickedPackageItem(objectSelectedItem,arrayItems) == true){
                        utilities.showDialogMessage("Already in your list","Sorry, the package item that you selected is already in your list","error");
                        return;
                    }
                    else{
                        arrayItems.put(objectSelectedItem);
                        displayItems();
                    }
                }
                else{

                    int item_type_id    = Integer.parseInt(objectSelectedItem.getString("item_type_id"));
                    if(ifItemIsAlready(selectedID,arrayItems) == true){
                        utilities.showDialogMessage("Already in your list","Sorry, this item is already in your list. Please choose other service","error");
                        return;
                    }
                    if(checkIfItemIsRestricted(item_type_id) == true){
                        utilities.showDialogMessage("Already in your list","Sorry, the service that you selected cannot be combined.","error");
                        return;
                    }
                    else{
                        arrayItems.put(objectSelectedItem);
                        displayItems();
                    }

                }
            }
            else{
                arrayItems.put(objectSelectedItem);
                displayItems();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfItemIsRestricted(int selectedItemTypeID) {

        boolean ifConflict = false;
        int index          = arrayItems.length();

        for (int x = 0; x < index; x++) {
            try {
                JSONObject objectArray  = arrayItems.getJSONObject(x);
                String item_type = objectArray.getString("item_type");

                if(item_type.equals("Services")){

                    if (objectArray.has("service_type_data")){
                        JSONObject object_type_data = new JSONObject(objectArray.getString("service_type_data"));
                        String objectRestricted     = object_type_data.getString("restricted");
                        JSONArray arrayRestricted   = new JSONArray(objectRestricted);

                        for (int y = 0; y < arrayRestricted.length(); y++){
                            int restrictedID = Integer.parseInt(arrayRestricted.getString(y));
                            Log.e("restrictedID",String.valueOf(restrictedID));
                            if(restrictedID == 0){
                                ifConflict = true;
                                return ifConflict;
                            }
                            if(selectedItemTypeID == restrictedID){
                                ifConflict = true;
                                return ifConflict;
                            }
                        }
                    }

                }
                if(item_type.equals("Packages")){
                    String objectRestricted     = objectArray.getString("item_service_id");
                    JSONArray arrayRestricted   = new JSONArray(objectRestricted);

                    for (int y = 0; y < arrayRestricted.length(); y++){
                        int restrictedID = Integer.parseInt(arrayRestricted.getString(y));
                        Log.e("restrictedID",String.valueOf(restrictedID));
                        if(restrictedID == 0){
                            ifConflict = true;
                            return ifConflict;
                        }
                        if(selectedItemTypeID == restrictedID){
                            ifConflict = true;
                            return ifConflict;
                        }
                    }
                }

                else{
                    continue;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ifConflict;
    }

    public boolean ifItemIsAlready(int selectedID, JSONArray arrayItems){

        boolean ifItemIsAlready = false;
        int index               = arrayItems.length();

        for (int x = 0; x < index; x++) {
            try {
                JSONObject objectArray = arrayItems.getJSONObject(x);
                int itemID = objectArray.getInt("item_id");
                if (itemID == selectedID) {
                    ifItemIsAlready = true;
                    return ifItemIsAlready;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ifItemIsAlready;
    }

    public boolean checkIfPackage(JSONObject objecDetails){

        boolean ifPackage = false;
        if(objecDetails.has("item_service_id")){
            ifPackage = true;
        }
        return  ifPackage;
    }

    public boolean checkIfCartHasPackage(JSONArray arrayItems){
        boolean itHas = false;
        int index               = arrayItems.length();
        for (int x = 0; x < index; x++) {
            try {
                JSONObject objectArray  =  arrayItems.getJSONObject(x);
                String itemType         = objectArray.getString("item_type");
                if(itemType.equals("Packages")){
                    itHas = true;
                    return itHas;
                }
                else{
                    continue;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return itHas;
    }

    public boolean validatePickedPackageItem(JSONObject objectSelected,JSONArray arrayItems){

        boolean ifConflict = false;
        int index          = arrayItems.length();

        try {
            for (int x = 0; x < index; x++) {

                JSONObject objectItems          = arrayItems.getJSONObject(x);
                int itemID                      = Integer.parseInt(objectItems.getString("item_type_id"));
                String item_array_id            = objectSelected.getString("item_service_id");
                JSONArray arrayPackageServiceID = new JSONArray(item_array_id);
                for (int y = 0; y < arrayPackageServiceID.length(); y++) {

                    int key = arrayPackageServiceID.getInt(y);
                    Log.e("validatePickedPackageItem",String.valueOf(itemID)+" - "+String.valueOf(key));

                    if (key == itemID) {
                        ifConflict = true;
                        return ifConflict;
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return ifConflict;
    }


    private void displayItems() {

        if(arrayItems.length() > 0){
            lblEmpty.setVisibility(View.GONE);
            recyclerItems.setVisibility(View.VISIBLE);
            recyclerItems_adapter.notifyDataSetChanged();
            int recyclerIndex = recyclerItems.getAdapter().getItemCount() - 1;
            recyclerItems.smoothScrollToPosition(recyclerIndex);
            recyclerItems_layoutManager.scrollToPosition(0);
        }
        else{
            lblEmpty.setVisibility(View.VISIBLE);
            recyclerItems.setVisibility(View.GONE);
            lblTotalPrice.setText("Php 0.00");
            lblTotalQty.setText("0");
        }
    }


    private void submitItems(){

        utilities.showProgressDialog("Loading.....");
        arrayItems                      = appointmentSingleton.Instance().getArrayItems();
        if(arrayItems.length() <= 0){
            utilities.hideProgressDialog();
            utilities.showDialogMessage("Verification!","Please choose your services / products first","info");
            return;
        }
        else{

            String app_reserved             = appointmentSingleton.Instance().getAppReserved();
            arrayQueue                      = appointmentSingleton.Instance().getArrayQueueing();
            int roomCount                   = appointmentSingleton.Instance().getRoomCount();
            JSONObject objectAppointment    = appointmentSingleton.Instance().getAppointmentObject();
            JSONObject objectTechSchedule   = appointmentSingleton.Instance().getTechnicianSchedule();
            JSONArray arrayServices         = new JSONArray();
            JSONArray arrayProducts         = new JSONArray();


            try {
                JSONObject objectTechnician     = objectAppointment.getJSONObject("technician");
                String technician_id            = objectTechnician.getString("value");
                String technician_name          = objectTechnician.getString("label");
                JSONObject objectBranch         = objectAppointment.getJSONObject("branch");
                String branch_id                = objectBranch.getString("value");
                String branch_name              = objectBranch.getString("label");
                int countService                = 0;

                for(int x = 0; x < arrayItems.length(); x++){

                    JSONObject jsonObject   = arrayItems.getJSONObject(x);
                    String type             = jsonObject.getString("item_type");
                    int itemID              = Integer.parseInt(jsonObject.getString("item_id"));
                    double itemPrice        = jsonObject.getDouble("item_price");
                    String itemStart        = app_reserved + " " +jsonObject.getString("item_start_time");
                    String itemEnd          = app_reserved + " " +jsonObject.getString("item_end_time");
                    int itemQty             =  Integer.parseInt(jsonObject.getString("item_quantity"));

                    if(type.equals("Services") || type.equals("Packages")){
                        countService++;

                        ArrayList<String> arrayListItemSchedule = new ArrayList<>();
                        arrayListItemSchedule.add(itemStart);
                        arrayListItemSchedule.add(itemEnd);

                        ArrayList<String> arrayListBranchSchedule = new ArrayList<>();
                        arrayListBranchSchedule.add(app_reserved+" "+branch_start_time);
                        arrayListBranchSchedule.add(app_reserved+" "+branch_end_time);

                        if(!technician_id.equals("0")){

                            ArrayList<String> arrayListTechSchedule = new ArrayList<>();
                            arrayListTechSchedule.add(app_reserved+" "+objectTechSchedule.getString("start"));
                            arrayListTechSchedule.add(app_reserved+" "+objectTechSchedule.getString("end"));

                            if(validateIfTimeIsConflict(arrayListItemSchedule,arrayListTechSchedule) == true){
                                utilities.hideProgressDialog();
                                String statement = "Sorry, the technician:" + technician_name + " is only available between " + utilities.convert12Hours(objectTechSchedule.getString("start")) + " to " + utilities.convert12Hours(objectTechSchedule.getString("end"));
                                utilities.showDialogMessage("Not available",statement, "error");
                                break;
                            }
                        }

                        if(validateQueuingIfConflict(roomCount,itemStart,itemEnd,technician_id,technician_name) == false){
                            utilities.hideProgressDialog();
                            if(technician_id.equals("0")){
                                utilities.showDialogMessage("Not available", "Sorry, no more cubicle available on this time slot.", "error");
                            }
                            else{
                                utilities.showDialogMessage("Not available", "Sorry, the selected technician ("+technician_name+") is not available this time", "error");
                            }
                            break;
                        }



                        if(validateIfBranchOperationIsEnable(arrayListItemSchedule,arrayListBranchSchedule) == true){
                            utilities.hideProgressDialog();
                            String statement = "Sorry, the branch (" + branch_name + ") operation hour is only " + utilities.convert12Hours(branch_start_time) + " to " + utilities.convert12Hours(branch_end_time);
                            utilities.showDialogMessage("Not available", statement, "error");
                            break;
                        }
                        JSONObject objectInsertService = new JSONObject();
                        objectInsertService.put("id",itemID);
                        objectInsertService.put("price",itemPrice);
                        objectInsertService.put("start",itemStart);
                        objectInsertService.put("end",itemEnd);
                        arrayServices.put(objectInsertService);
                    }

                    if(type.equals("Products")) {
                        JSONObject objectInsertProduct = new JSONObject();
                        objectInsertProduct.put("id",itemID);
                        objectInsertProduct.put("price",itemPrice);
                        objectInsertProduct.put("quantity",itemQty);
                        arrayProducts.put(objectInsertProduct);
                    }


                    if(x == arrayItems.length() - 1){
                        if(countService == 0){
                            utilities.hideProgressDialog();
                            utilities.showDialogMessage("Verification!","Please insert atleast one service to continue","info");
                            break;
                        }
                        else{
                            goToNextStep(arrayServices,arrayProducts);
                        }
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void goToNextStep(JSONArray arrayServices, JSONArray arrayProducts) {


        JSONObject objectAppointment = appointmentSingleton.Instance().getAppointmentObject();
        try {
            objectAppointment.put("services",arrayServices);
            objectAppointment.put("products",arrayProducts);
            utilities.hideProgressDialog();
            FragmentAppointmentStep3 fragmentAppointmentStep3   = new FragmentAppointmentStep3();
            FragmentManager fragmentManager                     = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction             = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameAppointment, fragmentAppointmentStep3,"FragmentAppointmentStep2");
            fragmentTransaction.addToBackStack("FragmentAppointmentStep2");
            fragmentTransaction.commit();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


    }


    //validate if technician is available or not
    private boolean validateIfTimeIsConflict(ArrayList<String> arrayItemSchedule, ArrayList<String> arrayTechSchedule) {

        String itemStart    = arrayItemSchedule.get(0);
        String itemEnd      = arrayItemSchedule.get(1);
        Log.e("Items / technician",arrayItemSchedule.toString()+" - "+arrayTechSchedule.toString());
        if (utilities.compareTwoTimeRangeIfAvailable(itemStart,"and", arrayTechSchedule) == false || utilities.compareTwoTimeRangeIfAvailable(itemEnd,"and",arrayTechSchedule) == false) {
            return true;
        }
        return false;
    }


    //validate if the last time is not available to the scheduled branch or tech
    private boolean validateIfBranchOperationIsEnable(ArrayList<String> arrayListItemSchedule, ArrayList<String> arrayListBranchSchedule) {

        String itemStart    = arrayListItemSchedule.get(0);
        String itemEnd      = arrayListItemSchedule.get(1);
        if (utilities.compareTwoTimeRangeIfAvailable(itemStart, "and", arrayListBranchSchedule) == false || utilities.compareTwoTimeRangeIfAvailable(itemEnd, "and", arrayListBranchSchedule) == false ) {
            return true;
        }
        return false;
    }

//    //validate if the branch queued is conflict to client's time
    private boolean validateQueuingIfConflict(int roomCount, String pickStartDateTime, String pickEndDateTime, String technician_id, String technician_name){

        boolean ifAvailable = true;
        if(arrayQueue.length() > 0){
            try {
                int countQueueIfConflict = 0;
                for (int x = 0; x < arrayQueue.length(); x++) {
                    JSONObject objectQueue      = arrayQueue.getJSONObject(x);
                    int duration                = objectQueue.getInt("duration");
                    int queuedTechID            = objectQueue.getInt("technician_id");
                    String dateTimeStart        = objectQueue.getString("transaction_datetime");
                    String dateTimeEnd          = utilities.addDurationToDateTime(dateTimeStart, duration);

                    ArrayList<String> arrayQueuing = new ArrayList<>();
                    arrayQueuing.add(dateTimeStart);
                    arrayQueuing.add(dateTimeEnd);

                    if (utilities.compareTwoTimeRangeIfAvailable(pickStartDateTime, "and", arrayQueuing) == true || utilities.compareTwoTimeRangeIfAvailable(pickEndDateTime, "and", arrayQueuing) == true) {
                        countQueueIfConflict++;
                        if(queuedTechID == Integer.parseInt(technician_id) && Integer.parseInt(technician_id) != 0){
                            ifAvailable = false;
                            return ifAvailable;
                        }
                    }
                    if(x >= arrayQueue.length() - 1){
                        if(roomCount <= countQueueIfConflict){
                            ifAvailable = false;
                            return ifAvailable;
                        }
                        else{
                            ifAvailable = true;
                            return ifAvailable;
                        }
                    }

                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        else{
            return ifAvailable;
        }
        return ifAvailable;
    }


}
