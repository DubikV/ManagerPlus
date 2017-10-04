package com.gmail.vanyadubik.managerplus.modules;

import android.app.Application;

import com.gmail.vanyadubik.managerplus.calendarapi.CalendarApiImpl;
import com.gmail.vanyadubik.managerplus.calendarapi.GoogleCalendarApi;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
public class GoogleCalendarApiModule {

    private Application application;

    public GoogleCalendarApiModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public GoogleCalendarApi getGoogleCalendarRepository() {
        return new CalendarApiImpl(application.getBaseContext());
    }
}
