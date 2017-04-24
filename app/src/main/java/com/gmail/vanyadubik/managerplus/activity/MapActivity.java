package com.gmail.vanyadubik.managerplus.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.gps.GoogleLocationService;
import com.gmail.vanyadubik.managerplus.service.gps.LocationUpdateListener;
import com.gmail.vanyadubik.managerplus.utils.GPSTaskUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.R.id.map;
import static com.gmail.vanyadubik.managerplus.activity.MapTrackerActivity.MAP_TTACK_ZOOM_PREF;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_ZOOM_TITLE_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;
import static com.gmail.vanyadubik.managerplus.common.Consts.TILT_CAMERA_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.TIME_MAP_ANIMATE_CAMERA;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;
import static com.gmail.vanyadubik.managerplus.common.Consts.WIDTH_POLYLINE_MAP;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback  {
    public static final String MAP_TYPE = "map_type";
    public static final int MAP_TYPE_SHOW_TRACK = 1;
    public static final int MAP_TYPE_GET_LOCATION = 2;
    public static final int MAP_TYPE_SHOW_POSITION  = 3;
    public static final String MAP_SHOW_TRACK_DATE_START = "map_activity_datestart";
    public static final String MAP_SHOW_TRACK_DATE_END = "map_activity_dateend";
    public static final String MAP_SHOW_POSITION_LAT = "map_activity_lat";
    public static final String MAP_SHOW_POSITION_LON = "map_activity_lon";
    public static final String MAP_ZOOM_PREF = "map_zoom";

    @Inject
    DataRepository dataRepository;
    @Inject
    GPSTaskUtils gpsTaskUtils;

    private SharedPreferences mPreferences;
    private GoogleMap mMap;
    private SupportMapFragment locationMapFragment;
    private GoogleLocationService googleLocationService;
    private Location lastCurrentLocation;
    private Marker mCurrLocationMarker;
    private Bundle extras;
    private int typeShow;
    private Boolean moveMarker;
    private SeekBar sbZoom;
    private TextView sbZoomProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setTitle(getResources().getString(R.string.map_name));

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        mPreferences = getPreferences(Context.MODE_PRIVATE);

        googleLocationService = new GoogleLocationService(this, new LocationUpdateListener() {
            @Override
            public void canReceiveLocationUpdates() {
            }

            @Override
            public void cannotReceiveLocationUpdates(String exception) {
                Toast.makeText(getApplicationContext(), exception, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateLocation(Location location) {

                Toast.makeText(getApplicationContext(),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                .format(location.getTime())
                                + " location is - \nLat: " + location.getLatitude()
                                + "\nLong: " + location.getLongitude()
                                + "\nSpeed: " + location.getSpeed()
                                + "\nDistance: " + location.distanceTo(lastCurrentLocation)
                                + "\nTime: " + String.valueOf((location.getTime() - lastCurrentLocation.getTime())/1000)
                                + "\nAccuracy: " + location.getAccuracy(),
                        Toast.LENGTH_LONG).show();

                if ( gpsTaskUtils.isBetterLocation(location, lastCurrentLocation,
                        MIN_TIME_LOCATION_MAP, MAX_COEFFICIENT_CURRENCY_LOCATION) ) {
                    lastCurrentLocation = location;
                }
                insertMarker(lastCurrentLocation);
            }

            @Override
            public void startLocation(Location location) {
                initData();
            }

        });
        googleLocationService.setTypePriorityConnection(TYPE_PRIORITY_CONNECTION_GPS);
        googleLocationService.setTimeInterval(MIN_TIME_LOCATION_MAP);
        // googleLocationService.setFastesInterval(MIN_TIME_LOCATION_MAP);
        googleLocationService.setDistance(MIN_DISTANCE_LOCATION_MAP);
        googleLocationService.startUpdates();

        typeShow = 0;

        setUpMap();

        sbZoomProgress = (TextView) findViewById(R.id.sbZoom_progress);
        sbZoom = (SeekBar) findViewById(R.id.sbZoom);

        // Initial zoom level
        sbZoom.setProgress(mPreferences.getInt(MAP_TTACK_ZOOM_PREF, 70));

        sbZoomProgress.setText(sbZoom.getProgress() + "%");

        sbZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progresValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progresValue = progress;
                sbZoomProgress.setText(progress + "%");

                CameraPosition position = CameraPosition.builder(mMap.getCameraPosition())
                        .tilt(progress > MIN_ZOOM_TITLE_MAP ? TILT_CAMERA_MAP : 0)
                        .zoom(sbZoom.getProgress()*mMap.getMaxZoomLevel()/sbZoom.getMax())
                        .build();

                CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                mMap.animateCamera(update, TIME_MAP_ANIMATE_CAMERA, null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sbZoomProgress.setText(progresValue + "%");
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

    }

    private void setUpMap() {

        locationMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);

        locationMapFragment.getMapAsync(this);

        locationMapFragment.getView().setClickable(true);



    }

    @Override
    protected void onResume() {
        super.onResume();

        googleLocationService.startLocationUpdates();

        extras = getIntent().getExtras();

        if (extras != null) {
            typeShow = extras.getInt(MAP_TYPE);
        }else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.map_not_init_param),
                    Toast.LENGTH_LONG).show();
            finish();
        }

        if(typeShow == MAP_TYPE_SHOW_TRACK){
            return;
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

        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(getResources().getString(R.string.map_you_position));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        if(moveMarker) {
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(
                    sbZoom.getProgress()*mMap.getMaxZoomLevel()/sbZoom.getMax()));
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
                    lastCurrentLocation = new Location("service Provider");
                    lastCurrentLocation.setLatitude(firstloc.latitude);
                    lastCurrentLocation.setLongitude(firstloc.longitude);
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
    protected void onDestroy() {
        super.onDestroy();
        if (googleLocationService != null) {
            googleLocationService.stopLocationUpdates();
        }
        googleLocationService.startGoogleApi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleLocationService != null) {
            googleLocationService.stopLocationUpdates();
        }

        mPreferences.edit().putInt(MAP_ZOOM_PREF, sbZoom.getProgress()).apply();
    }

    private void initData(){
        switch (typeShow) {
            case MAP_TYPE_SHOW_TRACK:
                moveMarker = false;
                getSupportActionBar().setTitle(getResources().getString(R.string.map_track_route));
                Date dateStart = new Date(Long.valueOf(extras.getString(MAP_SHOW_TRACK_DATE_START)));
                Date dateEnd = new Date(Long.valueOf(extras.getString(MAP_SHOW_TRACK_DATE_END)));
                showTrack(dateStart, dateEnd);
                moveMarker = true;
                insertMarker(lastCurrentLocation);
                moveMarker = false;
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
}

