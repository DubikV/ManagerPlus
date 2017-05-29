package com.gmail.vanyadubik.managerplus.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking;
import com.gmail.vanyadubik.managerplus.model.ParameterInfo;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService;
import com.gmail.vanyadubik.managerplus.task.TaskSchedure;
import com.gmail.vanyadubik.managerplus.utils.ActivityUtils;
import com.gmail.vanyadubik.managerplus.utils.PropertyUtils;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;

import io.hypertrack.smart_scheduler.Job;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEVELOP_MODE;
import static com.gmail.vanyadubik.managerplus.common.Consts.GPS_SYNK_SERVISE_JOB_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.LOGIN;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_CURRENT_ACCURACY;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_SYNK_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_SYNK_TRACK_NAME;
import static com.gmail.vanyadubik.managerplus.common.Consts.PASSWORD;
import static com.gmail.vanyadubik.managerplus.common.Consts.SERVER;
import static com.gmail.vanyadubik.managerplus.common.Consts.USING_SYNK_TRACK;

public class SettingsActivity extends AppCompatActivity {
    @Inject
    DataRepository dataRepository;
    @Inject
    ActivityUtils activityUtils;

    private EditText mLoginView, mAddressView, mPasswordView, mTimeSyncView, minCurrentAccuracyGPSText;
    private View signInButton, returnButton, minTimeTrackSyncLayout, minCurrentAccuracyGPSL;
    private Switch using_auto_sync_trackSwitch, using_develop_modeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_settings));

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        mAddressView = (EditText) findViewById(R.id.serverAddress);
        mLoginView = (EditText) findViewById(R.id.login);
        mPasswordView = (EditText) findViewById(R.id.password);
        mTimeSyncView = (EditText) findViewById(R.id.min_timetrack_sync_service_edit_text);
        minCurrentAccuracyGPSText = (EditText) findViewById(R.id.min_current_accuracy_edit_taxt);
        minTimeTrackSyncLayout = (LinearLayout) findViewById(R.id.min_timetrack_sync_layout);
        minCurrentAccuracyGPSL = (LinearLayout) findViewById(R.id.min_current_accuracy_layout);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptData();
                    return true;
                }
                return false;
            }
        });

        returnButton = findViewById(R.id.ret_login_button);
        returnButton.setFocusable(true);
        returnButton.setFocusableInTouchMode(true);
        returnButton.requestFocus();
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        using_develop_modeSwitch = (Switch) findViewById(R.id.using_develop_mode);
        using_develop_modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activityUtils.setVisiblyElement(minCurrentAccuracyGPSL, isChecked);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();

    }

    private void initData() {

        String adressServer = dataRepository.getUserSetting(SERVER);
        if (adressServer == null || adressServer.isEmpty()) {
            final String APPLICATION_PROPERTIES = "application.properties";
            try {
                Properties properties = PropertyUtils.getProperties(APPLICATION_PROPERTIES, this.getBaseContext());
                adressServer = properties.getProperty("server") + ":" + properties.getProperty("port");
            } catch (IOException e) {
                adressServer = "";
            }
        }


        mAddressView.setText(adressServer);
        mLoginView.setText(dataRepository.getUserSetting(LOGIN));
        mPasswordView.setText(dataRepository.getUserSetting(PASSWORD));

        if(SharedStorage.getBoolean(getApplicationContext(), USING_SYNK_TRACK, true)){
            String minTime = String.valueOf(
                    SharedStorage.getLong(getApplicationContext(), MIN_TIME_SYNK_TRACK_NAME, MIN_TIME_SYNK_TRACK));
            mTimeSyncView.setText(minTime);
            using_auto_sync_trackSwitch.setChecked(true);
            activityUtils.setVisiblyElement(minTimeTrackSyncLayout, true);
        }

        if(SharedStorage.getBoolean(getApplicationContext(), DEVELOP_MODE, false)){
            minCurrentAccuracyGPSL.setVisibility(View.VISIBLE);
            using_develop_modeSwitch.setChecked(true);
            activityUtils.setVisiblyElement(minCurrentAccuracyGPSL, true);
            minCurrentAccuracyGPSText.setText(dataRepository.getUserSetting(MIN_CURRENT_ACCURACY));
        }

    }

    private void attemptData() {

        mLoginView.setError(null);
        mPasswordView.setError(null);

        String address = mAddressView.getText().toString();
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();
        String minTimeSync = mTimeSyncView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        }

        if (TextUtils.isEmpty(address)) {
            mAddressView.setError(getString(R.string.error_field_required));
            focusView = mAddressView;
            cancel = true;
        }

        if (using_auto_sync_trackSwitch.isChecked()&& TextUtils.isEmpty(minTimeSync)) {
            mTimeSyncView.setError(getString(R.string.error_field_required));
            focusView = mTimeSyncView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            saveSettings();
        }
    }

    private void saveSettings() {

        dataRepository.insertUserSetting(new ParameterInfo(SERVER, String.valueOf(mAddressView.getText())));
        dataRepository.insertUserSetting(new ParameterInfo(LOGIN, String.valueOf(mLoginView.getText())));
        dataRepository.insertUserSetting(new ParameterInfo(PASSWORD, String.valueOf(mPasswordView.getText())));

        if (using_auto_sync_trackSwitch.isChecked()) {

            long interval = Long.valueOf(String.valueOf(mTimeSyncView.getText()));

            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(SharedStorage.APP_PREFS, 0).edit();
            editor.putLong(MIN_TIME_SYNK_TRACK_NAME, interval);
            editor.commit();

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

        String minCurrentAccText = minCurrentAccuracyGPSText.getText().toString();
        int minAccurancy = minCurrentAccText == null || minCurrentAccText.isEmpty() ? 0 :
                Integer.valueOf(minCurrentAccText);
        if(using_develop_modeSwitch.isChecked()) {
            dataRepository.insertUserSetting(
                    new ParameterInfo(MIN_CURRENT_ACCURACY, String.valueOf(minAccurancy)));
        }

        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(SharedStorage.APP_PREFS, 0).edit();
        editor.putBoolean(DEVELOP_MODE, using_develop_modeSwitch.isChecked());
        editor.putBoolean(USING_SYNK_TRACK, using_auto_sync_trackSwitch.isChecked());
        editor.commit();

        finish();
    }
}