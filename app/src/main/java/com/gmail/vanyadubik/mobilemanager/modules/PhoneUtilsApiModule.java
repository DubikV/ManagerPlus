package com.gmail.vanyadubik.mobilemanager.modules;


import android.app.Application;
import com.gmail.vanyadubik.mobilemanager.utils.PhoneUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PhoneUtilsApiModule {


    public PhoneUtilsApiModule(Application application) {
    }

    @Provides
    @Singleton
    public PhoneUtils getPhoneUtils() {
        return new PhoneUtils();
    }
}
