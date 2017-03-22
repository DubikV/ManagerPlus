package com.gmail.vanyadubik.managerplus.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public class LocationPointDTO {
    @Expose
    private DateTime dateTime;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    @SerializedName("inCar")
    private boolean isInCar;

    public LocationPointDTO(DateTime dateTime, double latitude, double longitude, boolean isInCar) {
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isInCar = isInCar;
    }

    public DateTime getDate() {
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
