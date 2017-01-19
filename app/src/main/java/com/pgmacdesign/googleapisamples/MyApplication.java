package com.pgmacdesign.googleapisamples;

import android.app.Application;
import android.content.Context;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class MyApplication extends Application {

    private static MyApplication myApplication = null;
    private static Context context;

    public static MyApplication getInstance(){
        if(myApplication == null)
            myApplication = new MyApplication();


        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}