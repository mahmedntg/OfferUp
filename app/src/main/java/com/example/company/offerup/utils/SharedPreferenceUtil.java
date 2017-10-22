package com.example.company.offerup.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mohamed Sayed on 10/21/2017.
 */

public class SharedPreferenceUtil {
    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String productKey = "product";
    private static SharedPreferenceUtil sharedPreferenceUtil;
    private Context context;

    public static SharedPreferenceUtil getInstance(Context context) {
        if (sharedPreferenceUtil == null) {
            sharedPreferenceUtil = new SharedPreferenceUtil(context);
        }
        return sharedPreferenceUtil;
    }

    private SharedPreferenceUtil() {
    }

    private SharedPreferenceUtil(Context context) {
        this.context = context;
    }

    public SharedPreferences getSharedPreferences() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings;
    }

    public void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences.contains(productKey)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }
    }

    public boolean isSharedPreferencesExists() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences.contains(productKey)) {
            return true;
        } else {
            return false;
        }
    }
}
