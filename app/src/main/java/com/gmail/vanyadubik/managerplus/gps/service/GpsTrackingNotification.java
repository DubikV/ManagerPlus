package com.gmail.vanyadubik.managerplus.gps.service;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.SettingsLocationActivity;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.PREF_TYPE_SERVICE;

public class GpsTrackingNotification extends Activity {

    public static final int ERROR_SETTING_GPS = 1;
    public static final int ERROR_LOCATION_GPS = 2;

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
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            GpsTrackingNotification.this.startActivity(intent);
            dialog.cancel();
            GpsTrackingNotification.this.finish();
        }
    }

    class SettingsAppButton implements OnClickListener {
        SettingsAppButton() {
        }

        public void onClick(DialogInterface dialog, int id) {
            Intent intent = new Intent(getBaseContext(), SettingsLocationActivity.class);
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
        int typeMessage = getIntent().getIntExtra(GpsTracking.SERVICE_GPS_NOTIFY, 0);
        if (typeMessage > 0) {
            getIntent().removeExtra(GpsTracking.SERVICE_GPS_NOTIFY);
            Locale locale = Locale.US;
            Builder builder = new Builder(this);
            String notifyText;
            if (typeMessage == 2) {
                GpsTracking gpsTracking = new GpsTracking(this);
                GpsTracking.GpsData gpsData = gpsTracking.getLastGpsData();
                notifyText = getResources().getString(R.string.service_tracking_last_gps);
                if(gpsData == null){
                    notifyText = notifyText + ": " + getResources().getString(R.string.not_found);
                }else {
                    notifyText = notifyText + ": "
                            + "\n" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(gpsData._date)
                            + "\n" + new DecimalFormat("#.####").format(gpsData._latitude) +
                            " : " + new DecimalFormat("#.####").format(gpsData._longitude);
                }
                builder.setTitle(R.string.service_tracking_error_location).
                        setMessage(notifyText).setCancelable(false).
                        setNegativeButton(R.string.questions_answer_ok, new DissmisButton());
            }else {
                boolean isGPSEnabled = ((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS);
                String str = "%s%s%s";
                Object[] objArr = new Object[4];
                objArr[0] = SharedStorage.getInteger(this, GpsTracking.PREF_INTERVAL, 0) == 0 ? getResources().getString(R.string.service_tracking_null_interval) : "";
                objArr[1] = !isGPSEnabled ? getResources().getString(R.string.gps_is_disabled) : "";
                objArr[2] = SharedStorage.getInteger(getApplicationContext(), PREF_TYPE_SERVICE, 0) > 2 ? getResources().getString(R.string.service_tracking_null_type) : "";
                notifyText = String.format(locale, str, objArr);
                builder.setTitle(R.string.service_tracking_error_message).
                        setMessage(notifyText).setCancelable(false).
                        setNegativeButton(R.string.questions_answer_ok, new DissmisButton());

                if (!isGPSEnabled) {
                    builder.setNeutralButton(R.string.action_settings, new SettingsButton());
                } else {
                    builder.setNeutralButton(R.string.action_settings, new SettingsAppButton());
                }
            }
            builder.create().show();
        }
   }

}