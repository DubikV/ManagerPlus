package com.gmail.vanyadubik.managerplus.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.gps.DirectionsJSONParser;
import com.gmail.vanyadubik.managerplus.model.db.Client_Element;
import com.gmail.vanyadubik.managerplus.model.db.Waybill_Element;
import com.gmail.vanyadubik.managerplus.model.map.MarkerMap;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import org.joda.time.LocalDateTime;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.R.id.map;
import static com.gmail.vanyadubik.managerplus.common.Consts.DIVISION_ZOOM_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_ZOOM_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_LOCATION_MAP_CHECK_NAVIGATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_SPEED_MAP_SET_ZOOM;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_ZOOM_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;
import static com.gmail.vanyadubik.managerplus.common.Consts.TILT_CAMERA_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;
import static com.gmail.vanyadubik.managerplus.common.Consts.WIDTH_POLYLINE_MAP;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
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

    private GoogleMap mMap;
    private SupportMapFragment locationMapFragment;
    private LatLng currentLocation;
    private int typeShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setTitle(getResources().getString(R.string.map_name));

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

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
                insertMarker(currentLocation);
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
                    showAskAlert();
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

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            typeShow = extras.getInt(MAP_TYPE);
        }else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.map_not_init_param),
                    Toast.LENGTH_LONG).show();
            finish();
        }

        Double lat = extras.getDouble(MAP_SHOW_POSITION_LAT);
        Double lon = extras.getDouble(MAP_SHOW_POSITION_LON);
        currentLocation = new LatLng(lat, lon);

        if(currentLocation==null) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.map_not_init_location),
                    Toast.LENGTH_LONG).show();
            finish();
            finish();
        }

        switch (typeShow) {
            case MAP_TYPE_SHOW_TRACK:
                getSupportActionBar().setTitle(getResources().getString(R.string.map_track_route));
                Date dateStart = new Date(extras.getInt(MAP_SHOW_TRACK_DATE_START));
                Date dateEnd = new Date(extras.getInt(MAP_SHOW_TRACK_DATE_END));
                showTrack(dateStart, dateEnd);
                return;
            case MAP_TYPE_GET_LOCATION:
                getSupportActionBar().setTitle(getResources().getString(R.string.map_select_location));
                insertMarker(currentLocation);
                return;
            case MAP_TYPE_SHOW_POSITION:
                getSupportActionBar().setTitle(getResources().getString(R.string.map_you_position));
                insertMarker(currentLocation);
                return;
            default:
                insertMarker(currentLocation);
        }

    }



    private void insertMarker(LatLng latLng) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getResources().getString(R.string.map_you_position));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM_MAP));

    }

    private void showTrack(Date dateStart, Date dateEnd){

        PolylineOptions pOptions = dataRepository.getBuildTrackLatLng(
                new PolylineOptions()
                        .width(WIDTH_POLYLINE_MAP)
                        .color(getResources().getColor(R.color.colorPrimary))
                        .geodesic(true), dateStart, dateEnd);

        if(pOptions!=null && mMap != null) {

            mMap.addPolyline(pOptions);

        }

    }

    public void showAskAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.map_coordinates));
        builder.setMessage(this.getString(R.string.map_get_location));
        builder.setPositiveButton(this.getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent();
                intent.putExtra(MAP_SHOW_POSITION_LAT, String.valueOf(currentLocation.latitude));
                intent.putExtra(MAP_SHOW_POSITION_LON, String.valueOf(currentLocation.longitude));
                setResult(RESULT_OK, intent);
            }
        });

        builder.setNegativeButton(this.getString(R.string.questions_title_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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
}

