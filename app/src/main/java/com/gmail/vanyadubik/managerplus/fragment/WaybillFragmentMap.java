package com.gmail.vanyadubik.managerplus.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.vanyadubik.managerplus.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class WaybillFragmentMap extends Fragment implements LocationListener, OnMapReadyCallback {
    private static final int LAYOUT = R.layout.fragment_map;
    private View view;
    private GoogleMap mMap;
    private LocationManager locationManager;

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

        return view;
    }

    @Override
    public void onLocationChanged(Location location) {

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

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }


    @Override
    public void onResume() {
        super.onResume();

        setUpMap();

      //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.05870, -82.4090), 15));
    }

    private void setUpMap() {

        SupportMapFragment locationMapFragment;

         if (Build.VERSION.SDK_INT < 21) {
        locationMapFragment = (SupportMapFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.map);
        } else {
            locationMapFragment = (SupportMapFragment)
                    this.getChildFragmentManager().findFragmentById(R.id.map);
        }
        locationMapFragment.getMapAsync(this);


//

    }
//        @Override
//        public void onActivityCreated(Bundle bundle) {
//            super.onActivityCreated(bundle);
//            setUpMap();
//        }
}
