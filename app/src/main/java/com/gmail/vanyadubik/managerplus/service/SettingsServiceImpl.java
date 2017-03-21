package com.gmail.vanyadubik.managerplus.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.gmail.vanyadubik.managerplus.common.Consts;
import com.gmail.vanyadubik.managerplus.utils.PropertyUtils;

import java.io.IOException;
import java.util.Properties;

public class SettingsServiceImpl implements SettingsService {

    private Context context;

    public SettingsServiceImpl(Context context) {
        this.context = context;
    }

    public String readData(String key) {
        SharedPreferences sharedPref = context.getSharedPreferences("SettingsApp", Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public void saveData(String key, String value) {
        if (key == null || key.isEmpty()) {
            return;
        }
        if (value == null) {
            return;
        }
        SharedPreferences sharedPref = context.getSharedPreferences("SettingsApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void clearData() {
        SharedPreferences.Editor editor = context.getSharedPreferences("SettingsApp", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public String readDataProperties(String key) {
        try {
            Properties properties = PropertyUtils.getProperties(Consts.APPLICATION_PROPERTIES, context);
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read auth properties", e);
        }
    }
}
