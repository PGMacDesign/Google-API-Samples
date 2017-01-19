package com.pgmacdesign.googleapisamples.utilitiesandmisc;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class MiscUtilities {




    public static void printOutMyHashKey(Context context, String packageName){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print out a hashmap
     * @param myMap Map of type String, ?
     */
    public static void printOutHashMap(Map<String, ?> myMap){
        if(myMap == null){
            return;
        }
        L.m("Printing out entire Hashmap:\n");
        for(Map.Entry<String, ?> map : myMap.entrySet()){
            String key = map.getKey();
            Object value = map.getValue();
            L.m("Key = " + key);
            L.m("Value = " + value.toString());
        }
        L.m("\nEnd printing out Hashmap:");
    }

    /**
     * This class will determine if the current loop being run is on the main thread or not
     * @return boolean, true if on main ui thread, false if not
     */
    public static boolean isRunningOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Remove nulls from a list of list of objects
     * @param nestedListObject
     * @return remove the nulls and return objects
     */
    public static List<List<Object>> removeNullsFromLists(List<List<?>> nestedListObject){
        List<List<Object>> listsToReturn = new ArrayList<>();
        for(int i = 0; i < nestedListObject.size(); i++){
            try {
                List<Object> obj = listsToReturn.get(i);
                if(obj == null){
                    continue;
                }
                obj = removeNullsFromList(obj);
                if(obj != null){
                    listsToReturn.add(obj);
                }
            } catch (Exception e){}
        }
        return listsToReturn;
    }

    /**
     * Remove nulls from a list of objects
     * @param myList
     * @return remove the nulls and return objects
     */
    public static List<Object> removeNullsFromList (List<?> myList){
        if(myList == null){
            return null;
        }
        List<Object> listToReturn = new ArrayList<>();
        for(int i = 0; i < myList.size(); i++){
            try {
                Object obj = myList.get(i);
                if(obj != null){
                    listToReturn.add(obj);
                }
            } catch (Exception e){}
        }
        return listToReturn;
    }
    /**
     * Checks if a user has the facebook application installed on their phone
     * @param context
     * @return
     */
    public static boolean doesUserHaveFacebookAppInstalled(Context context){
        try{
            ApplicationInfo info = context.getPackageManager().
                    getApplicationInfo("com.facebook.katana", 0 );
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
    }
    /**
     * Checks a list for either being empty or containing objects within it
     * @param myList List to check
     * @param <T> T extends object
     * @return Boolean, true if it is null or empty, false it if is not
     */
    public static <T extends Object> boolean isListNullOrEmpty(List<T> myList){
        if(myList == null){
            return true;
        }
        if(myList.size() <= 0){
            return true;
        }
        return false;
    }

    /**
     * Checks a list for either being empty or containing objects within it
     * @param myArray array to check
     * @param <T> T extends object
     * @return boolean, true if it is null or empty, false it if is not
     */
    public static <T extends Object> boolean isArrayNullOrEmpty(T[] myArray){
        if(myArray == null){
            return true;
        }
        if(myArray.length <= 0){
            return true;
        }
        return false;
    }


    /**
     * Checks a map for either being empty or containing objects within it
     * @param myMap map to check
     * @param <T> T extends object
     * @return Boolean, true if it is null or empty, false it if is not
     */
    public static <T extends Object> boolean isMapNullOrEmpty(Map<T, T> myMap){
        if(myMap == null){
            return true;
        }
        if(myMap.size() <= 0){
            return true;
        }
        return false;
    }


}
