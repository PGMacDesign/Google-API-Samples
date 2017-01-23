package com.pgmacdesign.googleapisamples.firebase.database;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by PatrickSSD2 on 1/23/2017.
 */

public class FirebaseDatabaseWrapper {
    //Table:
    public static final String CHAT_MESSAGES = "chat_messages";
    //Fields Within Table:
    public static final String CHAT_ID = "chatId";
    public static final String MESSAGE = "message";

    //Table:
    public static final String CHAT_ROOMS = "chat_rooms";
    //Fields Within Table:
    public static final String MAIN_USER = "mainUser";
    public static final String ROOM_ID = "roomId";

    //Firebase Objects
    private static DatabaseReference chatRoomsRef, chatMessagesRef;
    private static FirebaseDatabase mDatabase;
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;

    static {
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = getDatabase();
        chatRoomsRef = mDatabase.getReference().child(CHAT_ROOMS);
        chatMessagesRef = mDatabase.getReference().child(CHAT_MESSAGES);
    }

    public static FirebaseDatabase getDatabase(){
        if(mDatabase == null){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    public static void authenticate(final Activity activity,
                                    final OnTaskCompleteListener listener,
                                    final String customToken){

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    L.m("onAuthStateChanged:signed_in:" + user.getUid());
                    listener.onTaskComplete(user.getUid(), Constants.TAG_STRING);
                } else {
                    // User is signed out
                    L.m("onAuthStateChanged:signed_out");
                    listener.onTaskComplete(null, Constants.TAG_NULL);
                }
                // ...
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        L.m("starting signin with custom token");

        mAuth.signInWithCustomToken(customToken)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCustomToken:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCustomToken", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private static void initAdmin(){
        /*
        This would be done server side
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(
                        new FileInputStream("app/serviceAccountKey.json"))
                .setDatabaseUrl("https://pgmacdesign-apisamples.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        */
    }


}
