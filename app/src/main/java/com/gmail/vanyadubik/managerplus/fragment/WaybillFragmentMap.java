package com.gmail.vanyadubik.managerplus.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.common.Consts;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;
import static com.gmail.vanyadubik.managerplus.R.id.map;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class WaybillFragmentMap extends Fragment implements LocationListener,
        OnMapReadyCallback, FragmentBecameVisibleInterface {
    private static final int LAYOUT = R.layout.fragment_map;
    private GoogleMap mMap;
    private SupportMapFragment locationMapFragment;
    private LocationManager locationManager;
    private Location lastCurrentLocation;

    public static WaybillFragmentMap getInstance() {

        Bundle args = new Bundle();
        WaybillFragmentMap fragment = new WaybillFragmentMap();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT, null, false);

        setUpMap();

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        FloatingActionButton zoomUpButton = (FloatingActionButton)
                view.findViewById(R.id.zoom_up);
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
                view.findViewById(R.id.zoom_down);
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
                view.findViewById(R.id.satelite);
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
                view.findViewById(R.id.traffic);
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

        FloatingActionButton current_position = (FloatingActionButton)
                view.findViewById(R.id.current_position);
        current_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'current_position'");
                insertMarker();

            }
        });

        startLocation();

        return view;
    }

    @Override
    public void onLocationChanged(Location mLocation) {
        lastCurrentLocation = mLocation;
        insertMarker();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap != null) {
            return;
        }
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public void onResume() {
        super.onResume();

        insertMarker();
    }


    @Override
    public void onBecameVisible() {
        insertMarker();
    }

    private void setUpMap() {

         if (Build.VERSION.SDK_INT < 21) {
        locationMapFragment = (SupportMapFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(map);
        } else {
            locationMapFragment = (SupportMapFragment)
                    this.getChildFragmentManager().findFragmentById(map);
        }
        locationMapFragment.getMapAsync(this);

    }

    private void insertMarker() {

        if(lastCurrentLocation!=null&&mMap!=null) {

            mMap.clear();
            LatLng position = new LatLng(Math.round(lastCurrentLocation.getLatitude() * 10000d)/ 10000d,
                    Math.round(lastCurrentLocation.getLongitude() * 10000d)/ 10000d);
            mMap.addMarker(new MarkerOptions().position(position).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(position,
                            17));

        }
        if(lastCurrentLocation == null){
           startLocation();
        }

    }

    private void startLocation() {

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        try {

            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingsAlert();
            } else {

                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000 * Consts.MIN_TIME_WRITE_TRACK,
                            Consts.MIN_DISTANCE_WRITE_TRACK, this);
                    Log.d("TAGLOG_GPS", "GPS Enabled");
                    if (locationManager != null) {
                        lastCurrentLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }

                if (isNetworkEnabled) {
                    if (lastCurrentLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                1000 * Consts.MIN_TIME_WRITE_TRACK,
                                Consts.MIN_DISTANCE_WRITE_TRACK, this);
                        Log.d("TAGLOG_GPS", "Network");
                        if (locationManager != null) {
                            lastCurrentLocation = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle(this.getString(R.string.gps_is_setting));

        // Setting Dialog Message
        alertDialog.setMessage(this.getString(R.string.gps_is_enabled_open_settings));

        // On pressing Settings button
        alertDialog.setPositiveButton(this.getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
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
}
