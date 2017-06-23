package com.gmail.vanyadubik.managerplus.gps.service.old;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.StartActivity;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.location.GooglePlayLocationService;
import com.gmail.vanyadubik.managerplus.gps.location.GooglePlayLocationUpdateListener;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.GPSTaskUtils;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_GPS_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_SPEED_WRITE_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;

public class GPSTrackerService extends Service {

    @Inject
    DataRepository dataRepository;
    @Inject
    GPSTaskUtils gpsTaskUtils;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private Context mContext;

    private GooglePlayLocationService googlePlayLocationService;
    private Location currentBestLocation;
    private SimpleDateFormat dateFormat;
    private double minCurrentAccury;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        createLocationService();

        startNotification();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAGLOG_GPS, dateFormat.format(
                LocalDateTime.now(DateTimeZone.getDefault()).toDate().getTime()) + " start GPS servise");

//        try {
//            Double accuracy = Double.valueOf(dataRepository.getUserSetting(MIN_CURRENT_ACCURACY));
//            minCurrentAccury =  accuracy > MAX_COEFFICIENT_CURRENCY_LOCATION ? accuracy : MAX_COEFFICIENT_CURRENCY_LOCATION;
//        }catch(Exception e){
            minCurrentAccury = MAX_COEFFICIENT_CURRENCY_LOCATION;
//        }

        googlePlayLocationService.startUpdates();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (googlePlayLocationService != null) {
            googlePlayLocationService.stopLocationUpdates();
        }
        googlePlayLocationService.closeGoogleApi();

        mNotificationManager.cancel(DEFAULT_NOTIFICATION_GPS_TRACER_ID);
        stopForeground(true);
    }

    private void createLocationService(){

        googlePlayLocationService = new GooglePlayLocationService(this, new GooglePlayLocationUpdateListener() {
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

                if ( gpsTaskUtils.isBetterLocation(location, currentBestLocation,
                        MIN_TIME_WRITE_TRACK, minCurrentAccury, MIN_DISTANCE_WRITE_TRACK) ) {

                    currentBestLocation = location;
                }

                Date date = LocalDateTime.now(DateTimeZone.getDefault()).toDate();
                Log.d(TAGLOG_GPS, dateFormat
                        .format(date.getTime()) +
                        " location is null : " + String.valueOf(location == null));

                if (currentBestLocation != null) {
                    dataRepository.insertTrackPoint(new LocationPoint(date, currentBestLocation.getLatitude(),
                            currentBestLocation.getLongitude(), true));
                    sendNotification(
                            dateFormat.format(date.getTime())
                                    + "\n " + new DecimalFormat("#.####").format(currentBestLocation.getLatitude())
                                    + "\n: " + new DecimalFormat("#.####").format(currentBestLocation.getLongitude()), false);
                }

            }

            @Override
            public void startLocation(Location location) {
                currentBestLocation = location;
            }

        });
        googlePlayLocationService.setTypePriorityConnection(TYPE_PRIORITY_CONNECTION_GPS);
        googlePlayLocationService.setTimeInterval(MIN_TIME_WRITE_TRACK);
        googlePlayLocationService.setFastesInterval(MIN_SPEED_WRITE_LOCATION);
        googlePlayLocationService.setDistance(MIN_DISTANCE_WRITE_TRACK);

    }

    //Send custom notification
    public void startNotification() {

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
                .setContentText(mContext.getString(R.string.sync_processing))
                .setWhen(System.currentTimeMillis());

        Notification notification;
        if (Build.VERSION.SDK_INT <= 15) {
            notification = mBuilder.getNotification(); // API-15 and lower
        } else {
            notification = mBuilder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_GPS_TRACER_ID, notification);
    }

    public void sendNotification(String text, boolean error) {

        mBuilder.setContentText(text);

        if (error) {
            mBuilder.setSmallIcon(R.drawable.ic_gps_track_not_connect);
        }

        Notification notification;
        if (Build.VERSION.SDK_INT <= 15) {
            notification = mBuilder.getNotification(); // API-15 and lower
        } else {
            notification = mBuilder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_GPS_TRACER_ID, notification);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
