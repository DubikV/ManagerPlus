package com.gmail.vanyadubik.mobilemanager.repository;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.vanyadubik.mobilemanager.model.ParameterInfo;
import com.gmail.vanyadubik.mobilemanager.model.db.Client_Element;
import com.gmail.vanyadubik.mobilemanager.model.db.LocationPoint;
import com.gmail.vanyadubik.mobilemanager.model.db.Visit_Element;
import com.gmail.vanyadubik.mobilemanager.model.db.Waybill_Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.ClientContract;
import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.LocationPointContract;
import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.TrackListContract;
import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.UserSettings;
import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.VisitContract;
import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.WaybillContract;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.buildClient;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.buildLocationPoint;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.buildVisit;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.buildWaybill;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.convertClient;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.convertLocationPoint;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.convertVisit;
import static com.gmail.vanyadubik.mobilemanager.repository.ModelConverter.convertWaybill;

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
                UserSettings.PROJECTION_ALL, UserSettings.USER_SETTING_ID + " = ?",
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
        try (Cursor cursor = contentResolver.query(TrackListContract.CONTENT_URI,
                new String[]{"count()"}, null,
                null, TrackListContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex("count()")) == 0)
                return null;
        }

        try (Cursor cursor = contentResolver.query(TrackListContract.CONTENT_URI,
                TrackListContract.PROJECTION_ALL, TrackListContract.DATE
                        + " = (select MAX(" + TrackListContract.DATE + ") from " + TrackListContract.TABLE_NAME + ")",
                null, TrackListContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            return buildLocationPoint(cursor);
        }
    }

    @Override
    public LocationPoint getLocationPoint(String id) {
        try (Cursor cursor = contentResolver.query(LocationPointContract.CONTENT_URI,
                LocationPointContract.PROJECTION_ALL, LocationPointContract._ID + " =?",
                new String[]{id}, LocationPointContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            return buildLocationPoint(cursor);
        }
    }

    @Override
    public Waybill_Element getLastWaybill() {
        try (Cursor cursor = contentResolver.query(WaybillContract.CONTENT_URI,
                new String[]{"count()"}, null,
                null, WaybillContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex("count()")) == 0)
                return null;
        }

        try (Cursor cursor = contentResolver.query(WaybillContract.CONTENT_URI,
                WaybillContract.PROJECTION_ALL, WaybillContract.WAYBILL_DATE
                        + " = (select MAX(" + WaybillContract.WAYBILL_DATE + ") from " + WaybillContract.TABLE_NAME + ")",
                null, WaybillContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            return buildWaybill(cursor);
        }
    }

    @Override
    public List<Waybill_Element> getAllWaybill() {
        try (Cursor cursor = contentResolver.query(WaybillContract.CONTENT_URI,
                WaybillContract.PROJECTION_ALL, null, null, WaybillContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<Waybill_Element> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(buildWaybill(cursor));
            return result;
        }
    }

    @Override
    public List<Visit_Element> getAllVisit() {
        try (Cursor cursor = contentResolver.query(VisitContract.CONTENT_URI,
                VisitContract.PROJECTION_ALL, null, null, VisitContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<Visit_Element> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(buildVisit(cursor));
            return result;
        }
    }

    @Override
    public List<Visit_Element> getVisitByPeriod(Date dateFrom, Date dateBy) {
        try (Cursor cursor = contentResolver.query(
                VisitContract.CONTENT_URI,
                VisitContract.PROJECTION_ALL,
                VisitContract.VISIT_DATE + ">=" + dateFrom.getTime()+ " AND "
                        + VisitContract.VISIT_DATE + "<=" + dateBy.getTime(),
                new String[]{},
                VisitContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            List<Visit_Element> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(buildVisit(cursor));
            return result;
        }
    }

    @Override
    public Client_Element getClient(String externalId) {
        try (Cursor cursor = contentResolver.query(
                ClientContract.CONTENT_URI,
                ClientContract.PROJECTION_ALL, ClientContract.CLIENT_ID + "='" + externalId + "'",
                null, ClientContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                return buildClient(cursor);
            } else {
                return null;
            }
        }
    }

    @Override
    public void insertTrackPoint(LocationPoint locationPoint) {
        ContentValues values = convertLocationPoint(locationPoint);
        contentResolver.insert(TrackListContract.CONTENT_URI, values);
    }

    @Override
    public void insertLocationPoint(LocationPoint locationPoint) {
        ContentValues values = convertLocationPoint(locationPoint);
        contentResolver.insert(LocationPointContract.CONTENT_URI, values);
    }

    @Override
    public void insertClient(Client_Element client) {
        ContentValues values = convertClient(client);
        contentResolver.insert(ClientContract.CONTENT_URI, values);
    }

    @Override
    public void insertWaybill(Waybill_Element waybill) {
        ContentValues values = convertWaybill(waybill);
        contentResolver.insert(WaybillContract.CONTENT_URI, values);
    }

    @Override
    public void insertVisit(Visit_Element visit) {
        ContentValues values = convertVisit(visit);
        contentResolver.insert(VisitContract.CONTENT_URI, values);
    }

    @Override
    public void insertUserSetting(ParameterInfo usersetting) {

        ContentValues values = new ContentValues();
        values.put(UserSettings.USER_SETTING_ID, usersetting.getName());
        values.put(UserSettings.SETTING_VALUE, usersetting.getValue());

        contentResolver.insert(UserSettings.CONTENT_URI, values);
    }
}
