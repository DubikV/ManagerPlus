package com.gmail.vanyadubik.managerplus.repository;

import com.gmail.vanyadubik.managerplus.model.ParameterInfo;
import com.gmail.vanyadubik.managerplus.model.db.Client_Element;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.db.Visit_Element;
import com.gmail.vanyadubik.managerplus.model.db.Waybill_Element;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;
import java.util.List;

public interface DataRepository {

    List<LocationPoint> getTrack(Date dateFrom, Date dateBy);

    PolylineOptions getBuildTrackLatLng(PolylineOptions pOptionsDate, Date dateFrom, Date dateBy);

    List<LocationPoint> getUloadedLocationTrack();

    List<LocationPoint> getUloadedLocationTrack(int minCount);

    String getUserSetting(String settingId);

    LocationPoint getLastTrackPoint();

    LocationPoint getLocationPoint(String id);

    Waybill_Element getLastWaybill();

    List<Waybill_Element> getAllWaybill();

    List<Visit_Element> getAllVisit();

    List<Visit_Element> getVisitByPeriod(Date dateFrom, Date dateBy);

    Client_Element getClient(String externalId);

    void insertTrackPoint(LocationPoint locationPoint);

    void SetTrackListUloadedLocationTrack(Date dateFrom, Date dateBy);

    void insertLocationPoint(LocationPoint locationPoint);

    void insertClient(Client_Element client);

    void insertWaybill(Waybill_Element waybill);

    void insertVisit(Visit_Element visit);

    void insertUserSetting(ParameterInfo usersetting);

}
