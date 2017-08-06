package com.parsin.bletool.internal;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.util.Log;


public class FindUtils {

    // empty construtor
    private FindUtils() {

    }

    // Checking if WiFi adapter is ON or OFF
    public static boolean isWiFiAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            return true;
        }
        else{
            Log.e("FindUtils","Connection Problem");
            return false;
        }
    }

    // Checking Location service status
    public static boolean isLocationAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled)
            return false;
        else return true;
    }

    // Checking if we have location permission or not
    public static boolean hasAnyLocationPermission(Context context) {
        int fineLocationPermission = context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        int coarseLocationPermission = context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
        return fineLocationPermission == 0 || coarseLocationPermission == 0;
    }
}
