package com.gmail.vanyadubik.managerplus.gps.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.vanyadubik.managerplus.gps.service.android.ServiceGpsTracking;

public class OnBootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            context.startService(new Intent(context, ServiceGpsTracking.class));
        }
    }
}