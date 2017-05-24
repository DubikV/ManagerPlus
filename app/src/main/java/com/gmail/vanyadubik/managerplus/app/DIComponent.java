package com.gmail.vanyadubik.managerplus.app;

import com.gmail.vanyadubik.managerplus.activity.AddedPhotosActivity;
import com.gmail.vanyadubik.managerplus.activity.ClientDetailActivity;
import com.gmail.vanyadubik.managerplus.activity.FuelDetailActivity;
import com.gmail.vanyadubik.managerplus.activity.GalleryActivity;
import com.gmail.vanyadubik.managerplus.activity.MapActivity;
import com.gmail.vanyadubik.managerplus.activity.MapTrackerActivity;
import com.gmail.vanyadubik.managerplus.activity.SettingsActivity;
import com.gmail.vanyadubik.managerplus.activity.StartActivity;
import com.gmail.vanyadubik.managerplus.activity.VisitDetailActivity;
import com.gmail.vanyadubik.managerplus.fragment.ClientListFragment;
import com.gmail.vanyadubik.managerplus.fragment.FuelListFragment;
import com.gmail.vanyadubik.managerplus.fragment.VisitListFragment;
import com.gmail.vanyadubik.managerplus.fragment.WaybillListFragment;
import com.gmail.vanyadubik.managerplus.fragment.WorkPlaseFragment;
import com.gmail.vanyadubik.managerplus.gps.service.android.ServiceGpsTracking;
import com.gmail.vanyadubik.managerplus.modules.ActivityUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.DataApiModule;
import com.gmail.vanyadubik.managerplus.modules.ErrorUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.GPSTaskUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.NetworkUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.PhoneUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.PhotoFileUtilsApiModule;
import com.gmail.vanyadubik.managerplus.service.gps.GPSTrackerService;
import com.gmail.vanyadubik.managerplus.service.gps.GPSTrackerServiceAndroidAPI;
import com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService;
import com.gmail.vanyadubik.managerplus.task.SyncIntentService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataApiModule.class, NetworkUtilsApiModule.class,
        ActivityUtilsApiModule.class, ErrorUtilsApiModule.class,
        PhoneUtilsApiModule.class, GPSTaskUtilsApiModule.class,
        PhotoFileUtilsApiModule.class})
public interface DIComponent {

    void inject(StartActivity startActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(MapTrackerActivity mapTrackerActivity);

    void inject(MapActivity mapActivity);

    void inject(ClientDetailActivity clientDetailActivity);

    void inject(WorkPlaseFragment workPlaseFragment);

    void inject(WaybillListFragment waybillListFragment);

    void inject(FuelListFragment fuelListFragment);

    void inject(VisitListFragment visitListFragment);

    void inject(VisitDetailActivity visitDetailActivity);

    void inject(ClientListFragment clientListFragment);

    void inject(FuelDetailActivity fuelDetailActivity);

    void inject(AddedPhotosActivity addedPhotosActivity);

    void inject(GalleryActivity galleryActivity);

    void inject(GPSTrackerService gpsTrackerService);

    void inject(GPSTrackerServiceAndroidAPI gpsTrackerServiceAndroidAPI);

    void inject(ServiceGpsTracking serviceGpsTracking);

    void inject(SyncIntentTrackService syncIntentTrackService);

    void inject(SyncIntentService syncIntentService);
}
