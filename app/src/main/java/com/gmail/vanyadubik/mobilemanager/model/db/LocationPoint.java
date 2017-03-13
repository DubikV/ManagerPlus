package com.gmail.vanyadubik.mobilemanager.model.db;

import java.io.Serializable;
import java.util.Date;

public class LocationPoint implements Serializable {

    private int id;
    private Date date;
    private String latitude;
    private String longitude;
    private boolean inCar;

    public LocationPoint(int id, Date date, String latitude, String longitude, boolean inCar) {
        this.id = id;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.inCar = inCar;
    }

    public int getId() {
        return id;
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
        return inCar;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private Date date;
        private String latitude;
        private String longitude;
        private boolean inCar;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder latitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder inCar(Boolean inCar) {
            this.inCar = inCar;
            return this;
        }

        public LocationPoint build() {
            return new LocationPoint(id, date, latitude, longitude, inCar);
        }
    }
}
