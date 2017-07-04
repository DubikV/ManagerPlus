package com.gmail.vanyadubik.managerplus.modules;


import android.app.Application;

import com.gmail.vanyadubik.managerplus.utils.ElementUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ElementUtilsApiModule {

    private Application application;

    public ElementUtilsApiModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public ElementUtils getElementUtils() {
        return new ElementUtils(application.getBaseContext());
    }
}
