package com.gmail.vanyadubik.managerplus.modules;


import android.app.Application;

import com.gmail.vanyadubik.managerplus.utils.ServiceUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceUtilsApiModule {

    private Application application;

    public ServiceUtilsApiModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public ServiceUtils getServiceUtils() {
        return new ServiceUtils(application.getBaseContext());
    }
}
