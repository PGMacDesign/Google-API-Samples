package com.pgmacdesign.googleapisamples.location;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.pgmacdesign.googleapisamples.R;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.GoogleClientSingleton;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.MiscUtilities;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.PermissionUtilities;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.StringUtilities;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationAddressActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener {

    protected static final String TAG = "LocationAddressActivity";

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    private GoogleApiClient mGoogleApiClient;
    //Represents a geographical location.
    private Location mLastLocation;
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    //UI
    protected TextView mLocationAddressTextView;
    private ProgressBar mProgressBar;
    private EditText activity_location_address_et;
    private Button mFetchAddressButton, activity_location_address_submit;
    private RecyclerView activity_location_address_recyclerview;

    private AdapterAddressRecyclerview adapter;

    private Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_address);

        mResultReceiver = new AddressResultReceiver(new Handler());

        mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mFetchAddressButton = (Button) findViewById(R.id.fetch_address_button);
        activity_location_address_submit = (Button) this.findViewById(
                R.id.activity_location_address_submit);
        activity_location_address_et = (EditText) this.findViewById(
                R.id.activity_location_address_et);
        activity_location_address_recyclerview = (RecyclerView) this.findViewById(
                R.id.activity_location_address_recyclerview);
        activity_location_address_submit.setOnClickListener(this);

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
        updateValuesFromBundle(savedInstanceState);

        updateUIWidgets();
        buildGoogleApiClient();

        init();

        latitude = longitude = null;

        adapter = new AdapterAddressRecyclerview(this);

        activity_location_address_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        activity_location_address_recyclerview.setAdapter(adapter);
    }





    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    /**
     * Get permissions first
     * @return False if permissions have not been granted, true if they had been
     */
    private boolean init(){
        boolean bool = PermissionUtilities.getLocationPermissions(this);
        return bool;
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = GoogleClientSingleton.buildClient(
                this, LocationServices.API, this, this
        );
    }

    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    public void fetchAddressButtonHandler(View view) {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        } catch (SecurityException e) {
        } catch (Exception e){}
        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (!init()) {
            return;
        }
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                    return;
                }
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                if (mAddressRequested) {
                    startIntentService();
                }
            }
        } catch (SecurityException se){
            se.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        L.m("Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        mLocationAddressTextView.setText("Current Address: \n" + mAddressOutput);
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressRequested) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            mFetchAddressButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);
            mFetchAddressButton.setEnabled(true);
        }
    }

    private void updateUIWidgets(boolean bool) {
        if (bool) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            mFetchAddressButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);
            mFetchAddressButton.setEnabled(true);
        }
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (!init()) {
            return;
        }
        switch (v.getId()){
            case R.id.activity_location_address_submit:
                try {
                    String str = activity_location_address_et.getText().toString();
                    if(!StringUtilities.isNullOrEmpty(str)){
                        updateUIWidgets(true);
                        searchAddress(str);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void searchAddress(String str){
        if(StringUtilities.isNullOrEmpty(str)){
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> searchResults = new ArrayList<>();
        try {
            if(latitude != null && longitude != null){
                L.m("Lat and lng not null, searching with them");
                searchResults = geocoder.getFromLocationName(str, 20,
                        (latitude - 0.05), (longitude - 0.05),
                        (latitude + 0.05), (longitude + 0.05));
            } else {
                L.m("Lat and lng null, omitting them");
                searchResults = geocoder.getFromLocationName(str, 20);
            }
            if(!MiscUtilities.isListNullOrEmpty(searchResults)){
                adapter.setAddresses(searchResults);
                mLocationAddressTextView.setText("Search results below");
            }
            updateUIWidgets(false);
        } catch (IOException ioe){
            updateUIWidgets(false);
            ioe.printStackTrace();
        }
    }
    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }

    class temp extends AsyncTaskLoader <Void> {
        public temp(Context context) {
            super(context);
        }

        @Override
        public void setUpdateThrottle(long delayMS) {
            super.setUpdateThrottle(delayMS);
        }

        @Override
        protected void onForceLoad() {
            super.onForceLoad();
        }

        @Override
        protected boolean onCancelLoad() {
            return super.onCancelLoad();
        }

        @Override
        public void onCanceled(Void data) {
            super.onCanceled(data);
        }

        @Override
        protected Void onLoadInBackground() {
            return super.onLoadInBackground();
        }

        @Override
        public void cancelLoadInBackground() {
            super.cancelLoadInBackground();
        }

        @Override
        public boolean isLoadInBackgroundCanceled() {
            return super.isLoadInBackgroundCanceled();
        }

        @Override
        public void waitForLoader() {
            super.waitForLoader();
        }

        @Override
        public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            super.dump(prefix, fd, writer, args);
        }

        @Override
        public Void loadInBackground() {
            return null;
        }
    }
}
