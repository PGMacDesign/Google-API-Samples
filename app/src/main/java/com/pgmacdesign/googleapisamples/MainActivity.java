package com.pgmacdesign.googleapisamples;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pgmacdesign.googleapisamples.accountlogin.SimpleGoogleAccountLogin;
import com.pgmacdesign.googleapisamples.activityrecognition.DetectedActivitiesActivity;
import com.pgmacdesign.googleapisamples.firebase.database.FirebaseDatabaseWrapper;
import com.pgmacdesign.googleapisamples.location.GeoLocationUpdates;
import com.pgmacdesign.googleapisamples.location.Geofencing;
import com.pgmacdesign.googleapisamples.location.GetSimpleLocation;
import com.pgmacdesign.googleapisamples.location.LocationAddressActivity;
import com.pgmacdesign.googleapisamples.safetynet.SimpleSafetyNetExample;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleteListener {

    //Class Objects
    private GetSimpleLocation simpleLocation;
    private Geofencing geofencing;
    private GeoLocationUpdates geoLocationUpdates;
    private SimpleSafetyNetExample simpleSafetyNetExample;
    private SimpleGoogleAccountLogin simpleGoogleAccountLogin;

    //UI
    private Button location_simple_location, location_add_geofence, activity_recognition_checking,
            location_update_checking, location_address_activity, safety_net_checking,
            simple_google_account_login, firebase_dynamic_button;
    private TextView dynamic_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        MyApplication.getInstance();

        this.dynamic_tv = (TextView) this.findViewById(R.id.dynamic_tv);

        this.firebase_dynamic_button = (Button) this.findViewById(
                R.id.firebase_dynamic_button);
        this.location_simple_location = (Button) this.findViewById(
                R.id.location_simple_location);
        this.location_add_geofence = (Button) this.findViewById(
                R.id.location_add_geofence);
        this.activity_recognition_checking = (Button) this.findViewById(
                R.id.activity_recognition_checking);
        this.location_update_checking = (Button) this.findViewById(
                R.id.location_update_checking);
        this.location_address_activity = (Button) this.findViewById(
                R.id.location_address_activity);
        this.simple_google_account_login = (Button) this.findViewById(
                R.id.simple_google_account_login);
        this.safety_net_checking = (Button) this.findViewById(
                R.id.safety_net_checking);

        this.location_simple_location.setTransformationMethod(null);
        this.location_add_geofence.setTransformationMethod(null);
        this.activity_recognition_checking.setTransformationMethod(null);
        this.location_update_checking.setTransformationMethod(null);
        this.location_address_activity.setTransformationMethod(null);
        this.safety_net_checking.setTransformationMethod(null);
        this.simple_google_account_login.setTransformationMethod(null);
        this.firebase_dynamic_button.setTransformationMethod(null);

        this.location_simple_location.setOnClickListener(this);
        this.location_add_geofence.setOnClickListener(this);
        this.activity_recognition_checking.setOnClickListener(this);
        this.location_update_checking.setOnClickListener(this);
        this.location_address_activity.setOnClickListener(this);
        this.safety_net_checking.setOnClickListener(this);
        this.simple_google_account_login.setOnClickListener(this);
        this.firebase_dynamic_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.location_simple_location:
                simpleLocation = new GetSimpleLocation(this, this);
                simpleLocation.startLocationServices();
                break;

            case R.id.location_add_geofence:
                if(geofencing == null) {
                    geofencing = new Geofencing(this, this, null);
                }
                geofencing.startGPSTracking();
                break;

            case R.id.activity_recognition_checking:
                intent = new Intent(MainActivity.this, DetectedActivitiesActivity.class);
                startActivity(intent);
                break;

            case R.id.location_update_checking:
                if(geoLocationUpdates == null) {
                    geoLocationUpdates = new GeoLocationUpdates(MainActivity.this, this);
                }
                geoLocationUpdates.startStopLocationServices();
                break;

            case R.id.location_address_activity:
                intent = new Intent(this, LocationAddressActivity.class);
                startActivity(intent);
                break;

            case R.id.safety_net_checking:
                simpleSafetyNetExample = new SimpleSafetyNetExample(this, this);

                break;

            case R.id.simple_google_account_login:
                simpleGoogleAccountLogin = new SimpleGoogleAccountLogin(this, this);

                break;

            case R.id.firebase_dynamic_button:
                FirebaseDatabaseWrapper.authenticate(this, "password");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.GOOGLE_SIGNIN_REQUEST_CODE){
            L.m("data to String = " + data.getDataString());
            L.m("data to String2 = " + data.getData());
            simpleGoogleAccountLogin.onOnActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onTaskComplete(Object obj, int tag) {
        Location location;
        String str;

        switch (tag){

            case Constants.TAG_SAFETY_NET_SUCCESS:
                str = (String) obj;
                L.m("str = " + str);
                break;

            case Constants.TAG_LOCATION_FAILED:
                str = (String) obj;
                L.toast(this, "Location Failed: " + str);
                break;

            case Constants.TAG_LOCATION_OBJECT:
                location = (Location) obj;
                if(location != null){
                    //L.toast(this, "Location not null");
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    float accuracy = location.getAccuracy();
                    String str1 = "Lat: " + lat + ", Lng: " + lng + ", Accuracy: " + accuracy;
                    this.dynamic_tv.setText(str1);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Call the respective onStop() calls for the objects
        if(simpleLocation != null){
            simpleLocation.activityOnStop();
        }
    }
}
