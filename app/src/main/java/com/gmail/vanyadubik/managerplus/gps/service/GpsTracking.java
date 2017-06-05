package com.gmail.vanyadubik.managerplus.gps.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

public class GpsTracking {
    static final String BOOLEAN_PARAMS = "boolean_shared_preferences";
    static final String CHECK_PREFERENCES = "initializer_wait_for_check_preferences";
    static final String INITIALIZER_ACTION = "gps.service.GpsTracking.initializerBroadcaster";
    static final String LONG_PARAMS = "long_shared_preferences_from_service";
    static final String NEW_LOCATION = "service_have_a_new_location_for_initializer";
    static final String NUMERIC_PARAMS = "numeric_shared_preferences";
    static final String PREF_INTERVAL = "gpsTrackingInterval";
    static final String PREF_PERIOD = "gpsTrackingPeriod";
    static final String PREF_SERVERADDRESS = "gpsTrackingServerAddress";
    static final String RECEIVER_ACTION = "brc_receiver_action";
    static final String RENEW_PREFERENCES = "initializer_have_new_preferences";
    static final String SERVICE_ACTION = "gps.service.serviceGpsTracking.serviceBroadcaster";
    static final String SET_PREFERENCES = "send_preferences_to_service";
    static final String STRING_PARAMS = "string_shared_preferences";
    static final String STRING_PARAMS_FROM_SERVICE = "string_shared_preferences_from_service";
    private final String LAST_DATE_KEY;
    private final String LAST_LATITUDE_KEY;
    private final String LAST_LOCATIONSOURCE_KEY;
    private final String LAST_LONGITUDE_KEY;
    private final String LAST_SPEED_KEY;
    private final String PREF_DAYS;
    private final String PREF_ENABLE;
    private final String PREF_ERPID;
    private final String PREF_FILE;
    private final String PREF_GPSTIME;
    private final String PREF_ISLOCATIONSOURCE;
    private final String PREF_LOCATIONSOURCE;
    private final String PREF_PASSIVECONNECTION;
    private final String PREF_PASSWORD;
    private final String PREF_PORT;
    private final String PREF_PPCGUID;
    private final String PREF_SENDNULL;
    private final String PREF_SERVERTYPE;
    private final String PREF_SPEED;
    private final String PREF_TIME;
    private final String PREF_TRACKFILENAME;
    private final String PREF_TRACKFILEPATH;
    private final String PREF_USERNAME;
    private boolean[] _booleanParams;
    private Context _context;
    private int[] _integerParams;
    private boolean _isReceiverRegistered;
    private boolean _isStarted;
    private ArrayList<String> _stringParams;
    private BroadcastReceiver gpsTrackingReceiver;

    /* renamed from: ru.agentplus.tracking.GpsTracking.1 */
    class gpsTrackingReceiver extends BroadcastReceiver {
        gpsTrackingReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            setLastGpsData(intent.getLongExtra(GpsTracking.LONG_PARAMS, 0), intent.getStringArrayListExtra(GpsTracking.STRING_PARAMS_FROM_SERVICE));
        }
    }

    private class GpsData {
        String _date;
        double _latitude;
        int _locationSource;
        double _longitude;
        double _speed;

        GpsData(Context context) {
            long longTime = SharedStorage.getLong(context, "last_date_key", 0);
            Calendar timeStamp = Calendar.getInstance();
            timeStamp.setTimeInMillis(longTime);
            _date = String.format(Locale.US, "%d:%d:%d %d:%d", new Object[]{Integer.valueOf(timeStamp.get(1)), Integer.valueOf(timeStamp.get(2) + 1), Integer.valueOf(timeStamp.get(5)), Integer.valueOf(timeStamp.get(11)), Integer.valueOf(timeStamp.get(12))});
            _longitude = Double.parseDouble(SharedStorage.getString(context, "last_longitude_key", "0"));
            _latitude = Double.parseDouble(SharedStorage.getString(context, "last_latitude_key", "0"));
            _speed = Double.parseDouble(SharedStorage.getString(context, "last_speed_key", "0"));
            _locationSource = Integer.parseInt(SharedStorage.getString(context, "last_locationsource_key", "0"));
        }
    }

    enum booleanPrefs {
        SPEED(0),
        GPS_TIME(1),
        LOCATION_SOURCE(2),
        SEND_NULL(3),
        PASSIVE_CONNECTION(4);

        private int _prefId;

        private booleanPrefs(int prefId) {
            _prefId = prefId;
        }

        public int getID() {
            return _prefId;
        }
    }

    enum integerPrefs {
        TIME(0),
        INTERVAL(1),
        DAYS(2),
        PERIOD(3),
        PORT(4),
        SERVER_TYPE(5),
        INDEX_LOCATION_SOURCE(6);

        private int _prefId;

        private integerPrefs(int prefId) {
            _prefId = prefId;
        }

        public int getID() {
            return _prefId;
        }
    }

    enum serviceStringPrefs {
        LATITUDE(0),
        LONGTITUDE(1),
        SPEED(2),
        LOCATION_SOURCE(3);

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
        SERVER_ADDRESS(1),
        PPC_GUID(2),
        ERP_ID(3),
        USERNAME(4),
        PASSWORD(5),
        FILEPATH(6),
        FILENAME(7);

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
        PREF_DAYS = "gpsTrackingDays";
        PREF_TIME = "gpsTrackingTime";
        PREF_SPEED = "gpsTrackingSpeed";
        PREF_GPSTIME = "gpsTrackingGpsTime";
        PREF_SERVERTYPE = "gpsTrackingServerType";
        PREF_FILE = "gpsTrackingFile";
        PREF_PPCGUID = "gpsTrackingPPCGuid";
        PREF_ISLOCATIONSOURCE = "IsWriteLocationSource";
        PREF_ERPID = "gpsTrackingErpId";
        PREF_PORT = "gpsTrackingPort";
        PREF_SENDNULL = "gpsTrackingFixGpsDisabling";
        PREF_USERNAME = "gpsTrackingUsername";
        PREF_PASSWORD = "gpsTrackingPassword";
        PREF_TRACKFILEPATH = "gpsTrackingFilePath";
        PREF_TRACKFILENAME = "gpsTrackingFileName";
        PREF_PASSIVECONNECTION = "gpsTrackingIsPassiveConnection";
        LAST_DATE_KEY = "last_date_key";
        LAST_LATITUDE_KEY = "last_latitude_key";
        LAST_LONGITUDE_KEY = "last_longitude_key";
        LAST_SPEED_KEY = "last_speed_key";
        LAST_LOCATIONSOURCE_KEY = "last_locationsource_key";
        _isStarted = false;
        _isReceiverRegistered = false;
        _integerParams = new int[7];
        _booleanParams = new boolean[5];
        _stringParams = new ArrayList();
        _context = context;
    }

    private Context getContext() {
        return _context;
    }

    private boolean isSupported() {
        PackageManager packageManager = getContext().getPackageManager();
        boolean hasSystemFeatureLocation = packageManager.hasSystemFeature(Provider.FromIndex(SharedStorage.getInteger(getContext(), "LocationSource", 1)).getFeatureName());
        boolean gpsLocation = packageManager.hasSystemFeature("android.hardware.location.gps");
        boolean networkLocation = packageManager.hasSystemFeature("android.hardware.location.network");
        if ((gpsLocation || networkLocation) && hasSystemFeatureLocation) {
            return true;
        }
        return false;
    }

    private void sendNewPreferences(String intentAction) {
        readGpsTrackingSettings(true);
        Intent intent = new Intent(ServiceGpsTracking.RECEIVER_FILTER);//SERVICE_ACTION);
        intent.putExtra(NUMERIC_PARAMS, _integerParams);
        intent.putExtra(BOOLEAN_PARAMS, _booleanParams);
        intent.putExtra(STRING_PARAMS, _stringParams);
        intent.putExtra(RECEIVER_ACTION, intentAction);
        getContext().sendBroadcast(intent);
    }

    private void setReceiver() {
        gpsTrackingReceiver = new gpsTrackingReceiver();
        getContext().registerReceiver(gpsTrackingReceiver, new IntentFilter(INITIALIZER_ACTION));
        _isReceiverRegistered = true;
    }

    public boolean startGpsTracking() {
        if (!isSupported() || _isStarted) {
            return false;
        }
        _isStarted = true;
        SharedStorage.setBoolean(getContext(), "gpsTrackingEnable", Boolean.valueOf(true));
        setReceiver();
        getContext().startService(new Intent(getContext(), ServiceGpsTracking.class));
        SystemClock.sleep(300);
        sendNewPreferences(SET_PREFERENCES);
        return true;
    }

    public void stopGpsTracking() {
        _isStarted = false;
        SharedStorage.setBoolean(getContext(), "gpsTrackingEnable", Boolean.valueOf(false));
        if (_isReceiverRegistered) {
            _isReceiverRegistered = false;
            getContext().unregisterReceiver(gpsTrackingReceiver);
        }
        getContext().stopService(new Intent(getContext(), ServiceGpsTracking.class));
    }

    private int getGpsTrackingStatus() {
        return SharedStorage.getBoolean(getContext(), "gpsTrackingEnable", false) ? 1 : 0;
    }

    private void setLastGpsData(long datetime, ArrayList<String> stringParams) {
        SharedStorage.setLong(getContext(), "last_date_key", datetime);
        SharedStorage.setString(getContext(), "last_latitude_key", (String) stringParams.get(serviceStringPrefs.LATITUDE.getID()));
        SharedStorage.setString(getContext(), "last_longitude_key", (String) stringParams.get(serviceStringPrefs.LONGTITUDE.getID()));
        SharedStorage.setString(getContext(), "last_speed_key", (String) stringParams.get(serviceStringPrefs.SPEED.getID()));
        SharedStorage.setString(getContext(), "last_locationsource_key", (String) stringParams.get(serviceStringPrefs.LOCATION_SOURCE.getID()));
    }

    private GpsData getLastGpsData() {
        if (SharedStorage.getLong(getContext(), "last_date_key", 0) == 0) {
            return null;
        }
        return new GpsData(getContext());
    }

    private void writeGpsTrackingSettings(int interval, int time, int days, boolean bSpeed, boolean bGpsTime, String fileName, int serverType, String serverAddress, String ppcGuid, String erpId, int period, int port, int locationSource, boolean bLocationSource, boolean bSendNull, String FTPfileName, String userName, String password, String FTPfolder, boolean passiveConnection) {
        boolean isUpdated = false;
        SharedStorage.setBoolean(getContext(), "gpsTrackingFixGpsDisabling", Boolean.valueOf(bSendNull));
        if (interval != SharedStorage.getInteger(getContext(), PREF_INTERVAL, 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), PREF_INTERVAL, interval);
        }
        if (time != SharedStorage.getInteger(getContext(), "gpsTrackingTime", 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), "gpsTrackingTime", time);
        }
        if (days != SharedStorage.getInteger(getContext(), "gpsTrackingDays", 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), "gpsTrackingDays", days);
        }
        if (serverType != SharedStorage.getInteger(getContext(), "gpsTrackingServerType", 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), "gpsTrackingServerType", serverType);
        }
        if (period != SharedStorage.getInteger(getContext(), PREF_PERIOD, 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), PREF_PERIOD, period);
        }
        if (port != SharedStorage.getInteger(getContext(), "gpsTrackingPort", 0)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), "gpsTrackingPort", port);
        }
        if (locationSource != SharedStorage.getInteger(getContext(), "LocationSource", 1)) {
            isUpdated = true;
            SharedStorage.setInteger(getContext(), "LocationSource", locationSource);
        }
        if (bSpeed != SharedStorage.getBoolean(getContext(), "gpsTrackingSpeed", false)) {
            isUpdated = true;
            SharedStorage.setBoolean(getContext(), "gpsTrackingSpeed", Boolean.valueOf(bSpeed));
        }
        if (bGpsTime != SharedStorage.getBoolean(getContext(), "gpsTrackingGpsTime", false)) {
            isUpdated = true;
            SharedStorage.setBoolean(getContext(), "gpsTrackingGpsTime", Boolean.valueOf(bGpsTime));
        }
        if (bLocationSource != SharedStorage.getBoolean(getContext(), "IsWriteLocationSource", false)) {
            isUpdated = true;
            SharedStorage.setBoolean(getContext(), "IsWriteLocationSource", Boolean.valueOf(bLocationSource));
        }
        if (passiveConnection != SharedStorage.getBoolean(getContext(), "gpsTrackingIsPassiveConnection", false)) {
            isUpdated = true;
            SharedStorage.setBoolean(getContext(), "gpsTrackingIsPassiveConnection", Boolean.valueOf(passiveConnection));
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
        int time = SharedStorage.getInteger(getContext(), "gpsTrackingTime", 0);
        int days = SharedStorage.getInteger(getContext(), "gpsTrackingDays", 0);
        int serverType = SharedStorage.getInteger(getContext(), "gpsTrackingServerType", 0);
        int period = SharedStorage.getInteger(getContext(), PREF_PERIOD, 0);
        int port = SharedStorage.getInteger(getContext(), "gpsTrackingPort", 0);
        int indexLocationSource = SharedStorage.getInteger(getContext(), "LocationSource", 1);
        boolean bSpeed = SharedStorage.getBoolean(getContext(), "gpsTrackingSpeed", false);
        boolean bGpsTime = SharedStorage.getBoolean(getContext(), "gpsTrackingGpsTime", false);
        boolean bIsLocationSource = SharedStorage.getBoolean(getContext(), "IsWriteLocationSource", false);
        boolean enable = SharedStorage.getBoolean(getContext(), "gpsTrackingEnable", false);
        boolean bSendNull = SharedStorage.getBoolean(getContext(), "gpsTrackingFixGpsDisabling", false);
        boolean passiveConnection = SharedStorage.getBoolean(getContext(), "gpsTrackingIsPassiveConnection", false);
        if (isRenew) {
            _integerParams[integerPrefs.TIME.getID()] = time;
            _integerParams[integerPrefs.INTERVAL.getID()] = interval;
            _integerParams[integerPrefs.DAYS.getID()] = days;
            _integerParams[integerPrefs.PERIOD.getID()] = period;
            _integerParams[integerPrefs.PORT.getID()] = port;
            _integerParams[integerPrefs.SERVER_TYPE.getID()] = serverType;
            _integerParams[integerPrefs.INDEX_LOCATION_SOURCE.getID()] = indexLocationSource;
            _booleanParams[booleanPrefs.SPEED.getID()] = bSpeed;
            _booleanParams[booleanPrefs.GPS_TIME.getID()] = bGpsTime;
            _booleanParams[booleanPrefs.LOCATION_SOURCE.getID()] = bIsLocationSource;
            _booleanParams[booleanPrefs.SEND_NULL.getID()] = bSendNull;
            _booleanParams[booleanPrefs.PASSIVE_CONNECTION.getID()] = passiveConnection;
            return;
        }
    }
}