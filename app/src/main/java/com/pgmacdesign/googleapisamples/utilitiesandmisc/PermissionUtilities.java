package com.pgmacdesign.googleapisamples.utilitiesandmisc;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class PermissionUtilities {

    /**
     * Checks for camera permissions
     * @param activity
     * @return boolean, true if granted, false if not
     */
    public static boolean getCameraPermissions(Activity activity){
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(activity,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{android.Manifest.permission.CAMERA},
                            Constants.PERMISSION_CAMERA_REQUEST);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks for camera permissions
     * @param activity
     * @return boolean, true if granted, false if not
     */
    public static boolean getLocationPermissions(Activity activity){
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            Constants.PERMISSION_LOCATION_REQUEST);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
