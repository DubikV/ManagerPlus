package com.gmail.vanyadubik.managerplus.gps.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.SystemClock;

import com.gmail.vanyadubik.managerplus.gps.service.android.ServiceGpsTracking;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.repository.DataRepositoryImpl;

import java.sql.Timestamp;
import java.util.Date;

public class RepeatingAlarmService extends BroadcastReceiver {
    public static final String ACTION_WRITE_TRACK = "Write";
    public static String MY_TRACKING_ALARM = "MY_TRACKING_ALARM";

    public void onReceive(Context context, Intent intentA) {
        String action = intentA.getData().toString();
        Intent intent;
        if (action.equals(ACTION_WRITE_TRACK)) {
            ServiceGpsTracking.lastAlarmTick = SystemClock.elapsedRealtime();
            if (ServiceGpsTracking.gpsLatitude != 0.0 && ServiceGpsTracking.gpsLongitude != 0.0) {
                saveLastLocation(context, new Timestamp(ServiceGpsTracking.gpsTime),
                        Gps.CorrectGPSDegree(ServiceGpsTracking.gpsLatitude),
                        Gps.CorrectGPSDegree(ServiceGpsTracking.gpsLongitude),
                        ServiceGpsTracking.gpsSpeed,
                        ServiceGpsTracking.gpsLocationSource);

                DataRepositoryImpl dataRepository = new DataRepositoryImpl(context.getContentResolver());
                dataRepository.insertTrackPoint(
                        new LocationPoint(new Date(ServiceGpsTracking.gpsTime),
                                ServiceGpsTracking.gpsLatitude,
                                ServiceGpsTracking.gpsLongitude, true));

                intent = new Intent(MY_TRACKING_ALARM, Uri.parse(ACTION_WRITE_TRACK), ServiceGpsTracking.getContext(), RepeatingAlarmService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ServiceGpsTracking.getContext(), ServiceGpsTracking.REQUEST_CODE, intent, 0);
                ServiceGpsTracking.alarmManager.cancel(PendingIntent.getBroadcast(ServiceGpsTracking.getContext(), ServiceGpsTracking.REQUEST_CODE, intent, 0));
                long currentTime = SystemClock.elapsedRealtime();
                long nextAlarmTick = currentTime + ((long) ServiceGpsTracking.getInterval());
                if (ServiceGpsTracking.lastAlarmTick == -1 || nextAlarmTick < currentTime) {
                    ServiceGpsTracking.alarmManager.set(2, currentTime, pendingIntent);
                } else if (nextAlarmTick > currentTime) {
                    ServiceGpsTracking.alarmManager.set(2, nextAlarmTick, pendingIntent);
                }
            }
        }
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