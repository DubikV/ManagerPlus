//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//public class OnBootReceiver extends BroadcastReceiver {
//    public void onReceive(Context context, Intent intent) {
//        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
//            context.startService(new Intent(context, ServiceGpsTracking.class));
//        }
//    }
//}
