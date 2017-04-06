package com.gmail.vanyadubik.managerplus.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.db.Waybill_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.joda.time.LocalDateTime;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.R.id.map;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;

public class MapActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    @Inject
    DataRepository dataRepository;

    private GoogleMap mMap;
    private SupportMapFragment locationMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastCurrentLocation;
    private Marker mCurrLocationMarker;
    private Polyline polylineTrack;
    private PolylineOptions pOptions;
    private FloatingActionButton current_position;
    private Boolean moveMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        moveMarker = true;

        setPolylineOptions();

        setUpMap();

        FloatingActionButton zoomUpButton = (FloatingActionButton)
                findViewById(R.id.zoom_up);
        zoomUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'zoomUpButton'");
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory
                            .zoomTo(mMap.getCameraPosition().zoom + 0.5f));
                }

            }
        });

        FloatingActionButton zoomDownButton = (FloatingActionButton)
                findViewById(R.id.zoom_down);
        zoomDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'zoomDownButton'");
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory
                            .zoomTo(mMap.getCameraPosition().zoom - 0.5f));
                }

            }
        });

        FloatingActionButton sateliteButton = (FloatingActionButton)
                findViewById(R.id.satelite);
        sateliteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'sateliteButton'");
                if (mMap != null) {
                    switch (mMap.getMapType()) {
                        case GoogleMap.MAP_TYPE_NORMAL:
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            return;
                        case GoogleMap.MAP_TYPE_SATELLITE:
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            return;
                        case GoogleMap.MAP_TYPE_HYBRID:
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            return;
                        default:
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }

                }

            }
        });


        FloatingActionButton trafficButton = (FloatingActionButton)
                findViewById(R.id.traffic);
        trafficButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'trafficButton'");
                if (mMap != null) {
                    if (mMap.isTrafficEnabled()) {
                        mMap.setTrafficEnabled(false);
                    } else {
                        mMap.setTrafficEnabled(true);
                    }

                }

            }
        });

        current_position = (FloatingActionButton)
                findViewById(R.id.current_position);
        current_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'current_position'");
                moveMarker = true;
                current_position.setImageDrawable(getResources().getDrawable(R.drawable.ic_location));
                insertMarker();
            }
        });

    }


    @Override
    public void onLocationChanged(Location location) {
        if ( isBetterLocation(location, lastCurrentLocation) ) {
            lastCurrentLocation = location;
        }
        insertMarker();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(TYPE_PRIORITY_CONNECTION_GPS);
        mLocationRequest.setInterval(MIN_TIME_WRITE_TRACK);
        mLocationRequest.setFastestInterval(MIN_TIME_WRITE_TRACK);
        mLocationRequest.setSmallestDisplacement(MIN_DISTANCE_WRITE_TRACK);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap != null) {
            return;
        }
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                current_position.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_search));
                moveMarker = false;
            }
        });

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Waybill_Element waybill = dataRepository.getLastWaybill();

        if(waybill==null){
            return;
        }

        setPolylineOptions();

        pOptions = dataRepository.getBuildTrackLatLng(pOptions, waybill.getDateStart(),
                waybill.getDateEnd().getTime() <1000 ? LocalDateTime.now().toDate() : waybill.getDateEnd());

        insertMarker();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient == null){
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient == null){
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void setUpMap() {

//        if (Build.VERSION.SDK_INT < 21) {
            locationMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
//        } else {
//            locationMapFragment = (SupportMapFragment)
//                    this.getChildFragmentManager().findFragmentById(map);
//        }
        locationMapFragment.getMapAsync(this);

        locationMapFragment.getView().setClickable(true);



    }

    private void setPolylineOptions(){

        if(pOptions != null){
            return;
        }

        pOptions = new PolylineOptions()
                .width(5)
                .color(getResources().getColor(R.color.colorPrimary))
                .geodesic(true);
    }

    private void setPolylineTrack() {

        if (polylineTrack!=null){
            polylineTrack.remove();
        }


        if(pOptions!=null && mMap != null) {

            polylineTrack = mMap.addPolyline(pOptions);

        }


    }

    private void insertMarker() {

        setPolylineTrack();

        if(lastCurrentLocation!=null&&mMap!=null) {

            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            //Place current location marker
            LatLng latLng = new LatLng(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            //markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            if(moveMarker) {
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            }
        }

    }


    protected boolean isBetterLocation(Location location, Location currentBestLocation) {

        Toast.makeText(this, " Accuracy: " + location.getAccuracy() +
                "  \nSpeed: " + location.getSpeed(), Toast.LENGTH_LONG).show();

        if (currentBestLocation == null) {
            return true;
        }

        if (!isLocationAccurate(location) ||
                location.getAccuracy() > MAX_COEFFICIENT_CURRENCY_LOCATION ) {
            return false;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > MIN_TIME_WRITE_TRACK * 2;
        boolean isSignificantlyOlder = timeDelta < -MIN_TIME_WRITE_TRACK * 2;
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

    private boolean isSameProvider(String provider1, String provider2) {
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(this.getString(R.string.gps_is_setting));

        // Setting Dialog Message
        alertDialog.setMessage(this.getString(R.string.gps_is_enabled_open_settings));

        // On pressing Settings button
        alertDialog.setPositiveButton(this.getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(this.getString(R.string.questions_title_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    //    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
//        double earthRadius = 6371; //kilometers
//        double dLat = Math.toRadians(lat2-lat1);
//        double dLng = Math.toRadians(lng2-lng1);
//        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLng/2) * Math.sin(dLng/2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        float dist = (float) (earthRadius * c);
//
//        return dist;
//    }
}
