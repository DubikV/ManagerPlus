package com.gmail.vanyadubik.mobilemanager.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class LocationPointDTO {
    @Expose
    private Date date;
    @Expose
    private String latitude;
    @Expose
    private String longitude;
    @Expose
    @SerializedName("inCar")
    private boolean isInCar;

    public LocationPointDTO(String latitude, Date date, String longitude, boolean isInCar) {
        this.latitude = latitude;
        this.date = date;
        this.longitude = longitude;
        this.isInCar = isInCar;
    }

    public Date getDate() {
        return date;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public boolean isInCar() {
        return isInCar;
    }
}
