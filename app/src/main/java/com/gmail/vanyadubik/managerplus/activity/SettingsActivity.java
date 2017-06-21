package com.gmail.vanyadubik.managerplus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking;
import com.gmail.vanyadubik.managerplus.gps.service.TypeServiceGPS;
import com.gmail.vanyadubik.managerplus.model.ParameterInfo;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.ActivityUtils;
import com.gmail.vanyadubik.managerplus.utils.PropertyUtils;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEVELOP_MODE;
import static com.gmail.vanyadubik.managerplus.common.Consts.LOGIN;
import static com.gmail.vanyadubik.managerplus.common.Consts.PASSWORD;
import static com.gmail.vanyadubik.managerplus.common.Consts.SERVER;
import static com.gmail.vanyadubik.managerplus.common.Consts.USING_GPSTRACKING;
import static com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.PREF_TYPE_SERVICE;

public class SettingsActivity extends AppCompatActivity {
    @Inject
    DataRepository dataRepository;
    @Inject
    ActivityUtils activityUtils;

    private EditText mLoginView, mAddressView, mPasswordView;
    private View signInButton, returnButton, settingslocation;
    private Switch using_develop_modeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_settings));

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        mAddressView = (EditText) findViewById(R.id.serverAddress);
        mLoginView = (EditText) findViewById(R.id.login);
        mPasswordView = (EditText) findViewById(R.id.password);

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

        settingslocation = (LinearLayout) findViewById(R.id.settings_location_layout);
        settingslocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SettingsLocationActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
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

        using_develop_modeSwitch = (Switch) findViewById(R.id.using_develop_mode);

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

        if(SharedStorage.getBoolean(getApplicationContext(), DEVELOP_MODE, false)){
            using_develop_modeSwitch.setChecked(true);
        }

    }

    private void attemptData() {

        mLoginView.setError(null);
        mPasswordView.setError(null);

        String address = mAddressView.getText().toString();
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

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

        SharedStorage.setBoolean(getApplicationContext(), DEVELOP_MODE, using_develop_modeSwitch.isChecked());

        if(SharedStorage.getBoolean(getApplicationContext(), USING_GPSTRACKING, false)){
            GpsTracking gpsTracking = new GpsTracking(getApplicationContext());
            gpsTracking.sendNewPreferences(GpsTracking.RENEW_PREFERENCES);
        }

        finish();
    }
}