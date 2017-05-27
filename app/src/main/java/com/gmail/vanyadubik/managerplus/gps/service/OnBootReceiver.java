package com.gmail.vanyadubik.managerplus.gps.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, ServiceGpsTracking.class);
        context.startService(intentService);
    }
}
