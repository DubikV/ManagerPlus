package com.gmail.vanyadubik.managerplus.gps.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.StartActivity;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.gps.GoogleLocationService;
import com.gmail.vanyadubik.managerplus.service.gps.GoogleLocationUpdateListener;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_GPS_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;


public class ServiceGpsTrackingG extends Service {

    @Inject
    DataRepository dataRepository;

    private static final int CHANGE_LOCATION_INTERVAL = 1000;
    public static int REQUEST_CODE;
    public static AlarmManager alarmManager;
    private static Context mContext;
    public static Location location;
    public static Location lastCurrentLocation;
    public static int gpsLocationSource;
    public static DataRepository dataRepositoryDB;
    public static long lastAlarmTick;
    private static int interval;
    private static int period;
    private int gpsStatus;
    private Timer gpsStatusTimer;
    private long lastnLocationTimeMillis;
    private GoogleLocationService googleLocationService;
    public static NotificationManager mNotificationManager;
    public static NotificationCompat.Builder mBuilder;
    public static SimpleDateFormat dateFormat;

    public ServiceGpsTrackingG() {
        this.gpsStatus = 0;
        this.lastnLocationTimeMillis = 0;
        this.gpsStatusTimer = null;
    }

    static {
        REQUEST_CODE = 11223344;
    }

    public void onCreate() {
        super.onCreate();

        mContext = this;

   //     ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        dataRepositoryDB = dataRepository;

        mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");

        lastAlarmTick = -1;

        createLocationService();

        initNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (readPreference()) {

            googleLocationService.startUpdates();

            this.gpsStatusTimer = new Timer();
            this.gpsStatusTimer.schedule(new GpsTrackingStatusTimerTask(), 0, 1000);

            alarmManager = (AlarmManager) getSystemService(Notification.CATEGORY_ALARM);
            return START_REDELIVER_INTENT;
        }
        stopSelf();

        return START_REDELIVER_INTENT;
    }

    class GpsTrackingStatusTimerTask extends TimerTask {
        GpsTrackingStatusTimerTask() {
        }

        public void run() {
            if (ServiceGpsTrackingG.this.gpsStatus == 2) {
                if ((SystemClock.elapsedRealtime() - ServiceGpsTrackingG.this.lastnLocationTimeMillis > 3000) && ServiceGpsTrackingG.this.lastnLocationTimeMillis > 0) {
                    ServiceGpsTrackingG.this.gpsStatus = 1;
                    ServiceGpsTrackingG.this.OnGpsStatusChanged(ServiceGpsTrackingG.this.gpsStatus);
                }
            }
        }
    }

    private void createLocationService(){

        googleLocationService = new GoogleLocationService(this, new GoogleLocationUpdateListener() {
            @Override
            public void canReceiveLocationUpdates() {
            }

            @Override
            public void cannotReceiveLocationUpdates(String exception) {
                Log.i(TAGLOG_GPS, "Connection failed. Error: " + exception);
                sendNotification(
                        dateFormat
                                .format(LocalDateTime.now(DateTimeZone.getDefault()).toDate().getTime())
                                + " " + mContext.getString(R.string.gps_is_disabled), true);
            }

            @Override
            public void updateLocation(Location location) {

                if (location != null) {
                    ServiceGpsTrackingG.this.lastnLocationTimeMillis = SystemClock.elapsedRealtime();
                    ServiceGpsTrackingG.location = location;
                    ServiceGpsTrackingG.gpsLocationSource = Provider.FromName(location.getProvider()).getIndex();
                    if (ServiceGpsTrackingG.this.gpsStatus != 2) {
                        ServiceGpsTrackingG.this.gpsStatus = 2;
                        ServiceGpsTrackingG.this.OnGpsStatusChanged(ServiceGpsTrackingG.this.gpsStatus);

                    }
                }

            }

            @Override
            public void startLocation(Location location) {
               // lastCurrentLocation = location;
            }

        });
        googleLocationService.setTypePriorityConnection(TYPE_PRIORITY_CONNECTION_GPS);
        googleLocationService.setTimeInterval(1);
        googleLocationService.setFastesInterval(1);
        googleLocationService.setDistance(0);

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
            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE,
                    new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK),
                            this, RepeatingAlarmService.class), 0));
        }
        if (googleLocationService != null) {
            googleLocationService.stopLocationUpdates();
        }
        googleLocationService.closeGoogleApi();

        mNotificationManager.cancel(DEFAULT_NOTIFICATION_GPS_TRACER_ID);
        stopForeground(true);
    }

    private void OnGpsStatusChanged(int status) {
        if (2 == status) {
            startService();
            return;
        }
        if (alarmManager != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE,
                    new Intent(RepeatingAlarmService.MY_TRACKING_ALARM, Uri.parse(RepeatingAlarmService.ACTION_WRITE_TRACK),
                            this, RepeatingAlarmService.class), 0));
        }
       // location = null;
    }

    public static Context getContext() {
        return mContext;
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

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_gps_track_connect)
                .setContentTitle(mContext.getString(R.string.app_name) + " |" +
                        mContext.getString(R.string.gps_tracer_name))
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
