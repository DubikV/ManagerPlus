package com.gmail.vanyadubik.managerplus.modules;


import android.app.Application;

import com.gmail.vanyadubik.managerplus.utils.PhotoFIleUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PhotoFileUtilsApiModule {


    public PhotoFileUtilsApiModule(Application application) {
    }

    @Provides
    @Singleton
    public PhotoFIleUtils getPhotoFileUtils() {
        return new PhotoFIleUtils();
    }
}
