package com.app.mycan1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefManager {

    private static final String PREF_ID = "id";
    private static final String PREF_NAME = "name";
    private static final String PREF_PHONE = "phone";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_FAMILY_ID = "family_id";


    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    static public void setUserId(Context ctx , int id){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_ID , id);
        editor.commit();
    }

    static public int getUserId(Context ctx){
        return getSharedPreferences(ctx).getInt(PREF_ID , 0);
    }

    static public void setName(Context ctx , String name){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_NAME , name);
        editor.commit();
    }

    static public String getName(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_NAME , null);
    }

    static public void setPhone(Context ctx , String phone){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PHONE , phone);
        editor.commit();
    }

    static public String getPhone(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_PHONE , null);
    }

    static public void setEmail(Context ctx , String email){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL , email);
        editor.commit();
    }

    static public String getEmail(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_EMAIL , null);
    }

    static public void setPassword(Context ctx , String password){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PASSWORD , password);
        editor.commit();
    }

    static public String getPassword(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_PASSWORD , null);
    }

    static public void setFamilyId(Context ctx , String familyId){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_FAMILY_ID , familyId);
        editor.commit();
    }

    static public String getFamilyId(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_FAMILY_ID , "");
    }



    static public void logout(Context ctx){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.apply();
    }


}