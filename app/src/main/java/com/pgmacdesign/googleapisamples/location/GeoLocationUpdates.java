package com.pgmacdesign.googleapisamples.location;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.GoogleClientSingleton;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.PermissionUtilities;

import java.text.DateFormat;
import java.util.Date;

import static com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;

/**
 * This class is for location updates with a regular interval
 * For single pings, see {@link GetSimpleLocation} and
 * for geofencing, see {@link Geofencing}
 * Created by pmacdowell on 2017-01-19.
 */
public class GeoLocationUpdates  implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    // Keys for storing activity state in the Bundle.
    private final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private final static String LOCATION_KEY = "location-key";
    private final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    private static final String TAG = "GeoLocationUpdates";

    private Activity activity;
    private GoogleApiClient mGoogleApiClient;
    private OnTaskCompleteListener listener;
    private Location mCurrentLocation;
    //Stores parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;
    //Time when the location was updated represented as a String.
    private String mLastUpdateTime;

    public GeoLocationUpdates(Activity activity, OnTaskCompleteListener listener){
        this.activity = activity;
        this.listener = listener;
        this.mRequestingLocationUpdates = false;
        this.mLastUpdateTime = "";
        // If in an activity, this is where you would restore from instanceState
        //this.updateValuesFromBundle(savedInstanceState);
        if(!init()){
            return;
        }
        if(mGoogleApiClient == null){
            buildClient();
        }
        initLocationRequest();
    }


    /**
     * Get permissions first
     * @return False if permissions have not been granted, true if they had been
     */
    private boolean init(){
        boolean bool = PermissionUtilities.getLocationPermissions(activity);
        return bool;
    }

    private void buildClient(){
        L.m("build a client");
        mGoogleApiClient = GoogleClientSingleton.buildClient(
                activity.getApplicationContext(),
                LocationServices.API, this, this);
        final Handler handler = new Handler();
        mGoogleApiClient.connect();

    }

    public void startStopLocationServices(){
        if(!mRequestingLocationUpdates){
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }
        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(mRequestingLocationUpdates){
                            stopLocationServices();
                            L.m("stop services");
                        } else {
                            startLocationServices();
                            L.m("start services");
                        }
                    }
                }, (1000 * 2)
        );
    }
    /**
     * Start tracking location. If permission is denied, will not run
     */
    private void startLocationServices(){
        if(!init()){
            return;
        }
        if (!mGoogleApiClient.isConnected()) {
            L.toast(activity, "Error: Client not connected");
            return;
        }
        try {
            // The final argument to {@code requestLocationUpdates()} is a LocationListener
            // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, GeoLocationUpdates.this);
            mRequestingLocationUpdates = true;
            L.m("mRequestingLocationUpdates = true");

            buildLocationSettingsRequest();
            checkLocationSettings();
        } catch (SecurityException se){
            se.printStackTrace();
        }
    }

    //This is to show how to add a settings request
    private LocationSettingsRequest mLocationSettingsRequest;
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(
                new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                        Status status = locationSettingsResult.getStatus();

                        L.m("STATUS MESSAGE = " + status.getStatusMessage());

                        /*
                        From here, you can switch through the codes. Here are their meanings
                        (All have a precursor of LocationSettingsStatusCodes ):
                        1) .SUCCESS == Everything is good to go
                        2) .RESOLUTION_REQUIRED == Problem, upgrade location settings. Here you
                           would throw up a dialog or something
                        3) .SETTINGS_CHANGE_UNAVAILABLE == Problem, this phone cannot perform
                           the location services we want.
                         */
                        //If (2) is hit, can call this as a follow up:
                        if ((status.getStatusCode() ==  LocationSettingsStatusCodes.RESOLUTION_REQUIRED)) {
                            try {
                                status.startResolutionForResult(activity, 333);
                            } catch (IntentSender.SendIntentException sie) {
                                sie.printStackTrace();
                            }
                        }
                        /*
                        After this call hits, the onActivityResult will be pinged. Check the request
                        code (IE 333 here) and if the if the resultcode == activity.RESULT_OK, start
                        the location updates again. If the resultcode == activity.RESULT_CANCELED,
                        they 'denied' it and bail out.
                         */
                    }
                }
        );
    }

    private void initLocationRequest(){
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(Constants.DETECTION_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Stops tracking location
     */
    private void stopLocationServices(){
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mRequestingLocationUpdates = false;
            L.m("mRequestingLocationUpdates = false");
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }

        } catch (Exception e){}
    }

    /**
     * to be called when the onStop is called in the activity
     */
    public void activityOnStop(){
        stopLocationServices();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        L.m("onConnected");
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.

        try {
            if (mCurrentLocation == null) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            }

            // If the user presses the Start Updates button before GoogleApiClient connects, we set
            // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
            // the value of mRequestingLocationUpdates and if it is true, we start location updates.
            if (mRequestingLocationUpdates) {
                //startLocationServices();
            }
        } catch (SecurityException se){}
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        L.m("connection failed. Error code = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            return;
        }
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        listener.onTaskComplete(mCurrentLocation, Constants.TAG_LOCATION_OBJECT);
        //L.m("Location changed: lat = " + mCurrentLocation.getLatitude()
                //+ ", Lng = " + mCurrentLocation.getLongitude());
    }

    /*
    Not used here, if running in an activity, use this:
        @Override
        public void onResume() {
            super.onResume();
            // Within {@code onPause()}, we pause location updates, but leave the
            // connection to GoogleApiClient intact.  Here, we resume receiving
            // location updates if the user has requested them.

            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }

        @Override
        protected void onPause() {
            super.onPause();
            // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
        }

        @Override
        protected void onStop() {
            mGoogleApiClient.disconnect();

            super.onStop();
        }
     */
    /**NOT USED HERE! Left in code for reference
     * Stores activity data in the Bundle.

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
    */
    /**
     * NOT USED HERE! Left in code for reference
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }
    */

}
