package com.gmail.vanyadubik.managerplus.gps.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.support.v4.content.ContextCompat;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;

public class Gps {
    private static final int CHANGE_LOCATION_INTERVAL = 3000;
    public static final boolean s_gpsUseWGS84 = true;
    private Context _context;
    private long gpsDate;
    private double gpsLatitude;
    private double gpsLongitude;
    private int gpsStatus;
    private Timer gpsStatusTimer;
    private boolean isGpsEnabled;
    private boolean isSupported;
    private long lastnLocationTimeMillis;
    private LocationListener locListener;
    private LocationManager manager;
    private Handler onGpsStatusChange;

    class C02751 extends Handler {
        C02751() {
        }

        public void dispatchMessage(Message msg) {
            Gps.this.OnGpsStatusChanged(Gps.this.gpsStatus);
        }
    }

    private class GpsStatusTimerTask extends TimerTask {
        private GpsStatusTimerTask() {
        }

        public void run() {
            if (Gps.this.gpsStatus == 2) {
                if ((SystemClock.elapsedRealtime() - Gps.this.lastnLocationTimeMillis > 3000 ? Gps.s_gpsUseWGS84 : false) && Gps.this.lastnLocationTimeMillis > 0) {
                    Gps.this.gpsStatus = 1;
                    Gps.this.onGpsStatusChange.obtainMessage().sendToTarget();
                }
            }
        }
    }

    private class mylocationlistener implements LocationListener {

        private mylocationlistener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                Gps.this.lastnLocationTimeMillis = SystemClock.elapsedRealtime();
                Gps.this.gpsLatitude = location.getLatitude();
                Gps.this.gpsLongitude = location.getLongitude();
                Gps.this.gpsDate = location.getTime();
                if (Gps.this.gpsStatus != 2) {
                    Gps.this.gpsStatus = 2;
                    Gps.this.OnGpsStatusChanged(Gps.this.gpsStatus);
                }
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public native void OnGpsStatusChanged(int i);

    private Context getContext() {
        return this._context;
    }

    public Gps(Context context) {
        this.lastnLocationTimeMillis = 0;
        this.gpsStatusTimer = null;
        this.gpsStatus = 0;
        this.gpsLatitude = 0.0d;
        this.gpsLongitude = 0.0d;
        this.gpsDate = 0;
        this.isGpsEnabled = false;
        this.isSupported = false;
        this.onGpsStatusChange = new C02751();
        this._context = context;
        this.manager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        this.isSupported = getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        this.locListener = new mylocationlistener();
    }

    public boolean isEnabled() {
        return this.manager.isProviderEnabled(Provider.PROVIDER_GPS);
    }

    public boolean MockLocationAllowed() {
        if (Secure.getString(getContext().getContentResolver(), "mock_location").equals("0")) {
            return false;
        }
        return s_gpsUseWGS84;
    }

    public double GetLatitude() {
        return CorrectGPSDegree(this.gpsLatitude);
    }

    public double GetLongitude() {
        return CorrectGPSDegree(this.gpsLongitude);
    }

    public int[] GetGpsDate() {
        if (this.gpsDate == 0) {
            return null;
        }
        Timestamp dt = new Timestamp(this.gpsDate);
        return new int[]{dt.getYear() + 1900, dt.getMonth() + 1, dt.getDate(), dt.getDay(), dt.getHours(), dt.getMinutes(), dt.getSeconds()};
    }

    public int GetGpsStatus() {
        return this.gpsStatus;
    }

    public boolean IsSupported() {
        return this.isSupported;
    }

    public boolean EnableGps() {
        if (this.isGpsEnabled || !this.isSupported) {
            return false;
        }
        if (this.gpsStatusTimer == null) {
            this.gpsStatusTimer = new Timer();
            this.gpsStatusTimer.schedule(new GpsStatusTimerTask(), 0, 1000);
        }
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( _context, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( _context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        this.manager.requestLocationUpdates(Provider.PROVIDER_GPS, 0, 0.0f, this.locListener);
        this.isGpsEnabled = s_gpsUseWGS84;
        return s_gpsUseWGS84;
    }

    public void DisableGps() {
        if (this.isGpsEnabled) {
            if (this.gpsStatusTimer != null) {
                this.gpsStatusTimer.cancel();
                this.gpsStatusTimer = null;
            }
            this.lastnLocationTimeMillis = 0;
            this.manager.removeUpdates(this.locListener);
            this.gpsStatus = 0;
            this.gpsLatitude = 0.0d;
            this.gpsLongitude = 0.0d;
            this.gpsDate = 0;
            this.isGpsEnabled = false;
        }
    }

    public static double CorrectGPSDegree(double dblDegree) {
        int nD = (int) dblDegree;
        double dblS = (dblDegree - ((double) nD)) * 3600.0d;
        int nM = (int) (dblS / 60.0d);
        return ((double) ((nD * 100) + nM)) + ((dblS - ((double) (nM * 60))) / 60.0d);
    }
}