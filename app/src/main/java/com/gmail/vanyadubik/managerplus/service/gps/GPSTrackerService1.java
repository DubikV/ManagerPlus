package com.gmail.vanyadubik.managerplus.service.gps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.StartActivity;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_GPS_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_SYNC_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_CURRENT_ACCURACY;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_SPEED_WRITE_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;

public class GPSTrackerService1 extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @Inject
    DataRepository dataRepository;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location currentBestLocation;
    private SimpleDateFormat dateFormat;
    private double minCurrentAccury;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

     //   ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        startNotification();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAGLOG_GPS, dateFormat.format(
                LocalDateTime.now(DateTimeZone.getDefault()).toDate().getTime()) + " start GPS servise");

        try {
            Double accuracy = Double.valueOf(dataRepository.getUserSetting(MIN_CURRENT_ACCURACY));
            minCurrentAccury =  accuracy > MAX_COEFFICIENT_CURRENCY_LOCATION ? accuracy : MAX_COEFFICIENT_CURRENCY_LOCATION;
        }catch(Exception e){
            minCurrentAccury = MAX_COEFFICIENT_CURRENCY_LOCATION;
        }

        mGoogleApiClient.connect();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        mNotificationManager.cancel(DEFAULT_NOTIFICATION_GPS_TRACER_ID);
        stopForeground(true);
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

        startForeground(DEFAULT_NOTIFICATION_SYNC_TRACER_ID, notification);
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

        startForeground(DEFAULT_NOTIFICATION_SYNC_TRACER_ID, notification);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, currentBestLocation)) {
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
    public IBinder onBind(Intent arg0) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {

        if (currentBestLocation == null) {
            return true;
        }

        if (!isLocationAccurate(location)) {
            return false;
        }

        if (location.getAccuracy() - currentBestLocation.getAccuracy() > (minCurrentAccury/2) ||
                location.getAccuracy() > minCurrentAccury) {
            return false;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > MIN_TIME_WRITE_TRACK * 2;
        boolean isSignificantlyOlder = timeDelta < -MIN_TIME_WRITE_TRACK * 2;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public boolean isLocationAccurate(Location location) {
        if (location.hasAccuracy()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(TYPE_PRIORITY_CONNECTION_GPS);
        mLocationRequest.setInterval(MIN_TIME_WRITE_TRACK * 1000);
        mLocationRequest.setFastestInterval(MIN_SPEED_WRITE_LOCATION * 1000);
        mLocationRequest.setSmallestDisplacement(MIN_DISTANCE_WRITE_TRACK);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAGLOG_GPS, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAGLOG_GPS, "Connection failed. Error: " + connectionResult.getErrorCode());
        sendNotification(
                    dateFormat
                            .format(LocalDateTime.now(DateTimeZone.getDefault()).toDate().getTime())
                            + " " + mContext.getString(R.string.gps_is_disabled), true);
    }
}
