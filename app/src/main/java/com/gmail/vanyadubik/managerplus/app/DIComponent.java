package com.gmail.vanyadubik.managerplus.app;

import com.gmail.vanyadubik.managerplus.activity.TrackActivity;
import com.gmail.vanyadubik.managerplus.modules.ActivityUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.DataApiModule;
import com.gmail.vanyadubik.managerplus.modules.ErrorUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.NetworkUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.PhoneUtilsApiModule;
import com.gmail.vanyadubik.managerplus.service.gps.GPSTrackerService;
import com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService;
import com.gmail.vanyadubik.managerplus.task.SyncIntentService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataApiModule.class,
        NetworkUtilsApiModule.class, ActivityUtilsApiModule.class, ErrorUtilsApiModule.class, PhoneUtilsApiModule.class})
public interface DIComponent {

    void inject(TrackActivity trackActivity);

    void inject(GPSTrackerService gpsTrackerService);

    void inject(SyncIntentTrackService syncIntentTrackService);

    void inject(SyncIntentService syncIntentService);
}
