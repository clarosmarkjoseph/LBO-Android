package com.system.mobile.lay_bare.Classes;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.system.mobile.lay_bare.DataHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by paolohilario on 1/16/18.
 */


public class BranchClass {
    Context context;
    DataHandler handler;

    public BranchClass(Context ctx){
        this.context = ctx;
    }

    public JSONArray arrayReturnBranch(){
        JSONArray arrayBranch = new JSONArray();
        handler = new DataHandler(context);
        handler.open();
        Cursor query = handler.returnBranches();
        if(query.getCount() > 0){
            query.moveToFirst();
            try {
                String branches = query.getString(1);
                arrayBranch     = new JSONArray(branches);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        handler.close();
        return arrayBranch;
    }

    public int returnExtendedBranchSchedule(JSONObject objectBranchData){
        String type = "";
        int extension_minutes = 0;
        if(objectBranchData.has("type")){
            type                = objectBranchData.optString("type","");
            if(type.equals("stand-alone")){
                extension_minutes = Integer.parseInt(objectBranchData.optString("extension_minutes","0"));
                return extension_minutes;
            }
        }
        return extension_minutes;
    }




}
