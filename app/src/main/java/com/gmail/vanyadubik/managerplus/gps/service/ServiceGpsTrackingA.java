package com.gmail.vanyadubik.managerplus.gps.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.StartActivity;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_GPS_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;


public class ServiceGpsTrackingA extends Service {

    @Inject
    DataRepository dataRepository;

    private static final int CHANGE_LOCATION_INTERVAL = 1000;
    public static int REQUEST_CODE;
    public static AlarmManager alarmManager;
    private static Context context;
    public static Location location;
    public static Location lastCurrentLocation;
    public static int gpsLocationSource;
    public static DataRepository dataRepositoryDB;
    public static long lastAlarmTick;
    public static int locationSource;
    private static int interval;
    private static int period;
    private int gpsStatus;
    private Timer gpsStatusTimer;
    private long lastnLocationTimeMillis;
    private LocationListener locListener;
    private LocationManager locManager;

    public static NotificationManager mNotificationManager;
    public static NotificationCompat.Builder mBuilder;
    public static SimpleDateFormat dateFormat;

    class GpsTrackingStatusTimerTask extends TimerTask {
        GpsTrackingStatusTimerTask() {
        }

        public void run() {
            if (ServiceGpsTrackingA.this.gpsStatus == 2) {
                if ((SystemClock.elapsedRealtime() - ServiceGpsTrackingA.this.lastnLocationTimeMillis > 3000) && ServiceGpsTrackingA.this.lastnLocationTimeMillis > 0) {
                    ServiceGpsTrackingA.this.gpsStatus = 1;
                    ServiceGpsTrackingA.this.OnGpsStatusChanged(ServiceGpsTrackingA.this.gpsStatus);
                }
            }
        }
    }

    private class gpsTrackingLocationListener implements LocationListener {
        private gpsTrackingLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ServiceGpsTrackingA.this.lastnLocationTimeMillis = SystemClock.elapsedRealtime();
                ServiceGpsTrackingA.location = location;
                ServiceGpsTrackingA.gpsLocationSource = Provider.FromName(location.getProvider()).getIndex();
                if (ServiceGpsTrackingA.this.gpsStatus != 2) {
                    ServiceGpsTrackingA.this.gpsStatus = 2;
                    ServiceGpsTrackingA.this.OnGpsStatusChanged(ServiceGpsTrackingA.this.gpsStatus);

                }
            }
        }

        public void onProviderDisabled(String provider) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                sendNotification(
                        dateFormat.format(LocalDateTime.now(DateTimeZone.getDefault()).toDate().getTime())
                                + " " + context.getString(R.string.gps_is_disabled), true);
            }
        }

        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locManager.requestLocationUpdates(Provider.GPS.getName(), CHANGE_LOCATION_INTERVAL, 0.0f, locListener);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public ServiceGpsTrackingA() {
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

       // ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        dataRepositoryDB = dataRepository;

        mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");

        lastAlarmTick = -1;

        initNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (readPreference()) {

            this.locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            this.locListener = new gpsTrackingLocationListener();
            this.gpsStatusTimer = new Timer();
            this.gpsStatusTimer.schedule(new GpsTrackingStatusTimerTask(), 0, 1000);
            Provider provider = Provider.FromIndex(locationSource);

            if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                sendNotification(
                        dateFormat.format(LocalDateTime.now(DateTimeZone.getDefault()).toDate().getTime())
                                + " " + context.getString(R.string.gps_is_disabled), true);
            }

            if (provider == Provider.PASSIVE) {
                List<String> providerList = this.locManager.getAllProviders();
                if (providerList.contains(Provider.PROVIDER_GPS)) {

                    if ( Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return START_REDELIVER_INTENT;
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
            return START_REDELIVER_INTENT;
        }
        stopSelf();

        return START_REDELIVER_INTENT;
    }

    private void startService() {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK), this, RepeatingAlarmService.class), 0);
        long currentTime = SystemClock.elapsedRealtime();
        long nextAlarmTick = currentTime + ((long) interval);
        if (lastAlarmTick != -1) {
            nextAlarmTick = lastAlarmTick + ((long) interval);
        }
        if (lastAlarmTick == -1 || nextAlarmTick < currentTime) {
            setStartTimeInAlamManager(currentTime, pendingIntent);
        } else if (nextAlarmTick > currentTime) {
            setStartTimeInAlamManager(nextAlarmTick, pendingIntent);
        }
    }

    public static void setStartTimeInAlamManager(long timeStart, PendingIntent pendingIntent){
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    timeStart, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeStart, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeStart, pendingIntent);
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
        mNotificationManager.cancel(DEFAULT_NOTIFICATION_GPS_TRACER_ID);
        stopForeground(true);
    }

    private void OnGpsStatusChanged(int status) {
        if (2 == status) {
            startService();
            return;
        }
        if (alarmManager != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK), this, RepeatingAlarmService.class), 0));
        }
        // location = null;
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

    public void initNotification() {

        Intent notificationIntent = new Intent(this, StartActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_gps_track_connect)
                .setContentTitle(context.getString(R.string.app_name) + " |" +
                        context.getString(R.string.gps_tracer_name))
                .setWhen(System.currentTimeMillis());
    }

    public void sendNotification(String text, boolean error) {

        mBuilder.setContentText(text);

        if (error) {
            mBuilder.setSmallIcon(R.drawable.ic_gps_track_not_connect);
        }else{
            mBuilder.setSmallIcon(R.drawable.ic_gps_track_connect);
        }

        Notification notification;
        if (Build.VERSION.SDK_INT <= 15) {
            notification = mBuilder.getNotification(); // API-15 and lower
        } else {
            notification = mBuilder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_GPS_TRACER_ID, notification);
    }

}
