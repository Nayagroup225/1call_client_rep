package com.block.phonecall.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreference {
    private static final String DB_NAME = "pch";
    private static AppSharedPreference appSharedPreference;
    private static SharedPreferences preferences;

    private AppSharedPreference() {
    }

    public static AppSharedPreference getInstance(Context context) {
        if (preferences == null) {
            appSharedPreference = new AppSharedPreference();
            preferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        }
        return appSharedPreference;
    }

    public String getDeviceId() {
        return preferences.getString("device_id", "");
    }

    public void setDeviceId(String deviceId) {
        preferences.edit().putString("device_id", deviceId).apply();
    }

    public void setState(String state) {
        preferences.edit().putString("state", state).apply();
    }

    public String getState() {
        return preferences.getString("state", "0");
    }

    public void setBlockNumber(String blockNumber) {
        preferences.edit().putString("block_number", blockNumber).apply();
    }

    public String getBlockNumber() {
        return preferences.getString("block_number", "12121");
    }

    public void setLimitNumberArray(String limitNumber) {
        preferences.edit().putString("limit_number", limitNumber).apply();
    }

    public String getLimitNumberArray() {
        return preferences.getString("limit_number", "[]");
    }

}
