package com.gmail.vanyadubik.managerplus.common;

import com.google.android.gms.location.LocationRequest;

public class Consts {

    public final static String TAGLOG = "ManagerPlus";
    public final static String TAGLOG_PHONE = "ManagerPlus_Phone";
    public final static String TAGLOG_GPS = "ManagerPlus_GPS";
    public final static String TAGLOG_SYNC = "ManagerPlus_Sync";
    public final static String TAGLOG_SYNC_TRACK = "ManagerPlus_Sync_Track";
    public final static String TAGLOG_TASK = "ManagerPlus_Task_schedure";
    public final static String TAGLOG_IMAGE = "ManagerPlus_Task_image";
    public final static String TAGLOG_G_CALENDAR = "ManagerPlus_G_Calendar";

    public static final String APPLICATION_PROPERTIES = "application.properties";
    public final static String DIRECTORY_APP = "ManagerPlusDir";
    public final static String DIR_PICTURES = "Pictures";

    // Synchronization
    public final static int STATUS_STARTED_SYNC = 0;
    public final static int STATUS_FINISHED_SYNC = 1;
    public final static int STATUS_ERROR_SYNC = -1;
    public final static int CONNECT_TIMEOUT_SECONDS_RETROFIT = 180;
    public final static int MIN_SIZE_TRACK_LIST_UPLOAD = 1000;

    // Name parameters
    public final static String LOGIN = "mLogin";
    public final static String PASSWORD = "mPassword";
    public final static String SERVER = "mServer";
    public final static String MIN_TIME_SYNK_TRACK_NAME = "minTimeSyncTrack";
    public final static String USING_SYNK_TRACK = "usingSyncTrack";
    public final static String DEVELOP_MODE = "developMode";
    public final static String USING_GPSTRACKING = "usingGpsTrackin";
    public final static String MIN_CURRENT_ACCURACY = "gpsTrackingMinAcuuracy";

    // GOOGLE
    public final static String GOOGLE_EMAIL_PARAM = "com.google";

    // GPS
    public static final long MIN_DISTANCE_WRITE_TRACK = 20; //30 meters  //20 - impression  40???
    public static final long MIN_TIME_WRITE_TRACK = 5; // 10seconds //5 - impression
    public static final long MIN_SPEED_WRITE_LOCATION = MIN_TIME_WRITE_TRACK/2; //
    public static final long MIN_TIME_SYNK_TRACK = 15*60; // seconds
    public static final double  MAX_COEFFICIENT_CURRENCY_LOCATION = 50.0; //100.0 //50.0 - impression
    public static final int TYPE_PRIORITY_CONNECTION_GPS = LocationRequest.PRIORITY_HIGH_ACCURACY;

    // Map
    public static final long MIN_DISTANCE_MAP = 5;
    public static final int WIDTH_POLYLINE_MAP = 20;
    public static final long MIN_DISTANCE_LOCATION_MAP_CHECK_NAVIGATION = 100;// meters
    public static final float  TILT_CAMERA_MAP = 67;
    public static final float  MIN_ZOOM_TITLE_MAP = 80;
    public static final int  TIME_MAP_ANIMATE_CAMERA = 1000;

    // notifications
    public static final int DEFAULT_NOTIFICATION_GPS_TRACER_ID = 101;
    public static final int DEFAULT_NOTIFICATION_SYNC_TRACER_ID = 102;

    //Tasks
    public final static int GPS_TRACK_SERVISE_JOB_ID = 1;
    public final static int GPS_SYNK_SERVISE_JOB_ID = 2;

    public final static String CLEAR_DATE = "00.00.0000 00:00";

    //Google Account
    public final static String GOOGLE_ACC_CONNECTED = "google_account_connected";
    public final static String GOOGLE_ACC_ID = "google_account_id";
    public final static String GOOGLE_ACC_URL = "google_account_url";
    public final static String GOOGLE_ACC_EMAIL = "google_account_email";
    public final static String GOOGLE_ACC_IMAGE = "google_account_image";
    public final static String GOOGLE_ACC_NAME = "google_account_name";
    public final static String GOOGLE_ACC_CONNECTED_VISITS = "google_account_connected_visits";

}
