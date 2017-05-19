//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//import android.content.Context;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.provider.Settings.Secure;
//import android.util.Log;
//import java.util.List;
//
//public class Geolocation {
//    public static final int ACTUAL_LOCATION_PERIOD = 3000;
//    private Context context;
//    private boolean isActive;
//    private Location location;
//    private LocationListener locationListener;
//    private LocationManager locationManager;
//    private Provider provider;
//
//    public Geolocation(Context context) {
//        this.provider = Provider.GPS;
//        this.isActive = false;
//        this.context = context;
//        this.locationManager = (LocationManager) context.getSystemService("location");
//        this.locationListener = new Geolocationlistener(this);
//    }
//
//    public Geolocation(Context context, Provider provider) {
//        this(context);
//        this.provider = provider;
//    }
//
//    public Geolocation(Context context, Provider provider, LocationListener locationListener) {
//        this(context, provider);
//        this.locationListener = locationListener;
//    }
//
//    public Location getLocation() {
//        return this.location;
//    }
//
//    public void setLocation(Location location) {
//        this.location = location;
//    }
//
//    public Provider getProvider() {
//        return this.provider;
//    }
//
//    public void setProvider(Provider provider) {
//        if (this.provider != provider) {
//            setIsActive(false);
//            this.provider = provider;
//        }
//    }
//
//    public void setIsActive(boolean isActive) {
//        if (!this.isActive && isActive && IsGeolocationSupported()) {
//            if (getProvider() == Provider.PASSIVE) {
//                List<String> providerList = this.locationManager.getAllProviders();
//                if (providerList.contains(Provider.PROVIDER_GPS)) {
//                    Log.v("agentp2", "LocationProvider GPS - starting");
//                    this.locationManager.requestLocationUpdates(Provider.GPS.getName(), 0, 0.0f, this.locationListener);
//                    Log.v("agentp2", "LocationProvider GPS - ready");
//                }
//                if (providerList.contains(Provider.PROVIDER_NETWORK)) {
//                    Log.v("agentp2", "LocationProvider NETWORK - starting");
//                    this.locationManager.requestLocationUpdates(Provider.NETWORK.getName(), 0, 0.0f, this.locationListener);
//                    Log.v("agentp2", "LocationProvider NETWORK - ready");
//                }
//            } else {
//                this.locationManager.requestLocationUpdates(getProvider().getName(), 0, 0.0f, this.locationListener);
//            }
//            this.isActive = true;
//        } else if (this.isActive && !isActive) {
//            this.locationManager.removeUpdates(this.locationListener);
//            this.isActive = false;
//        }
//    }
//
//    public boolean getIsActive() {
//        if (getProvider() == Provider.PASSIVE) {
//            boolean provaiderEnabled = false;
//            List<String> providerList = this.locationManager.getAllProviders();
//            if (providerList.contains(Provider.PROVIDER_GPS)) {
//                provaiderEnabled = this.locationManager.isProviderEnabled(Provider.GPS.getName());
//            } else if (providerList.contains(Provider.PROVIDER_NETWORK)) {
//                provaiderEnabled = this.locationManager.isProviderEnabled(Provider.NETWORK.getName());
//            }
//            if (provaiderEnabled && this.isActive) {
//                return true;
//            }
//            return false;
//        } else if (this.locationManager.isProviderEnabled(getProvider().getName()) && this.isActive) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public boolean IsGeolocationSupported() {
//        return this.context.getPackageManager().hasSystemFeature(getProvider().getFeatureName());
//    }
//
//    public boolean IsMockLocationAllowed() {
//        if (Secure.getString(this.context.getContentResolver(), "mock_location").equals("0")) {
//            return false;
//        }
//        return true;
//    }
//
//    protected void finalize() throws Throwable {
//        setIsActive(false);
//        super.finalize();
//    }
//}
