package com.gmail.vanyadubik.managerplus.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking;
import com.gmail.vanyadubik.managerplus.gps.service.TypeServiceGPS;
import com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService;
import com.gmail.vanyadubik.managerplus.task.TaskSchedure;
import com.gmail.vanyadubik.managerplus.utils.ActivityUtils;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.hypertrack.smart_scheduler.Job;

import static com.gmail.vanyadubik.managerplus.common.Consts.GPS_SYNK_SERVISE_JOB_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.MAX_COEFFICIENT_CURRENCY_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_SYNK_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_SYNK_TRACK_NAME;
import static com.gmail.vanyadubik.managerplus.common.Consts.USING_GPSTRACKING;
import static com.gmail.vanyadubik.managerplus.common.Consts.USING_SYNK_TRACK;
import static com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.PREF_ACCURACY;
import static com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.PREF_DAYS;
import static com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.PREF_INTERVAL;
import static com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.PREF_TIME_END;
import static com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.PREF_TIME_START;
import static com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.PREF_TYPE_SERVICE;

public class SettingsLocationActivity extends AppCompatActivity {

    @Inject
    ActivityUtils activityUtils;

    private EditText mTimeWriteTrack, mTimeStartTrack, mTimeEndTrack, minCurrentAccuracyGPS, mTimeSyncTrack;
    private View signInButton, returnButton, minTimeTrackSyncLayout, settingsLocationLayout;
    private Switch using_auto_sync_trackSwitch, using_gps_tracking;
    private Spinner mSpinner, typeGPSTrackingSpinner;
    private List<String> listSetDays = new ArrayList<String>();
    private Boolean usingGPSTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_location);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_settings_location));

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        settingsLocationLayout = (LinearLayout) findViewById(R.id.settings_location_l);

        using_gps_tracking = (Switch) findViewById(R.id.using_gps_tracing_switch);
        using_gps_tracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activityUtils.setVisiblyElement(settingsLocationLayout, isChecked);
                usingGPSTracker = isChecked;
            }
        });

        mTimeWriteTrack = (EditText) findViewById(R.id.min_time_gpstracking_edit_taxt);
        mTimeStartTrack = (EditText) findViewById(R.id.starttime_gpstracking_edit_taxt);
        mTimeStartTrack.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    mTimeStartTrack.setText("");
                }
            }
        });
        mTimeStartTrack.addTextChangedListener(new TextWatcher() {
            int length_before = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length_before = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (length_before < s.length()) {
                    if (s.length() == 2) {
                        s.append(":");
                    }
                }
            }
        });

        mTimeEndTrack = (EditText) findViewById(R.id.endtime_gpstracking_edit_taxt);
        mTimeEndTrack.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    mTimeEndTrack.setText("");
                }
            }
        });
        mTimeEndTrack.addTextChangedListener(new TextWatcher() {
            int length_before = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length_before = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (length_before < s.length()) {
                    if (s.length() == 2) {
                        s.append(":");
                    }
                }
            }
        });

        mTimeSyncTrack = (EditText) findViewById(R.id.min_timetrack_sync_service_edit_text);
        minCurrentAccuracyGPS = (EditText) findViewById(R.id.min_current_accuracy_edit_taxt);

        minTimeTrackSyncLayout = (LinearLayout) findViewById(R.id.min_timetrack_sync_layout);

        mSpinner = (Spinner) findViewById(R.id.gpstracking_spinner);

        listSetDays.add("5");
        listSetDays.add("7");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.item_with_spinner,listSetDays);

        adapter.setDropDownViewResource(R.layout.item_with_spinner);
        mSpinner.setAdapter(adapter);

        typeGPSTrackingSpinner = (Spinner) findViewById(R.id.typegpstracking_spinner);

        List<String> listType = new ArrayList<>();
        listType.add(TypeServiceGPS.SERVICE_GPS_ANDROID.getName());
        listType.add(TypeServiceGPS.SERVICE_GPS_ANDROID_PLAY.getName());
        listType.add(TypeServiceGPS.SERVICE_GPS_GOOGLE_PLAY.getName());

        adapter = new ArrayAdapter<String>(this,
                R.layout.item_with_spinner,listType);

        adapter.setDropDownViewResource(R.layout.item_with_spinner);
        typeGPSTrackingSpinner.setAdapter(adapter);

        returnButton = findViewById(R.id.ret_login_button);
        returnButton.setFocusable(true);
        returnButton.setFocusableInTouchMode(true);
        returnButton.requestFocus();
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });

        signInButton = findViewById(R.id.login_sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptData();
            }
        });

        using_auto_sync_trackSwitch = (Switch) findViewById(R.id.using_track_sync_service);
        using_auto_sync_trackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activityUtils.setVisiblyElement(minTimeTrackSyncLayout, isChecked);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();

    }

    private void initData() {

        using_gps_tracking.setChecked(SharedStorage.getBoolean(getApplicationContext(), USING_GPSTRACKING, false));
        activityUtils.setVisiblyElement(settingsLocationLayout, using_gps_tracking.isChecked());

        mTimeWriteTrack.setText(String.valueOf(SharedStorage.getInteger(getApplicationContext(), PREF_INTERVAL, 5)));

        int timeStart = SharedStorage.getInteger(getApplicationContext(), PREF_TIME_START, 360);

        mTimeStartTrack.setText(String.format("%02d", Integer.valueOf(timeStart/60)) +":"
                + String.format("%02d", timeStart - (Integer.valueOf(timeStart/60)*60)));

        int timeEnd = SharedStorage.getInteger(getApplicationContext(), PREF_TIME_END, 1320);

        mTimeEndTrack.setText(String.format("%02d", Integer.valueOf(timeEnd/60)) +":"
                + String.format("%02d", timeEnd - (Integer.valueOf(timeEnd/60)*60)));

        minCurrentAccuracyGPS.setText(String.valueOf(SharedStorage.getDouble(getApplicationContext(),
                PREF_ACCURACY, MAX_COEFFICIENT_CURRENCY_LOCATION)));

        String days = String.valueOf(SharedStorage.getInteger(getApplicationContext(), PREF_DAYS, 7));
        mSpinner.setSelection(listSetDays.indexOf(days));

        int indexTypeGps = SharedStorage.getInteger(getApplicationContext(), PREF_TYPE_SERVICE, 0);
        typeGPSTrackingSpinner.setSelection(indexTypeGps);

        if(SharedStorage.getBoolean(getApplicationContext(), USING_SYNK_TRACK, true)){
            long period = SharedStorage.getLong(getApplicationContext(), MIN_TIME_SYNK_TRACK_NAME, MIN_TIME_SYNK_TRACK);
            period = period/60;
            mTimeSyncTrack.setText(String.valueOf(period));
            using_auto_sync_trackSwitch.setChecked(true);
            activityUtils.setVisiblyElement(minTimeTrackSyncLayout, true);
        }

    }

    private void attemptData() {

        String mintTimeWrite = mTimeWriteTrack.getText().toString();
        mTimeWriteTrack.setError(null);

        String timeStart = mTimeStartTrack.getText().toString();
        mTimeStartTrack.setError(null);

        String timeEnd = mTimeEndTrack.getText().toString();
        mTimeEndTrack.setError(null);

        String timeSync = mTimeSyncTrack.getText().toString();
        mTimeSyncTrack.setError(null);

        String minAccuracy = minCurrentAccuracyGPS.getText().toString();
        minCurrentAccuracyGPS.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (usingGPSTracker) {
            if (TextUtils.isEmpty(mintTimeWrite)) {
                mTimeWriteTrack.setError(getString(R.string.error_field_required));
                focusView = mTimeWriteTrack;
                cancel = true;
            }

            if (TextUtils.isEmpty(timeStart)) {
                mTimeStartTrack.setError(getString(R.string.error_field_required));
                focusView = mTimeStartTrack;
                cancel = true;
            }

            if (TextUtils.isEmpty(timeEnd)) {
                mTimeEndTrack.setError(getString(R.string.error_field_required));
                focusView = mTimeEndTrack;
                cancel = true;
            }

            if (TextUtils.isEmpty(minAccuracy)) {
                minCurrentAccuracyGPS.setError(getString(R.string.error_field_required));
                focusView = minCurrentAccuracyGPS;
                cancel = true;
            }

            if (using_auto_sync_trackSwitch.isChecked() && TextUtils.isEmpty(timeSync)) {
                mTimeSyncTrack.setError(getString(R.string.error_field_required));
                focusView = mTimeSyncTrack;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            saveSettings();

            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    private void saveSettings() {

        if (using_auto_sync_trackSwitch.isChecked()) {

            long interval = Long.valueOf(String.valueOf(mTimeSyncTrack.getText()))*60;

            SharedStorage.setBoolean(getApplicationContext(), USING_SYNK_TRACK, using_auto_sync_trackSwitch.isChecked());
            SharedStorage.setLong(getApplicationContext(), MIN_TIME_SYNK_TRACK_NAME, interval);

            if (interval > 0 && interval != MIN_TIME_SYNK_TRACK) {
                TaskSchedure taskTrackerSync = new TaskSchedure.Builder(SyncIntentTrackService.class, getApplicationContext())
                        .jobID(GPS_SYNK_SERVISE_JOB_ID)
                        .jobType(Job.Type.JOB_TYPE_HANDLER)
                        .jobNetworkType(Job.NetworkType.NETWORK_TYPE_ANY)
                        .requiresCharging(false)
                        .interval(interval)
                        .build();
                taskTrackerSync.startTask();
            }
        }

        SharedStorage.setBoolean(getApplicationContext(), USING_GPSTRACKING, using_gps_tracking.isChecked());
        SharedStorage.setInteger(getApplicationContext(), PREF_INTERVAL, Integer.valueOf(String.valueOf(mTimeWriteTrack.getText())));
        int minutesStart = ((Integer.valueOf(Character.toString(mTimeStartTrack.getText().charAt(0))+
                Character.toString(mTimeStartTrack.getText().charAt(1)))) * 60) +
                (Integer.valueOf(Character.toString(mTimeStartTrack.getText().charAt(3))+
                        Character.toString(mTimeStartTrack.getText().charAt(4))));
        SharedStorage.setInteger(getApplicationContext(), PREF_TIME_START, minutesStart);

        int minutesEnd = ((Integer.valueOf(Character.toString(mTimeEndTrack.getText().charAt(0))+
                Character.toString(mTimeEndTrack.getText().charAt(1)))) * 60) +
                (Integer.valueOf(Character.toString(mTimeEndTrack.getText().charAt(3))+
                        Character.toString(mTimeEndTrack.getText().charAt(4))));

        SharedStorage.setInteger(getApplicationContext(), PREF_TIME_END, minutesEnd);
        SharedStorage.setDouble(getApplicationContext(),  PREF_ACCURACY, Double.valueOf(String.valueOf(minCurrentAccuracyGPS.getText())));
        SharedStorage.setInteger(getApplicationContext(), PREF_DAYS, Integer.valueOf(String.valueOf(mSpinner.getSelectedItem().toString())));
        SharedStorage.setInteger(getApplicationContext(), PREF_TYPE_SERVICE, TypeServiceGPS.getIndexbyName(typeGPSTrackingSpinner.getSelectedItem().toString()));

        if(usingGPSTracker){
            GpsTracking gpsTracking = new GpsTracking(getApplicationContext());
            gpsTracking.sendNewPreferences(GpsTracking.RENEW_PREFERENCES);
        }
        finish();
    }
}