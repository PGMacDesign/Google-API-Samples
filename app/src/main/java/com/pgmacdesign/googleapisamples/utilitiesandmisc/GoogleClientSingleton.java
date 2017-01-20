package com.pgmacdesign.googleapisamples.utilitiesandmisc;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class GoogleClientSingleton {

    private static GoogleApiClient googleApiClient;

    /**
     * Build a google api client.
     * @param context Context
     * @param api API To work with (IE LocationServices.API)
     * @param connectionCallbacks Connection callback
     * @param listener OnConnectionFailedListener
     * @return GoogleApiClient object
     */
    public static synchronized GoogleApiClient buildClient(
            @NonNull Context context, @NonNull Api api,
            @NonNull GoogleApiClient.ConnectionCallbacks connectionCallbacks,
            @NonNull GoogleApiClient.OnConnectionFailedListener listener){

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(listener)
                .addApi(api)
                .build();
        return googleApiClient;
    }


}
