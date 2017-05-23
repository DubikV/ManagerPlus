package com.gmail.vanyadubik.managerplus.gps.service.google;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.StartActivity;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking;
import com.gmail.vanyadubik.managerplus.gps.service.Provider;
import com.gmail.vanyadubik.managerplus.gps.service.RepeatingAlarmService;
import com.gmail.vanyadubik.managerplus.gps.service.SharedStorage;
import com.gmail.vanyadubik.managerplus.service.gps.GoogleLocationService;
import com.gmail.vanyadubik.managerplus.service.gps.GoogleLocationUpdateListener;

import java.util.Timer;
import java.util.TimerTask;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_SYNC_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;


public class ServiceGpsTracking extends Service {
    private static final int CHANGE_LOCATION_INTERVAL = 1000;
    public static int REQUEST_CODE;
    public static AlarmManager alarmManager;
    private static Context context;
    public static double gpsLatitude;
    public static int gpsLocationSource;
    public static double gpsLongitude;
    public static double gpsSpeed;
    public static long gpsTime;
    private static int interval;
    public static long lastAlarmTick;
    private static int period;
    private int gpsStatus;
    private Timer gpsStatusTimer;
    private long lastnLocationTimeMillis;
    private GoogleLocationService googleLocationService;

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
            googleLocationService = new GoogleLocationService(this, new GoogleLocationUpdateListener() {
                @Override
                public void canReceiveLocationUpdates() {

                }

                @Override
                public void cannotReceiveLocationUpdates(String exception) {

                }

                @Override
                public void updateLocation(Location location) {
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

                @Override
                public void startLocation(Location location) {

                }
            });
            googleLocationService.setTypePriorityConnection(TYPE_PRIORITY_CONNECTION_GPS);
            googleLocationService.setTimeInterval(CHANGE_LOCATION_INTERVAL);
            googleLocationService.setFastesInterval(CHANGE_LOCATION_INTERVAL);
            googleLocationService.setDistance(0);
            googleLocationService.startUpdates();
            this.gpsStatusTimer = new Timer();
            this.gpsStatusTimer.schedule(new GpsTrackingStatusTimerTask(), 0, 1000);

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
        if (googleLocationService != null) {
            googleLocationService.stopLocationUpdates();
        }
        googleLocationService.closeGoogleApi();
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

    public void sendNotification(String text, boolean error) {

//        Intent notificationIntent = new Intent(this, StartActivity.class);
//        notificationIntent.setAction(Intent.ACTION_MAIN);
//        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
//        mBuilder.setContentIntent(contentIntent)
//                .setOngoing(true)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                .setContentTitle(this.getString(R.string.app_name) + " |" +
//                        this.getString(R.string.gps_tracer_name))
//                .setContentText(this.getString(R.string.sync_processing))
//                .setWhen(System.currentTimeMillis());
//
//        if (error) {
//            mBuilder.setSmallIcon(R.drawable.ic_gps_track_not_connect);
//        }else{
//            mBuilder.setSmallIcon(R.drawable.ic_gps_track_connect);
//        }
//
//        Notification notification;
//        if (Build.VERSION.SDK_INT <= 15) {
//            notification = mBuilder.getNotification(); // API-15 and lower
//        } else {
//            notification = mBuilder.build();
//        }
//
//        startForeground(DEFAULT_NOTIFICATION_SYNC_TRACER_ID, notification);

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
