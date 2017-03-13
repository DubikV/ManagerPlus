package com.gmail.vanyadubik.mobilemanager.modules;

import android.app.Application;

import com.gmail.vanyadubik.mobilemanager.repository.DataRepository;
import com.gmail.vanyadubik.mobilemanager.repository.DataRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataApiModule {

    private Application application;

    public DataApiModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public DataRepository getDataRepository() {
        return new DataRepositoryImpl(application.getBaseContext().getContentResolver());
    }
}
