package com.pgmacdesign.googleapisamples.utilitiesandmisc;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class L {


    private static final String TAG = "GoogleAPIExamples";

    /**
     * Quick println
     * @param myObject The string to print (or double, int, whatever)
     * @param <E> Extends object
     */
    public static <E> void m(E myObject){
        //System.out.println("PGMAC: " + myObject);
        //writeToOutput(myObject);
        String str = myObject + "";
        if(StringUtilities.isNullOrEmpty(str)){
            return;
        }
        if (str.length() > 4000) {
            Log.v(TAG, "sb.length = " + str.length());
            int chunkCount = str.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= str.length()) {
                    Log.d(TAG, "chunk " + i + " of " + chunkCount + ":" + str.substring(4000 * i));
                } else {
                    Log.d(TAG, "chunk " + i + " of " + chunkCount + ":" + str.substring(4000 * i, max));
                }
            }
        } else {
            Log.d(TAG, str);
        }
    }

    /**
     * Quick println
     * @param myObject The string to print (or double, int, whatever)
     * @param <E> Extends object
     */
    public static <E> void m(E myObject, boolean printToFile){
        L.m(myObject);
    }

    /**
     * Quick println for the line number
     * @param x int, line number
     */
    public static void l(int x){
        System.out.println("PGMAC: " + "Line Number " + x + " hit");
    }

    /**
     * Quick println for the line number
     * @param x int, line number
     */
    public static void l(Context context, int x){
        String activityName = null;
        try {
            activityName = context.getClass().getSimpleName();
        } catch (Exception e){
            activityName = "N/A";
        }

        System.out.println("PGMAC: " + "Activity: " + activityName + ", "
                + "Line Number " + x + " hit");
    }


    /**
     * Short toast
     * @param context context
     * @param myObject String to print (If other things are passed in, it converts it to a String first)
     */
    public static <E> void toast(Context context, E myObject){
        String str = myObject + ""; //Cast it to a String
        try {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException npe){
        } catch (Exception e){}

    }

    /**
     * Long toast
     * @param context context
     * @param myObject String to print (If other things are passed in, it converts it to a String first)
     */
    public static <E> void Toast(Context context, E myObject){
        String str = myObject + ""; //Cast it to a String
        try {
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
        } catch (NullPointerException npe){
        } catch (Exception e){}
    }

    /**
     * Long toast. Overloaded to include option to alter length
     * @param context context
     * @param myObject String to print (If other things are passed in, it converts it to a String first)
     */
    public static <E> void Toast(Context context, E myObject, int length){
        String str = myObject + ""; //Cast it to a String
        try {
            Toast.makeText(context, str, length).show();
        } catch (NullPointerException npe){
        } catch (Exception e){}
    }

    /**
     * This method calls a switch on the open fragments after X seconds.
     * @param seconds The number of seconds to wait before this is called
     */
    public static void switchToDefaultAfter(int seconds){
        L.m("Seconds total: " + 1000*seconds);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //Do stuff
            }
        }, (1000 * seconds));
    }

}
