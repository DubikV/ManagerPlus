package com.gmail.vanyadubik.mobilemanager.modules;

import android.app.Application;
import com.gmail.vanyadubik.mobilemanager.utils.ErrorUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ErrorUtilsApiModule {

    private Application application;

    public ErrorUtilsApiModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public ErrorUtils getErrorUtils() {
        return new ErrorUtils(application.getBaseContext());
    }
}
