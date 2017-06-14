package com.gmail.vanyadubik.managerplus.gps.service.old;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSTrackerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, GPSTrackerService.class);
        context.startService(intentService);
    }
}