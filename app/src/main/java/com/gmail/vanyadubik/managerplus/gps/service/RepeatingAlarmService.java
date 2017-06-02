package com.gmail.vanyadubik.managerplus.gps.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_GPS_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.gps.service.ServiceGpsTracking.location;
import static com.gmail.vanyadubik.managerplus.gps.service.ServiceGpsTracking.mBuilder;

public class RepeatingAlarmService extends BroadcastReceiver {
    public static final String ACTION_WRITE_TRACK = "Write";
    public static String MY_TRACKING_ALARM = "MY_TRACKING_ALARM";

    public void onReceive(Context context, Intent intentA) {
        String action = intentA.getData().toString();
        Intent intent;
        if (action.equals(ACTION_WRITE_TRACK)) {

            ServiceGpsTracking.lastAlarmTick = SystemClock.elapsedRealtime();

            if (location != null) {

                if (isBettherLocation(location)) {

                    saveLastLocation(context, new Timestamp(location.getTime()),
                            Gps.CorrectGPSDegree(location.getLatitude()),
                            Gps.CorrectGPSDegree(location.getLongitude()),
                            location.getSpeed(),
                            ServiceGpsTracking.gpsLocationSource);

                    ServiceGpsTracking.dataRepositoryDB.insertTrackPoint(
                            new LocationPoint(new Date(location.getTime()),
                                    location.getLatitude(),
                                    location.getLongitude(), true));


                    String text = ServiceGpsTracking.dateFormat.format(location.getTime()) + " |W " + location.getProvider() + ": " + location.getAccuracy() + " |"
                                        + "\n " + new DecimalFormat("#.####").format(location.getLatitude())
                                        + "\n: " + new DecimalFormat("#.####").format(location.getLongitude());

                    mBuilder.setContentText(text);
                    mBuilder.setSmallIcon(R.drawable.ic_gps_track_connect);

                    Notification notification;
                    if (Build.VERSION.SDK_INT <= 15) {
                        notification = mBuilder.getNotification(); // API-15 and lower
                    } else {
                        notification = mBuilder.build();
                    }

                    ServiceGpsTracking.mNotificationManager.notify(DEFAULT_NOTIFICATION_GPS_TRACER_ID, notification);


                }

                intent = new Intent(MY_TRACKING_ALARM, Uri.parse(ACTION_WRITE_TRACK), ServiceGpsTracking.getContext(), RepeatingAlarmService.class);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ServiceGpsTracking.getContext(), ServiceGpsTracking.REQUEST_CODE, intent, 0);
                ServiceGpsTracking.alarmManager.cancel(PendingIntent.getBroadcast(ServiceGpsTracking.getContext(), ServiceGpsTracking.REQUEST_CODE, intent, 0));
                long currentTime = SystemClock.elapsedRealtime();
                long nextAlarmTick = currentTime + ((long) ServiceGpsTracking.getInterval());
                if (ServiceGpsTracking.lastAlarmTick == -1 || nextAlarmTick < currentTime) {
                    ServiceGpsTracking.setStartTimeInAlamManager(currentTime, pendingIntent);
                } else if (nextAlarmTick > currentTime) {
                    ServiceGpsTracking.setStartTimeInAlamManager(nextAlarmTick, pendingIntent);
                }
            }
        }
    }

    private Boolean isBettherLocation(Location location){
         if(!location.hasAccuracy() ||
                location.getAccuracy() > MAX_COEFFICIENT_CURRENCY_LOCATION ){
             return false;
         }

        if (ServiceGpsTracking.lastCurrentLocation == null) {
            ServiceGpsTracking.lastCurrentLocation = location;
            return true;
        }

        if(ServiceGpsTracking.lastCurrentLocation.distanceTo(location) > MIN_DISTANCE_WRITE_TRACK){

            ServiceGpsTracking.lastCurrentLocation = location;

            return true;
        }

        return false;
    }

    public void saveLastLocation(Context context, Timestamp dt, double latitude, double longitude, double speed, int locationSource) {
        Editor editor = context.getSharedPreferences(SharedStorage.APP_PREFS, 0).edit();
        editor.putLong(GpsTracking.LAST_DATE_KEY, dt.getTime());
        editor.putString(GpsTracking.LAST_LATITUDE_KEY, Double.toString(latitude));
        editor.putString(GpsTracking.LAST_LONGITUDE_KEY, Double.toString(longitude));
        editor.putString(GpsTracking.LAST_SPEED_KEY, Double.toString(speed));
        editor.putString(GpsTracking.LAST_LOCATIONSOURCE_KEY, Integer.toString(locationSource));
        editor.commit();
    }
}