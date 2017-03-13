package com.gmail.vanyadubik.mobilemanager.repository;

import com.gmail.vanyadubik.mobilemanager.model.ParameterInfo;
import com.gmail.vanyadubik.mobilemanager.model.db.LocationPoint;

import java.util.Date;
import java.util.List;

public interface DataRepository {

    List<LocationPoint> getTrack(Date dateFrom, Date dateBy);

    void insertLocationPoint(LocationPoint locationPoint);

    String getUserSetting(String settingId);

    void insertUserSetting(ParameterInfo usersetting);

}
