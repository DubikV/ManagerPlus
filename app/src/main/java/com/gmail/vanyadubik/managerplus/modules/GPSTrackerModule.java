package com.gmail.vanyadubik.managerplus.modules;

import android.app.Application;

import com.gmail.vanyadubik.managerplus.gps.GPSTracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GPSTrackerModule {

    private Application application;

    public GPSTrackerModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public GPSTracker getGPSTracker() {
        return new GPSTracker(application.getBaseContext());
    }
}
