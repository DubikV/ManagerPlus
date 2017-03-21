package com.gmail.vanyadubik.managerplus.service;


public interface SettingsService {

    String readData(String key);

    void saveData(String key, String value);

    void clearData();
}