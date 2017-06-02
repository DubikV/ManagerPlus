package com.gmail.vanyadubik.managerplus.gps.service;


import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import java.util.Locale;

public class GpsTrackingNotification extends Activity {

    class DissmisButton implements OnClickListener {
        DissmisButton() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            GpsTrackingNotification.this.finish();
        }
    }

    class SettingsButton implements OnClickListener {
        SettingsButton() {
        }

        public void onClick(DialogInterface dialog, int id) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setAction("android.settings.LOCATION_SOURCE_SETTINGS");
            intent.addCategory("android.intent.category.DEFAULT");
            GpsTrackingNotification.this.startActivity(intent);
            dialog.cancel();
            GpsTrackingNotification.this.finish();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_tracking_notification);
    }

    public void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("fromServiceGpsTrackingNotify", false)) {
            getIntent().removeExtra("fromServiceGpsTrackingNotify");
            boolean isGPSEnabled = ((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS);
            Locale locale = Locale.US;
            String str = "%s%s%s%s";
            Object[] objArr = new Object[4];
            objArr[0] = SharedStorage.getInteger(this, "gpsTrackingInterval", 0) == 0 ? getResources().getString(R.string.service_tracking_null_interval) : BuildConfig.VERSION_NAME;
            objArr[1] = SharedStorage.getInteger(this, "gpsTrackingPeriod", 0) == 0 ? getResources().getString(R.string.service_tracking_null_period) : BuildConfig.VERSION_NAME;
            objArr[2] = !isGPSEnabled ? getResources().getString(R.string.gps_is_disabled) : BuildConfig.VERSION_NAME;
            objArr[3] = SharedStorage.getString(this, "gpsTrackingServerAddress", BuildConfig.VERSION_NAME).equals(BuildConfig.VERSION_NAME) ? getResources().getString(R.string.service_tracking_null_address) : BuildConfig.VERSION_NAME;
            String notifyText = String.format(locale, str, objArr);
            Builder builder = new Builder(this);
            builder.setTitle(R.string.service_tracking_error_message).setMessage(notifyText).setCancelable(false).setNegativeButton(R.string.questions_answer_ok, new DissmisButton());
            if (!isGPSEnabled) {
                builder.setNeutralButton(R.string.button_goto_gps_settings, new SettingsButton());
            }
            builder.create().show();
        }
    }
}