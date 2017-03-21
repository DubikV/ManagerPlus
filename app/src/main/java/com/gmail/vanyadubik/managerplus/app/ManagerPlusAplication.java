package com.gmail.vanyadubik.managerplus.app;

import android.app.Application;

import com.gmail.vanyadubik.managerplus.modules.DataApiModule;

import net.danlew.android.joda.JodaTimeAndroid;

public class ManagerPlusAplication extends Application{

    DIComponent diComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        diComponent = DaggerDIComponent.builder()
                .dataApiModule(new DataApiModule(this))
                .build();
    }

    public DIComponent getComponent() {
        return diComponent;
    }

}
