package com.pgmacdesign.googleapisamples.safetynet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.GoogleClientSingleton;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.StringUtilities;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.VolleyUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.pgmacdesign.googleapisamples.utilitiesandmisc.L.m;

/**
 * Created by pmacdowell on 2017-01-20.
 */

public class SimpleSafetyNetExample implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Activity activity;
    private GoogleApiClient mGoogleApiClient;
    private OnTaskCompleteListener listener;
    protected Location mLastLocation;

    private String mResult;

    public SimpleSafetyNetExample(Activity activity, OnTaskCompleteListener listener){
        this.activity = activity;
        this.listener = listener;
        init();
        setupGoogleAPI();
    }

    /**
     * Get permissions first
     * @return False if permissions have not been granted, true if they had been
     */
    private boolean init(){
        //boolean bool = PermissionUtilities.getLocationPermissions(activity);
        return true;
    }

    private void setupGoogleAPI(){
        mGoogleApiClient = GoogleClientSingleton.buildClient(
                activity, SafetyNet.API, this, this
        );
        mGoogleApiClient.connect();
        /*
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(SafetyNet.API)
                .enableAutoManage(activity, this)
                .build();
        */
    }

    private void sendSafetyNetRequest() {
        m("Sending SafetyNet API request.");

         /*
        Create a nonce for this request.
        The nonce is returned as part of the response from the
        SafetyNet API. Here we append the string to a number of random bytes to ensure it larger
        than the minimum 16 bytes required.
        Read out this value and verify it against the original request to ensure the
        response is correct and genuine.
        NOTE: A nonce must only be used once and a different nonce should be used for each request.
        As a more secure option, you can obtain a nonce from your own server using a secure
        connection. Here in this sample, we generate a String and append random bytes, which is not
        very secure. Follow the tips on the Security Tips page for more information:
        https://developer.android.com/training/articles/security-tips.html#Crypto
         */
        // TODO(developer): Change the nonce generation to include your own, used once value,
        // ideally from your remote server.
        String nonceData = "Safety Net Sample: " + System.currentTimeMillis();
        byte[] nonce = getRequestNonce(nonceData);

        // Call the SafetyNet API asynchronously. The result is returned through the result callback.
        SafetyNet.SafetyNetApi.attest(mGoogleApiClient, nonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {

                    @Override
                    public void onResult(SafetyNetApi.AttestationResult result) {
                        Status status = result.getStatus();
                        if (status.isSuccess()) {
                            /*
                             Successfully communicated with SafetyNet API.
                             Use result.getJwsResult() to get the signed result data. See the server
                             component of this sample for details on how to verify and parse this
                             result.
                             */
                            mResult = result.getJwsResult();
                            m("Success! SafetyNet result:\n" + mResult + "\n");

                            listener.onTaskComplete(mResult, Constants.TAG_SAFETY_NET_SUCCESS);
                            shareResult();
                            /*
                             TODO(developer): Forward this result to your server together with
                             the nonce for verification.
                             You can also parse the JwsResult locally to confirm that the API
                             returned a response by checking for an 'error' field first and before
                             retrying the request with an exponential backoff.
                             NOTE: Do NOT rely on a local, client-side only check for security, you
                             must verify the response on a remote server!
                             */

                        } else {
                            // An error occurred while communicating with the service.
                            L.m("ERROR! " + status.getStatusCode() + " " + status
                                    .getStatusMessage());
                            mResult = null;
                        }
                    }
                });
    }

    private final Random mRandom = new SecureRandom();
    /**
     * Generates a 16-byte nonce with additional data.
     * The nonce should also include additional information, such as a user id or any other details
     * you wish to bind to this attestation. Here you can provide a String that is included in the
     * nonce after 24 random bytes. During verification, extract this data again and check it
     * against the request that was made with this nonce.
     */
    private byte[] getRequestNonce(String data) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[24];
        mRandom.nextBytes(bytes);
        try {
            byteStream.write(bytes);
            byteStream.write(data.getBytes());
        } catch (IOException e) {
            return null;
        }

        return byteStream.toByteArray();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        m("onConnected");
        sendSafetyNetRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        m("connection failed = " + connectionResult.getErrorMessage());
    }

    /**
     * Shares the result of the SafetyNet API call via an {@link Intent#ACTION_SEND} intent.
     * You can use this call to extract the result from the device for testing purposes.
     */
    private void shareResult() {
        if (StringUtilities.isNullOrEmpty(mResult)) {
            L.m("No result received yet. Run the verification first.");
            return;
        }

        Context context = activity.getApplicationContext();
        Map<String, String> myMap = new HashMap<>();
        myMap.put("signedAttestation", mResult);
        L.m("staring volley request");
        /*
         * NOTE!!!
         * This should be run server side. THIS SHOULD NOT BE RUN CLIENT SIDE!!!
         * I am putting it here for reference, but this should not be run client side as it
         * can be tampered with and consequently, have the results altered.
         */
        VolleyUtilities.simplePostSample(
                new OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(Object obj, int tag) {
                        try {
                            String str = (String) obj;
                            L.m("RESULT = " + str);
                            //If successful, should receive: {"isValidSignature":true}
                        } catch (Exception e){}
                    }
                }, context, Constants.ANDROID_CHECK_ATTESTATIONS_VERIFICATION
                        + "AIzaSyDkNQFcWtPQQBwYCAw1cZmaAhb1lDupFp8", myMap
        );


        /*
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mResult);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
        */
    }
}
