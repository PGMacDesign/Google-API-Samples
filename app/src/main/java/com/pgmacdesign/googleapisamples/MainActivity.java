package com.pgmacdesign.googleapisamples;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pgmacdesign.googleapisamples.activityrecognition.DetectedActivitiesActivity;
import com.pgmacdesign.googleapisamples.location.GeoLocationUpdates;
import com.pgmacdesign.googleapisamples.location.Geofencing;
import com.pgmacdesign.googleapisamples.location.GetSimpleLocation;
import com.pgmacdesign.googleapisamples.location.LocationAddressActivity;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleteListener {

    //Class Objects
    private GetSimpleLocation simpleLocation;
    private Geofencing geofencing;
    private GeoLocationUpdates geoLocationUpdates;

    //UI
    private Button location_simple_location, location_add_geofence, activity_recognition_checking,
            location_update_checking, location_address_activity;
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

        this.location_simple_location.setTransformationMethod(null);
        this.location_add_geofence.setTransformationMethod(null);
        this.activity_recognition_checking.setTransformationMethod(null);
        this.location_update_checking.setTransformationMethod(null);
        this.location_address_activity.setTransformationMethod(null);

        this.location_simple_location.setOnClickListener(this);
        this.location_add_geofence.setOnClickListener(this);
        this.activity_recognition_checking.setOnClickListener(this);
        this.location_update_checking.setOnClickListener(this);
        this.location_address_activity.setOnClickListener(this);

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


        }
    }

    @Override
    public void onTaskComplete(Object obj, int tag) {
        Location location;
        String str;

        switch (tag){
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
