package com.gmail.vanyadubik.managerplus.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.ParameterInfo;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.PropertyUtils;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.LOGIN;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_SYNK_TRACK_NAME;
import static com.gmail.vanyadubik.managerplus.common.Consts.PASSWORD;
import static com.gmail.vanyadubik.managerplus.common.Consts.SERVER;

public class SettingsActivity extends AppCompatActivity {
    @Inject
    DataRepository dataRepository;

    private EditText mLoginView, mAddressView, mPasswordView, mTimeSyncView;
    private View signInButton, returnButton, minTimeTrackSyncLayout;
    private Switch using_auto_sync_trackSwitch;


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
        minTimeTrackSyncLayout = (LinearLayout) findViewById(R.id.min_timetrack_sync_layout);

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
        using_auto_sync_trackSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityElements();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();

    }

    private void initData() {

        //getSupportActionBar().setTitle(getString(R.string.item_settings));


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
        String minTime = dataRepository.getUserSetting(MIN_TIME_SYNK_TRACK_NAME);
        if(minTime!=null&&!minTime.isEmpty()){
            mTimeSyncView.setText(minTime);
            using_auto_sync_trackSwitch.setChecked(true);
        }

        setVisibilityElements();
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
            mAddressView.setError(getString(R.string.error_field_required));
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
            dataRepository.insertUserSetting(new ParameterInfo(MIN_TIME_SYNK_TRACK_NAME,
                    String.valueOf(mTimeSyncView.getText())));
        }
        finish();
    }

    private void setVisibilityElements() {
        if (using_auto_sync_trackSwitch.isChecked()) {
            minTimeTrackSyncLayout.setVisibility(View.VISIBLE);
        } else {
            minTimeTrackSyncLayout.setVisibility(View.GONE);
        }
    }
}