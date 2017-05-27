package com.gmail.vanyadubik.managerplus.gps.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;
import java.sql.Timestamp;

public class GpsTracking {
    public static final String LAST_DATE_KEY = "last_date_key";
    public static final String LAST_LATITUDE_KEY = "last_latitude_key";
    public static final String LAST_LOCATIONSOURCE_KEY = "last_locationsource_key";
    public static final String LAST_LONGITUDE_KEY = "last_longitude_key";
    public static final String LAST_SPEED_KEY = "last_speed_key";
    public static final String PREF_ENABLE = "gpsTrackingEnable";
    public static final String PREF_LOCATIONSOURCE = "LocationSource";
    private Context _context;

    public class GpsData {
        public String date;
        public double latitude;
        public int locationSource;
        public double longitude;
        public double speed;
    }

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