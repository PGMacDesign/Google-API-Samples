package com.pgmacdesign.googleapisamples.utilitiesandmisc;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class Constants {

    //Links
    /*
    1) https://github.com/googlesamples/google-services/tree/master/android
    2) https://github.com/googlesamples?utf8=%E2%9C%93&q=&type=&language=java
    3) https://developers.google.com/android/reference/com/google/android/gms/actions/ItemListIntents
    4) https://github.com/googlesamples/android-play-location
    5)
    */

    ///////////////////
    //Regex Strings////
    ///////////////////

    //Regex
    public static final String PASSWORD_PATTERN = "^\\S*(?=\\S*[a-zA-Z])(?=\\S*[0-9])\\S*$";
    public static final String WEB_URL_ENCODING = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    ///////////////////
    //Permission tags//
    ///////////////////

    //Shared Preferences
    public static final String PREFS_NAME = "google_api_samples_prefs";

    //Permissions tags
    public static final int PERMISSION_LOCATION_REQUEST = 4000;
    public static final int PERMISSION_CAMERA_REQUEST = 4001;

    ////////////////////////
    //Location tags / Misc//
    ////////////////////////

    //Location Object
    public static final int TAG_LOCATION_OBJECT = 5100;
    //String explanation of why it failed
    public static final int TAG_LOCATION_FAILED = 5101;
    //For this sample, geofences expire after twelve hours.
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    // 1 mile, 1.6 km
    public static final float GEOFENCE_RADIUS_IN_METERS = 1609;
    public static final String GEOFENCES_ADDED_KEY = "geofences_added_key";
    //Sample Map for storing information about airports in the San Francisco bay area.
    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<String, LatLng>();
    static {
        // San Francisco International Airport.
        BAY_AREA_LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));
        // Googleplex.
        BAY_AREA_LANDMARKS.put("GOOGLE", new LatLng(37.422611,-122.0840577));
    }




}
