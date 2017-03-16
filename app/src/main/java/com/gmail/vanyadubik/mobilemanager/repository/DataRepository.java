package com.gmail.vanyadubik.mobilemanager.repository;

import com.gmail.vanyadubik.mobilemanager.model.ParameterInfo;
import com.gmail.vanyadubik.mobilemanager.model.db.Client_Element;
import com.gmail.vanyadubik.mobilemanager.model.db.LocationPoint;
import com.gmail.vanyadubik.mobilemanager.model.db.Visit_Element;
import com.gmail.vanyadubik.mobilemanager.model.db.Waybill_Element;

import java.util.Date;
import java.util.List;

public interface DataRepository {

    List<LocationPoint> getTrack(Date dateFrom, Date dateBy);

    String getUserSetting(String settingId);

    LocationPoint getLastTrackPoint();

    LocationPoint getLocationPoint(String id);

    Waybill_Element getLastWaybill();

    List<Waybill_Element> getAllWaybill();

    List<Visit_Element> getAllVisit();

    List<Visit_Element> getVisitByPeriod(Date dateFrom, Date dateBy);

    Client_Element getClient(String externalId);

    void insertTrackPoint(LocationPoint locationPoint);

    void insertLocationPoint(LocationPoint locationPoint);

    void insertClient(Client_Element client);

    void insertWaybill(Waybill_Element waybill);

    void insertVisit(Visit_Element visit);

    void insertUserSetting(ParameterInfo usersetting);

}
