package com.unclekong.ebookdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class StoreageData extends Activity {
    Context context;
    SharedPreferences.Editor settings = null;
    SharedPreferences uiState = null;
    public StoreageData(Context context) {
        this.context = context;
    }

    public void setDate(String spkey, String key, String value) {
        uiState = context.getSharedPreferences(spkey, 0);
        settings = uiState.edit();
        settings.putString(key, value).commit();
    }

    public void setDateString(String spkey, String vaule) {
        uiState = context.getSharedPreferences(spkey, 0);
        settings = uiState.edit();
        settings.putString(spkey, vaule).commit();
    }

    public void setDateInt(String spkey, int vaule) {
        uiState = context.getSharedPreferences(spkey, 0);
        settings = uiState.edit();
        settings.putInt(spkey, vaule).commit();
    }

    public Map<?, ?> getDate(String spkey) {
        uiState = context.getSharedPreferences(spkey, 0);
        return uiState.getAll();
    }

    public String getDateString(String spkey) {
        uiState = context.getSharedPreferences(spkey, 0);
        return uiState.getString(spkey, null);
    }

    public int getDateInt(String spkey) {
        uiState = context.getSharedPreferences(spkey, 0);
        return uiState.getInt(spkey, -1);
    }

    public Map<String, ?> getDateIntAll(String spkey) {
        uiState = context.getSharedPreferences(spkey, 0);
        return uiState.getAll();
    }
}
