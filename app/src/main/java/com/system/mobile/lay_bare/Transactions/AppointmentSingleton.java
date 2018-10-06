package com.system.mobile.lay_bare.Transactions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 20/10/2017.
 */

public class AppointmentSingleton {

    private static AppointmentSingleton instance;
    boolean ifAppointment;
    int roomCount       = 0;
    int branch_id       = 0;
    String app_reserved = "";
    JSONObject objWaiverAnswer;
    JSONArray arrayService;
    JSONArray arrayProduct;
    JSONObject objectAppointments;
    JSONArray arrayItems;
    JSONArray arrayQueuing = new JSONArray();
    JSONArray arrayOnlyAvailableService;
    JSONArray arrayOnlyAvailableProduct;
    JSONObject objectUserInformation;
    JSONObject objectTechnicianSchedule;
    int timeExtension   = 0;

    public static AppointmentSingleton Instance() {
        if (instance == null) {
            instance = new AppointmentSingleton();
        }
        return instance;
    }

    //branch id(selected when choosing a branch(step1))
    public int getBranchID(){
        return branch_id;
    }
    public void setBranchID(int branch_id){
        this.branch_id = branch_id;
    }


    //set and get date reserved
    public String getAppReserved(){
        return this.app_reserved;
    }

    public void setAppReserved(String app_reserved){
        this.app_reserved = app_reserved;
    }

    //save queued in the branch
    public void setArrayQueueing(JSONArray arrayQueueing){
        this.arrayQueuing = arrayQueueing;
    }

    public JSONArray getArrayQueueing(){
       return this.arrayQueuing;
    }

    //item cart (compiled service & products)
    public void setArrayItems(JSONArray arrayItems){
        this.arrayItems = arrayItems;
    }
    public JSONArray getArrayItems(){
        return this.arrayItems;
    }


    //user info(picked start time, end time of branch, array
    public void setUserInformation(JSONObject objectUserInformation){
        this.objectUserInformation = objectUserInformation;
    }
    public JSONObject getUserInformation(){
        return this.objectUserInformation;
    }

    //appointment object(to be submit)
    public void setAppointmentObject(JSONObject objectAppointments){
        this.objectAppointments = objectAppointments;
    }

    public JSONObject getAppointmentObject(){
        return this.objectAppointments;
    }



    //only available service,(depends on cluster)
    public void setArrayOnlyAvailableService(JSONArray arrayItems){
        this.arrayOnlyAvailableService = arrayItems;
    }
    public JSONArray getArrayOnlyAvailableService(){
        return this.arrayOnlyAvailableService;
    }

    //only available Products,(depends on cluster)
    public void setArrayOnlyAvailableProduct(JSONArray arrayItems){
        this.arrayOnlyAvailableProduct = arrayItems;
    }
    public JSONArray getArrayOnlyAvailableProduct(){
        return this.arrayOnlyAvailableProduct;
    }



    //set & get selected technician schedule
    public void setTechnicianSchedule(JSONObject objectTechnicianSchedule){
        this.objectTechnicianSchedule = objectTechnicianSchedule;
    }
    public JSONObject getTechnicianSchedule(){
        return this.objectTechnicianSchedule;
    }



    public void resetArrayItems(){
        this.arrayItems = new JSONArray();
        try {
            JSONArray arrayService = objectAppointments.getJSONArray("services");
            JSONArray arrayProduct = objectAppointments.getJSONArray("products");
            arrayService = new JSONArray();
            arrayProduct = new JSONArray();
            this.objectAppointments.put("services",arrayService);
            this.objectAppointments.put("products",arrayProduct);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void resetWaiver(){
        this.objWaiverAnswer = new JSONObject();
        try {
            JSONObject objectWaiver = objectAppointments.getJSONObject("waiver_data");
            objectWaiver            = new JSONObject();
            objectAppointments.put("waiver_data",objectWaiver);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //reset All
    public void resetAppointment(){

        this.roomCount                  = 0;
        this.branch_id                  = 0;
        this.timeExtension              = 0;
        this.app_reserved               = "";
        this.objWaiverAnswer            = new JSONObject();
        this.objectUserInformation      = new JSONObject();
        this.arrayService               = new JSONArray();
        this.arrayProduct               = new JSONArray();
        this.objectAppointments         = new JSONObject();
        this.arrayItems                 = new JSONArray();
        this.arrayQueuing               = new JSONArray();
        this.arrayOnlyAvailableService  = new JSONArray();
        this.arrayOnlyAvailableProduct  = new JSONArray();
        this.objectTechnicianSchedule   = new JSONObject();
        this.ifAppointment              = false;
    }

    public void setWaiverAnswer(JSONObject jsonObject){
        this.objWaiverAnswer = jsonObject;
    }

    public JSONObject getWaiverAnswer(){
        return this.objWaiverAnswer;
    }


    //save room count
    public void setRoomCount(int roomCounts){
        this.roomCount = roomCounts;
    }

    public int getRoomCount(){
        return this.roomCount;
    }



    public boolean checkIfAppointment(){
        return this.ifAppointment;
    }

    public void setIfAppointment(boolean ifAppointment){
         this.ifAppointment = ifAppointment;
    }



    public void setTimeExtension(int minute){
        this.timeExtension = minute;
    }

    public int getTimeExtension(){
        return this.timeExtension;
    }



}
