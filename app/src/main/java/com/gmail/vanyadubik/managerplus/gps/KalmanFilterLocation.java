package com.gmail.vanyadubik.managerplus.gps;

import android.location.Location;

public class KalmanFilterLocation {
    private final float MinAccuracy = 1;
    private float speedChangeAccuracy; //meters per second
    private float variance; // P matrix.  Negative means object uninitialised.  NB: units irrelevant, as long as same units used throughout

    public KalmanFilterLocation() {
        this.speedChangeAccuracy = 10; //3
        variance = -1;
    }

    public Location FilteredLocation(Location lastlocation, Location newLocation) {

        Location currentlocation = lastlocation;

        float variance = currentlocation.getAccuracy() * currentlocation.getAccuracy();
        float newLocAccuracy = newLocation.getAccuracy();



        if (newLocAccuracy < MinAccuracy) newLocAccuracy = MinAccuracy;
        if (variance < 0) {
            currentlocation.setTime(newLocation.getTime());
            currentlocation.setLatitude(newLocation.getLatitude());
            currentlocation.setLongitude(newLocation.getLongitude());
            currentlocation.setAccuracy((float)Math.sqrt(newLocAccuracy*newLocAccuracy));
        } else {

            long deltaTime = newLocation.getTime() - currentlocation.getTime();

            if (deltaTime > 0) {
                variance += deltaTime * speedChangeAccuracy * speedChangeAccuracy / 1000;
                currentlocation.setTime(newLocation.getTime());
            }

            float K = variance / (variance + newLocAccuracy * newLocAccuracy);

            double latLocation  = currentlocation.getLatitude();

            latLocation += K * (newLocation.getLatitude() - latLocation);

            currentlocation.setLatitude(latLocation);

            double longLocation  = currentlocation.getLongitude();

            longLocation += K * (newLocation.getLongitude() - longLocation);

            currentlocation.setLongitude(longLocation);

            currentlocation.setAccuracy((float)Math.sqrt((1 - K) * variance));
        }

        return currentlocation;
    }


}
