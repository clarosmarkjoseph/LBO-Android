package com.system.mobile.lay_bare.PLC;

import org.json.JSONObject;

/**
 * Created by Mark on 27/11/2017.
 */

public class PLCSingleton {

    private static PLCSingleton instance;
    JSONObject plcApplicationStatus;
    String plcApplicationCaption            = "";
    double total_transaction = 0.0;
    double total_discount    = 0.0;
    int steps = 0;

    public static PLCSingleton Instance() {
        if (instance == null) {
            instance = new PLCSingleton();
        }
        return instance;
    }


    public JSONObject returnPLCObject(){
        return this.plcApplicationStatus;
    }
    public void setPLCObject(JSONObject status){
        this.plcApplicationStatus = status;
    }

//    public String returnPLCCaption(){
//        return this.plcApplicationCaption;
//    }
//    public void setPLCCaption(String caption){
//        this.plcApplicationCaption = caption;
//    }
//    public boolean returnIfApplicationIsEnabled(){
//        return this.ifPLCApplicationIsEnabled;
//    }
//    public void setIfApplicationIsEnabled(boolean ifEnabled){
//        this.ifPLCApplicationIsEnabled = ifEnabled;
//    }






}
