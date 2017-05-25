package com.gmail.vanyadubik.managerplus.gps.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.Uri;
import android.os.SystemClock;

import com.gmail.vanyadubik.managerplus.gps.service.android.ServiceGpsTracking;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;

import java.sql.Timestamp;
import java.util.Date;

import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_WRITE_TRACK;

public class RepeatingAlarmService extends BroadcastReceiver {
    public static final String ACTION_WRITE_TRACK = "Write";
    public static String MY_TRACKING_ALARM = "MY_TRACKING_ALARM";

    public void onReceive(Context context, Intent intentA) {
        String action = intentA.getData().toString();
        Intent intent;
        if (action.equals(ACTION_WRITE_TRACK)) {

            ServiceGpsTracking.lastAlarmTick = SystemClock.elapsedRealtime();

            if (ServiceGpsTracking.location != null) {

                if (isBettherLocation(ServiceGpsTracking.location)) {

                    saveLastLocation(context, new Timestamp(ServiceGpsTracking.location.getTime()),
                            Gps.CorrectGPSDegree(ServiceGpsTracking.location.getLatitude()),
                            Gps.CorrectGPSDegree(ServiceGpsTracking.location.getLongitude()),
                            ServiceGpsTracking.location.getSpeed(),
                            ServiceGpsTracking.gpsLocationSource);

                    ServiceGpsTracking.dataRepositoryDB.insertTrackPoint(
                            new LocationPoint(new Date(ServiceGpsTracking.location.getTime()),
                                    ServiceGpsTracking.location.getLatitude(),
                                    ServiceGpsTracking.location.getLongitude(), true));

                }

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

    private Boolean isBettherLocation(Location location){
        if (ServiceGpsTracking.lastCurrentLocation == null) {
            ServiceGpsTracking.lastCurrentLocation = location;
            return true;
        }

        if(location.hasAccuracy() &&

                location.getAccuracy() < MAX_COEFFICIENT_CURRENCY_LOCATION &&

                ServiceGpsTracking.lastCurrentLocation.distanceTo(location) > MIN_DISTANCE_WRITE_TRACK){

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