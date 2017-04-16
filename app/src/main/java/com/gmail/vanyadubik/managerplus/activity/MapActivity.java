package com.gmail.vanyadubik.managerplus.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.GPSTaskUtils;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.Date;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.R.id.map;
import static com.gmail.vanyadubik.managerplus.common.Consts.DIVISION_ZOOM_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_ZOOM_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;
import static com.gmail.vanyadubik.managerplus.common.Consts.WIDTH_POLYLINE_MAP;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback  {
    public static final String MAP_TYPE = "map_type";
    public static final int MAP_TYPE_SHOW_TRACK = 1;
    public static final int MAP_TYPE_GET_LOCATION = 2;
    public static final int MAP_TYPE_SHOW_POSITION  = 3;
    public static final String MAP_SHOW_TRACK_DATE_START = "map_activity_datestart";
    public static final String MAP_SHOW_TRACK_DATE_END = "map_activity_dateend";
    public static final String MAP_SHOW_POSITION_LAT = "map_activity_lat";
    public static final String MAP_SHOW_POSITION_LON = "map_activity_lon";

    @Inject
    DataRepository dataRepository;
    @Inject
    GPSTaskUtils gpsTaskUtils;

    private GoogleMap mMap;
    private SupportMapFragment locationMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastCurrentLocation;
    private Marker mCurrLocationMarker;
    private Bundle extras;
    private int typeShow;
    private Boolean moveMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setTitle(getResources().getString(R.string.map_name));

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        typeShow = 0;

        setUpMap();

        FloatingActionButton zoomUpButton = (FloatingActionButton)
                findViewById(R.id.zoom_up);
        zoomUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'zoomUpButton'");
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory
                            .zoomTo(mMap.getCameraPosition().zoom + DIVISION_ZOOM_MAP));
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
                            .zoomTo(mMap.getCameraPosition().zoom - DIVISION_ZOOM_MAP));
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

        FloatingActionButton current_position = (FloatingActionButton)
                findViewById(R.id.current_position);
        current_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'current_position'");
                moveMarker = true;
                insertMarker(lastCurrentLocation);
                moveMarker = false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_back) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap != null) {
            return;
        }
        mMap = googleMap;

        if (typeShow == MAP_TYPE_GET_LOCATION) {
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    showAskAlert(latLng);
                }
            });
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);

        switch (typeShow) {
            case MAP_TYPE_SHOW_TRACK:
                moveMarker = false;
                getSupportActionBar().setTitle(getResources().getString(R.string.map_track_route));
                Date dateStart = new Date(Long.valueOf(extras.getString(MAP_SHOW_TRACK_DATE_START)));
                Date dateEnd = new Date(Long.valueOf(extras.getString(MAP_SHOW_TRACK_DATE_END)));
                showTrack(dateStart, dateEnd);
                return;
            case MAP_TYPE_GET_LOCATION:
                getSupportActionBar().setTitle(getResources().getString(R.string.map_select_location));
                moveMarker = true;
                insertMarker(lastCurrentLocation);
                moveMarker = false;
                return;
            case MAP_TYPE_SHOW_POSITION:
                getSupportActionBar().setTitle(getResources().getString(R.string.map_you_position));
                moveMarker = true;
                insertMarker(lastCurrentLocation);
                return;
            default:
                moveMarker = true;
                insertMarker(lastCurrentLocation);
        }

    }

    private void setUpMap() {

        locationMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);

        locationMapFragment.getMapAsync(this);

        locationMapFragment.getView().setClickable(true);



    }

    @Override
    protected void onResume() {
        super.onResume();

        extras = getIntent().getExtras();

        if (extras != null) {
            typeShow = extras.getInt(MAP_TYPE);
        }else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.map_not_init_param),
                    Toast.LENGTH_LONG).show();
            finish();
        }

        String lat = extras.getString(MAP_SHOW_POSITION_LAT);
        String lon = extras.getString(MAP_SHOW_POSITION_LON);

        if(lat!=null && lon !=null) {
            lastCurrentLocation = new Location("service Provider");
            lastCurrentLocation.setLatitude(Double.valueOf(lat));
            lastCurrentLocation.setLongitude(Double.valueOf(lon));

        }

        if(lastCurrentLocation==null&&typeShow==MAP_TYPE_SHOW_POSITION) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.map_not_init_location),
                    Toast.LENGTH_LONG).show();
        }
    }



    private void insertMarker(Location location) {

        if(location==null){
            return;
        }

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(
                new LatLng(location.getLatitude(), location.getLongitude()));
        markerOptions.title(getResources().getString(R.string.map_you_position));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        if(moveMarker) {
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(location.getLatitude(), location.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM_MAP));
        }

    }

    private void showTrack(Date dateStart, Date dateEnd){

        PolylineOptions pOptions = dataRepository.getBuildTrackLatLng(
                new PolylineOptions()
                        .width(WIDTH_POLYLINE_MAP)
                        .color(getResources().getColor(R.color.colorPrimary))
                        .geodesic(true), dateStart, dateEnd);

        if(pOptions!=null && mMap != null) {

            mMap.addPolyline(pOptions);

            if(pOptions.getPoints().size()>0) {
                LatLng firstloc = pOptions.getPoints().get(0);
                if (firstloc != null) {
                    moveMarker = true;
                    Location location = new Location("service Provider");
                    location.setLatitude(firstloc.latitude);
                    location.setLongitude(firstloc.longitude);
                    insertMarker(location);
                    moveMarker = false;
                }
            }

        }

    }

    public void showAskAlert(final LatLng latLng){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.map_coordinates));
        builder.setMessage(getString(R.string.map_get_location));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra(MAP_SHOW_POSITION_LAT,
                        String.valueOf(
                                (double)Math.round(latLng.latitude * 1000000d) / 1000000d));
                intent.putExtra(MAP_SHOW_POSITION_LON,
                        String.valueOf(
                                (double)Math.round(latLng.longitude * 1000000d) / 1000000d));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        // TODO: (end stub) ------------------

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(TYPE_PRIORITY_CONNECTION_GPS);
        mLocationRequest.setInterval(MIN_TIME_LOCATION_MAP);
        mLocationRequest.setFastestInterval(MIN_TIME_LOCATION_MAP);
        mLocationRequest.setSmallestDisplacement(MIN_DISTANCE_LOCATION_MAP);
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
    public void onLocationChanged(Location location) {
        if ( gpsTaskUtils.isBetterLocation(location, lastCurrentLocation,
                MIN_TIME_LOCATION_MAP, MAX_COEFFICIENT_CURRENCY_LOCATION) ) {
            lastCurrentLocation = location;
        }
        insertMarker(lastCurrentLocation);
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
}

