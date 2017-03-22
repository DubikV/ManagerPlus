package com.gmail.vanyadubik.managerplus.common;

public class Consts {

    public final static String TAGLOG = "MobileManager";
    public final static String TAGLOG_PHONE = "MobileManager_Phone";
    public final static String TAGLOG_GPS = "MobileManager_GPS";

    public static final String APPLICATION_PROPERTIES = "application.properties";

    // Synchronization
    public final static int STATUS_STARTED_SYNC = 0;
    public final static int STATUS_FINISHED_SYNC = 1;
    public final static int STATUS_ERROR_SYNC = -1;
    public final static int CONNECT_TIMEOUT_SECONDS_RETROFIT = 180;

    // Name parameters
    public final static String LOGIN = "mLogin";
    public final static String PASSWORD = "mPassword";
    public final static String SERVER = "mServer";

    // GPS
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // meters
    public static final long MIN_TIME_BW_UPDATES = 60; // seconds
    public static final long MIN_TIME_WRITE_TRACK = 5; // seconds

}