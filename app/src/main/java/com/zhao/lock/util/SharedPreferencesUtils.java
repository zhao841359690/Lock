package com.zhao.lock.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhao.lock.app.BaseApp;

public class SharedPreferencesUtils {
    private static SharedPreferencesUtils sInstance;
    private SharedPreferences mPreferences;

    public static synchronized SharedPreferencesUtils getInstance() {
        if (sInstance == null) {
            sInstance = new SharedPreferencesUtils();
        }
        return sInstance;
    }

    private SharedPreferencesUtils() {
        mPreferences = BaseApp.getContext().getSharedPreferences("my_shared_preference", Context.MODE_PRIVATE);
    }

    public void setToken(String value) {
        mPreferences.edit().putString("token", value).apply();
    }

    public String getToken() {
        return mPreferences.getString("token", null);
    }
}
