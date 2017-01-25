package com.pgmacdesign.googleapisamples.firebase.database;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.Constants;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.L;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.OnTaskCompleteListener;
import com.pgmacdesign.googleapisamples.utilitiesandmisc.StringUtilities;

import java.util.ArrayList;
import java.util.List;

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
    private static OnCompleteListener onCompleteListener;

    //TEST
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PW = "password";


    static {
        initFirebase();
    }

    private static void initFirebase(){
        if(mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        if(mDatabase == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mDatabase = getDatabase();
        }
        if(chatRoomsRef == null) {
            chatRoomsRef = mDatabase.getReference().child(CHAT_ROOMS);
        }
        if(chatMessagesRef == null) {
            chatMessagesRef = mDatabase.getReference().child(CHAT_MESSAGES);
        }
    }

    public static FirebaseDatabase getDatabase(){
        if(mDatabase == null){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    private static FirebaseAuth.AuthStateListener setupAuthListener(final OnTaskCompleteListener listener){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    L.m("onAuthStateChanged:signed_in:");
                    L.m("all data I can gleam:");
                    L.m("Uid = " + user.getUid());
                    L.m("photoUrl = " + user.getPhotoUrl());
                    L.m("email = " + user.getEmail());
                    L.m("displayName = " + user.getDisplayName());
                    L.m("providerId = " + user.getProviderId());
                    List<String> tokens = user.getProviders();
                    for(String str : tokens){
                        L.m("provider str = " + str);
                    }
                    //updateEmail();
                    if(listener != null) {
                        listener.onTaskComplete(user.getUid(), Constants.TAG_STRING);
                    }
                } else {
                    // User is signed out
                    L.m("onAuthStateChanged:signed_out");
                    if(listener != null) {
                        listener.onTaskComplete(null, Constants.TAG_NULL);
                    }
                }
                // ...
            }
        };
        return mAuthListener;
    }


    public static void authenticate(final Activity activity,
                                    final OnTaskCompleteListener listener,
                                    final String customToken){

        mAuthListener = setupAuthListener(listener);
        mAuth.addAuthStateListener(mAuthListener);
        //customSignIn(activity);
        signInWithEmail(activity, TEST_EMAIL, TEST_PW);
    }




    /**
     * Signin with their email / pw
     * @param activity
     */
    private static void signInWithEmail(Activity activity, String email, String pw){
        mAuth.signInWithEmailAndPassword(email, pw)
                .addOnSuccessListener(activity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        if(user != null){
                            //initFirebase();
                            getChatRooms(user);
                        }
                    }
                })
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        L.m("signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            L.m("signInWithEmail:failed = " + task.getException());
                            //Do stuff here with failure
                        }


                        // ...
                    }
                });
    }

    private static void getChatRooms(FirebaseUser user){
        if(user == null){
            return;
        }

        //String displayName = user.getDisplayName();
        //String email = user.getEmail();

        //com.google.firebase.database.Query query = chatRoomsRef.child(user.getDisplayName());
        com.google.firebase.database.Query query = chatRoomsRef.push();
        query
                //.addValueEventListener()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    /*
                    Map<String, Object> chatRoom = (Map<String, Object>) dataSnapshot.getValue();
                    L.m("size of map == " + chatRoom.size());
                    for(Map.Entry<String, Object> aMap : chatRoom.entrySet()){
                        String key = aMap.getKey();
                        Object value = aMap.getValue();
                        //Iterate here
                        L.m("ITEM NUMBER " + x);
                        L.m("KEY = " + key);
                        L.m("VALUE = " + value);
                        //x++;
                    }
                    */

                    int x = 0;
                    Iterable<DataSnapshot> iterableList = dataSnapshot.getChildren();
                    List<DataSnapshot> chatRooms = new ArrayList<DataSnapshot>();
                    for(DataSnapshot snapshot1 : iterableList){
                        if(snapshot1 != null){
                            chatRooms.add(snapshot1);
                        }
                    }
                    L.m("total num of chat rooms = " + chatRooms.size());
                    for(DataSnapshot dataSnapshot1 : chatRooms){
                        Object object = dataSnapshot1.getValue();
                        String str = dataSnapshot1.getKey();
                        L.m("KEY = " + str);
                        L.m("VALUE = " + object.toString());
                        /*
                        Map<String, Object> chatRoom = (Map<String, Object>) dataSnapshot1.getValue();
                        for(Map.Entry<String, Object> aMap : chatRoom.entrySet()){
                            String key = aMap.getKey();
                            Object value = aMap.getValue();
                            //Iterate here
                            L.m("ITEM NUMBER " + x);
                            L.m("KEY = " + key);
                            L.m("VALUE = " + value);
                            x++;
                        }
                        */
                    }


                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                L.m("onCancelled");
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


    /**
     * Update a user's email
     * @param email
     */
    private static void updateUserEmail(String email){
        if(StringUtilities.isNullOrEmpty(email)){
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            return;
        }

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            L.m("User email address updated.");
                        }
                    }
                });
    }

    /**
     * Update a user's password
     * @param newPassword
     */
    private static void updateUserPW(String newPassword){
        if(StringUtilities.isNullOrEmpty(newPassword)){
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            return;
        }

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            L.m("User password updated.");
                        }
                    }
                });
    }

    /**
     * Update the user's Id with their regular id
     */
    private static void updateUserId(String userId){
        if(StringUtilities.isNullOrEmpty(userId)){
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userId)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            L.m("User profile updated.");
                        }
                    }
                });
    }



    /**
     * Sign in anonymously. (Disabled currently on server)
     * @param activity
     */
    private static void anonymousSignIn(final Activity activity){

        mAuth.signInAnonymously()
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        L.m("signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Exception exception = task.getException();
                            if(exception != null){
                                if(exception instanceof FirebaseAuthInvalidUserException){
                                    //No user exists, create account
                                } else if (exception instanceof FirebaseAuthInvalidCredentialsException){
                                    //Wrong signin info
                                }
                            }
                            L.m("signInAnonymously: " + task.getException());
                        }

                        // ...
                    }
                });
    }

    class TestPojo {
        //String
    }
}
