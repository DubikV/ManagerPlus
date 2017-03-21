package com.gmail.vanyadubik.managerplus.modules;


import android.app.Application;
import com.gmail.vanyadubik.managerplus.utils.PhoneUtils;

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
