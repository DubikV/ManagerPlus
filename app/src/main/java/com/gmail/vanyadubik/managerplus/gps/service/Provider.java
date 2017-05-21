package com.gmail.vanyadubik.managerplus.gps.service;


import android.location.LocationManager;

public enum Provider {
    GPS(1, LocationManager.GPS_PROVIDER, "android.hardware.location.gps"),
    NETWORK(2, LocationManager.NETWORK_PROVIDER, "android.hardware.location.network"),
    PASSIVE(3, LocationManager.PASSIVE_PROVIDER, "android.hardware.location");

    public static final String PROVIDER_GPS = "gps";
    public static final String PROVIDER_NETWORK = "network";
    private String featureName;
    private int index;
    private String name;

    private Provider(int index, String name, String featureName) {
        this.index = index;
        this.name = name;
        this.featureName = featureName;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public String getFeatureName() {
        return this.featureName;
    }

    public static Provider FromIndex(int index) {
        for (Provider item : values()) {
            if (item.getIndex() == index) {
                return item;
            }
        }
        return PASSIVE;
    }

    public static Provider FromName(String name) {
        for (Provider item : values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return PASSIVE;
    }
}