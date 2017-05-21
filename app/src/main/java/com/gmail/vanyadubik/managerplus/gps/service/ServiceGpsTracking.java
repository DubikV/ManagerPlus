package com.gmail.vanyadubik.managerplus.gps.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;


public class ServiceGpsTracking extends Service {
    private static final int CHANGE_LOCATION_INTERVAL = 1000;
    public static int REQUEST_CODE;
    public static AlarmManager alarmManager;
    public static boolean bGpsTime;
    private static Context context;
    public static int days;
    public static double gpsLatitude;
    public static int gpsLocationSource;
    public static double gpsLongitude;
    public static double gpsSpeed;
    public static long gpsTime;
    private static int interval;
    public static long lastAlarmTick;
    public static int locationSource;
    private static int period;
    private int gpsStatus;
    private Timer gpsStatusTimer;
    private long lastnLocationTimeMillis;
    private LocationListener locListener;
    private LocationManager locManager;

    class GpsTrackingStatusTimerTask extends TimerTask {
        GpsTrackingStatusTimerTask() {
        }

        public void run() {
            if (ServiceGpsTracking.this.gpsStatus == 2) {
                if ((SystemClock.elapsedRealtime() - ServiceGpsTracking.this.lastnLocationTimeMillis > 3000) && ServiceGpsTracking.this.lastnLocationTimeMillis > 0) {
                    ServiceGpsTracking.this.gpsStatus = 1;
                    ServiceGpsTracking.this.OnGpsStatusChanged(ServiceGpsTracking.this.gpsStatus);
                }
            }
        }
    }

    private class gpsTrackingLocationListener implements LocationListener {
        private gpsTrackingLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ServiceGpsTracking.this.lastnLocationTimeMillis = SystemClock.elapsedRealtime();
                ServiceGpsTracking.gpsLatitude = location.getLatitude();
                ServiceGpsTracking.gpsLongitude = location.getLongitude();
                ServiceGpsTracking.gpsSpeed = (double) location.getSpeed();
                ServiceGpsTracking.gpsTime = location.getTime();
                ServiceGpsTracking.gpsLocationSource = Provider.FromName(location.getProvider()).getIndex();
                if (ServiceGpsTracking.this.gpsStatus != 2) {
                    ServiceGpsTracking.this.gpsStatus = 2;
                    ServiceGpsTracking.this.OnGpsStatusChanged(ServiceGpsTracking.this.gpsStatus);
                }
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public ServiceGpsTracking() {
        this.gpsStatus = 0;
        this.lastnLocationTimeMillis = 0;
        this.gpsStatusTimer = null;
    }

    static {
        REQUEST_CODE = 11223344;
    }

    public void onCreate() {
        super.onCreate();
        context = this;
        lastAlarmTick = -1;
        if (readPreference()) {
            this.locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            this.locListener = new gpsTrackingLocationListener();
            this.gpsStatusTimer = new Timer();
            this.gpsStatusTimer.schedule(new GpsTrackingStatusTimerTask(), 0, 1000);
            Provider provider = Provider.FromIndex(locationSource);
            if (provider == Provider.PASSIVE) {
                List<String> providerList = this.locManager.getAllProviders();
                if (providerList.contains(Provider.PROVIDER_GPS)) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    this.locManager.requestLocationUpdates(Provider.GPS.getName(), CHANGE_LOCATION_INTERVAL, 0.0f, this.locListener);
                }
                if (providerList.contains(Provider.PROVIDER_NETWORK)) {
                    this.locManager.requestLocationUpdates(Provider.NETWORK.getName(), CHANGE_LOCATION_INTERVAL, 0.0f, this.locListener);
                }
            } else {
                this.locManager.requestLocationUpdates(provider.getName(), CHANGE_LOCATION_INTERVAL, 0.0f, this.locListener);
            }
            alarmManager = (AlarmManager) getSystemService(Notification.CATEGORY_ALARM);
            return;
        }
        stopSelf();
    }

    private void startService() {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK), this, RepeatingAlarmService.class), 0);
        long currentTime = SystemClock.elapsedRealtime();
        long nextAlarmTick = currentTime + ((long) interval);
        if (lastAlarmTick != -1) {
            nextAlarmTick = lastAlarmTick + ((long) interval);
        }
        if (lastAlarmTick == -1 || nextAlarmTick < currentTime) {
            alarmManager.set(2, currentTime, pendingIntent);
        } else if (nextAlarmTick > currentTime) {
            alarmManager.set(2, nextAlarmTick, pendingIntent);
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean readPreference() {
        SharedPreferences settings = getSharedPreferences(SharedStorage.APP_PREFS, 0);
        if (!settings.getBoolean(GpsTracking.PREF_ENABLE, false)) {
            return false;
        }
        interval = (int)MIN_TIME_WRITE_TRACK * 1000;
        period = (int)MIN_TIME_WRITE_TRACK * 1000;
        return true;
    }

    public void onDestroy() {
        if (this.gpsStatusTimer != null) {
            this.gpsStatusTimer.cancel();
            this.gpsStatusTimer = null;
        }
        if (alarmManager != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK), this, RepeatingAlarmService.class), 0));
        }
        if (this.locManager != null) {
            this.locManager.removeUpdates(this.locListener);
        }
    }

    private void OnGpsStatusChanged(int status) {
        if (2 == status) {
            startService();
            return;
        }
        if (alarmManager != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK), this, RepeatingAlarmService.class), 0));
        }
        gpsLatitude = 0.0d;
        gpsLongitude = 0.0d;
    }

    public static Context getContext() {
        return context;
    }

    public static int getPeriod() {
        return period;
    }

    public static int getInterval() {
        return interval;
    }
}
