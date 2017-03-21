package com.gmail.vanyadubik.mobilemanager.app;

import com.gmail.vanyadubik.mobilemanager.modules.ActivityUtilsApiModule;
import com.gmail.vanyadubik.mobilemanager.modules.DataApiModule;
import com.gmail.vanyadubik.mobilemanager.modules.ErrorUtilsApiModule;
import com.gmail.vanyadubik.mobilemanager.modules.NetworkUtilsApiModule;
import com.gmail.vanyadubik.mobilemanager.modules.PhoneUtilsApiModule;
import com.gmail.vanyadubik.mobilemanager.task.SyncIntentService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataApiModule.class,
        NetworkUtilsApiModule.class, ActivityUtilsApiModule.class, ErrorUtilsApiModule.class, PhoneUtilsApiModule.class})
public interface DIComponent {

    void inject(SyncIntentService syncIntentService);
}
