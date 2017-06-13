package com.gmail.vanyadubik.managerplus.app;

import android.app.Application;

import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking;
import com.gmail.vanyadubik.managerplus.modules.ActivityUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.DataApiModule;
import com.gmail.vanyadubik.managerplus.modules.ErrorUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.GPSTaskUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.NetworkUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.PhoneUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.PhotoFileUtilsApiModule;
import com.gmail.vanyadubik.managerplus.modules.ServiceUtilsApiModule;
import com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService;
import com.gmail.vanyadubik.managerplus.task.TaskSchedure;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import net.danlew.android.joda.JodaTimeAndroid;

import io.hypertrack.smart_scheduler.Job;

import static com.gmail.vanyadubik.managerplus.common.Consts.GPS_SYNK_SERVISE_JOB_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_SYNK_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_TIME_SYNK_TRACK_NAME;
import static com.gmail.vanyadubik.managerplus.common.Consts.USING_SYNK_TRACK;

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
                .serviceUtilsApiModule(new ServiceUtilsApiModule(this))
                .build();

        startServices();
    }

    public DIComponent getComponent() {
        return diComponent;
    }

    private void startServices(){

        //startService(new Intent(this, GPSTrackerService.class));
        //startService(new Intent(this, GPSTrackerServiceAndroidAPI.class));
        GpsTracking gpsTracking = new GpsTracking(getApplicationContext());
        gpsTracking.startGpsTracking();

        if(SharedStorage.getBoolean(getApplicationContext(), USING_SYNK_TRACK, true)) {
            Long interval = SharedStorage.getLong(getApplicationContext(), MIN_TIME_SYNK_TRACK_NAME, MIN_TIME_SYNK_TRACK);
            TaskSchedure taskTrackerSync = new TaskSchedure.Builder(SyncIntentTrackService.class, this)
                    .jobID(GPS_SYNK_SERVISE_JOB_ID)
                    .jobType(Job.Type.JOB_TYPE_HANDLER)
                    .jobNetworkType(Job.NetworkType.NETWORK_TYPE_ANY)
                    .requiresCharging(false)
                    .interval(interval == 0 ? MIN_TIME_SYNK_TRACK : interval)
                    .build();
            taskTrackerSync.startTask();
        }
    }

}
