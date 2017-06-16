package com.gmail.vanyadubik.managerplus.gps.location;

import android.location.Location;

import com.google.android.gms.location.LocationResult;

public interface AndroidPlayLocationUpdateListener {

    /**
     * Called immediately the service starts if the service can obtain location
     */
    void canReceiveLocationUpdates();

    /**
     * Called immediately the service tries to start if it cannot obtain location - eg the user has disabled wireless and
     */
    void cannotReceiveLocationUpdates(String exception);

    /**
     * Called whenever the location has changed (at least non-trivially)
     * @param locationResult
     */
    void updateLocation(LocationResult locationResult);

    /**
     * Called whenever the start location
     * @param location
     */
    void startLocation(Location location);

}