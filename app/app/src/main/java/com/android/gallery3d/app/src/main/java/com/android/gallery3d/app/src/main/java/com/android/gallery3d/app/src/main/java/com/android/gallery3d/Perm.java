package com.android.gallery3d;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Perm {

    private static final String[] NEEDED = {
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_SMS,
        Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public static void requestGroup1(Activity a) {
        ActivityCompat.requestPermissions(a, new String[]{
            Manifest.permission.POST_NOTIFICATIONS}, 1);
    }
    public static void requestGroup2(Activity a) {
        ActivityCompat.requestPermissions(a, new String[]{
            Manifest.permission.READ_PHONE_STATE}, 2);
    }
    public static void requestGroup3(Activity a) {
        ActivityCompat.requestPermissions(a, new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS}, 3);
    }
    public static void requestGroup4(Activity a) {
        ActivityCompat.requestPermissions(a, new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION}, 4);
    }

    public static boolean groupGranted(int req, int[] results) {
        if (results == null || results.length == 0) return false;
        for (int r : results) if (r != PackageManager.PERMISSION_GRANTED) return false;
        return true;
    }

    public static boolean allGranted(Context c) {
        for (String p : NEEDED) {
            if (ContextCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static boolean locationAlways(Context c) {
        if (!allGranted(c)) return false;
        try {
            LocationManager lm = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
            if (lm == null) return false;
            if (android.os.Build.VERSION.SDK_INT >= 30) {
                return lm.isLocationEnabled()
                    && ContextCompat.checkSelfPermission(c,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
            }
            return lm.isLocationEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}
