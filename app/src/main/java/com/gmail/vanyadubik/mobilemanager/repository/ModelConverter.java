package com.gmail.vanyadubik.mobilemanager.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.TrackListContract;
import com.gmail.vanyadubik.mobilemanager.model.db.LocationPoint;

import java.util.Date;

public class ModelConverter {

    static ContentValues convertLocationPoint(LocationPoint locationPoint) {
        ContentValues values = new ContentValues();
        values.put(TrackListContract.DATE, locationPoint.getDate().getTime());
        values.put(TrackListContract.LATITUDE, locationPoint.getLatitude());
        values.put(TrackListContract.LONGITUDE, locationPoint.getLongitude());
        values.put(TrackListContract.IN_CAR, locationPoint.isInCar());
        return values;
    }

    static LocationPoint buildLocationPoint(Cursor cursor) {
        return LocationPoint.builder()
                .id(cursor.getInt(cursor.getColumnIndex(TrackListContract._ID)))
                .date(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(TrackListContract.DATE)))))
                .latitude(cursor.getString(cursor.getColumnIndex(TrackListContract.LATITUDE)))
                .longitude(cursor.getString(cursor.getColumnIndex(TrackListContract.LONGITUDE)))
                .inCar(cursor.getInt(cursor.getColumnIndex(TrackListContract.IN_CAR))== 1)
                .build();
    }
}
