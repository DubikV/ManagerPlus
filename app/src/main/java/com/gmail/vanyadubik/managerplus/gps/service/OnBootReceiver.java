package com.gmail.vanyadubik.managerplus.gps.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {
    final String POWEROFF_1;
    final String POWEROFF_2;
    final String POWERON_1;
    final String POWERON_2;
    final String POWERON_3;

    public OnBootReceiver() {
        this.POWEROFF_1 = Intent.ACTION_SHUTDOWN;
        this.POWEROFF_2 = "android.intent.action.QUICKBOOT_POWEROFF";
        this.POWERON_1 = Intent.ACTION_BOOT_COMPLETED;
        this.POWERON_2 = "android.intent.action.QUICKBOOT_POWERON";
        this.POWERON_3 = "com.htc.intent.action.QUICKBOOT_POWERON";
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN) || intent.getAction().equals("android.intent.action.QUICKBOOT_POWEROFF")) {
            new GpsTracking(context.getApplicationContext()).stopGpsTracking();
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") || intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON")) {
            new GpsTracking(context.getApplicationContext()).startGpsTracking();
        }
    }
}
