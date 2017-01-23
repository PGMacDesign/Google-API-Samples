package com.pgmacdesign.googleapisamples.utilitiesandmisc;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;
import com.pgmacdesign.googleapisamples.R;

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
    //Misc ////////////
    ///////////////////

    public static final String PACKAGE_NAME = "com.pgmacdesign.googleapisamples";
    public static final String ANDROID_CHECK_ATTESTATIONS_VERIFICATION =
            "https://www.googleapis.com/androidcheck/v1/attestations/verify?key=";


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

    //Misc Tags
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    //////////////////////////////
    //Location Address Activity //
    //////////////////////////////

    public static final String RECEIVER = PACKAGE_NAME + "locationaddress.RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + "locationaddress.RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + "locationaddress.LOCATION_DATA_EXTRA";

    //Location tags / Misc//
    ////////////////////////

    //Location Object
    public static final int TAG_LOCATION_OBJECT = 5100;
    //String explanation of why it failed
    public static final int TAG_LOCATION_FAILED = 5101;
    //SafetyNet Sample Tag
    public static final int TAG_SAFETY_NET_SUCCESS = 5102;
    //Google signin request tag
    public static final int GOOGLE_SIGNIN_REQUEST_CODE = 5103;
    //Firebase signin request tag
    public static final int TAG_STRING = 5104;
    //Null tag returned
    public static final int TAG_NULL = 5105;



    //For this sample, geofences expire after twelve hours.
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    // 1 mile, 1.6 km
    public static final float GEOFENCE_RADIUS_IN_METERS = 1609;
    public static final String GEOFENCES_ADDED_KEY = "geofences_added_key";
    //Sample Map for storing information about airports in the San Francisco bay area.
    public static final HashMap<String, LatLng> MY_LOCATIONS = new HashMap<String, LatLng>();

    static {

        //The String ID in the first part is the ID we will use for parsing

        // San Francisco International Airport.
        MY_LOCATIONS.put("SFO", new LatLng(37.621313, -122.378955));
        // Googleplex.
        MY_LOCATIONS.put("GOOGLE", new LatLng(37.422611,-122.0840577));
        //HOTB
        MY_LOCATIONS.put("HOTB", new LatLng(33.632190,-117.734856));
    }

    ////////////////////////
    //Activity Recognition//
    ////////////////////////

    public static final String ACTIVITY_RECOGNITION_PACKAGE_STRING = ".activityrecognition";

    public static final String BROADCAST_ACTION = PACKAGE_NAME +
            ACTIVITY_RECOGNITION_PACKAGE_STRING + ".BROADCAST_ACTION";

    public static final String ACTIVITY_EXTRA = PACKAGE_NAME +
            ACTIVITY_RECOGNITION_PACKAGE_STRING + ".ACTIVITY_EXTRA";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME +
            ACTIVITY_RECOGNITION_PACKAGE_STRING + ".SHARED_PREFERENCES";

    public static final String ACTIVITY_UPDATES_REQUESTED_KEY = PACKAGE_NAME +
            ACTIVITY_RECOGNITION_PACKAGE_STRING + ".ACTIVITY_UPDATES_REQUESTED";

    public static final String DETECTED_ACTIVITIES = PACKAGE_NAME +
            ACTIVITY_RECOGNITION_PACKAGE_STRING + ".DETECTED_ACTIVITIES";

    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 100;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            DETECTION_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    public static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };


    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }
}
