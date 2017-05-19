//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.os.SystemClock;
//import android.support.v4.internal.view.SupportMenu;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//import com.gmail.vanyadubik.managerplus.gps.agent.MdmService;
//import com.gmail.vanyadubik.managerplus.gps.agent.SharedStorage;
//import com.gmail.vanyadubik.managerplus.gps.agent.BuildConfig;
//import com.gmail.vanyadubik.managerplus.gps.agent.Provider;
//
//
//
//public class ServiceGpsTracking extends Service {
//    private static final int CHANGE_LOCATION_INTERVAL = 3000;
//    public static int REQUEST_CODE;
//    public static AlarmManager alarmManager;
//    public static boolean bGpsTime;
//    public static boolean bLocationSource;
//    public static boolean bSpeed;
//    private static Context context;
//    public static int days;
//    public static int endTime;
//    public static String erpId;
//    public static double gpsLatitude;
//    public static int gpsLocationSource;
//    public static double gpsLongitude;
//    public static double gpsSpeed;
//    public static long gpsTime;
//    private static int interval;
//    public static long lastAlarmTick;
//    public static int locationSource;
//    private static int period;
//    public static int port;
//    public static String ppcGuid;
//    public static String serverAddress;
//    public static int serverType;
//    public static int startTime;
//    public static String trackFile;
//    private int gpsStatus;
//    private Timer gpsStatusTimer;
//    private long lastnLocationTimeMillis;
//    private LocationListener locListener;
//    private LocationManager locManager;
//
//    class GpsTrackingStatusTimerTask extends TimerTask {
//        GpsTrackingStatusTimerTask() {
//        }
//
//        public void run() {
//            if (ServiceGpsTracking.this.gpsStatus == 2) {
//                if ((SystemClock.elapsedRealtime() - ServiceGpsTracking.this.lastnLocationTimeMillis > 3000) && ServiceGpsTracking.this.lastnLocationTimeMillis > 0) {
//                    ServiceGpsTracking.this.gpsStatus = 1;
//                    ServiceGpsTracking.this.OnGpsStatusChanged(ServiceGpsTracking.this.gpsStatus);
//                }
//            }
//        }
//    }
//
//    private class gpsTrackingLocationListener implements LocationListener {
//        private gpsTrackingLocationListener() {
//        }
//
//        public void onLocationChanged(Location location) {
//            if (location != null) {
//                ServiceGpsTracking.this.lastnLocationTimeMillis = SystemClock.elapsedRealtime();
//                ServiceGpsTracking.gpsLatitude = location.getLatitude();
//                ServiceGpsTracking.gpsLongitude = location.getLongitude();
//                ServiceGpsTracking.gpsSpeed = (double) location.getSpeed();
//                ServiceGpsTracking.gpsTime = location.getTime();
//                ServiceGpsTracking.gpsLocationSource = Provider.FromName(location.getProvider()).getIndex();
//                if (ServiceGpsTracking.this.gpsStatus != 2) {
//                    ServiceGpsTracking.this.gpsStatus = 2;
//                    ServiceGpsTracking.this.OnGpsStatusChanged(ServiceGpsTracking.this.gpsStatus);
//                }
//            }
//        }
//
//        public void onProviderDisabled(String provider) {
//        }
//
//        public void onProviderEnabled(String provider) {
//        }
//
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//        }
//    }
//
//    public ServiceGpsTracking() {
//        this.gpsStatus = 0;
//        this.lastnLocationTimeMillis = 0;
//        this.gpsStatusTimer = null;
//    }
//
//    static {
//        REQUEST_CODE = 11223344;
//    }
//
//    public void onCreate() {
//        super.onCreate();
//        context = this;
//        lastAlarmTick = -1;
//        if (readPreference()) {
//            this.locManager = (LocationManager) getSystemService("location");
//            this.locListener = new gpsTrackingLocationListener();
//            this.gpsStatusTimer = new Timer();
//            this.gpsStatusTimer.schedule(new GpsTrackingStatusTimerTask(), 0, 1000);
//            Provider provider = Provider.FromIndex(locationSource);
//            if (provider == Provider.PASSIVE) {
//                List<String> providerList = this.locManager.getAllProviders();
//                if (providerList.contains(Provider.PROVIDER_GPS)) {
//                    this.locManager.requestLocationUpdates(Provider.GPS.getName(), 1000, 0.0f, this.locListener);
//                }
//                if (providerList.contains(Provider.PROVIDER_NETWORK)) {
//                    this.locManager.requestLocationUpdates(Provider.NETWORK.getName(), 1000, 0.0f, this.locListener);
//                }
//            } else {
//                this.locManager.requestLocationUpdates(provider.getName(), 1000, 0.0f, this.locListener);
//            }
//            alarmManager = (AlarmManager) getSystemService(NotificationCompatApi21.CATEGORY_ALARM);
//            if (period > 0) {
//                alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) period), PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_SEND_TRACK), this, RepeatingAlarmService.class), 0));
//                return;
//            }
//            return;
//        }
//        stopSelf();
//    }
//
//    private void startService() {
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK), this, RepeatingAlarmService.class), 0);
//        long currentTime = SystemClock.elapsedRealtime();
//        long nextAlarmTick = currentTime + ((long) interval);
//        if (lastAlarmTick != -1) {
//            nextAlarmTick = lastAlarmTick + ((long) interval);
//        }
//        if (lastAlarmTick == -1 || nextAlarmTick < currentTime) {
//            alarmManager.set(2, currentTime, pendingIntent);
//        } else if (nextAlarmTick > currentTime) {
//            alarmManager.set(2, nextAlarmTick, pendingIntent);
//        }
//    }
//
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    public boolean readPreference() {
//        SharedPreferences settings = getSharedPreferences(SharedStorage.APP_PREFS, 0);
//        if (!settings.getBoolean(GpsTracking.PREF_ENABLE, false)) {
//            return false;
//        }
//        interval = settings.getInt(GpsTracking.PREF_INTERVAL, 5) * 1000;
//        bSpeed = settings.getBoolean(GpsTracking.PREF_SPEED, false);
//        bGpsTime = settings.getBoolean(GpsTracking.PREF_GPSTIME, false);
//        days = settings.getInt(GpsTracking.PREF_DAYS, 31);
//        trackFile = settings.getString(GpsTracking.PREF_FILE, BuildConfig.VERSION_NAME);
//        int time = settings.getInt(GpsTracking.PREF_TIME, 0);
//        startTime = time >> 16;
//        endTime = SupportMenu.USER_MASK & time;
//        period = settings.getInt(GpsTracking.PREF_PERIOD, 0) * 1000;
//        port = settings.getInt(GpsTracking.PREF_PORT, 0);
//        serverType = settings.getInt(GpsTracking.PREF_SERVERTYPE, 0);
//        serverAddress = settings.getString(GpsTracking.PREF_SERVERADDRESS, BuildConfig.VERSION_NAME);
//        ppcGuid = settings.getString(GpsTracking.PREF_PPCGUID, MdmService.getDeviceId(getApplicationContext()));
//        erpId = settings.getString(GpsTracking.PREF_ERPID, "00000000-0000-0000-0000-000000000000");
//        bLocationSource = settings.getBoolean(GpsTracking.PREF_ISLOCATIONSOURCE, false);
//        locationSource = settings.getInt(GpsTracking.PREF_LOCATIONSOURCE, Provider.PASSIVE.getIndex());
//        return true;
//    }
//
//    public void onDestroy() {
//        if (this.gpsStatusTimer != null) {
//            this.gpsStatusTimer.cancel();
//            this.gpsStatusTimer = null;
//        }
//        if (alarmManager != null) {
//            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK), this, RepeatingAlarmService.class), 0));
//            if (period > 0) {
//                alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_SEND_TRACK), this, RepeatingAlarmService.class), 0));
//            }
//        }
//        if (this.locManager != null) {
//            this.locManager.removeUpdates(this.locListener);
//        }
//    }
//
//    private void OnGpsStatusChanged(int status) {
//        if (2 == status) {
//            startService();
//            return;
//        }
//        if (alarmManager != null) {
//            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK), this, RepeatingAlarmService.class), 0));
//        }
//        gpsLatitude = 0.0d;
//        gpsLongitude = 0.0d;
//    }
//
//    public static Context getContext() {
//        return context;
//    }
//
//    public static int getPeriod() {
//        return period;
//    }
//
//    public static int getInterval() {
//        return interval;
//    }
//}
