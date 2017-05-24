package com.gmail.vanyadubik.managerplus.utils;

import android.app.ActivityManager;
import android.content.Context;

public class ServiceUtils {

    private Context mContext;

    public ServiceUtils(Context mContext) {
        this.mContext = mContext;
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
