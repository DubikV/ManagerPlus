package com.gmail.vanyadubik.managerplus.gps.location;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;

public class AndroidPlayLocationService {

    private static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

    private Context mContext;

    private AndroidPlayLocationUpdateListener androidPlayLocationUpdateListener;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    private static int typePriorityConnection;
    private static long timeInterval, fastesInterval, distance;
    private boolean isStarted;

    public AndroidPlayLocationService(Context mContext,
                                      final AndroidPlayLocationUpdateListener androidPlayLocationUpdateListener) {
        this.mContext = mContext;
        this.androidPlayLocationUpdateListener = androidPlayLocationUpdateListener;
        this.isStarted = false;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                androidPlayLocationUpdateListener.updateLocation(locationResult);
            }
        };

        createLocationRequest();

        getLastLocation();

    }

    public void setTypePriorityConnection(int typePriorityConnection) {
        this.typePriorityConnection = typePriorityConnection;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void setFastesInterval(long fastesInterval) {
        this.fastesInterval = fastesInterval;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public boolean isStarted() {
        return isStarted;
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(typePriorityConnection == 0 ?
                LocationRequest.PRIORITY_HIGH_ACCURACY : typePriorityConnection);
        mLocationRequest.setInterval(1000 * timeInterval);
        mLocationRequest.setFastestInterval(1000 * fastesInterval);
        mLocationRequest.setSmallestDisplacement(distance);
    }

    private void getLastLocation() {

        final Location[] mLocation = new Location[1];

        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation[0] =  task.getResult();
                            } else {
                                Log.w(TAGLOG_GPS, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAGLOG_GPS, "Lost location permission." + unlikely);
        }

        androidPlayLocationUpdateListener.startLocation(mLocation[0]);
    }

    public void startLocationUpdates() {
        Log.i(TAGLOG_GPS, "Requesting location updates");
        setRequestingLocationUpdates(true);
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
            isStarted = true;
        } catch (SecurityException unlikely) {
            setRequestingLocationUpdates(false);
            Log.e(TAGLOG_GPS, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    public void stopLocationUpdates() {
        Log.i(TAGLOG_GPS, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            setRequestingLocationUpdates(false);
            isStarted = false;
        } catch (SecurityException unlikely) {
            setRequestingLocationUpdates(true);
            Log.e(TAGLOG_GPS, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    private boolean requestingLocationUpdates() {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    private void setRequestingLocationUpdates(boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }
}
