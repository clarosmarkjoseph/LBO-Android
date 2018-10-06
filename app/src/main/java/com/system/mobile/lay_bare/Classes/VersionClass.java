package com.system.mobile.lay_bare.Classes;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.system.mobile.lay_bare.BuildConfig;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.Utilities.Utilities;

/**
 * Created by paolohilario on 1/10/18.
 */

public class VersionClass {

    DataHandler handler;
    Context context;

    public VersionClass(Context ctx) {
        this.context = ctx;
    }


    public double returnBannerVersion(){
        handler = new DataHandler(context);
        handler.open();
        Cursor c = handler.returnBanner();
        if(c.getCount() > 0){
            c.moveToFirst();
            double versionBanner = Double.parseDouble(c.getString(0));
            handler.close();
            return versionBanner;
        }
        handler.close();
        return 0;
    }

    public double returnServiceVersion(){
        handler = new DataHandler(context);
        handler.open();
        Cursor c = handler.returnServices();
        if(c.getCount() > 0){
            c.moveToFirst();
            double versionService = Double.parseDouble(c.getString(0));
            handler.close();
            return versionService;
        }
        handler.close();
        return 0;
    }

    public double returnPackagesVersion(){
        handler = new DataHandler(context);
        handler.open();
        Cursor c = handler.returnPackage();
        if(c.getCount() > 0){
            c.moveToFirst();
            double versionPackage = Double.parseDouble(c.getString(0));
            handler.close();
            return versionPackage;
        }
        handler.close();
        return 0;
    }


    public double returnProductVersion(){
        handler = new DataHandler(context);
        handler.open();
        Cursor c = handler.returnProducts();
        if(c.getCount() > 0){
            c.moveToFirst();
            double versionProducts = Double.parseDouble(c.getString(0));
            handler.close();
            return versionProducts;
        }
        handler.close();
        return 0;
    }

    public double returnBranchesVersion(){
        handler = new DataHandler(context);
        handler.open();
        Cursor c = handler.returnBranches();
        if(c.getCount() > 0){
            c.moveToFirst();
            double versionBranches = Double.parseDouble(c.getString(0));
            handler.close();
            return versionBranches;
        }
        handler.close();
        return 0;
    }



    public double returnCommercialVersion(){
//        handler = new DataHandler(context);
//        handler.open();
//        Cursor c = handler.returnCommercial();
//        if(c.getCount() > 0){
//            c.moveToFirst();
//            double versionService = Double.parseDouble(c.getString(1));
//            handler.close();
//            return versionService;
//        }
//        handler.close();
        return 0;
    }


    public double returnAppVersion(){
        double versionCode      = BuildConfig.VERSION_CODE;
        String versionNames     = BuildConfig.VERSION_NAME;
        String currentVersion   = "";
        try {
            currentVersion    = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("versions: ",versionCode+" - "+versionNames+" - "+currentVersion);
        return Double.parseDouble(versionNames);
    }





}
