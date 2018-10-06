package com.system.mobile.lay_bare;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by OrangeApps Zeus on 12/15/2015.
 */
public class MyLocationListener implements LocationListener {
    int f = 1;
    Context c;
    public MyLocationListener(Context context)
    {
        c= context;

    }
    public void onLocationChanged(Location loc) {
        // TODO Auto-generated method stub
        if (f == 1) {
            loc.getLatitude();
            loc.getLongitude();
            f = 2;
        }

        String Text = "My current branch_location is: " +
                "Latitud = " + loc.getLatitude() +
                "Longitud = " + loc.getLongitude();


//        Toast.makeText(c, Text, Toast.LENGTH_SHORT).show();

    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Toast.makeText(c, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
//        Toast.makeText(c, "GPS is enabled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
//        Toast.makeText(c, "GPS is disabled!", Toast.LENGTH_SHORT).show();
    }

}