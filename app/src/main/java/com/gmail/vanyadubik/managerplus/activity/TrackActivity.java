package com.gmail.vanyadubik.managerplus.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.common.Consts;
import com.gmail.vanyadubik.managerplus.gps.GPSTracker;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.gps.GPSTrackerService;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

public class TrackActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @Inject
    DataRepository dataRepository;

    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

       // ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        gpsTracker = new GPSTracker(TrackActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // create class object

                // check if GPS enabled
                if(gpsTracker.canGetLocation()){

                    LocationPoint locationPoint = gpsTracker.getLocationPoint();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(),
                            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                    .format(locationPoint.getDateTime().getMillis())
                                    + " location is - \nLat: " + locationPoint.getLatitude()
                                    + "\nLong: " + locationPoint.getLongitude(),
                            Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gpsTracker.showSettingsAlert();
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (!isGPSTrackerServiceRunning(GPSTrackerService.class)) {

            Intent ishintent = new Intent(TrackActivity.this, GPSTrackerService.class);
            PendingIntent pintent = PendingIntent.getService(TrackActivity.this, 0, ishintent, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pintent);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * Consts.MIN_TIME_WRITE_TRACK, pintent);

            if (gpsTracker.canGetLocation()) {
                gpsTracker.showSettingsAlert();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isGPSTrackerServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}