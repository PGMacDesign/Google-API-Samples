package com.pgmacdesign.googleapisamples;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class MyApplication extends MultiDexApplication {

    /*
    To read:
    1) https://firebase.google.com/docs/auth/android/custom-auth
    2) https://console.firebase.google.com/project/pgmacdesign-apisamples/settings/serviceaccounts/adminsdk
    3) https://firebase.google.com/docs/reference/security/database/#variables
    4) https://firebase.google.com/docs/reference/security/database/
    5) https://firebase.google.com/docs/database/security/quickstart#sample-rules
     */
    private static MyApplication myApplication = null;
    private static Context context;

    static {
        myApplication = getInstance();
    }

    public static synchronized MyApplication getInstance(){
        if(myApplication == null) {
            myApplication = new MyApplication();
        }

        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = getInstance();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
