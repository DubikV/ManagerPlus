package com.gmail.vanyadubik.managerplus.app;

import android.app.Application;
import android.content.Intent;

import com.gmail.vanyadubik.managerplus.modules.ActivityUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.DataApiModule;
import com.gmail.vanyadubik.managerplus.modules.ErrorUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.GPSTaskUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.NetworkUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.PhoneUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.PhotoFileUtilsApiModule;
import com.gmail.vanyadubik.managerplus.service.gps.GPSTrackerService;
import com.gmail.vanyadubik.managerplus.service.gps.GPSTrackerServiceAndroidAPI;

import net.danlew.android.joda.JodaTimeAndroid;

public class ManagerPlusAplication extends Application{

    DIComponent diComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        diComponent = DaggerDIComponent.builder()
                .dataApiModule(new DataApiModule(this))
                .networkUtilsApiModule(new NetworkUtilsApiModule(this))
                .activityUtilsApiModule(new ActivityUtilsApiModule())
                .errorUtilsApiModule(new ErrorUtilsApiModule(this))
                .phoneUtilsApiModule(new PhoneUtilsApiModule(this))
                .gPSTaskUtilsApiModule(new GPSTaskUtilsApiModule(this))
                .photoFileUtilsApiModule(new PhotoFileUtilsApiModule(this))
                .build();
        //startService(new Intent(this, GPSTrackerService.class));
        startService(new Intent(this, GPSTrackerServiceAndroidAPI.class));
    }

    public DIComponent getComponent() {
        return diComponent;
    }

}
