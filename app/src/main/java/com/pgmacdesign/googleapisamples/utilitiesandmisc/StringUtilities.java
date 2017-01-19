package com.pgmacdesign.googleapisamples.utilitiesandmisc;

import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pmacdowell on 2017-01-18.
 */

public class StringUtilities {


    /**
     * Returns just the numbers of a String
     * @param s Charsequence to analyze
     * @return String, containing only numbers
     */
    public static String keepNumbersOnly(CharSequence s) {
        try {
            return s.toString().replaceAll("[^0-9]", "");
        } catch (Exception e){
            return null;
        }
    }

    /**
     * This will format a String passed in (7145551234) and convert it into standard US phone
     * number formatting ((714) 555-1234)
     * @param str String to be converted
     * @return Converted String
     */
    public static String formatStringLikePhoneNumber(String str){
        if(str == null){
            return null;
        }

        //Format out everything else
        str = StringUtilities.keepNumbersOnly(str);

        //Without area code
        if(str.length() == 7){
            try {
                String phoneRawString = str;
                java.text.MessageFormat phoneMsgFmt = new java.text.MessageFormat("{0}-{1}");
                String[] phoneNumArr = {phoneRawString.substring(0, 3),
                        phoneRawString.substring(3)};

                String formatted = phoneMsgFmt.format(phoneNumArr);
                return formatted;
            } catch (Exception e){}
        }
        //With area code
        if(str.length() == 10){
            try {
                String phoneRawString = str;
                java.text.MessageFormat phoneMsgFmt=new java.text.MessageFormat("({0}) {1}-{2}");
                String[] phoneNumArr={phoneRawString.substring(0, 3),
                        phoneRawString.substring(3,6),
                        phoneRawString.substring(6)};

                String formatted = phoneMsgFmt.format(phoneNumArr);
                return formatted;
            } catch (Exception e){}
        }
        //With area code and possibly extra 1
        if(str.length() == 11){
            L.m("number formatter, number = " + str);
            //check if the first number is one, if it is, sub out the first number
            String testFirst = str.substring(0, 1);
            if(testFirst.equalsIgnoreCase("1")){
                L.m("first number == 1");
                str = str.substring(1);
            }

            try {
                String phoneRawString = str;
                java.text.MessageFormat phoneMsgFmt=new java.text.MessageFormat("({0}) {1}-{2}");
                String[] phoneNumArr={phoneRawString.substring(0, 3),
                        phoneRawString.substring(3,6),
                        phoneRawString.substring(6)};

                String formatted = phoneMsgFmt.format(phoneNumArr);
                return formatted;
            } catch (Exception e){}
        }
        return str;
    }

    /**
     * Use this method to fix URIs that are not usable or are in a format not readable. An example
     * would be one that starts with content://.......... This tries to make a file and when it
     * succeeds, it means that the URI was correct. The main purpose of this method is to handle
     * how some phone makers handle this differently (IE Motorola vs HTC vs Samsung)
     * @param context Context
     * @param selectedImageUri The Uri to work with
     * @return
     */
    public static Uri fixFileUri(Context context, android.net.Uri selectedImageUri){
        File fileToPassAround = null;
        //Attempt 1
        try {
            String selectedImageUriString = selectedImageUri.toString();
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 2
        try {
            String toAppend = "file://";
            String selectedImageUriString = selectedImageUri.toString();
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 3
        try {
            String selectedImageUriString = StringUtilities.getPath(context, selectedImageUri);
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 4
        try {
            String toAppend = "file://";
            String selectedImageUriString = StringUtilities.getPath(context, selectedImageUri);
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 5
        try {
            String selectedImageUriString = StringUtilities.getAbsolutePath(context, selectedImageUri);
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 6
        try {
            String toAppend = "file://";
            String selectedImageUriString = StringUtilities.getAbsolutePath(context, selectedImageUri);
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 7
        try {
            String toAppend = "file:/";
            String selectedImageUriString = StringUtilities.getAbsolutePath(context, selectedImageUri);
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //Attempt 8
        try {
            String toAppend = "file:/";
            String selectedImageUriString = selectedImageUri.toString();
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return StringUtilities.convertStringToAndroidUri(selectedImageUriString);
        } catch (Exception e){}

        //If none have worked by this point, file will likely not work. Maybe permission issues
        return null;
    }

    /**
     * Use this method to fix URIs that are not usable or are in a format not readable. An example
     * would be one that starts with content://.......... This tries to make a file and when it
     * succeeds, it means that the URI was correct. The main purpose of this method is to handle
     * how some phone makers handle this differently (IE Motorola vs HTC vs Samsung)
     * @param context Context
     * @param selectedImageUri The Uri to work with
     * @return
     */
    public static File fixAndBuildFileUri(Context context, android.net.Uri selectedImageUri){
        File fileToPassAround = null;
        //Attempt 1
        try {
            String selectedImageUriString = selectedImageUri.toString();
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return fileToPassAround;
        } catch (Exception e){}

        //Attempt 2
        try {
            String toAppend = "file://";
            String selectedImageUriString = selectedImageUri.toString();
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return fileToPassAround;
        } catch (Exception e){}

        //Attempt 3
        try {
            String selectedImageUriString = StringUtilities.getPath(context, selectedImageUri);
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return fileToPassAround;
        } catch (Exception e){}

        //Attempt 4
        try {
            String toAppend = "file://";
            String selectedImageUriString = StringUtilities.getPath(context, selectedImageUri);
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return fileToPassAround;
        } catch (Exception e){}

        //Attempt 5
        try {
            String selectedImageUriString = StringUtilities.getAbsolutePath(context, selectedImageUri);
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return fileToPassAround;
        } catch (Exception e){}

        //Attempt 6
        try {
            String toAppend = "file://";
            String selectedImageUriString = StringUtilities.getAbsolutePath(context, selectedImageUri);
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return fileToPassAround;
        } catch (Exception e){}

        //Attempt 7
        try {
            String toAppend = "file:/";
            String selectedImageUriString = StringUtilities.getAbsolutePath(context, selectedImageUri);
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return fileToPassAround;
        } catch (Exception e){}

        //Attempt 8
        try {
            String toAppend = "file:/";
            String selectedImageUriString = selectedImageUri.toString();
            selectedImageUriString = toAppend + selectedImageUriString;
            selectedImageUriString = StringUtilities.removeSpaces(selectedImageUriString);
            java.net.URI myUri = new java.net.URI(selectedImageUriString);
            File file = new File(myUri);
            fileToPassAround = file;
            return fileToPassAround;
        } catch (Exception e){}

        //If none have worked by this point, file will likely not work. Maybe permission issues
        return null;
    }

    /**
     * Takes in a String and converts it to a number matching a phone number type. Example would be
     * you send int "ABC", it will return "222" as on a phone dial screen, ABC are all on the 2 key.
     * @param str String to parse
     * @return String of numbers to match converted number
     */
    public static String convertNameToPhoneNumber(String str){
        if(StringUtilities.isNullOrEmpty(str)){
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int strLen = str.length();
        for (int currCharacter = 0; currCharacter < strLen; currCharacter++){
            String currentLetter = null;
            char ch = str.charAt(currCharacter);
            switch(ch)
            {
                case 'A' : case 'B' : case 'C' : currentLetter = "2"; break;
                case 'D' : case 'E' : case 'F' : currentLetter = "3"; break;
                case 'G' : case 'H' : case 'I' : currentLetter = "4"; break;
                case 'J' : case 'K' : case 'L' : currentLetter = "5"; break;
                case 'M' : case 'N' : case 'O' : currentLetter = "6"; break;
                case 'P' : case 'Q' : case 'R' : case 'S' : currentLetter = "7"; break;
                case 'T' : case 'U' : case 'V' : currentLetter = "8"; break;
                case 'W' : case 'X' : case 'Y' : case 'Z' : currentLetter = "9"; break;
            }
            if(currentLetter != null){
                sb.append(currentLetter);
            }
        }

        return sb.toString();
        /*
        Not sure if this code is applicable here. Leaving it out for now

        long number = 0;
        int strLen = str.length();
        for (int currCharacter = 0; currCharacter < strLen; currCharacter++)
        {
            char ch = str.charAt(currCharacter);
            // For A-Z & 0-9, multiply by 10, add the 'char' to number.
            // i.e., Shift existing value to the left by 1 digit, add current 'char' to it
            // Use long instead of int if the string will be longer than max int value (2147483647)
            if (Character.isLetter(ch))
            {
                switch(ch)
                {
                    case 'A' : case 'B' : case 'C' : number *= 10; number += 2; break;
                    case 'D' : case 'E' : case 'F' : number *= 10; number += 3; break;
                    case 'G' : case 'H' : case 'I' : number *= 10; number += 4; break;
                    case 'J' : case 'K' : case 'L' : number *= 10; number += 5; break;
                    case 'M' : case 'N' : case 'O' : number *= 10; number += 6; break;
                    case 'P' : case 'Q' : case 'R' : case 'S' : number *= 10; number += 7; break;
                    case 'T' : case 'U' : case 'V' : number *= 10; number += 8; break;
                    case 'W' : case 'X' : case 'Y' : case 'Z' : number *= 10; number += 9; break;
                }
            }
            else if (Character.isDigit(ch))
            {
                number *= 10; number += Character.getNumericValue(ch);
            }
            else
            {
                System.out.println("Invalid character!");
            }

        } // End of for loop
         */
    }
    /**
     * Takes in a String and converts it to a string list matching a query using a phone keypad. An
     * example would be, send in 728 and it would return a list containing: {"PQRS", "ABC", "TUV"}
     * @param str String to parse into an integer. If it fails int parsing, returns null
     * @param includeBothCases Boolean, if true, it will add both upper and lower cases to string
     *                         list. IE, 728 would return: {"PpQqRrSs","AaBbCc","TtUuVv"}
     * @return List of Strings to match converted number
     */
    public static List<String> convertNumberToStringList(String str, boolean includeBothCases){
        if(StringUtilities.isNullOrEmpty(str)){
            return null;
        }

        List<String> toReturn = new ArrayList<>();
        int strLen = str.length();
        Integer intx = null;
        try {
            intx = Integer.parseInt(str);
        } catch (Exception e){}
        if(intx == null){
            return null;
        }
        for (int currCharacter = 0; currCharacter < strLen; currCharacter++){
            char ch = str.charAt(currCharacter);
            int currentNum = Character.getNumericValue(ch);
            switch(currentNum)
            {
                case 2:
                    if(includeBothCases) {
                        toReturn.add("AaBbCc");
                    } else {
                        toReturn.add("ABC");
                    }
                    break;

                case 3:
                    if(includeBothCases) {
                        toReturn.add("DdEeFf");
                    } else {
                        toReturn.add("DEF");
                    }
                    break;

                case 4:
                    if(includeBothCases) {
                        toReturn.add("GgHhIi");
                    } else {
                        toReturn.add("GHI");
                    }
                    break;

                case 5:
                    if(includeBothCases) {
                        toReturn.add("JjKkLl");
                    } else {
                        toReturn.add("JKL");
                    }
                    break;

                case 6:
                    if(includeBothCases) {
                        toReturn.add("MmNnOo");
                    } else {
                        toReturn.add("MNO");
                    }
                    break;

                case 7:
                    if(includeBothCases) {
                        toReturn.add("PpQqRrSs");
                    } else {
                        toReturn.add("PQRS");
                    }
                    break;

                case 8:
                    if(includeBothCases) {
                        toReturn.add("TtUuVv");
                    } else {
                        toReturn.add("TUV");
                    }
                    break;

                case 9:
                    if(includeBothCases) {
                        toReturn.add("WwXxYyZz");
                    } else {
                        toReturn.add("WXYZ");
                    }
                    break;

                case 0:
                case 1:
                default:
                    break;
            }
        }

        return toReturn;
    }

    /**
     * Checks if a string passed in is numeric (IE pass in "2" and it will return true)
     * @param str String to check against
     * @return Return true if it is numeric, false if it is not
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        final int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
    /**
     * Increments a String (IE, converts a to b)
     * @param str String to convert
     * @return Returns a converted String
     */
    public static String incrementString(@NonNull String str){
        if(StringUtilities.isNullOrEmpty(str)){
            return str;
        }
        if(str.equalsIgnoreCase("#")){
            return "A";
        }
        StringBuilder sb = new StringBuilder();
        for(char c: str.toCharArray()){
            sb.append(++c);
        }
        return sb.toString();
    }

    /**
     * Decrement a String (IE, converts b to a)
     * @param str String to convert
     * @return Returns a converted String
     */
    public static String decrementString(@NonNull String str){
        if(StringUtilities.isNullOrEmpty(str)){
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for(char c: str.toCharArray()){
            sb.append(--c);
        }
        return sb.toString();
    }

    /**
     * Convert a String to a Java URI (Not Uri)
     * @param path
     * @return
     */
    public static java.net.URI convertStringToJavaUri(String path){
        try {
            java.net.URI toReturn = new java.net.URI(path);
            return toReturn;
        } catch (URISyntaxException e){
            return null;
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Convert a String to an Android Uri (Not URI)
     * @param path
     * @return
     */
    public static android.net.Uri convertStringToAndroidUri(String path){
        try {
            android.net.Uri toReturn = Uri.parse(path);
            return toReturn;
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Convert an Android Uri to a String
     * @param uri
     * @return
     */
    public static String convertAndroidUriToString(android.net.Uri uri){
        try {
            return uri.toString();
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Convert a Java URI to a String (Not Uri)
     * @param uri
     * @return
     */
    public static String convertJavaUriToString(java.net.URI uri){
        return uri.toString();
    }

    /**
     * Convert an Android Uri to a Java URI
     * @param uri Android Uri
     * @return Java URI
     */
    public static java.net.URI convertAndroidUriToJavaURI(android.net.Uri uri){
        String ss = StringUtilities.convertAndroidUriToString(uri);
        return StringUtilities.convertStringToJavaUri(ss);
    }

    /**
     * Convert a Java URI to an Android Uri
     * @param uri Java URI
     * @return Android Uri
     */
    public static android.net.Uri convertJavaURIToAndroidUri(java.net.URI uri){
        String ss = StringUtilities.convertJavaUriToString(uri);
        return StringUtilities.convertStringToAndroidUri(ss);
    }

    /**
     * Using code from this link: http://hmkcode.com/android-display-selected-image-and-its-real-path/
     * This method will return the absolute path Android.net.Uri.
     * NOTE!!! THIS DOES NOT SUPPORT API 10 OR BELOW!!! IF YOU NEED TO WORK WITH THAT, CHECK LINK ABOVE
     * @param context Context
     * @param uri Uri to check
     * @return String for the absolute path
     */
    public static String getAbsolutePath(Context context, android.net.Uri uri) {
        if(Build.VERSION.SDK_INT >= 19){
            try {
                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(uri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else{
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                String result = null;

                CursorLoader cursorLoader = new CursorLoader(
                        context,
                        uri, proj, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();

                if(cursor != null){
                    int column_index =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    result = cursor.getString(column_index);
                }
                return result;

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Checks if it is a valid email address. IE, no.com would not pass but bob@gmail.com would.
     * @param email
     * @return
     */
    public static boolean isValidEmail(CharSequence email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            boolean booleanToReturn = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            return booleanToReturn;
        }
    }
    /**
     * Checks if it is a valid email address. Slightly less strict thatn the other one
     * IE, no.com would not pass but bob@gmail.com would.
     * @param email
     * @return
     */
    public static boolean isValidEmailV2(CharSequence email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(Constants.EMAIL_REGEX);
            return (pattern.matcher(email).matches());
        }
    }
    /**
     * Checks if it is a valid phone number (US Formatting only)
     * @param phoneNumberToCheck
     * @return
     */
    public static boolean isValidPhoneNumber(String phoneNumberToCheck) {
        if (StringUtilities.isNullOrEmpty(phoneNumberToCheck)) {
            return false;
        }
        phoneNumberToCheck = phoneNumberToCheck.trim();
        phoneNumberToCheck = StringUtilities.keepNumbersOnly(phoneNumberToCheck);
        try {
            if (phoneNumberToCheck.length() == 10) {
                return true;
            }
        } catch (NullPointerException e){
            return false;
        }
        return false;
    }
    /**
     * Shortens a String to X characters or less
     * @param str String to shorten
     * @param cutAt
     * @return If string is <= 100 in length, returns that, else, shortens to 100 and returns
     */
    public static String shortenToXChar(String str, int cutAt){
        //Check Params first
        if(str == null){
            return "";
        }
        int x = -1;
        x = cutAt;
        if(x == -1){
            return str;
        }

        String return_str = str;
        if(return_str.length() <= cutAt){
            return_str = return_str.trim(); //Cut out whitespace at end or beginning
            return return_str;
        } else {
            str = str.trim(); //Cut out whitespace
            return_str = str.substring(0, cutAt) + "..."; //Shorten to 100 characters and add ellipsis
            return return_str;
        }
    }

    public static String removeSpaces(String str){
        if(str == null){
            return null;
        }
        str.replace(" ", "");
        str = str.trim();
        return  str;
    }

    /**
     * This checks if the password they entered contains an uppercase, lowercase, and a number OR is >16 digits
     * @param password The password being checked
     * @return Boolean, true if it is complicated enough, false if it is not
     */
    public static boolean checkForComplicatedPassword(String password){
        //Valid passed variable
        if(password == null || password.equalsIgnoreCase("")){
            return false;
        }
        password = password.trim(); //Trim it first

        if(password.length()>15) {
            //If someone has a length of 15 on their password, it's good enough dagnabit
            return true;
        } else if(password.length() < 6){
            return false; //Needs to be longer than or = to 6 characters
        } else {
            //Check for letters, then numbers, then special characters
            Matcher matcher;
            Pattern pattern;
            pattern = Pattern.compile(Constants.PASSWORD_PATTERN);
            matcher = pattern.matcher(password);
            boolean hasLetterAndNumber = matcher.matches();

            if(hasLetterAndNumber){
                //It is ok, has letters and numbers
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Set the textview with HTML style text IE, <b>Words</b>
     * @param viewToSet The view to set
     * @param stringToSet The string to set
     * @param <T> T extends TextView
     */
    public static <T extends TextView> void setTextWithHtml(T viewToSet, String stringToSet){
        if(viewToSet == null || StringUtilities.isNullOrEmpty(stringToSet)){
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            viewToSet.setText(Html.fromHtml(stringToSet, Html.FROM_HTML_MODE_LEGACY));
        } else {
            viewToSet.setText(Html.fromHtml(stringToSet));
        }
    }
    public static <T extends TextView> void setTextWithHtml(T viewToSet, String stringToSet,
                                                            Boolean hasUrlLinks){
        if(hasUrlLinks == null){
            hasUrlLinks = false;
        }

        if(viewToSet == null || StringUtilities.isNullOrEmpty(stringToSet)){
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            viewToSet.setText(Html.fromHtml(stringToSet, Html.FROM_HTML_MODE_LEGACY));
        } else {
            viewToSet.setText(Html.fromHtml(stringToSet));
        }
        if(hasUrlLinks){
            viewToSet.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static String encodeURIStringWithRegex(String uriToEncode){
        try {
            //Pattern pattern = Pattern.compile(Constants.WEB_URL_ENCODING);
            //Matcher matcher = pattern.matcher(uriToEncode);

            //TEST
            uriToEncode = uriToEncode.replaceAll(Constants.WEB_URL_ENCODING, uriToEncode);
            L.m("testing code = " + uriToEncode);

            return uriToEncode;
        } catch (Exception e){
            return null;
        }
    }
    public static String encodeURIStringWithRegex(android.net.Uri uriToEncode){
        //Convert to String and pass to overloaded method
        try {
            return encodeURIStringWithRegex(convertAndroidUriToString(uriToEncode));
        } catch (Exception e){
            return null;
        }
    }
    public static String encodeURIStringWithRegex(java.net.URI uriToEncode){
        //Convert to String and pass to overloaded method
        try {
            return encodeURIStringWithRegex(convertJavaUriToString(uriToEncode));
        } catch (Exception e){
            return null;
        }
    }
    /**
     * Simple method to remove all spaces, parentheses, and hyphens
     * @param input String to adjust
     * @return
     */
    public static String formatPhoneRemoveFormatting(String input){
        input = input.replace("(", "");
        input = input.replace(")", "");
        input = input.replace("-", "");
        input = input.replace(" ", "");
        input = input.trim();
        return input;
    }

    /**
     * Formats by adding a hyphen for every 4 numbers (IE like a credit card)
     * @param s Charsequence being altered.
     * @return Return an altered String with hyphens in it
     */
    public static String formatNumbersAsCreditCard(CharSequence s) {
        int groupDigits = 0;
        String tmp = "";
        for (int i = 0; i < s.length(); ++i) {
            tmp += s.charAt(i);
            ++groupDigits;
            if (groupDigits == 4) {
                if(groupDigits == 16){
                } else {
                    tmp += "-";
                }
                groupDigits = 0;
            }
        }
        if(tmp.length() == 20){
            tmp = tmp.substring(0, tmp.length()-1); //Get rid of last digit
        }
        return tmp;
    }

    /**
     * Checks if the passed String is null or empty
     * @param t object to check
     * @return Boolean, true if it is null or empty, false if it is not.
     */
    public static <T> boolean isNullOrEmpty(T t){
        if(t == null){
            return true;
        }
        String str = t.toString();
        if(str.isEmpty()){
            return true;
        }
        if(str.length() == 0){
            return true;
        }
        return false;
    }

    /**
     * Checks if the passed String is null or empty
     * @param str String to check
     * @return Boolean, true if it is null or empty, false if it is not.
     */
    public static boolean isNullOrEmpty(String str){
        if(str == null){
            return true;
        }
        if(str.isEmpty()){
            return true;
        }
        if(str.length() == 0){
            return true;
        }
        if(str.equalsIgnoreCase(" ")){
            return true;
        }
        return false;
    }
    /**
     * Builds a String via the passed String array. If the individual String is null or empty,
     * it skips it. If the delimiter is empty, skips that too
     * @param delimiter Delimiter to use (IE , or a space or _ or / or | )
     * @param args String array to use
     * @return Fully completed and written String
     */
    public static String buildAStringFromUnknowns(String delimiter, String[] args){
        StringBuilder sb = new StringBuilder();
        sb.append(""); //So that it will always return something
        try {
            for (int i = 0; i < args.length; i++) {
                String str = args[i];

                //Boot out nulls and blanks
                if (str == null) {
                    continue;
                }
                if (str.equalsIgnoreCase("")) {
                    continue;
                }

                sb.append(str);
                if (i < args.length - 1) {
                    boolean checkNext = true;
                    try {
                        String str1 = args[(i+1)];
                        if(str1 == null){
                            checkNext = false;
                        } else {
                            if (str1.isEmpty()) {
                                checkNext = false;
                            }
                        }
                    } catch (Exception e){

                    }
                    if(checkNext) {
                        //Format via delimiter
                        if (delimiter != null) {
                            sb.append(delimiter);
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * Convert an input stream to a STring
     * @param is InputStream to convert
     * @return
     */
    public static String convertInputStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder out = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append("\r\n");
            }
            return out.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if any of the Strings passed via an array are null
     * @param args String array to check
     * @return boolean, true if any are null
     */
    public static boolean anyNullsInStrings(String[] args){
        boolean anyNuls = false;
        for(String str : args){
            if(str == null){
                anyNuls = true;
            }
        }
        return anyNuls;
    }
    /**
     * Checks if any of the Strings passed via an array are null or are empty ("");
     * @param args String array to check
     * @return boolean, true of any are null or are empty
     */
    public static boolean anyNullsOrEmptyInStrings(String[] args){
        boolean anyNuls = false;
        for(String str : args){
            if(str == null){
                anyNuls = true;
            } else {
                if(str.isEmpty()){
                    anyNuls = true;
                }
            }
        }
        return anyNuls;
    }

    /**
     * From - http://stackoverflow.com/questions/19985286/convert-content-uri-to-actual-path-in-android-4-4
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }



}
