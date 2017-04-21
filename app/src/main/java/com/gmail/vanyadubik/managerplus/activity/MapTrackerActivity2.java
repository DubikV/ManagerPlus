package com.gmail.vanyadubik.managerplus.activity;

import android.Manifest;
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

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.gps.DirectionsJSONParser;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;
import com.gmail.vanyadubik.managerplus.model.map.MarkerMap;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.gps.GoogleLocationService;
import com.gmail.vanyadubik.managerplus.service.gps.LocationUpdateListener;
import com.gmail.vanyadubik.managerplus.utils.GPSTaskUtils;
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

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.R.id.map;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_TIME_MAP_ANIMATE_CAMERA;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_ZOOM_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_LOCATION_MAP_CHECK_NAVIGATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_SPEED_MAP_SET_ZOOM;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_LOCATION_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_MAP_ANIMATE_CAMERA;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_ZOOM_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;
import static com.gmail.vanyadubik.managerplus.common.Consts.TILT_CAMERA_MAP;
import static com.gmail.vanyadubik.managerplus.common.Consts.TYPE_PRIORITY_CONNECTION_GPS;
import static com.gmail.vanyadubik.managerplus.common.Consts.WIDTH_POLYLINE_MAP;

public class MapTrackerActivity2 extends AppCompatActivity implements OnMapReadyCallback {

    @Inject
    DataRepository dataRepository;
    @Inject
    GPSTaskUtils gpsTaskUtils;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setTitle(getResources().getString(R.string.map_track_route));

       // ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        googleLocationService = new GoogleLocationService(getApplicationContext(),
                TYPE_PRIORITY_CONNECTION_GPS, MIN_TIME_LOCATION_MAP,
                MIN_TIME_LOCATION_MAP, MIN_DISTANCE_LOCATION_MAP, new LocationUpdateListener() {
            @Override
            public void canReceiveLocationUpdates() {
            }

            @Override
            public void cannotReceiveLocationUpdates() {
            }

            @Override
            public void updateLocation(Location location) {
                if ( gpsTaskUtils.isBetterLocation(location, lastCurrentLocation,
                        MIN_TIME_LOCATION_MAP, MAX_COEFFICIENT_CURRENCY_LOCATION) ) {

                    oldCurrentLocation = lastCurrentLocation;

                    lastCurrentLocation = location;
                }
                insertMarker();

                setOtherTracks();
            }

            @Override
            public void updateLocationName(String localityName, Location location) {

               // googleLocationService.stopLocationUpdates();
            }
        });

        moveMarker = true;
        polylineNavigation = new ArrayList<>();

        setUpMap();

//        FloatingActionButton zoomUpButton = (FloatingActionButton)
//                findViewById(R.id.zoom_up);
//        zoomUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAGLOG, "Press button 'zoomUpButton'");
//                if (mMap != null) {
//                    mMap.animateCamera(CameraUpdateFactory
//                            .zoomTo(mMap.getCameraPosition().zoom + DIVISION_ZOOM_MAP));
//                }
//
//            }
//        });
//
//        FloatingActionButton zoomDownButton = (FloatingActionButton)
//                findViewById(R.id.zoom_down);
//        zoomDownButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAGLOG, "Press button 'zoomDownButton'");
//                if (mMap != null) {
//                    mMap.animateCamera(CameraUpdateFactory
//                            .zoomTo(mMap.getCameraPosition().zoom - DIVISION_ZOOM_MAP));
//                }
//
//            }
//        });

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
        //mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);

        setOtherMarkers();

    }

    @Override
    protected void onResume() {
        super.onResume();

        googleLocationService.startUpdates();

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

        insertMarker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(googleLocationService == null){
            return;
        }
        googleLocationService.stopLocationUpdates();
        googleLocationService.closeGoogleApi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(googleLocationService == null){
            return;
        }
        googleLocationService.stopLocationUpdates();
        googleLocationService.closeGoogleApi();
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
                        .width(WIDTH_POLYLINE_MAP)
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
//            markerOptions.rotation(
//                    lastCurrentLocation!=null && oldCurrentLocation != null ?
//                            lastCurrentLocation.bearingTo(oldCurrentLocation) : 0);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            if(moveMarker) {
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(
                        lastCurrentLocation.getSpeed() < MIN_SPEED_MAP_SET_ZOOM  ?
                                MIN_ZOOM_MAP : MAX_ZOOM_MAP));
            }

            setCameraPosition(oldCurrentLocation, lastCurrentLocation);
        }

        setPolylineTrack();

    }

    private void setCameraPosition(Location firstLocation, Location secondLocation) {

        if (moveMarker) {

            if (firstLocation == null || secondLocation == null) {
                return;
            }

            float targetBearing = secondLocation.bearingTo(firstLocation);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(secondLocation.getLatitude(),
                            secondLocation.getLongitude()))
                    .bearing(targetBearing + 530)
                    .tilt(TILT_CAMERA_MAP)
                    .zoom(lastCurrentLocation.getSpeed() < MIN_SPEED_MAP_SET_ZOOM  ?
                            MIN_ZOOM_MAP : MAX_ZOOM_MAP)
                    .build();

            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    lastCurrentLocation.getSpeed() < MIN_SPEED_MAP_SET_ZOOM ?
                            MIN_TIME_MAP_ANIMATE_CAMERA :
                            MAX_TIME_MAP_ANIMATE_CAMERA,
                    null);
        }

    }

    private void setWidthPolylines() {

        if (polylineTrack!=null) {

            polylineTrack.setWidth(
                    (float)(MIN_DISTANCE_WRITE_TRACK * mMap.getCameraPosition().zoom)/MAX_ZOOM_MAP);
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

    private void setOtherTracks(){

        if(locationCheckNavigation==null) {
            locationCheckNavigation = lastCurrentLocation;
        }else {
            if (lastCurrentLocation.distanceTo(locationCheckNavigation)
                    < MIN_DISTANCE_LOCATION_MAP_CHECK_NAVIGATION && polylineNavigation.size() > 0) {
                return;
            }
        }

        if (polylineNavigation!=null){
            for (Polyline polyline : polylineNavigation){
                polyline.remove();
            }
            polylineNavigation.clear();
        }

        if (mMap != null && lastCurrentLocation !=null && markerMaps != null) {

            LatLng oldPoint = new LatLng(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude());
            for (MarkerMap markerMap : markerMaps) {

                String url = getDirectionsUrl(oldPoint, markerMap.getLatLng());

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

                //new GetDirection(oldPoint, markerMap.getLatLng()).execute();
                oldPoint = markerMap.getLatLng();
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

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d(TAGLOG,e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            if(result == null){
                return;
            }
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions()
                        .width(WIDTH_POLYLINE_MAP)
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

            if(lineOptions != null) {
                polylineNavigation.add(mMap.addPolyline(lineOptions));
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

}
