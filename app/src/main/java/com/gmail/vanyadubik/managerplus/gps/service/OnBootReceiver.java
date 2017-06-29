package com.gmail.vanyadubik.managerplus.gps.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking;

import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;

public class OnBootReceiver extends BroadcastReceiver {
    final String POWEROFF_1;
    final String POWEROFF_2;
    final String POWERON_1;
    final String POWERON_2;
    final String POWERON_3;
    final String POWERON_4;

    public OnBootReceiver() {
        this.POWEROFF_1 = Intent.ACTION_SHUTDOWN;
        this.POWEROFF_2 = "android.intent.action.QUICKBOOT_POWEROFF";
        this.POWERON_1 = Intent.ACTION_BOOT_COMPLETED;
        this.POWERON_2 = "android.intent.action.QUICKBOOT_POWERON";
        this.POWERON_3 = "com.htc.intent.action.QUICKBOOT_POWERON";
        this.POWERON_4 = Intent.ACTION_REBOOT;
    }

    public void onReceive(Context context, Intent intent) {
        Log.i(TAGLOG_GPS, "BroadcastReceiver on receive");
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN) ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWEROFF")) {
            Log.i(TAGLOG_GPS, "Boot receive Stop ServiceGpsTracking ");
            new GpsTracking(context.getApplicationContext()).stopGpsTracking();
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals(Intent.ACTION_REBOOT) ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") ||
                intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON")) {
            Log.i(TAGLOG_GPS, "Boot receive Start ServiceGpsTracking ");
            new GpsTracking(context.getApplicationContext()).startGpsTracking();
        }
    }
}
