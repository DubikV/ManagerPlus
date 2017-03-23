package com.gmail.vanyadubik.managerplus.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.GPSTracker;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.gps.GPSTrackerService;
import com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService;
import com.gmail.vanyadubik.managerplus.task.SyncIntentService;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import io.hypertrack.smart_scheduler.SmartScheduler;

import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_SYNK_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_WRITE_TRACK;

public class TrackActivity extends AppCompatActivity {
    @Inject
    DataRepository dataRepository;

    @Inject
    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

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

        Button btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(gpsTracker.canGetLocation()){
                    LocationPoint locationPoint = gpsTracker.getLocationPoint();
                    Toast.makeText(getApplicationContext(),
                            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                    .format(locationPoint.getDate().getTime())
                                    + " location is - \nLat: " + locationPoint.getLatitude()
                                    + "\nLong: " + locationPoint.getLongitude(),
                            Toast.LENGTH_LONG).show();
                }else{
                    gpsTracker.showSettingsAlert();
                }

            }
        });

        Button btnSync = (Button) findViewById(R.id.btnSync);
        btnSync.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(TrackActivity.this, SyncIntentService.class);
                startService(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        startServices();

        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
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

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startRegisterService(Class<?> serviceClass, long delayInSeconds){

        if (!isServiceRunning(serviceClass)) {

            final int SDK_INT = Build.VERSION.SDK_INT;

            Intent ishintent = new Intent(TrackActivity.this, serviceClass);
            PendingIntent pintent = PendingIntent.getService(TrackActivity.this, 0, ishintent, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pintent);

            if (SDK_INT < Build.VERSION_CODES.KITKAT) {
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * delayInSeconds, pintent);
            }
            else  {
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * delayInSeconds,  pintent);
            }

        }

    }

    private void startServices(){

//        startRegisterService(GPSTrackerService.class, MIN_TIME_WRITE_TRACK);
//
//        startRegisterService(SyncIntentTrackService.class, MIN_TIME_SYNK_TRACK);

        SmartScheduler jobScheduler = SmartScheduler.getInstance(this);

    }
}
