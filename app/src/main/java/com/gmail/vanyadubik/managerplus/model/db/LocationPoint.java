package com.gmail.vanyadubik.managerplus.model.db;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class LocationPoint implements Serializable {

    private int id;
    private DateTime dateTime;
    private double latitude;
    private double longitude;
    private boolean inCar;

    public LocationPoint(DateTime dateTime, double latitude, double longitude, boolean inCar) {
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.inCar = inCar;
    }

    public LocationPoint(int id, DateTime dateTime, double latitude, double longitude, boolean inCar) {
        this.id = id;
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.inCar = inCar;
    }

    public int getId() {
        return id;
    }

    public DateTime getDateTime() {
        return dateTime;
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
        private DateTime dateTime;
        private double latitude;
        private double longitude;
        private boolean inCar;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder dateTime(DateTime dateTime) {
            this.dateTime = dateTime;
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
            return new LocationPoint(id, dateTime, latitude, longitude, inCar);
        }
    }
}
