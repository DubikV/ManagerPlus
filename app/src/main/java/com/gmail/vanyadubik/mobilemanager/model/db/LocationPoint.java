package com.gmail.vanyadubik.mobilemanager.model.db;

import java.io.Serializable;
import java.util.Date;

public class LocationPoint implements Serializable {

    private int id;
    private Date date;
    private double latitude;
    private double longitude;
    private boolean inCar;

    public LocationPoint(Date date, double latitude, double longitude, boolean inCar) {
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.inCar = inCar;
    }

    public LocationPoint(int id, Date date, double latitude, double longitude, boolean inCar) {
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
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
        private double latitude;
        private double longitude;
        private boolean inCar;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
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
