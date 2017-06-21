package com.gmail.vanyadubik.managerplus.gps.service;

public enum TypeServiceGPS{

    SERVICE_GPS_ANDROID(1,"Android Location", ServiceGpsTrackingAndroid.class),
    SERVICE_GPS_ANDROID_PLAY(2, "Android Play Location", ServiceGpsTrackingAndroidPlay.class),
    SERVICE_GPS_GOOGLE_PLAY(3, "Google Play Location", ServiceGpsTrackingGooglePlay.class);

    private int index;
    private String name;
    private Class<?> serviceClass;

    private TypeServiceGPS(int index, String name, Class<?> serviceClass) {
        this.index = index;
        this.name = name;
        this.serviceClass = serviceClass;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public String getNamebyIndex(int index) {
        if(index == 1){
            return SERVICE_GPS_ANDROID.getName();
        }else if(index == 2){
            return SERVICE_GPS_ANDROID_PLAY.getName();
        }else if(index == 3){
            return SERVICE_GPS_GOOGLE_PLAY.getName();
        }
        return "";
    }

    public Class<?> getClassbyIndex(int index) {
        if(index == 1){
            return SERVICE_GPS_ANDROID.getServiceClass();
        }else if(index == 2){
            return SERVICE_GPS_ANDROID_PLAY.getServiceClass();
        }else if(index == 3){
            return SERVICE_GPS_GOOGLE_PLAY.getServiceClass();
        }
        return null;
    }

}

