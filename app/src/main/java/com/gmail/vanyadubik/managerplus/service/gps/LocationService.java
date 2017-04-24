package com.gmail.vanyadubik.managerplus.service.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;

public class LocationService extends Service {

    private GoogleLocationService googleLocationService;

    @Override
    public void onCreate() {
        super.onCreate();

        updateLocation(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    //get current location os user
    private void updateLocation(Context context) {
        googleLocationService = new GoogleLocationService(context, new LocationUpdateListener() {


            @Override
            public void canReceiveLocationUpdates() {

            }

            @Override
            public void cannotReceiveLocationUpdates(String exception) {

            }

            @Override
            public void updateLocation(Location location) {
                if (location != null ) {

                }
            }

            @Override
            public void startLocation(Location location) {

            }

        });
        googleLocationService.setTypePriorityConnection(TYPE_PRIORITY_CONNECTION_GPS);
        googleLocationService.setTimeInterval(MIN_TIME_WRITE_TRACK);
        googleLocationService.setFastesInterval(MIN_TIME_WRITE_TRACK);
        googleLocationService.setDistance(MIN_DISTANCE_WRITE_TRACK);
        googleLocationService.startUpdates();
    }


    IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //stop location updates on stopping the service
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleLocationService != null) {
            googleLocationService.stopLocationUpdates();
        }
    }
}
