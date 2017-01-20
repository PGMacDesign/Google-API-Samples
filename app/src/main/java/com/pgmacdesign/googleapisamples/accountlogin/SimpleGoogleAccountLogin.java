package com.pgmacdesign.googleapisamples.accountlogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;

/**
 * Created by pmacdowell on 2017-01-20.
 */

public class SimpleGoogleAccountLogin implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Activity activity;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private OnTaskCompleteListener listener;

    public SimpleGoogleAccountLogin(Activity activity, OnTaskCompleteListener listener){
        this.activity = activity;
        this.listener = listener;
        init();
        initialSetup();
        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        signIn();
                    }
                }, (2000)
        );

        Handler handler2 = new Handler();
        handler2.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        startGoogleLogin();
                    }
                }, (4000)
        );

    }

    private void initialSetup() {
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        /*
        mGoogleApiClient = GoogleClientSingleton.buildClient(
            activity, Auth.GOOGLE_SIGN_IN_API, this, this
        );
        */
        /*
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(FragmentActivity , OnConnectionFailedListener )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
         */
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Get permissions first
     * @return False if permissions have not been granted, true if they had been
     */
    private boolean init(){
        //boolean bool = PermissionUtilities.getLocationPermissions(activity);
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startGoogleLogin();
    }

    @Override
    public void onConnectionSuspended(int i) {
        try {
            mGoogleApiClient.connect();
        } catch (Exception e){}
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            mGoogleApiClient.connect();
        } catch (Exception e){}
    }


    private void startGoogleLogin() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            L.m("Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            L.toast(activity, "Starting Signin");
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        L.m("handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String str = "acct info = " + acct.getDisplayName() + ", " +
                    acct.getEmail() + ", " + acct.getPhotoUrl();
            L.m(str);
            listener.onTaskComplete(str, 0);
        } else {
            // Signed out, show unauthenticated UI.
            L.toast(activity, "Signed out / Unauthenticated");
        }
    }

    public void onOnActivityResult(int requestCode, int resultCode, Intent data){
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, Constants.GOOGLE_SIGNIN_REQUEST_CODE);
    }

    /**
     * Logout of google account
     */
    public void signout(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //Logged out
                    }
                });
    }

    /**
     * Revoke access to google account
     */
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //Revoked
                    }
                });
    }

}
