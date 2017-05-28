package com.gmail.vanyadubik.managerplus.service.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;

public class AndroidLocationService implements LocationListener {


    private AndroidLocationUpdateListener androidLocationUpdateListener;
    private Context mContext;
    protected LocationManager locationManager;
    private boolean isGPSEnabled, isNetworkEnabled;
    private static long timeInterval, distance;

    public AndroidLocationService(Context mContext, AndroidLocationUpdateListener androidLocationUpdateListener) {
        this.androidLocationUpdateListener = androidLocationUpdateListener;
        this.mContext = mContext;

        isGPSEnabled = isNetworkEnabled  = false;

        buildlocationManager();
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }


    private void  buildlocationManager() {

        locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    public void startLocationUpdates() {

        Location location = null;
        try {
            if ( Build.VERSION.SDK_INT >= 23 &&

                    ContextCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            if (!isGPSEnabled && !isNetworkEnabled) {

                locationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        1000 * timeInterval,
                        distance,
                        this);
                Log.d(TAGLOG_GPS, "pasive provider");
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                androidLocationUpdateListener.startLocation(location);

                androidLocationUpdateListener.cannotReceiveLocationUpdates("isGPSEnabled = " + isGPSEnabled +"; isNetworkEnabled = " + isNetworkEnabled);

            }else {

                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000 * timeInterval,
                            distance,
                            this);
                    Log.d(TAGLOG_GPS, "GPS used");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        androidLocationUpdateListener.startLocation(location);

                    }
                }

                if (isNetworkEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                1000 * timeInterval,
                                distance,
                                this);
                        Log.d(TAGLOG_GPS, "Network used");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            androidLocationUpdateListener.startLocation(location);

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            androidLocationUpdateListener.cannotReceiveLocationUpdates("Android location service not updated");
        }
    }

    public void stopLocationUpdates() {

        if (locationManager != null) {
            locationManager.removeUpdates(AndroidLocationService.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        androidLocationUpdateListener.updateLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        androidLocationUpdateListener.onProviderDisabledEnabled(false, provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        androidLocationUpdateListener.onProviderDisabledEnabled(true, provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        androidLocationUpdateListener.onStatusChanged(provider, status, extras);
    }
}
