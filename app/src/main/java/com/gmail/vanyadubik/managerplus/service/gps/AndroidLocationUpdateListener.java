package com.gmail.vanyadubik.managerplus.service.gps;

import android.location.Location;
import android.os.Bundle;

public interface AndroidLocationUpdateListener {

    /**
     * Called whenever the location has changed (at least non-trivially)
     * @param location
     */
    void updateLocation(Location location);

    /**
     * Called whenever the start location
     * @param location
     */
    void startLocation(Location location);


    void onProviderDisabledEnabled(Boolean using, String provider);


    void onStatusChanged(String provider, int status, Bundle extras);
}