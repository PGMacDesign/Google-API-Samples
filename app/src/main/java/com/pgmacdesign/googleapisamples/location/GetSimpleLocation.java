package com.pgmacdesign.googleapisamples.location;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.GoogleClientSingleton;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.PermissionUtilities;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class GetSimpleLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Activity activity;
    private GoogleApiClient mGoogleApiClient;
    private OnTaskCompleteListener listener;
    protected Location mLastLocation;

    public GetSimpleLocation(Activity activity, OnTaskCompleteListener listener){
        this.activity = activity;
        this.listener = listener;
        init();
    }

    /**
     * Get permissions first
     * @return False if permissions have not been granted, true if they had been
     */
    private boolean init(){
        boolean bool = PermissionUtilities.getLocationPermissions(activity);
        return bool;
    }

    /**
     * Runs when a GoogleApiClient object successfully connects. This way is ideal for apps
     * that do not need a fine location, but only need coarse location.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                L.m("latitude = " + latitude);
                L.m("longitude = " + longitude);
                listener.onTaskComplete(mLastLocation, Constants.TAG_LOCATION_OBJECT);
            } else {
                listener.onTaskComplete("No location detected", Constants.TAG_LOCATION_FAILED);
            }
        } catch (SecurityException se){
            se.printStackTrace();
        }
    }

    /**
     * The connection to Google Play services was lost for some reason. We call connect() to
     * attempt to re-establish the connection.
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        listener.onTaskComplete(connectionResult.getErrorMessage(), Constants.TAG_LOCATION_FAILED);
    }

    /**
     * Start tracking location. If permission is denied, will not run
     */
    public void startLocationServices(){
        if(!init()){
            return;
        }
        mGoogleApiClient = GoogleClientSingleton.buildClient(
                activity.getApplicationContext(),
                LocationServices.API, this, this);
        mGoogleApiClient.connect();
    }

    /**
     * Stops tracking location
     */
    public void stopLocationServices(){
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * to be called when the onStop is called in the activity
     */
    public void activityOnStop(){
        stopLocationServices();
    }

}
