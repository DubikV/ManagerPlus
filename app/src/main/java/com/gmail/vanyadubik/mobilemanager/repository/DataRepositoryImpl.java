package com.gmail.vanyadubik.mobilemanager.repository;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.TrackListContract;
import com.gmail.vanyadubik.mobilemanager.model.ParameterInfo;
import com.gmail.vanyadubik.mobilemanager.model.db.Client_Element;
import com.gmail.vanyadubik.mobilemanager.model.db.LocationPoint;
import com.gmail.vanyadubik.mobilemanager.model.db.Visit_Element;
import com.gmail.vanyadubik.mobilemanager.model.db.Waybill_Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.UserSettings;
import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.UserSettings.USER_SETTING_ID;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.buildLocationPoint;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.convertLocationPoint;

public class DataRepositoryImpl implements DataRepository{

    private ContentResolver contentResolver;

    public DataRepositoryImpl(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public List<LocationPoint> getTrack(Date dateFrom, Date dateBy) {
        try (Cursor cursor = contentResolver.query(
                TrackListContract.CONTENT_URI,
                TrackListContract.PROJECTION_ALL,
                TrackListContract.DATE + ">=" + dateFrom.getTime() + " AND "
                        + TrackListContract.DATE + "<=" + dateBy.getTime(),
                new String[]{},
                TrackListContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            List<LocationPoint> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(buildLocationPoint(cursor));
            return result;
        }
    }

    @Override
    public String getUserSetting(String settingId) {
        try (Cursor cursor = contentResolver.query(UserSettings.CONTENT_URI,
                UserSettings.PROJECTION_ALL, USER_SETTING_ID + " = ?",
                new String[]{settingId}, UserSettings.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(UserSettings.SETTING_VALUE));
            } else {
                return null;
            }
        }
    }

    @Override
    public LocationPoint getLastTrackPoint() {
        return null;
    }

    @Override
    public LocationPoint getLocationPoint(String externalId) {
        return null;
    }

    @Override
    public Waybill_Element getLastWaybill() {
        return null;
    }

    @Override
    public List<Waybill_Element> getAllWaybill() {
        return null;
    }

    @Override
    public List<Visit_Element> getAllVisit() {
        return null;
    }

    @Override
    public List<Visit_Element> getVisitByPeriod(Date dateFrom, Date dateBy) {
        return null;
    }

    @Override
    public Client_Element getClient(String externalId) {
        return null;
    }

    @Override
    public void insertTrackPoint(LocationPoint locationPoint) {

    }

    @Override
    public void insertLocationPoint(LocationPoint locationPoint) {
        ContentValues values = convertLocationPoint(locationPoint);
        contentResolver.insert(TrackListContract.CONTENT_URI, values);
    }

    @Override
    public void insertClient(Client_Element client) {

    }

    @Override
    public void insertWaybill(Waybill_Element waybill) {

    }

    @Override
    public void insertVisit(Visit_Element visit) {

    }

    @Override
    public void insertUserSetting(ParameterInfo usersetting) {

        ContentValues values = new ContentValues();
        values.put(UserSettings.USER_SETTING_ID, usersetting.getName());
        values.put(UserSettings.SETTING_VALUE, usersetting.getValue());

        contentResolver.insert(UserSettings.CONTENT_URI, values);
    }
}
