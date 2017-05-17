package com.gmail.vanyadubik.managerplus.service.gps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_SPEED_WRITE_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;

public class GoogleLocationService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleLocationUpdateListener googleLocationUpdateListener;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static int typePriorityConnection;
    private static long timeInterval, fastesInterval, distance;


    public GoogleLocationService(Context mContext, GoogleLocationUpdateListener googleLocationUpdateListener) {
        this.googleLocationUpdateListener = googleLocationUpdateListener;
        this.mContext = mContext;
        buildGoogleApiClient();
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

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(typePriorityConnection == 0 ?
                LocationRequest.PRIORITY_HIGH_ACCURACY : typePriorityConnection);
        mLocationRequest.setInterval(
                1000 * (timeInterval == 0 ? MIN_TIME_WRITE_TRACK : timeInterval));
        mLocationRequest.setFastestInterval(
                fastesInterval == 0 ? MIN_SPEED_WRITE_LOCATION : fastesInterval);
        mLocationRequest.setSmallestDisplacement(
                distance == 0 ? MIN_DISTANCE_WRITE_TRACK : distance);

    }

        @Override
        public void onConnected(Bundle bundle) {
            createLocationRequest();
            startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            if (connectionResult.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                Log.i(TAGLOG_GPS, "Google play service not updated");

            }
            googleLocationUpdateListener.cannotReceiveLocationUpdates("Google play service not updated");
        }

        @Override
        public void onLocationChanged(Location location) {
            googleLocationUpdateListener.updateLocation(location);
        }

    private static boolean locationEnabled(Context context) {
        boolean gps_enabled = false;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled;
    }

    private boolean servicesConnected(Context context) {
        return isPackageInstalled(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE, context);
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void startUpdates() {
    /*
     * Connect the client. Don't re-start any requests here; instead, wait
     * for onResume()
     */
        if (servicesConnected(mContext)) {
            if (locationEnabled(mContext)) {
                googleLocationUpdateListener.canReceiveLocationUpdates();
                startLocationUpdates();
            } else {
                googleLocationUpdateListener.cannotReceiveLocationUpdates("Unable to get your location.Please turn on your device Gps");
                Log.e(TAGLOG_GPS, "Unable to get your location.Please turn on your device Gps");
            }
        } else {
            googleLocationUpdateListener.cannotReceiveLocationUpdates("Google play service not available");
            Log.e(TAGLOG_GPS, "Google play service not available");
        }
    }

    //stop location updates
    public void stopUpdates() {
        stopLocationUpdates();
    }

    //start location updates
    public void startLocationUpdates() {

        googleLocationUpdateListener.startLocation(LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient));

        if (ActivityCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void startGoogleApi() {
        if(mGoogleApiClient == null){
            return;
        }
        mGoogleApiClient.connect();
    }

    public void closeGoogleApi() {
        if(mGoogleApiClient == null){
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

}
