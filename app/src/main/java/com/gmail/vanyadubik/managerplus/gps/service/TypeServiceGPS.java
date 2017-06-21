package com.gmail.vanyadubik.managerplus.gps.service;

public enum TypeServiceGPS{

    SERVICE_GPS_ANDROID(1,"Android Location"),
    SERVICE_GPS_ANDROID_PLAY(2, "Android Play Location"),
    SERVICE_GPS_GOOGLE_PLAY(3, "Google Play Location");

    private int index;
    private String name;

    private TypeServiceGPS(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public static String getNamebyIndex(int index) {
        if(index == 1){
            return SERVICE_GPS_ANDROID.getName();
        }else if(index == 2){
            return SERVICE_GPS_ANDROID_PLAY.getName();
        }else if(index == 3){
            return SERVICE_GPS_GOOGLE_PLAY.getName();
        }
        return "";
    }

    public static int getIndexbyName(String name) {
        if(name.equals(SERVICE_GPS_ANDROID.getName())){
            return SERVICE_GPS_ANDROID.getIndex();
        }else if(name.equals(SERVICE_GPS_ANDROID_PLAY.getName())){
            return SERVICE_GPS_ANDROID_PLAY.getIndex();
        }else if(name.equals(SERVICE_GPS_GOOGLE_PLAY.getName())){
            return SERVICE_GPS_GOOGLE_PLAY.getIndex();
        }
        return 0;
    }
}

