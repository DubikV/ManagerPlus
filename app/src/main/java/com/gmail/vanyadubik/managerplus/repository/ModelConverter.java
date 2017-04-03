package com.gmail.vanyadubik.managerplus.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ClientContract;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.TrackListContract;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.VisitContract;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.WaybillContract;
import com.gmail.vanyadubik.managerplus.model.db.Client_Element;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.db.Visit_Element;
import com.gmail.vanyadubik.managerplus.model.db.Waybill_Element;

import java.util.Date;

public class ModelConverter {

    static ContentValues convertLocationPoint(LocationPoint locationPoint) {
        ContentValues values = new ContentValues();
        values.put(TrackListContract.DATE, locationPoint.getDate().getTime());
        values.put(TrackListContract.LATITUDE, locationPoint.getLatitude());
        values.put(TrackListContract.LONGITUDE, locationPoint.getLongitude());
        values.put(TrackListContract.IN_CAR, locationPoint.isInCar());
        values.put(TrackListContract.UNLOADED, false);
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

    static ContentValues convertWaybill(Waybill_Element waybill) {
        ContentValues values = new ContentValues();
        values.put(WaybillContract.WAYBILL_ID, waybill.getExternalId());
        values.put(WaybillContract.WAYBILL_DELETED, waybill.isDeleted());
        values.put(WaybillContract.WAYBILL_INDB, waybill.isInDB());
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

    static ContentValues convertVisit(Visit_Element visit) {
        ContentValues values = new ContentValues();
        values.put(VisitContract.VISIT_ID, visit.getExternalId());
        values.put(VisitContract.VISIT_DELETED, visit.isDeleted());
        values.put(VisitContract.VISIT_INDB, visit.isInDB());
        values.put(VisitContract.VISIT_DATE, visit.getDate().getTime());
        values.put(VisitContract.VISIT_DATE_VISIT, visit.getDateVisit().getTime());
        values.put(VisitContract.VISIT_CLIENT, visit.getClientExternalId());
        values.put(VisitContract.VISIT_POINT_CREATE, visit.getCreateLP());
        values.put(VisitContract.VISIT_POINT_VISIT, visit.getVisitLP());
        values.put(VisitContract.VISIT_INFORMATION, visit.getInformation());
        return values;
    }


    static LocationPoint buildLocationPoint(Cursor cursor) {
        return LocationPoint.builder()
                .id(cursor.getInt(cursor.getColumnIndex(TrackListContract._ID)))
                .date(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(TrackListContract.DATE)))))
                .latitude(cursor.getDouble(cursor.getColumnIndex(TrackListContract.LATITUDE)))
                .longitude(cursor.getDouble(cursor.getColumnIndex(TrackListContract.LONGITUDE)))
                .inCar(cursor.getInt(cursor.getColumnIndex(TrackListContract.IN_CAR))== 1)
                .build();
    }

    static Waybill_Element buildWaybill(Cursor cursor) {
        return Waybill_Element.builder()
                .id(cursor.getInt(cursor.getColumnIndex(WaybillContract._ID)))
                .externalId(cursor.getString(cursor.getColumnIndex(WaybillContract.WAYBILL_ID)))
                .deleted(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_DELETED))== 1)
                .inDB(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_INDB))== 1)
                .dateStart(convertDate(cursor, WaybillContract.WAYBILL_DATE_START))
                .dateEnd(convertDate(cursor, WaybillContract.WAYBILL_DATE_END))
                .startLP(cursor.getString(cursor.getColumnIndex(WaybillContract.WAYBILL_POINT_START)))
                .endLP(cursor.getString(cursor.getColumnIndex(WaybillContract.WAYBILL_POINT_START)))
                .startOdometer(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_ODOMETER_START)))
                .endOdometer(cursor.getInt(cursor.getColumnIndex(WaybillContract.WAYBILL_ODOMETER_END)))
                .build();
    }

    static Visit_Element buildVisit(Cursor cursor) {
        return Visit_Element.builder()
                .id(cursor.getInt(cursor.getColumnIndex(VisitContract._ID)))
                .externalId(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_ID)))
                .deleted(cursor.getInt(cursor.getColumnIndex(VisitContract.VISIT_DELETED))== 1)
                .inDB(cursor.getInt(cursor.getColumnIndex(VisitContract.VISIT_INDB))== 1)
                .date(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_DATE)))))
                .dateVisit(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_DATE_VISIT)))))
                .clientExternalId(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_CLIENT)))
                .createLP(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_POINT_CREATE)))
                .visitLP(cursor.getString(cursor.getColumnIndex(VisitContract.VISIT_POINT_VISIT)))
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
                .positionLP(cursor.getString(cursor.getColumnIndex(ClientContract.CLIENT_POSITION)))
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
