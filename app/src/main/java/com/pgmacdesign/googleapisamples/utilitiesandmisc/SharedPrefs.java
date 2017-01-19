package com.pgmacdesign.googleapisamples.utilitiesandmisc;


import android.content.Context;
import android.content.SharedPreferences;

import com.pgmacdesign.googleapisamples.MyApplication;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pmacdowell on 5/14/2015.
 */
public class SharedPrefs {
    
    private static SharedPreferences.Editor edit1;
    private static SharedPreferences prefs1;
    /*
    All the methods below are static save methods. First param is the key and the second is the
    value. There are multiple overloaded types depending on type passed in.
    */

    public static void save(String valueKey, String value) {
        init();
        edit1.putString(valueKey, value);
        edit1.commit();
    }

    public static void save(String valueKey, int value) {
        //SharedPreferences.Editor edit = getEditor();
        init();
        edit1.putInt(valueKey, value);
        edit1.commit();
    }

    public static void save(String valueKey, boolean value) {
        init();
        edit1.putBoolean(valueKey, value);
        edit1.commit();
    }

    public static void save(String valueKey, long value) {
        init();
        edit1.putLong(valueKey, value);
        edit1.commit();
    }

    public static void save(String valueKey, double value) {
        init();
        edit1.putLong(valueKey, Double.doubleToRawLongBits(value));
        edit1.commit();
    }

    public static void save(String valueKey, Set<String> values) {
        init();
        edit1.putStringSet(valueKey, values);
        edit1.commit();
    }

    public static <T extends Object> void save(String valueKey, List<T> values) {
        init();
        for(int i=0 ; i<values.size(); i++){
            //Get the object
            Object object = values.get(i);
            //Check the type and cast it, then put it into edit object
            if(object instanceof String){
                String objectConverted = (String) object;
                edit1.putString(valueKey + "-" + i, objectConverted);
            } else if (object instanceof Integer){
                int objectConverted = (int) object;
                edit1.putInt(valueKey + "-" + i, objectConverted);
            } else if (object instanceof Long){
                long objectConverted = (long) object;
                edit1.putLong(valueKey + "-" + i, objectConverted);
            } else if (object instanceof Double){
                double objectConverted = (double) object;
                edit1.putLong(valueKey + "-" + i, Double.doubleToRawLongBits(objectConverted));
            } else if (object instanceof Boolean){
                boolean objectConverted = (boolean) object;
                edit1.putBoolean(valueKey + "-" + i, objectConverted);
            } else {
            }
        }
        //Finally, commit it
        edit1.commit();
    }

    public static void save(String valueKey, String[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            edit1.putString(valueKey + "-" + i, values[i]);
        }
        edit1.commit();
    }

    public static void save(String valueKey, int[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            edit1.putInt(valueKey + "-" + i, values[i]);
        }
        edit1.commit();
    }

    public static void save(String valueKey, long[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            edit1.putLong(valueKey + "-" + i, values[i]);
        }
        edit1.commit();
    }

    public static void save(String valueKey, double[] values) {
        init();
        for(int i = 0 ; i < values.length; i++){
            edit1.putLong(valueKey + "-" + i, Double.doubleToRawLongBits(values[i]));
        }
        edit1.commit();
    }

    public static void save(String valueKey, boolean[] values) {
        SharedPreferences.Editor edit = getEditor();
        for(int i = 0 ; i < values.length; i++){
            edit.putBoolean(valueKey + "-" + i, values[i]);
        }
        edit.commit();
    }

    /**
     * Will convert the secondary value of type ? to a String and set it into the Shared Prefs
     * @param map Map of type <String, ?>
     */
    public static void save(Map<String, ?> map){
        init();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object object = entry.getValue();
            if(!StringUtilities.isNullOrEmpty(key) && object != null){
                edit1.putString(key, object.toString());
            }
        }
        edit1.commit();
    }




    //
    //Get methods
    //

    public static String getString(String valueKey, String defaultValue) {
        init();
        return prefs1.getString(valueKey, defaultValue);
    }

    public static int getInt(String valueKey, int defaultValue) {
        init();
        return prefs1.getInt(valueKey, defaultValue);
    }

    public static boolean getBoolean(String valueKey, boolean defaultValue) {
        init();
        return prefs1.getBoolean(valueKey, defaultValue);
    }

    public static long getLong(String valueKey, long defaultValue) {
        init();
        return prefs1.getLong(valueKey, defaultValue);
    }

    public static double getDouble(String valueKey, double defaultValue) {
        init();
        return Double.longBitsToDouble(prefs1.getLong(valueKey, Double.doubleToLongBits(defaultValue)));
    }

    public static Set<String> getSet(String valueKey, Set<String> defaultValues) {
        init();
        return prefs1.getStringSet(valueKey, defaultValues);
    }

    public static SharedPreferences getPrefs(){
        //Use this top one for default preferences. For now, using custom shared preferences as defined in constants
        //SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        prefs1 = MyApplication.getAppContext().getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        return prefs1;
    }

    public static SharedPreferences.Editor getEditor(){
        SharedPreferences prefs1 = getPrefs();
        if(edit1 == null){
            edit1 = prefs1.edit();
        }
        return edit1;
    }

    public static void init(){
        if(prefs1 == null){
            getPrefs();
        }
        if(edit1 == null){
            edit1 = prefs1.edit();
        }
    }
    
    public static void clearPref(String key){
        SharedPreferences.Editor edit = getEditor();
        edit.remove(key);
        edit.commit();
    }

    /**
     * Clears ALL preferences stored.
     */
    public static void clearAllPrefs(){
        SharedPreferences.Editor edit = getEditor();
        edit.clear();
        edit.commit();
    }

    /**
     * Gets everything in the shared preferences
     * @return A map of String to objects
     */
    public static Map<String, ?> getAll(){
        init();
        Map<String, ?> myMap = prefs1.getAll();
        return myMap;
    }

    public static void printAllSharedPrefs(){
        init();
        Map<String, ?> myMap = prefs1.getAll();
        for(Map.Entry<String, ?> aMap : myMap.entrySet()){
            try {
                String key = aMap.getKey();
                Object value = aMap.getValue();
                L.m("KEY = " + key + " : VALUE = " + value.toString());
            } catch (Exception e){}
        }
    }
	/*
	//Global Variables
		public static final String PREFS_NAME = Constants.PREFS_NAME;
		SharedPrefs sp = new SharedPrefs();
		SharedPreferences settings;
		SharedPreferences.Editor editor;
	//Add this into the onCreate() or Initialize() section:
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();

	*/
    //End Boiler Plate Code

    /**
     * Returns a SharedPreferences Object
     * @param context
     * @return

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This allows doubles to be entered into the data field.
     * Used - IE) sp.putDouble(editor, "sales_dollars", 2.4231);
     * @param edit Editor being used
     * @param key Which 'column' the data is being entered to
     * @param value Value To Enter
     * @return editor object

    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value){
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    /**
     * Returns a double from the shared preferences data field.
     * @param prefs SharedPreferences Variable
     * @param key The String key to match against
     * @param defaultValue default value to be returned if no value available
     * @return double

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue){
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    /**
     *  This allows ints to be entered into the data field.
     * @param edit Editor being used
     * @param key String key to match against
     * @param value value to be inserted
     * @return editor object

    public static SharedPreferences.Editor putInt (final SharedPreferences.Editor edit, final String key, final int value){
        return edit.putInt(key, (value));
    }

    /**
     * Returns an int from the shared preferences data field
     * @param prefs SharedPreferences Variable
     * @param key The String key to match against
     * @param defaultValue default value to be returned if no value available
     * @return double

    public static int getInt(final SharedPreferences prefs, final String key, final int defaultValue){
        return prefs.getInt(key, (defaultValue));
    }

    /**
     * This allows Strings to be entered into the data field.
     * @param edit Editor being used
     * @param key String key to match against
     * @param value value to be inserted
     * @return returns an editor object

    public static SharedPreferences.Editor putString (final SharedPreferences.Editor edit, final String key, final String value){
        return edit.putString(key, value);
    }

    /**
     * Returns a String from the shared preferences data field
     * @param prefs SharedPreferences Variable
     * @param key The String key to match against
     * @param defaultValue default value to be returned if no value available
     * @return String

    public static String getString(final SharedPreferences prefs, final String key, final String defaultValue){
        return prefs.getString(key, defaultValue);
    }

    /**
     * Clears the shared pref
     * @param edit The editor being used
     * @param key The String key to be removed from shared preferences

    public static void clearPref(final SharedPreferences.Editor edit, final String key){
        edit.remove(key);
        edit.commit();
    }

    /**
     * Clears the shared pref via an array
     * @param edit The editor being used
     * @param keys The String key to be removed from shared preferences

    public static void clearPref(final SharedPreferences.Editor edit, String[] keys){
        for(int i = 0; i<keys.length; i++){
            edit.remove(keys[i]);
        }
        edit.commit();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Below are the implemented shared preferences methods

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        return null;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
////////////////////////////////////////////////////////////////////////////////////////////////////



    */

}
