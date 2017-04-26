package com.gmail.vanyadubik.managerplus.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.DirectionsJSONParser;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;
import com.gmail.vanyadubik.managerplus.model.map.MarkerMap;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.gps.GoogleLocationService;
import com.gmail.vanyadubik.managerplus.service.gps.LocationUpdateListener;
import com.gmail.vanyadubik.managerplus.utils.GPSTaskUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.R.id.map;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_LOCATION_MAP_CHECK_NAVIGATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_ZOOM_TITLE_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;
import static com.gmail.vanyadubik.managerplus.common.Consts.TILT_CAMERA_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.TIME_MAP_ANIMATE_CAMERA;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;
import static com.gmail.vanyadubik.managerplus.common.Consts.WIDTH_POLYLINE_MAP;

public class MapTrackerActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String MAP_TTACK_ZOOM_PREF = "map_track_zoom";

    @Inject
    DataRepository dataRepository;
    @Inject
    GPSTaskUtils gpsTaskUtils;

    private SharedPreferences mPreferences;
    private GoogleMap mMap;
    private SupportMapFragment locationMapFragment;
    private GoogleLocationService googleLocationService;
    private Location lastCurrentLocation, locationCheckNavigation, oldCurrentLocation;
    private Marker mCurrLocationMarker, mOtherLocationMarker;
    private Polyline polylineTrack;
    private List<Polyline> polylineNavigation;
    private FloatingActionButton current_position;
    private Boolean moveMarker;
    private Waybill_Document waybill;
    private List<MarkerMap> markerMaps;
    private SeekBar sbZoom;
    private TextView sbZoomProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setTitle(getResources().getString(R.string.map_track_route));

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
                                + "\nAccuracy: " + location.getAccuracy()
                                + "\nTime: " + (lastCurrentLocation!=null ?
                                String.valueOf((location.getTime() - lastCurrentLocation.getTime())/1000) : "0")
                                + "\nDistance: " + (lastCurrentLocation!=null ?
                                String.valueOf(location.distanceTo(lastCurrentLocation)) : "0"),
                        Toast.LENGTH_LONG).show();

                if ( gpsTaskUtils.isBetterLocation(location, lastCurrentLocation,
                        MIN_TIME_LOCATION_MAP, MAX_COEFFICIENT_CURRENCY_LOCATION) ) {

                    oldCurrentLocation = lastCurrentLocation;

                    lastCurrentLocation = location;
                }

                setCameraPosition();

                setPolylineTrack();

                setOtherTracks();
            }

            @Override
            public void startLocation(Location location) {
                lastCurrentLocation = location;

                insertMarker();
            }

        });
        googleLocationService.setTypePriorityConnection(TYPE_PRIORITY_CONNECTION_GPS);
        googleLocationService.setTimeInterval(MIN_TIME_LOCATION_MAP);
        // googleLocationService.setFastesInterval(MIN_TIME_LOCATION_MAP);
        googleLocationService.setDistance(MIN_DISTANCE_LOCATION_MAP);
        googleLocationService.startUpdates();

        moveMarker = true;
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
                        .zoom(sbZoom.getProgress()*mMap.getMaxZoomLevel()/sbZoom.getMax())//sbZoom.getProgress() / 10.0f + 10.0f)
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

        current_position = (FloatingActionButton)
                findViewById(R.id.current_position);
        current_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'current_position'");
                moveMarker = true;
                current_position.setImageDrawable(getResources().getDrawable(R.drawable.ic_location));

                setCameraPosition();
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

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                current_position.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_search));
                moveMarker = false;
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                setWidthPolylines();
            }
        });

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        //mMap.setMyLocationEnabled(true);

        setOtherMarkers();

        setPolylineTrack();

    }

    @Override
    protected void onResume() {
        super.onResume();

        googleLocationService.startLocationUpdates();

        waybill = dataRepository.getLastWaybill();

        if (waybill!=null) {
            Date dateEnd = waybill.getDateEnd();
            if (dateEnd.getTime() < 1000) {
                dateEnd = LocalDateTime.now().toDate();
                dateEnd.setHours(23);
                dateEnd.setMinutes(59);
                dateEnd.setSeconds(59);
            }

            markerMaps = dataRepository.getBuildVisitsMarkers(waybill.getDateStart(), dateEnd);

        }

        setCameraPosition();
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

        mPreferences.edit().putInt(MAP_TTACK_ZOOM_PREF, sbZoom.getProgress()).apply();
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

    private void setPolylineTrack() {

        if(waybill==null){
            return;
        }

        if (polylineTrack!=null){
            polylineTrack.remove();
        }

        PolylineOptions pOptions = dataRepository.getBuildTrackLatLng(
                new PolylineOptions()
                        .width((float)(WIDTH_POLYLINE_MAP * mMap.getCameraPosition().zoom)/mMap.getMaxZoomLevel())
                        .color(getResources().getColor(R.color.colorPrimary))
                        .geodesic(true),
                waybill.getDateStart(),
                waybill.getDateEnd().getTime() <1000 ?
                        LocalDateTime.now().toDate() : waybill.getDateEnd());

        if(pOptions!=null && mMap != null) {

            polylineTrack = mMap.addPolyline(pOptions);

        }
    }

    private void insertMarker() {

        if(lastCurrentLocation!=null && mMap!=null) {

            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            LatLng latLng = new LatLng(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.anchor(0.5f, 0.5f);
            //  markerOptions.rotation(270);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            if(moveMarker) {
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(
                        sbZoom.getProgress()*mMap.getMaxZoomLevel()/sbZoom.getMax()));//sbZoom.getProgress() / 10.0f + 10.0f));
            }

        }

    }

    private void setOtherMarkers(){
        if (mOtherLocationMarker != null) {
            mOtherLocationMarker.remove();
        }

        if(markerMaps!=null) {
            for (MarkerMap markerMap : markerMaps) {

                IconGenerator iconFactory = new IconGenerator(this);
                iconFactory.setRotation(90);
                iconFactory.setContentRotation(-90);
                iconFactory.setStyle(IconGenerator.STYLE_BLUE);

                MarkerOptions markerOptions = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(markerMap.getName())))
                        .position(new LatLng(markerMap.getLatLng().latitude, markerMap.getLatLng().longitude))
                        .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

                mOtherLocationMarker = mMap.addMarker(markerOptions);
            }
        }
    }


    private void setCameraPosition() {

        if (lastCurrentLocation == null) {
            return;
        }

        if (moveMarker) {

            if (mCurrLocationMarker == null) {
                insertMarker();
            }

            LatLng position = new LatLng(lastCurrentLocation.getLatitude(),
                    lastCurrentLocation.getLongitude());

            mCurrLocationMarker.setPosition(position);

            float targetBearing = (float) 0.0;

            if (oldCurrentLocation != null && lastCurrentLocation != null) {
                targetBearing = lastCurrentLocation.bearingTo(oldCurrentLocation);
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)
                    .bearing(targetBearing + 530)
                    .tilt(sbZoom.getProgress() > MIN_ZOOM_TITLE_MAP ? TILT_CAMERA_MAP : 0)
                    .zoom(sbZoom.getProgress()*mMap.getMaxZoomLevel()/sbZoom.getMax())//sbZoom.getProgress() / 10.0f + 10.0f)
                    .build();

            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    TIME_MAP_ANIMATE_CAMERA,
                    null);
        }

    }

    private void setWidthPolylines() {

        float polyWidth = (float)(WIDTH_POLYLINE_MAP * mMap.getCameraPosition().zoom)/mMap.getMaxZoomLevel();

        if (polylineTrack!=null) {

            polylineTrack.setWidth(polyWidth);
        }

        if (polylineNavigation!=null) {
            for(Polyline polyline : polylineNavigation){
                polyline.setWidth(polyWidth);
            }

        }
    }

    private void setOtherTracks(){

        if(locationCheckNavigation==null) {
            locationCheckNavigation = lastCurrentLocation;
        }else {
            if (lastCurrentLocation.distanceTo(locationCheckNavigation)
                    < MIN_DISTANCE_LOCATION_MAP_CHECK_NAVIGATION && polylineNavigation!=null) {
                return;
            }
        }

        if (mMap != null && lastCurrentLocation !=null && markerMaps != null) {

            LatLng oldPoint = new LatLng(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude());

            if (polylineNavigation == null){
                int i = 0;
                for (MarkerMap markerMap : markerMaps) {
                    String url = getDirectionsUrl(oldPoint, markerMap.getLatLng());
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(new ParamDownloadTask(i ,url));
                    oldPoint = markerMap.getLatLng();
                    i++;
                }
            }else {
                MarkerMap markerMap = markerMaps.get(0);
                String url = getDirectionsUrl(oldPoint, markerMap.getLatLng());
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(new ParamDownloadTask(0 ,url));
            }
        }

        locationCheckNavigation = lastCurrentLocation;
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();

            br.close();
        }catch(Exception e){
            Log.d(TAGLOG, e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<ParamDownloadTask, Void, ParamDownloadTask> {
        @Override
        protected ParamDownloadTask doInBackground(ParamDownloadTask... param) {
            ParamDownloadTask paramData = param[0];
            try{
                String data = downloadUrl(paramData.getData());
                paramData.setData(data);
            }catch(Exception e){
                Log.d(TAGLOG,e.toString());
            }
            return paramData;
        }
        @Override
        protected void onPostExecute(ParamDownloadTask result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<ParamDownloadTask, Integer, ParamParserTask >{

        @Override
        protected ParamParserTask doInBackground(ParamDownloadTask... data) {

            ParamDownloadTask jsonData = data[0];
            ParamParserTask routesData = new ParamParserTask(jsonData.getId(), null);
            try{
                JSONObject jObject = new JSONObject(jsonData.getData());
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routesData.setData(parser.parse(jObject));
            }catch(Exception e){
                e.printStackTrace();
            }
            return routesData;
        }

        @Override
        protected void onPostExecute(ParamParserTask data) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            List<List<HashMap<String,String>>> result = data.getData();

            if(result == null){
                return;
            }
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions()
                        .width((float)(WIDTH_POLYLINE_MAP * mMap.getCameraPosition().zoom)/mMap.getMaxZoomLevel())
                        .color(getResources().getColor(R.color.colorBlue))
                        .geodesic(true);

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
            }

            if(polylineNavigation==null) {
                polylineNavigation = new ArrayList<>();
            }

            if(lineOptions != null) {
                try{
                    Polyline polyline = polylineNavigation.get(data.getId());
                    if(polyline!=null) {
                        polyline.remove();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                try{
                    polylineNavigation.set(data.getId(), mMap.addPolyline(lineOptions));
                }catch(Exception e){
                    e.printStackTrace();
                    polylineNavigation.add(mMap.addPolyline(lineOptions));
                }
            }

//            if(points!=null) {
//                LatLng firstLatLng = points.get(0);
//                Location firstLocation = new Location("service Provider");
//                firstLocation.setLatitude(firstLatLng.latitude);
//                firstLocation.setLongitude(firstLatLng.longitude);
//
//                LatLng secondLatLng = points.get(0);
//                Location secondLocation = new Location("service Provider");
//                secondLocation.setLatitude(secondLatLng.latitude);
//                secondLocation.setLongitude(secondLatLng.longitude);
//
//                setCameraPosition(firstLocation, secondLocation);
//            }
        }
    }

    protected class ParamDownloadTask{
        private int id;
        private String data;

        public ParamDownloadTask(int id, String data) {
            this.id = id;
            this.data = data;
        }

        public int getId() {
            return id;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    private class ParamParserTask{
        private int id;
        private List<List<HashMap<String,String>>> data;

        public ParamParserTask(int id, List<List<HashMap<String, String>>> data) {
            this.id = id;
            this.data = data;
        }

        public int getId() {
            return id;
        }

        public List<List<HashMap<String, String>>> getData() {
            return data;
        }

        public void setData(List<List<HashMap<String, String>>> data) {
            this.data = data;
        }
    }


}
