package com.gmail.vanyadubik.managerplus.modules;


import android.app.Application;

import com.gmail.vanyadubik.managerplus.utils.GPSTaskUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GPSTaskUtilsApiModule {

    private Application application;

    public GPSTaskUtilsApiModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public GPSTaskUtils getGPSUtils() {
        return new GPSTaskUtils(application.getBaseContext());
    }
}
