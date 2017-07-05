package com.gmail.vanyadubik.managerplus.repository;

import com.gmail.vanyadubik.managerplus.model.ParameterInfo;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.db.document.Document;
import com.gmail.vanyadubik.managerplus.model.db.document.Fuel_Document;
import com.gmail.vanyadubik.managerplus.model.db.document.Visit_Document;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;
import com.gmail.vanyadubik.managerplus.model.db.element.Element;
import com.gmail.vanyadubik.managerplus.model.db.element.Photo_Element;
import com.gmail.vanyadubik.managerplus.model.map.MarkerMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;
import java.util.List;

public interface DataRepository {

    List<LocationPoint> getTrack(Date dateFrom, Date dateBy);

    List<LatLng> getTrackLatLng(Date dateFrom, Date dateBy);

    PolylineOptions getBuildTrackLatLng(PolylineOptions pOptionsDate, Date dateFrom, Date dateBy);

    PolylineOptions getBuildVisitsTrackLatLng(PolylineOptions pOptionsDate, Date dateFrom, Date dateBy);

    List<MarkerMap> getBuildVisitsMarkers(Date dateFrom, Date dateBy);

    List<LocationPoint> getUloadedLocationTrack();

    List<LocationPoint> getUloadedLocationTrack(int minCount);

    String getUserSetting(String settingId);

    LocationPoint getLastTrackPoint();

    LocationPoint getLocationPoint(int id);

    Waybill_Document getLastWaybill();

    List<Waybill_Document> getAllWaybill();

    List<Waybill_Document> getWaybillByPeriod(Date dateFrom, Date dateBy);

    List<Visit_Document> getAllVisit();

    List<Client_Element> getAllClients();

    List<Fuel_Document> getAllFuel();

    List<Visit_Document> getVisitByPeriod(Date dateFrom, Date dateBy);

    Client_Element getClient(String externalId);

    Visit_Document getVisit(String externalId);

    Boolean isInCar();

    List<String> getChangedElements(String nameElement);

    Fuel_Document getFuel(String externalId);

    List<Photo_Element> getPhotoByElement(String nameElement, String idElement);

    Element getElementByExternaID(String nameElement, String externalId);

    Document getDocumentByExternaID(String nameDocument, String externalId);

    void insertTrackPoint(LocationPoint locationPoint);

    void SetTrackListUloadedLocationTrack(Date dateFrom, Date dateBy);

    int insertLocationPoint(LocationPoint locationPoint);

    void insertClient(Client_Element client);

    void insertWaybill(Waybill_Document waybill);

    void insertVisit(Visit_Document visit);

    void insertUserSetting(ParameterInfo usersetting);

    void insertInCar(Date date, boolean inCar);

    void insertChangedElement(String nameElement, String externalID);

    void insertFuel(Fuel_Document fuelDoc);

    void insertPhoto(Photo_Element photo);

    void setElementByExternalId(String nameElement, Element element);

    void setDocumentByExternalId(String nameElement, Document document);

    void deletedElement(String nameElement, String externalId);

    void clearDataBase();
}
