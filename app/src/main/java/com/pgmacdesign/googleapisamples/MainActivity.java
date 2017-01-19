package com.pgmacdesign.googleapisamples;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.pgmacdesign.googleapisamples.location.Geofencing;
import com.pgmacdesign.googleapisamples.location.GetSimpleLocation;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleteListener {

    //Class Objects
    private GetSimpleLocation simpleLocation;
    private Geofencing geofencing;

    //UI
    private Button location_simple_location, location_add_geofence, location_remove_geofence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        this.location_simple_location = (Button) this.findViewById(
                R.id.location_simple_location);
        this.location_add_geofence = (Button) this.findViewById(
                R.id.location_add_geofence);
        this.location_remove_geofence = (Button) this.findViewById(
                R.id.location_remove_geofence);

        location_simple_location.setTransformationMethod(null);
        location_add_geofence.setTransformationMethod(null);
        location_remove_geofence.setTransformationMethod(null);


        this.location_simple_location.setOnClickListener(this);
        this.location_add_geofence.setOnClickListener(this);
        this.location_remove_geofence.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.location_simple_location:
                simpleLocation = new GetSimpleLocation(this, this);
                simpleLocation.startLocationServices();
                break;

            case R.id.location_remove_geofence:
                if(geofencing == null) {
                    geofencing = new Geofencing(this, this, null);
                }
                geofencing.removeGeofences();
                break;

            case R.id.location_add_geofence:
                if(geofencing == null) {
                    geofencing = new Geofencing(this, this, null);
                }
                geofencing.addGeofences();
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
                    L.toast(this, "Location not null");
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
