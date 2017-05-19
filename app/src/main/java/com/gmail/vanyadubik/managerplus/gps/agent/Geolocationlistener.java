//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//
//import android.location.Location;
//import android.location.LocationListener;
//import android.os.Bundle;
//
//public class Geolocationlistener implements LocationListener {
//    private Geolocation geolocation;
//
//    public Geolocationlistener(Geolocation geolocation) {
//        this.geolocation = geolocation;
//    }
//
//    public void onLocationChanged(Location locationChanged) {
//        if (locationChanged != null) {
//            this.geolocation.setLocation(new Location(CorrectGPSDegree(locationChanged.getLatitude()), CorrectGPSDegree(locationChanged.getLongitude()), Provider.FromName(locationChanged.getProvider())));
//        }
//    }
//
//    public void onProviderDisabled(String provider) {
//    }
//
//    public void onProviderEnabled(String provider) {
//    }
//
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//    }
//
//    public static double CorrectGPSDegree(double dblDegree) {
//        int nD = (int) dblDegree;
//        double dblS = (dblDegree - ((double) nD)) * 3600.0d;
//        int nM = (int) (dblS / 60.0d);
//        return Math.floor((Math.pow(10.0d, 4.0d) * (((double) ((nD * 100) + nM)) + ((dblS - ((double) (nM * 60))) / 60.0d))) + 0.5d) / Math.pow(10.0d, 4.0d);
//    }
//}
