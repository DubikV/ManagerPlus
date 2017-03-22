package com.gmail.vanyadubik.managerplus.utils;

import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.json.LocationPointDTO;

public final class Db2JsonModelConverter {
    public static LocationPointDTO convertLocationPoint(LocationPoint locationPoint) {
        return new LocationPointDTO(
                locationPoint.getDateTime(),
                locationPoint.getLatitude(),
                locationPoint.getLongitude(),
                locationPoint.isInCar()
        );
    }
}
