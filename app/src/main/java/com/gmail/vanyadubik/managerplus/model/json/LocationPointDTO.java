package com.gmail.vanyadubik.managerplus.model.json;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class LocationPointDTO {
    @Expose
    private Date dateTime;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private boolean isInCar;

    public LocationPointDTO(Date dateTime, double latitude, double longitude, boolean isInCar) {
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isInCar = isInCar;
    }

    public Date getDate() {
        return dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isInCar() {
        return isInCar;
    }
}
