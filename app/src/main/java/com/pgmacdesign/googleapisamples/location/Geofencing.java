package com.pgmacdesign.googleapisamples.location;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.GoogleClientSingleton;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.MiscUtilities;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.PermissionUtilities;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.SharedPrefs;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class Geofencing  implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>{

    private Activity activity;
    private GoogleApiClient mGoogleApiClient;
    private OnTaskCompleteListener listener;



    //The list of geofences used in this sample.
    private ArrayList<Geofence> mGeofenceList;
    //Used to keep track of whether geofences were added.
    private boolean mGeofencesAdded;
    //Used when requesting to add or remove geofences.
    private PendingIntent mGeofencePendingIntent;



    public Geofencing(Activity activity, OnTaskCompleteListener listener,
                      ArrayList<Geofence> mGeofenceList){
        this.activity = activity;
        this.listener = listener;
        if(MiscUtilities.isListNullOrEmpty(mGeofenceList)){
            mGeofenceList = populateGeofenceList();
        }
        this.mGeofenceList = mGeofenceList;
        mGeofencesAdded = SharedPrefs.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);
        init();

        if(mGoogleApiClient == null) {
            mGoogleApiClient = GoogleClientSingleton.buildClient(
                    activity.getApplicationContext(),
                    LocationServices.API, this, this);
        }
        mGoogleApiClient.connect();
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
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofences() {
        if(!init()){
            return;
        }
        if (!mGoogleApiClient.isConnected()) {
            listener.onTaskComplete("Connection Failed", Constants.TAG_LOCATION_FAILED);
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofences() {
        if(!init()){
            return;
        }
        if (!mGoogleApiClient.isConnected()) {
            listener.onTaskComplete("Not Connected", Constants.TAG_LOCATION_FAILED);
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {}

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
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPrefs.save(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);

            L.m("GeoFences Removed");
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = status.getStatusMessage();
            listener.onTaskComplete(errorMessage, Constants.TAG_LOCATION_FAILED);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(activity, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public ArrayList<Geofence> populateGeofenceList() {
        ArrayList<Geofence> aList = new ArrayList<>();
        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {

            aList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
        return aList;
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
