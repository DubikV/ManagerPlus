package com.gmail.vanyadubik.managerplus.gps.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import com.gmail.vanyadubik.managerplus.utils.ServiceUtils;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEVELOP_MODE;

public class GpsTracking {

    public static final String PREF_INTERVAL = "gpsTrackingInterval";
    public static final String PREF_TIME_START = "gpsTrackingTimeStart";
    public static final String PREF_TIME_END = "gpsTrackingTimeEND";
    public static final String PREF_DAYS = "gpsTrackingDays";
    public static final String PREF_ACCURACY = "gpsTrackingMinAcuuracy";
    public static final String RENEW_PREFERENCES = "initializer_have_new_preferences";
    public static final String SET_PREFERENCES = "send_preferences_to_service";
    public static final String CHECK_PREFERENCES = "initializer_wait_for_check_preferences";
    public static final String PREF_TYPE_SERVICE = "type_gps_service";
    static final String BOOLEAN_PARAMS = "boolean_shared_preferences";
    static final String INITIALIZER_ACTION = "gps.service.GpsTracking.initializerBroadcaster";
    static final String LONG_PARAMS = "long_shared_preferences_from_service";
    static final String NUMERIC_PARAMS = "numeric_shared_preferences";
    static final String DOUBLE_PARAMS = "double_shared_preferences";
    static final String RECEIVER_ACTION = "brc_receiver_action";
    static final String SERVICE_ACTION = "gps.service.serviceGpsTracking.serviceBroadcaster";
    static final String STRING_PARAMS = "string_shared_preferences";
    static final String STRING_PARAMS_FROM_SERVICE = "string_shared_preferences_from_service";
    static final String SERVICE_GPS_NOTIFY = "fromServiceGpsTrackingNotify";

    @Inject
    ServiceUtils serviceUtils;

    private final String LAST_DATE_KEY;
    private final String LAST_LATITUDE_KEY;
    private final String LAST_LOCATIONSOURCE_KEY;
    private final String LAST_LONGITUDE_KEY;
    private final String PREF_ENABLE;
    private final String PREF_GPSTIME;
    private final String PREF_ISLOCATIONSOURCE;
    private final String PREF_LOCATIONSOURCE;
    private final String PREF_PASSIVECONNECTION;
    private boolean[] _booleanParams;
    private Context _context;
    private int[] _integerParams;
    private double[] _doubleParams;
    private boolean _isReceiverRegistered;
    private boolean _isStarted;
    private ArrayList<String> _stringParams;
    private BroadcastReceiver gpsTrackingReceiver;

    protected class GpsData {
        long _date;
        double _latitude;
        int _locationSource;
        double _longitude;

        GpsData(Context context) {
            _date = SharedStorage.getLong(context, LAST_DATE_KEY, 0);
            _longitude = Double.parseDouble(SharedStorage.getString(context, LAST_LONGITUDE_KEY, "0"));
            _latitude = Double.parseDouble(SharedStorage.getString(context, LAST_LATITUDE_KEY, "0"));
            _locationSource = Integer.parseInt(SharedStorage.getString(context, LAST_LOCATIONSOURCE_KEY, "0"));
        }
    }

    enum booleanPrefs {
        GPS_TIME(0),
        LOCATION_SOURCE(1),
        PASSIVE_CONNECTION(2),
        DEVELOP_MODE(3),;

        private int _prefId;

        private booleanPrefs(int prefId) {
            _prefId = prefId;
        }

        public int getID() {
            return _prefId;
        }
    }

    enum integerPrefs {
        TIMESTART(0),
        TIMEEND(1),
        INTERVAL(2),
        DAYS(3),
        INDEX_LOCATION_SOURCE(4),
        TYPE_SERVICE(5);

        private int _prefId;

        private integerPrefs(int prefId) {
            _prefId = prefId;
        }

        public int getID() {
            return _prefId;
        }
    }

    enum doublePrefs {
        ACCURY(0);

        private int _prefId;

        private doublePrefs(int prefId) {
            _prefId = prefId;
        }

        public int getID() {
            return _prefId;
        }
    }

    enum serviceStringPrefs {
        LATITUDE(0),
        LONGTITUDE(1),
        LOCATION_SOURCE(2);

        private int _prefId;

        private serviceStringPrefs(int prefId) {
            _prefId = prefId;
        }

        public int getID() {
            return _prefId;
        }
    }

    enum stringPrefs {
        FILE(0),
        PPC_GUID(1),
        ERP_ID(2);

        private int _prefId;

        private stringPrefs(int prefId) {
            _prefId = prefId;
        }

        public int getID() {
            return _prefId;
        }
    }

    public GpsTracking(Context context) {
        PREF_ENABLE = "gpsTrackingEnable";
        PREF_LOCATIONSOURCE = "LocationSource";
        PREF_GPSTIME = "gpsTrackingGpsTime";
        PREF_ISLOCATIONSOURCE = "IsWriteLocationSource";
        PREF_PASSIVECONNECTION = "gpsTrackingIsPassiveConnection";
        LAST_DATE_KEY = "last_date_key";
        LAST_LATITUDE_KEY = "last_latitude_key";
        LAST_LONGITUDE_KEY = "last_longitude_key";
        LAST_LOCATIONSOURCE_KEY = "last_locationsource_key";
        _isStarted = false;
        _isReceiverRegistered = false;
        _integerParams = new int[7];
        _doubleParams = new double[2];
        _booleanParams = new boolean[5];
        _stringParams = new ArrayList();
        _context = context;
    }

    private Context getContext() {
        return _context;
    }

    private boolean isSupported() {
        PackageManager packageManager = getContext().getPackageManager();
        boolean hasSystemFeatureLocation = packageManager.hasSystemFeature(
                Provider.FromIndex(SharedStorage.getInteger(getContext(), PREF_LOCATIONSOURCE, 1)).getFeatureName());
        boolean gpsLocation = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        boolean networkLocation = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK);
        if ((gpsLocation || networkLocation) && hasSystemFeatureLocation) {
            return true;
        }
        return false;
    }

    public void sendNewPreferences(String intentAction) {
        readGpsTrackingSettings(true);
        Intent intent = new Intent(SERVICE_ACTION);
        intent.putExtra(NUMERIC_PARAMS, _integerParams);
        intent.putExtra(DOUBLE_PARAMS, _doubleParams);
        intent.putExtra(BOOLEAN_PARAMS, _booleanParams);
        intent.putExtra(STRING_PARAMS, _stringParams);
        intent.putExtra(RECEIVER_ACTION, intentAction);
        getContext().sendBroadcast(intent);
    }

    private void setReceiver() {
        gpsTrackingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setLastGpsData(intent.getLongExtra(GpsTracking.LONG_PARAMS, 0),
                        intent.getStringArrayListExtra(GpsTracking.STRING_PARAMS_FROM_SERVICE));
            }
        };

        getContext().registerReceiver(gpsTrackingReceiver, new IntentFilter(INITIALIZER_ACTION));
        _isReceiverRegistered = true;
    }

    public boolean startGpsTracking() {
        if (!isSupported() || _isStarted) {
            return false;
        }
        _isStarted = true;
        SharedStorage.setBoolean(getContext(), PREF_ENABLE, Boolean.valueOf(true));
        setReceiver();
        getContext().startService(new Intent(getContext(), ServiceGpsTracking.class));
        SystemClock.sleep(100);
        sendNewPreferences(SET_PREFERENCES);
        return true;
    }

    public void stopGpsTracking() {
        _isStarted = false;
        SharedStorage.setBoolean(getContext(), PREF_ENABLE, Boolean.valueOf(false));
        if (_isReceiverRegistered) {
            _isReceiverRegistered = false;
            getContext().unregisterReceiver(gpsTrackingReceiver);
        }
        getContext().stopService(new Intent(getContext(), ServiceGpsTracking.class));
    }

    public int getGpsTrackingStatus() {
        return SharedStorage.getBoolean(getContext(), PREF_ENABLE, false) ? 1 : 0;
    }

    private void setLastGpsData(long datetime, ArrayList<String> stringParams) {
        SharedStorage.setLong(getContext(), LAST_DATE_KEY, datetime);
        SharedStorage.setString(getContext(), LAST_LATITUDE_KEY,
                (String) stringParams.get(serviceStringPrefs.LATITUDE.getID()));
        SharedStorage.setString(getContext(), LAST_LONGITUDE_KEY,
                (String) stringParams.get(serviceStringPrefs.LONGTITUDE.getID()));
        SharedStorage.setString(getContext(), LAST_LOCATIONSOURCE_KEY,
                (String) stringParams.get(serviceStringPrefs.LOCATION_SOURCE.getID()));
    }

    protected GpsData getLastGpsData() {
        if (SharedStorage.getLong(getContext(), LAST_DATE_KEY, 0) == 0) {
            return null;
        }
        return new GpsData(getContext());
    }

    private void writeGpsTrackingSettings(int interval, int timeStart, int timeEnd, int days, boolean bGpsTime,
                                          int period, int locationSource, boolean bLocationSource,
                                          boolean passiveConnection) {
        boolean isUpdated = false;
        if (interval != SharedStorage.getInteger(getContext(), PREF_INTERVAL, 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), PREF_INTERVAL, interval);
        }
        if (timeStart != SharedStorage.getInteger(getContext(), PREF_TIME_START, 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), PREF_TIME_START, timeStart);
        }

        if (timeEnd != SharedStorage.getInteger(getContext(), PREF_TIME_END, 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), PREF_TIME_END, timeEnd);
        }

        if (days != SharedStorage.getInteger(getContext(), PREF_DAYS, 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), PREF_DAYS, days);
        }
        if (locationSource != SharedStorage.getInteger(getContext(), PREF_LOCATIONSOURCE, 1)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), PREF_LOCATIONSOURCE, locationSource);
        }
        if (bGpsTime != SharedStorage.getBoolean(getContext(), PREF_GPSTIME, false)) {
            isUpdated = true;
            SharedStorage.setBoolean(getContext(), PREF_GPSTIME, Boolean.valueOf(bGpsTime));
        }
        if (bLocationSource != SharedStorage.getBoolean(getContext(), PREF_ISLOCATIONSOURCE, false)) {
            isUpdated = true;
            SharedStorage.setBoolean(getContext(), PREF_ISLOCATIONSOURCE, Boolean.valueOf(bLocationSource));
        }
        if (passiveConnection != SharedStorage.getBoolean(getContext(), PREF_PASSIVECONNECTION, false)) {
            isUpdated = true;
            SharedStorage.setBoolean(getContext(), PREF_PASSIVECONNECTION, Boolean.valueOf(passiveConnection));
        }
        if (_isStarted && isUpdated) {
            sendNewPreferences(RENEW_PREFERENCES);
        }
        if (_isStarted && !isUpdated) {
            sendNewPreferences(CHECK_PREFERENCES);
        }
    }

    private void readGpsTrackingSettings(boolean isRenew) {
        int interval = SharedStorage.getInteger(getContext(), PREF_INTERVAL, 0);
        int timeStart = SharedStorage.getInteger(getContext(), PREF_TIME_START, 0);
        int timeEnd = SharedStorage.getInteger(getContext(), PREF_TIME_END, 0);
        int days = SharedStorage.getInteger(getContext(), PREF_DAYS, 0);
        int indexLocationSource = SharedStorage.getInteger(getContext(), PREF_LOCATIONSOURCE, 1);
        int typeService = SharedStorage.getInteger(getContext(), PREF_TYPE_SERVICE, 0);
        double accury = SharedStorage.getDouble(getContext(), PREF_ACCURACY, 0.0);
        boolean bGpsTime = SharedStorage.getBoolean(getContext(), PREF_GPSTIME, true);
        boolean bIsLocationSource = SharedStorage.getBoolean(getContext(), PREF_ISLOCATIONSOURCE, true);
        boolean passiveConnection = SharedStorage.getBoolean(getContext(), PREF_PASSIVECONNECTION, true);
        boolean devMode = SharedStorage.getBoolean(getContext(), DEVELOP_MODE, false);
        if (isRenew) {
            _integerParams[integerPrefs.TIMESTART.getID()] = timeStart;
            _integerParams[integerPrefs.TIMEEND.getID()] = timeEnd;
            _integerParams[integerPrefs.INTERVAL.getID()] = interval;
            _integerParams[integerPrefs.DAYS.getID()] = days;
            _integerParams[integerPrefs.INDEX_LOCATION_SOURCE.getID()] = indexLocationSource;
            _integerParams[integerPrefs.TYPE_SERVICE.getID()] = typeService;
            _doubleParams[doublePrefs.ACCURY.getID()] = accury;
            _booleanParams[booleanPrefs.GPS_TIME.getID()] = bGpsTime;
            _booleanParams[booleanPrefs.LOCATION_SOURCE.getID()] = bIsLocationSource;
            _booleanParams[booleanPrefs.PASSIVE_CONNECTION.getID()] = passiveConnection;
            _booleanParams[booleanPrefs.DEVELOP_MODE.getID()] = devMode;
            return;
        }
    }
}