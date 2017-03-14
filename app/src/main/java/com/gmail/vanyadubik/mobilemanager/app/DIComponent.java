package com.gmail.vanyadubik.mobilemanager.app;

import com.gmail.vanyadubik.mobilemanager.activity.TrackActivity;
import com.gmail.vanyadubik.mobilemanager.modules.DataApiModule;
import com.gmail.vanyadubik.mobilemanager.task.SyncIntentService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataApiModule.class})
public interface DIComponent {

    void inject(TrackActivity activity);

    void inject(SyncIntentService syncIntentService);
}
