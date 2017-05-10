package com.gmail.vanyadubik.managerplus.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.gmail.vanyadubik.managerplus.model.ParameterInfo;
import com.gmail.vanyadubik.managerplus.model.db.document.Fuel_Document;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.db.document.Visit_Document;
import com.gmail.vanyadubik.managerplus.model.map.MarkerMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_SIZE_TRACK_LIST_UPLOAD;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ClientContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.LocationPointContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.TrackListContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.UserSettings;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.VisitContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.WaybillContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.UsingCarContrack;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ChangingContrack;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.FuelContract;
import static com.gmail.vanyadubik.managerplus.repository.ModelConverter.buildClient;
import static com.gmail.vanyadubik.managerplus.repository.ModelConverter.buildFuelDoc;
import static com.gmail.vanyadubik.managerplus.repository.ModelConverter.buildVisit;
import static com.gmail.vanyadubik.managerplus.repository.ModelConverter.convertLocationPoint;

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

            if (cursor == null) {
                return null;
            }

            List<LocationPoint> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(ModelConverter.buildTrackPoint(cursor));
            return result;
        }
    }

    @Override
    public List<LatLng> getTrackLatLng(Date dateFrom, Date dateBy) {
        try (Cursor cursor = contentResolver.query(
                TrackListContract.CONTENT_URI,
                TrackListContract.PROJECTION_ALL,
                TrackListContract.DATE + ">=" + dateFrom.getTime() + " AND "
                        + TrackListContract.DATE + "<=" + dateBy.getTime(),
                new String[]{},
                TrackListContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return null;
            }

            List<LatLng> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(new LatLng(
                        cursor.getDouble(cursor.getColumnIndex(TrackListContract.LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(TrackListContract.LONGITUDE))));
            return result;
        }
    }

    @Override
    public PolylineOptions getBuildTrackLatLng(PolylineOptions pOptions, Date dateFrom, Date dateBy) {
        try (Cursor cursor = contentResolver.query(
                TrackListContract.CONTENT_URI,
                TrackListContract.PROJECTION_ALL,
                TrackListContract.DATE + ">=" + dateFrom.getTime() + " AND "
                        + TrackListContract.DATE + "<=" + dateBy.getTime(),
                new String[]{},
                TrackListContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return null;
            }

            while (cursor.moveToNext())
                pOptions.add(
                        new LatLng(
                                cursor.getDouble(cursor.getColumnIndex(TrackListContract.LATITUDE)),
                                cursor.getDouble(cursor.getColumnIndex(TrackListContract.LONGITUDE))));
            return pOptions;
        }
    }

    @Override
    public PolylineOptions getBuildVisitsTrackLatLng(PolylineOptions pOptions, Date dateFrom, Date dateBy) {

        try (Cursor cursor = contentResolver.query(
                VisitContract.CONTENT_URI,
                new String[]{VisitContract.VISIT_CLIENT},
                VisitContract.VISIT_DATE + ">=" + dateFrom.getTime() + " AND "
                        + VisitContract.VISIT_DATE + "<=" + dateBy.getTime(),
                new String[]{},
                VisitContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;

            while (cursor.moveToNext()){

                Client_Element client = getClient(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_CLIENT)));

                if(client!=null){
                    LocationPoint locationPoint = getLocationPoint(client.getPositionLP());

                    if(locationPoint!=null){
                        pOptions.add(
                                new LatLng(locationPoint.getLatitude(),
                                        locationPoint.getLongitude()));
                    }
                }
            }
        }
        return pOptions;
    }

    @Override
    public List<MarkerMap> getBuildVisitsMarkers(Date dateFrom, Date dateBy) {
        List<MarkerMap> markers;
        try (Cursor cursor = contentResolver.query(
                VisitContract.CONTENT_URI,
                new String[]{VisitContract.VISIT_CLIENT},
                VisitContract.VISIT_DATE + ">=" + dateFrom.getTime() + " AND "
                        + VisitContract.VISIT_DATE + "<=" + dateBy.getTime(),
                new String[]{},
                VisitContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;

            markers = new ArrayList<>();

            while (cursor.moveToNext()) {

                Client_Element client = getClient(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_CLIENT)));

                if (client != null) {
                    LocationPoint locationPoint = getLocationPoint(client.getPositionLP());

                    if (locationPoint != null) {

                        markers.add(new MarkerMap(client.getName(), new LatLng(locationPoint.getLatitude(),
                                locationPoint.getLongitude())));
                    }
                }
            }
        }
        return markers;
    }

    @Override
    public List<LocationPoint> getUloadedLocationTrack() {
        try (Cursor cursor = contentResolver.query(
                TrackListContract.CONTENT_URI,
                TrackListContract.PROJECTION_ALL,
                TrackListContract.UNLOADED + "=0",
                null, String.format("%s limit "+String.valueOf(MIN_SIZE_TRACK_LIST_UPLOAD),
                TrackListContract.DEFAULT_SORT_ORDER))) {

            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            List<LocationPoint> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(ModelConverter.buildTrackPoint(cursor));
            return result;
        }
    }

    @Override
    public List<LocationPoint> getUloadedLocationTrack(int minCount) {
        try (Cursor cursor = contentResolver.query(
                TrackListContract.CONTENT_URI,
                TrackListContract.PROJECTION_ALL,
                TrackListContract.UNLOADED + "=0",
                null, String.format("%s limit "+String.valueOf(minCount),
                        TrackListContract.DEFAULT_SORT_ORDER))) {

            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            List<LocationPoint> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(ModelConverter.buildTrackPoint(cursor));
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
            return ModelConverter.buildTrackPoint(cursor);
        }
    }

    @Override
    public LocationPoint getLocationPoint(int id) {
        try (Cursor cursor = contentResolver.query(LocationPointContract.CONTENT_URI,
                LocationPointContract.PROJECTION_ALL, LocationPointContract._ID + "=" + id,
                null, LocationPointContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            return ModelConverter.buildLocationPoint(cursor);
        }
    }

    @Override
    public Waybill_Document getLastWaybill() {
        try (Cursor cursor = contentResolver.query(WaybillContract.CONTENT_URI,
                new String[]{"count()"}, null,
                null, WaybillContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex("count()")) == 0)
                return null;
        }

        try (Cursor cursor = contentResolver.query(WaybillContract.CONTENT_URI,
                WaybillContract.PROJECTION_ALL, WaybillContract.WAYBILL_DATE_START
                        + " = (select MAX(" + WaybillContract.WAYBILL_DATE_START + ") from " + WaybillContract.TABLE_NAME + ")",
                null, WaybillContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            return ModelConverter.buildWaybill(cursor);
        }
    }

    @Override
    public List<Waybill_Document> getAllWaybill() {
        try (Cursor cursor = contentResolver.query(WaybillContract.CONTENT_URI,
                WaybillContract.PROJECTION_ALL, null, null, WaybillContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<Waybill_Document> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(ModelConverter.buildWaybill(cursor));
            return result;
        }
    }

    @Override
    public List<Visit_Document> getAllVisit() {
        try (Cursor cursor = contentResolver.query(VisitContract.CONTENT_URI,
                VisitContract.PROJECTION_ALL, null, null, VisitContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<Visit_Document> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(ModelConverter.buildVisit(cursor));
            return result;
        }
    }

    @Override
    public List<Client_Element> getAllClients() {
        try (Cursor cursor = contentResolver.query(ClientContract.CONTENT_URI,
                ClientContract.PROJECTION_ALL, null, null, ClientContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<Client_Element> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(buildClient(cursor));
            return result;
        }
    }

    @Override
    public List<Fuel_Document> getAllFuel() {
        try (Cursor cursor = contentResolver.query(FuelContract.CONTENT_URI,
                FuelContract.PROJECTION_ALL, null, null, FuelContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<Fuel_Document> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(ModelConverter.buildFuelDoc(cursor));
            return result;
        }
    }

    @Override
    public List<Visit_Document> getVisitByPeriod(Date dateFrom, Date dateBy) {
        try (Cursor cursor = contentResolver.query(
                VisitContract.CONTENT_URI,
                VisitContract.PROJECTION_ALL,
                VisitContract.VISIT_DATE + ">=" + dateFrom.getTime() + " AND "
                        + VisitContract.VISIT_DATE + "<=" + dateBy.getTime(),
                new String[]{},
                VisitContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<Visit_Document> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(ModelConverter.buildVisit(cursor));
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
    public Visit_Document getVisit(String externalId) {
        try (Cursor cursor = contentResolver.query(
                VisitContract.CONTENT_URI,
                VisitContract.PROJECTION_ALL, VisitContract.VISIT_ID + "='" + externalId + "'",
                null, VisitContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                return buildVisit(cursor);
            } else {
                return null;
            }
        }
    }

    @Override
    public Boolean isInCar() {
        try (Cursor cursor = contentResolver.query(UsingCarContrack.CONTENT_URI,
                new String[]{"count()"}, null,
                null, UsingCarContrack.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex("count()")) == 0)
                return true;
        }

        try (Cursor cursor = contentResolver.query(UsingCarContrack.CONTENT_URI,
                UsingCarContrack.PROJECTION_ALL, UsingCarContrack.DATE
                        + " = (select MAX(" + UsingCarContrack.DATE + ") from " + UsingCarContrack.TABLE_NAME + ")",
                null, UsingCarContrack.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return true;
            return cursor.getInt(cursor.getColumnIndex(UsingCarContrack.INCAR))== 1;
        }
    }

    @Override
    public List<String> getChangedElements(String nameElement) {
        try (Cursor cursor = contentResolver.query(
                ChangingContrack.CONTENT_URI,
                ChangingContrack.PROJECTION_ALL,
                ChangingContrack.MANE_ELEMENT + "='" + nameElement + "'",
                new String[]{},
                ChangingContrack.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<String> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(cursor.getString(cursor.getColumnIndex(ChangingContrack.ELEMENT_ID)));
            return result;
        }
    }

    @Override
    public Fuel_Document getFuel(String externalId) {
        try (Cursor cursor = contentResolver.query(
                FuelContract.CONTENT_URI,
                FuelContract.PROJECTION_ALL, FuelContract.EXTERNAL_ID + "='" + externalId + "'",
                null, FuelContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                return buildFuelDoc(cursor);
            } else {
                return null;
            }
        }
    }

    @Override
    public void insertTrackPoint(LocationPoint locationPoint) {
        ContentValues values = ModelConverter.convertTrackPoint(locationPoint);
        contentResolver.insert(TrackListContract.CONTENT_URI, values);
    }

    @Override
    public void SetTrackListUloadedLocationTrack(Date dateFrom, Date dateBy) {
        ContentValues newTrackContentValues = new ContentValues();

        newTrackContentValues.put(TrackListContract.UNLOADED, true);
        contentResolver.update(
                TrackListContract.CONTENT_URI,
                newTrackContentValues,
                TrackListContract.DATE+ ">='" + dateFrom.getTime() + "' AND "
                        + TrackListContract.DATE + "<='" + dateBy.getTime() + "'",
                null);
    }

    @Override
    public int insertLocationPoint(LocationPoint locationPoint) {
        ContentValues values = convertLocationPoint(locationPoint);
        Uri uri = contentResolver.insert(LocationPointContract.CONTENT_URI, values);
        return (int)ContentUris.parseId(uri);
    }

    @Override
    public void insertClient(Client_Element client) {
        ContentValues values = ModelConverter.convertClient(client);
        contentResolver.insert(ClientContract.CONTENT_URI, values);
    }

    @Override
    public void insertWaybill(Waybill_Document waybill) {
        ContentValues values = ModelConverter.convertWaybill(waybill);
        contentResolver.insert(WaybillContract.CONTENT_URI, values);
//        contentResolver.update(WaybillContract.CONTENT_URI,
//                values,
//                WaybillContract.WAYBILL_ID + "='" + waybill.getExternalId()+"'",
//                null);
    }

    @Override
    public void insertVisit(Visit_Document visit) {
        ContentValues values = ModelConverter.convertVisit(visit);
        contentResolver.insert(VisitContract.CONTENT_URI, values);
    }

    @Override
    public void insertUserSetting(ParameterInfo usersetting) {

        ContentValues values = new ContentValues();
        values.put(UserSettings.USER_SETTING_ID, usersetting.getName());
        values.put(UserSettings.SETTING_VALUE, usersetting.getValue());

        contentResolver.insert(UserSettings.CONTENT_URI, values);
    }

    @Override
    public void insertInCar(Date date, boolean inCar) {

        ContentValues values = new ContentValues();
        values.put(UsingCarContrack.DATE, date.getTime());
        values.put(UsingCarContrack.INCAR, inCar);

        contentResolver.insert(UsingCarContrack.CONTENT_URI, values);
    }

    @Override
    public void insertChangedElement(String nameElement, String externalID) {

        ContentValues values = new ContentValues();
        values.put(ChangingContrack.MANE_ELEMENT, nameElement);
        values.put(ChangingContrack.ELEMENT_ID, externalID);

        contentResolver.insert(ChangingContrack.CONTENT_URI, values);

    }

    @Override
    public void insertFuel(Fuel_Document fuelDoc) {
        ContentValues values = ModelConverter.convertFuelDoc(fuelDoc);
        contentResolver.insert(FuelContract.CONTENT_URI, values);
    }
}
