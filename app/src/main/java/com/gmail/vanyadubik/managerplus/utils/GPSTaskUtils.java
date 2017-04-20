package com.gmail.vanyadubik.managerplus.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.provider.Settings;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;

import java.text.SimpleDateFormat;

import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;

public class GPSTaskUtils {

    private static Context context;

    public GPSTaskUtils(Context context) {
        this.context = context;
    }

    public boolean isBetterLocation(Location location, Location currentBestLocation,
                                        Long minTime, Double maxCoefficient) {

        if (currentBestLocation == null) {
            return true;
        }

//        if (!isLocationAccurate(location) ||
//                location.getAccuracy() > maxCoefficient ) {
//            return false;
//        }
        if (!isLocationAccurate(location)) {
            return false;
        }

        if (location.getAccuracy() - currentBestLocation.getAccuracy() > 5 &&
                location.getAccuracy() > MAX_COEFFICIENT_CURRENCY_LOCATION) {
            return false;
        }

        Toast.makeText(context,
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                .format(location.getTime())
                                + " location is - \nLat: " + location.getLatitude()
                                + "\nLong: " + location.getLongitude()
                                + "\nSpeed: " + location.getSpeed()
                                + "\nDistance: " + location.distanceTo(currentBestLocation)
                                + "\nTime: " + String.valueOf((location.getTime() - currentBestLocation.getTime())/1000)
                                + "\nAccuracy: " + location.getAccuracy(),
                        Toast.LENGTH_LONG).show();

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > minTime * 2;
        boolean isSignificantlyOlder = timeDelta < -minTime * 2;
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

    public boolean isSameProvider(String provider1, String provider2) {
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

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(context.getString(R.string.gps_is_setting));

        // Setting Dialog Message
        alertDialog.setMessage(context.getString(R.string.gps_is_enabled_open_settings));

        // On pressing Settings button
        alertDialog.setPositiveButton(context.getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(context.getString(R.string.questions_title_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371; //kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
}
