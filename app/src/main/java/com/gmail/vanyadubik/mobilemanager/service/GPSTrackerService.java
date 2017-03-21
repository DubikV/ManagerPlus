package com.gmail.vanyadubik.mobilemanager.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.gmail.vanyadubik.mobilemanager.R;
import com.gmail.vanyadubik.mobilemanager.activity.TrackActivity;
import com.gmail.vanyadubik.mobilemanager.repository.DataRepository;
import com.gmail.vanyadubik.mobilemanager.repository.DataRepositoryImpl;

import org.joda.time.LocalDate;

import static com.gmail.vanyadubik.mobilemanager.common.Consts.MIN_DISTANCE_CHANGE_FOR_UPDATES;
import static com.gmail.vanyadubik.mobilemanager.common.Consts.MIN_TIME_BW_UPDATES;

public class GPSTrackerService extends Service implements LocationListener {

    DataRepository dataRepository;

    private NotificationManager notificationManager;
    public static final int DEFAULT_NOTIFICATION_ID = 101;

    private Context mContext;
    private boolean isGPSEnabled, isNetworkEnabled;
    protected LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        dataRepository = new DataRepositoryImpl(mContext.getContentResolver());

        notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        isGPSEnabled = isNetworkEnabled  = false;

        locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled || isNetworkEnabled) {
            getLocation();
        }else{
            sendNotification("Ticker","GPSTracker","Date:"+LocalDate.now().toDate() +"not nave signal");
        }

        return START_REDELIVER_INTENT;
    }

    public void getLocation() {

        Location location = null;
        try {
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000 * MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("TAGLOG_GPS", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1000 * MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("TAGLOG_GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (location != null){
//            dataRepository.insertTrackPoint(new LocationPoint(LocalDate.now().toDateTimeAtCurrentTime(), location.getLatitude(),
//                    location.getLongitude(), true));
            sendNotification("Ticker","GPSTracker","Date:"+LocalDate.now().toDateTimeAtCurrentTime() +"\nLat: " + location.getLatitude() + "\nLong: " + location.getLongitude());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(GPSTrackerService.this);
            notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
        }
    }

    //Send custom notification
    public void sendNotification(String Ticker,String Title,String Text) {

        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, TrackActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.large))   // большая картинка
                .setTicker(Ticker)
                .setContentTitle(Title)
                .setContentText(Text)
                .setWhen(System.currentTimeMillis());

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT<=15) {
            notification = builder.getNotification(); // API-15 and lower
        }else{
            notification = builder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_ID, notification);
    }


    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
