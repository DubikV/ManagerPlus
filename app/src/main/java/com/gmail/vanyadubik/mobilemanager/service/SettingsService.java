package com.gmail.vanyadubik.mobilemanager.service;


public interface SettingsService {

    String readData(String key);

    void saveData(String key, String value);

    void clearData();
}