package com.gmail.vanyadubik.managerplus.gps.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.gmail.vanyadubik.managerplus.gps.service.android.ServiceGpsTracking;

import java.io.File;
import java.sql.Timestamp;

public class GpsTracking {
    public static final String LAST_DATE_KEY = "last_date_key";
    public static final String LAST_LATITUDE_KEY = "last_latitude_key";
    public static final String LAST_LOCATIONSOURCE_KEY = "last_locationsource_key";
    public static final String LAST_LONGITUDE_KEY = "last_longitude_key";
    public static final String LAST_SPEED_KEY = "last_speed_key";
    public static final String PREF_DAYS = "gpsTrackingDays";
    public static final String PREF_ENABLE = "gpsTrackingEnable";
    public static final String PREF_ERPID = "gpsTrackingErpId";
    public static final String PREF_FILE = "gpsTrackingFile";
    public static final String PREF_GPSTIME = "gpsTrackingGpsTime";
    public static final String PREF_INTERVAL = "gpsTrackingInterval";
    public static final String PREF_ISLOCATIONSOURCE = "IsWriteLocationSource";
    public static final String PREF_LOCATIONSOURCE = "LocationSource";
    public static final String PREF_PERIOD = "gpsTrackingPeriod";
    public static final String PREF_PORT = "gpsTrackingPort";
    public static final String PREF_PPCGUID = "gpsTrackingPPCGuid";
    public static final String PREF_SERVERADDRESS = "gpsTrackingServerAddress";
    public static final String PREF_SERVERTYPE = "gpsTrackingServerType";
    public static final String PREF_SPEED = "gpsTrackingSpeed";
    public static final String PREF_TIME = "gpsTrackingTime";
    private Context _context;

    public class GpsData {
        public String date;
        public double latitude;
        public int locationSource;
        public double longitude;
        public double speed;
    }

    public native void TakeGpsTrackingSettingsFromJava(boolean z, int i, int i2, int i3, boolean z2, boolean z3, String str, int i4, String str2, String str3, String str4, int i5, int i6, int i7, boolean z4);

    public GpsTracking(Context context) {
        this._context = context;
    }

    private Context getContext() {
        return this._context;
    }

    public boolean IsSupported() {
        PackageManager packageManager = getContext().getPackageManager();
        boolean hasSystemFeatureLocation = packageManager.hasSystemFeature(Provider.FromIndex(SharedStorage.getInteger(getContext(), PREF_LOCATIONSOURCE, 1)).getFeatureName());
        if (packageManager.hasSystemFeature("android.hardware.location.gps") && hasSystemFeatureLocation) {
            return true;
        }
        return false;
    }

    public boolean StartGpsTracking() {
        if (!IsSupported()) {
            return false;
        }
        SharedStorage.setBoolean(getContext(), PREF_ENABLE, Boolean.valueOf(true));
        getContext().startService(new Intent(getContext(), ServiceGpsTracking.class));
        return true;
    }

    public void StopGpsTracking() {
        SharedStorage.setBoolean(getContext(), PREF_ENABLE, Boolean.valueOf(false));
        getContext().stopService(new Intent(getContext(), ServiceGpsTracking.class));
    }

    public int GetGpsTrackingStatus() {
        return SharedStorage.getBoolean(getContext(), PREF_ENABLE, false) ? 1 : 0;
    }

    public void ApplyGpsTrackingSettings(int interval, int time, int days, boolean bSpeed, boolean bGpsTime, String fileName, int serverType, String serverAddress, String ppcGuid, String erpId, int period, int port, int locationSource, boolean bLocationSource) {
        getContext().stopService(new Intent(getContext(), ServiceGpsTracking.class));
        SharedStorage.setInteger(getContext(), PREF_INTERVAL, interval);
        SharedStorage.setInteger(getContext(), PREF_TIME, time);
        SharedStorage.setInteger(getContext(), PREF_DAYS, days);
        SharedStorage.setBoolean(getContext(), PREF_SPEED, Boolean.valueOf(bSpeed));
        SharedStorage.setBoolean(getContext(), PREF_SPEED, Boolean.valueOf(bSpeed));
        SharedStorage.setBoolean(getContext(), PREF_GPSTIME, Boolean.valueOf(bGpsTime));
        SharedStorage.setInteger(getContext(), PREF_SERVERTYPE, serverType);
        SharedStorage.setString(getContext(), PREF_SERVERADDRESS, serverAddress);
        if (!TextUtils.isEmpty(ppcGuid)) {
            SharedStorage.setString(getContext(), PREF_PPCGUID, ppcGuid);
        }
        if (!TextUtils.isEmpty(erpId)) {
            SharedStorage.setString(getContext(), PREF_ERPID, erpId);
        }
        SharedStorage.setInteger(getContext(), PREF_PERIOD, period);
        SharedStorage.setInteger(getContext(), PREF_PORT, port);
        SharedStorage.setInteger(getContext(), PREF_LOCATIONSOURCE, locationSource);
        SharedStorage.setBoolean(getContext(), PREF_ISLOCATIONSOURCE, Boolean.valueOf(bLocationSource));
        String oldFileName = SharedStorage.getString(getContext(), PREF_FILE, BuildConfig.VERSION_NAME);
        if (!(oldFileName == BuildConfig.VERSION_NAME || oldFileName == fileName)) {
            File file = new File(oldFileName);
            if (!file.exists()) {
                file.renameTo(new File(fileName));
            }
        }
        SharedStorage.setString(getContext(), PREF_FILE, fileName);
        if (GetGpsTrackingStatus() == 1 && IsSupported()) {
            getContext().startService(new Intent(getContext(), ServiceGpsTracking.class));
        }
    }

    public void ReadGpsTrackingSettings() {
        boolean enable = SharedStorage.getBoolean(getContext(), PREF_ENABLE, false);
        int interval = SharedStorage.getInteger(getContext(), PREF_INTERVAL, 0);
        boolean bSpeed = SharedStorage.getBoolean(getContext(), PREF_SPEED, false);
        boolean bGpsTime = SharedStorage.getBoolean(getContext(), PREF_GPSTIME, false);
        int days = SharedStorage.getInteger(getContext(), PREF_DAYS, 0);
        int time = SharedStorage.getInteger(getContext(), PREF_TIME, 0);
        String file = SharedStorage.getString(getContext(), PREF_FILE, BuildConfig.VERSION_NAME);
        int period = SharedStorage.getInteger(getContext(), PREF_PERIOD, 0);
        int port = SharedStorage.getInteger(getContext(), PREF_PORT, 0);
        TakeGpsTrackingSettingsFromJava(enable, interval, time, days, bSpeed, bGpsTime, file, SharedStorage.getInteger(getContext(), PREF_SERVERTYPE, 0), SharedStorage.getString(getContext(), PREF_SERVERADDRESS, "MdmService.getDeviceId(this._context)"), SharedStorage.getString(getContext(), PREF_PPCGUID, BuildConfig.VERSION_NAME), SharedStorage.getString(getContext(), PREF_ERPID, BuildConfig.VERSION_NAME), period, port, SharedStorage.getInteger(getContext(), PREF_LOCATIONSOURCE, 1), SharedStorage.getBoolean(getContext(), PREF_ISLOCATIONSOURCE, false));
    }

    public GpsData getLastGpsData() {
        long longTime = SharedStorage.getLong(getContext(), LAST_DATE_KEY, 0);
        if (longTime == 0) {
            return null;
        }
        GpsData data = new GpsData();
        Timestamp ts = new Timestamp(longTime);
        data.date = String.format("%d:%d:%d %d:%d", new Object[]{Integer.valueOf(ts.getYear() + 1900), Integer.valueOf(ts.getMonth() + 1), Integer.valueOf(ts.getDate()), Integer.valueOf(ts.getHours()), Integer.valueOf(ts.getMinutes())});
        data.longitude = Double.parseDouble(SharedStorage.getString(getContext(), LAST_LONGITUDE_KEY, "0"));
        data.latitude = Double.parseDouble(SharedStorage.getString(getContext(), LAST_LATITUDE_KEY, "0"));
        data.speed = Double.parseDouble(SharedStorage.getString(getContext(), LAST_SPEED_KEY, "0"));
        data.locationSource = Integer.parseInt(SharedStorage.getString(getContext(), LAST_LOCATIONSOURCE_KEY, "0"));
        return data;
    }
}