package com.pgmacdesign.googleapisamples.safetynet;

import android.os.AsyncTask;

import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;

/**

 * Created by pmacdowell on 2017-01-20.
 */
public class CompleteSafetyNetVerification extends AsyncTask <Void, Void, Boolean> {

    private OnTaskCompleteListener listener;
    private String jwsResult;

    public CompleteSafetyNetVerification(OnTaskCompleteListener listener, String jwsResult) {
        super();
        this.listener = listener;
        this.jwsResult = jwsResult;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {

            String BASE_URL = "";
            String API_KEY = "";

        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
