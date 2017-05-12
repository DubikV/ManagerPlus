package com.gmail.vanyadubik.managerplus.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.PhotoContract;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.FuelContract;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.LocationPointContract;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ClientContract;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.TrackListContract;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.VisitContract;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.WaybillContract;
import com.gmail.vanyadubik.managerplus.model.db.document.Fuel_Document;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.db.document.Visit_Document;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;
import com.gmail.vanyadubik.managerplus.model.db.element.Photo_Element;

import java.util.Date;

public class ModelConverter {

    static ContentValues convertTrackPoint(LocationPoint locationPoint) {
        ContentValues values = new ContentValues();
        values.put(TrackListContract.DATE, locationPoint.getDate().getTime());
        values.put(TrackListContract.LATITUDE, locationPoint.getLatitude());
        values.put(TrackListContract.LONGITUDE, locationPoint.getLongitude());
        values.put(TrackListContract.UNLOADED, false);
        return values;
    }

    static ContentValues convertLocationPoint(LocationPoint locationPoint) {
        ContentValues values = new ContentValues();
        values.put(LocationPointContract.LOCATION_DATE, locationPoint.getDate().getTime());
        values.put(LocationPointContract.LOCATION_LATITUDE, locationPoint.getLatitude());
        values.put(LocationPointContract.LOCATION_LONGITUDE, locationPoint.getLongitude());
        return values;
    }

    static ContentValues convertClient(Client_Element client) {
        ContentValues values = new ContentValues();
        values.put(ClientContract.CLIENT_ID, client.getExternalId());
        values.put(ClientContract.CLIENT_DELETED, client.isDeleted());
        values.put(ClientContract.CLIENT_INDB, client.isInDB());
        values.put(ClientContract.CLIENT_NAME, client.getName());
        values.put(ClientContract.CLIENT_ADDRESS, client.getAddress());
        values.put(ClientContract.CLIENT_PHONE, client.getPhone());
        values.put(ClientContract.CLIENT_POSITION, client.getPositionLP());
        return values;
    }

    static ContentValues convertWaybill(Waybill_Document waybill) {
        ContentValues values = new ContentValues();
        values.put(WaybillContract.WAYBILL_ID, waybill.getExternalId());
        values.put(WaybillContract.WAYBILL_DELETED, waybill.isDeleted());
        values.put(WaybillContract.WAYBILL_INDB, waybill.isInDB());
        values.put(WaybillContract.WAYBILL_DATE, waybill.getDate()== null?
                null: waybill.getDate().getTime());
        values.put(WaybillContract.WAYBILL_DATE_START, waybill.getDateStart()== null?
                null: waybill.getDateStart().getTime());
        values.put(WaybillContract.WAYBILL_DATE_END, waybill.getDateEnd()== null?
                null: waybill.getDateEnd().getTime());
        values.put(WaybillContract.WAYBILL_POINT_START, waybill.getStartLP());
        values.put(WaybillContract.WAYBILL_POINT_END, waybill.getEndLP());
        values.put(WaybillContract.WAYBILL_ODOMETER_START, waybill.getStartOdometer());
        values.put(WaybillContract.WAYBILL_ODOMETER_END, waybill.getEndOdometer());
        return values;
    }

    static ContentValues convertFuelDoc(Fuel_Document fuelDoc) {
        ContentValues values = new ContentValues();
        values.put(FuelContract.EXTERNAL_ID, fuelDoc.getExternalId());
        values.put(FuelContract.DELETED, fuelDoc.isDeleted());
        values.put(FuelContract.INDB, fuelDoc.isInDB());
        values.put(FuelContract.DATE, fuelDoc.getDate()== null?
                null: fuelDoc.getDate().getTime());
        values.put(FuelContract.TYPE_FUEL, fuelDoc.getTypeFuel());
        values.put(FuelContract.TYPE_PAYMENT, fuelDoc.getTypePayment());
        values.put(FuelContract.LITRES, fuelDoc.getLitres());
        values.put(FuelContract.MONEY, fuelDoc.getMoney());
        values.put(FuelContract.POINT_CREATE, fuelDoc.getCreateLP());
        return values;
    }

    static ContentValues convertPhoto(Photo_Element photo) {
        ContentValues values = new ContentValues();
        values.put(PhotoContract.EXTERNAL_ID, photo.getExternalId());
        values.put(PhotoContract.DELETED, photo.isDeleted());
        values.put(PhotoContract.INDB, photo.isInDB());
        values.put(PhotoContract.NAME, photo.getName());
        values.put(PhotoContract.HOLDERNAME, photo.getHoldername());
        values.put(PhotoContract.HOLDERID, photo.getHolderId());
        values.put(PhotoContract.DATE, photo.getCreateDate()== null?
                null: photo.getCreateDate().getTime());
        values.put(PhotoContract.INFO, photo.getInfo());
        return values;
    }

    static ContentValues convertVisit(Visit_Document visit) {
        ContentValues values = new ContentValues();
        values.put(VisitContract.VISIT_ID, visit.getExternalId());
        values.put(VisitContract.VISIT_DELETED, visit.isDeleted());
        values.put(VisitContract.VISIT_INDB, visit.isInDB());
        values.put(VisitContract.VISIT_DATE, visit.getDate()== null?
                null: visit.getDate().getTime());
        values.put(VisitContract.VISIT_DATE_VISIT, visit.getDateVisit()== null?
                null: visit.getDateVisit().getTime());
        values.put(VisitContract.VISIT_CLIENT, visit.getClientExternalId());
        values.put(VisitContract.VISIT_POINT_CREATE, visit.getCreateLP());
        values.put(VisitContract.VISIT_POINT_VISIT, visit.getVisitLP());
        values.put(VisitContract.VISIT_TYPE, visit.getTypeVisit());
        values.put(VisitContract.VISIT_INFORMATION, visit.getInformation());
        return values;
    }

    static LocationPoint buildTrackPoint(Cursor cursor) {
        return LocationPoint.builder()
                .id(cursor.getInt(cursor.getColumnIndex(TrackListContract._ID)))
                .date(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(TrackListContract.DATE)))))
                .latitude(cursor.getDouble(cursor.getColumnIndex(TrackListContract.LATITUDE)))
                .longitude(cursor.getDouble(cursor.getColumnIndex(TrackListContract.LONGITUDE)))
                .build();
    }

    static LocationPoint buildLocationPoint(Cursor cursor) {
        return LocationPoint.builder()
                .id(cursor.getInt(cursor.getColumnIndex(TrackListContract._ID)))
                .date(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(LocationPointContract.LOCATION_DATE)))))
                .latitude(cursor.getDouble(cursor.getColumnIndex(LocationPointContract.LOCATION_LATITUDE)))
                .longitude(cursor.getDouble(cursor.getColumnIndex(LocationPointContract.LOCATION_LONGITUDE)))
                .build();
    }

    static Waybill_Document buildWaybill(Cursor cursor) {
        return Waybill_Document.builder()
                .id(cursor.getInt(cursor.getColumnIndex(WaybillContract._ID)))
                .externalId(cursor.getString(cursor.getColumnIndex(WaybillContract.WAYBILL_ID)))
                .deleted(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_DELETED))== 1)
                .inDB(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_INDB))== 1)
                .date(convertDate(cursor, WaybillContract.WAYBILL_DATE))
                .dateStart(convertDate(cursor, WaybillContract.WAYBILL_DATE_START))
                .dateEnd(convertDate(cursor, WaybillContract.WAYBILL_DATE_END))
                .startLP(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_POINT_START)))
                .endLP(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_POINT_START)))
                .startOdometer(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_ODOMETER_START)))
                .endOdometer(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_ODOMETER_END)))
                .build();
    }

    static Visit_Document buildVisit(Cursor cursor) {
        return Visit_Document.builder()
                .id(cursor.getInt(cursor.getColumnIndex(VisitContract._ID)))
                .externalId(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_ID)))
                .deleted(cursor.getInt(cursor.getColumnIndex(VisitContract.VISIT_DELETED))== 1)
                .inDB(cursor.getInt(cursor.getColumnIndex(VisitContract.VISIT_INDB))== 1)
                .date(convertDate(cursor, VisitContract.VISIT_DATE))
                .dateVisit(convertDate(cursor, VisitContract.VISIT_DATE_VISIT))
                .clientExternalId(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_CLIENT)))
                .createLP(cursor.getInt(cursor.getColumnIndex(VisitContract.VISIT_POINT_CREATE)))
                .visitLP(cursor.getInt(cursor.getColumnIndex(VisitContract.VISIT_POINT_VISIT)))
                .typeVisit(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_TYPE)))
                .information(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_INFORMATION)))
                .build();
    }

    static Client_Element buildClient(Cursor cursor) {
        return Client_Element.builder()
                .id(cursor.getInt(cursor.getColumnIndex(ClientContract._ID)))
                .externalId(cursor.getString(cursor.getColumnIndex(ClientContract.CLIENT_ID)))
                .deleted(cursor.getInt(cursor.getColumnIndex(ClientContract.CLIENT_DELETED))== 1)
                .inDB(cursor.getInt(cursor.getColumnIndex(ClientContract.CLIENT_INDB))== 1)
                .name(cursor.getString(cursor.getColumnIndex(ClientContract.CLIENT_NAME)))
                .address(cursor.getString(cursor.getColumnIndex(ClientContract.CLIENT_ADDRESS)))
                .phone(cursor.getString(cursor.getColumnIndex(ClientContract.CLIENT_PHONE)))
                .positionLP(cursor.getInt(cursor.getColumnIndex(ClientContract.CLIENT_POSITION)))
                .build();
    }

    static Fuel_Document buildFuelDoc(Cursor cursor) {
        return Fuel_Document.builder()
                .id(cursor.getInt(cursor.getColumnIndex(FuelContract._ID)))
                .externalId(cursor.getString(cursor.getColumnIndex(FuelContract.EXTERNAL_ID)))
                .deleted(cursor.getInt(cursor.getColumnIndex(FuelContract.DELETED))== 1)
                .inDB(cursor.getInt(cursor.getColumnIndex(FuelContract.INDB))== 1)
                .date(convertDate(cursor, FuelContract.DATE))
                .typeFuel(cursor.getString(cursor.getColumnIndex(FuelContract.TYPE_FUEL)))
                .typePayment(cursor.getString(cursor.getColumnIndex(FuelContract.TYPE_PAYMENT)))
                .litres(cursor.getDouble(cursor.getColumnIndex(FuelContract.LITRES)))
                .money(cursor.getDouble(cursor.getColumnIndex(FuelContract.MONEY)))
                .createLP(cursor.getInt(cursor.getColumnIndex(FuelContract.POINT_CREATE)))
                .build();
    }

    static Photo_Element buildPhoto(Cursor cursor) {
        return Photo_Element.builder()
                .id(cursor.getInt(cursor.getColumnIndex(PhotoContract._ID)))
                .externalId(cursor.getString(cursor.getColumnIndex(PhotoContract.EXTERNAL_ID)))
                .deleted(cursor.getInt(cursor.getColumnIndex(PhotoContract.DELETED))== 1)
                .inDB(cursor.getInt(cursor.getColumnIndex(PhotoContract.INDB))== 1)
                .name(cursor.getString(cursor.getColumnIndex(PhotoContract.NAME)))
                .holdername(cursor.getString(cursor.getColumnIndex(PhotoContract.HOLDERNAME)))
                .holderId(cursor.getString(cursor.getColumnIndex(PhotoContract.HOLDERID)))
                .createDate(convertDate(cursor, PhotoContract.DATE))
                .info(cursor.getString(cursor.getColumnIndex(PhotoContract.INFO)))
                .build();
    }

    private static Date convertDate(Cursor cursor, String nameColum){

        String value = cursor.getString(cursor.getColumnIndex(nameColum));

        if(value==null|| value.isEmpty()){
            return new Date(Long.valueOf("0"));
        }else {
            return new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(nameColum))));
        }
    }
}
