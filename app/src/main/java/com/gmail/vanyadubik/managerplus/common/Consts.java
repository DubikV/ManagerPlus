package com.gmail.vanyadubik.managerplus.common;

import com.google.android.gms.location.LocationRequest;

public class Consts {

    public final static String TAGLOG = "ManagerPlus";
    public final static String TAGLOG_PHONE = "ManagerPlus_Phone";
    public final static String TAGLOG_GPS = "ManagerPlus_GPS";
    public final static String TAGLOG_SYNC = "ManagerPlus_Sync";
    public final static String TAGLOG_SYNC_TRACK = "ManagerPlus_Sync_Track";
    public final static String TAGLOG_TASK = "ManagerPlus_Task_schedure";

    public static final String APPLICATION_PROPERTIES = "application.properties";

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
    public final static String DEVELOP_MODE = "developMode";
    public final static String MIN_CURRENT_ACCURACY = "min_current_accuracy";

    // GPS
    public static final long MIN_DISTANCE_WRITE_TRACK = 20; //15 meters
    public static final long MIN_TIME_WRITE_TRACK = 10; // 5seconds
    public static final long MIN_TIME_SYNK_TRACK = 15*60; // seconds
    public static final double  MAX_COEFFICIENT_CURRENCY_LOCATION = 20.0; //100.0
    public static final double  MIN_SPEED_WRITE_LOCATION = 0.005; //
    public static final int TYPE_PRIORITY_CONNECTION_GPS = LocationRequest.PRIORITY_HIGH_ACCURACY;

    // Map
    public static final int WIDTH_POLYLINE_MAP = 20;
    public static final long MIN_DISTANCE_LOCATION_MAP = 20; // 10 meters
    public static final long MIN_TIME_LOCATION_MAP = 10; // seconds
    public static final long MIN_DISTANCE_LOCATION_MAP_CHECK_NAVIGATION = 100;// meters
    public static final double  MIN_SPEED_MAP_SET_ZOOM = 3.0; //
    public static final float  MIN_ZOOM_MAP = 18; //18
    public static final float  MAX_ZOOM_MAP = 18; //21
    public static final float  TILT_CAMERA_MAP = 67;
    public static final float  MIN_ZOOM_TITLE_MAP = 80;
    public static final float  DIVISION_ZOOM_MAP = 1.0f; //0.5f
    public static final int  TIME_MAP_ANIMATE_CAMERA = 1000;

    // notifications
    public static final int DEFAULT_NOTIFICATION_GPS_TRACER_ID = 101;
    public static final int DEFAULT_NOTIFICATION_SYNC_TRACER_ID = 102;

    //Tasks
    public final static int GPS_TRACK_SERVISE_JOB_ID = 1;
    public final static int GPS_SYNK_SERVISE_JOB_ID = 2;

    public final static String CLEAR_DATE = "00.00.0000 00:00";

}
