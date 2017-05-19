//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//import java.util.Date;
//
//public class Location {
//    private Date date;
//    private double latitude;
//    private double longitude;
//    private Provider provider;
//
//    public Location(double latitude, double longitude, long date, Provider provider) {
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.date = new Date(date);
//        this.provider = provider;
//    }
//
//    public Location(double latitude, double longitude, Provider provider) {
//        this(latitude, longitude, System.currentTimeMillis(), provider);
//    }
//
//    public double getLatitude() {
//        return this.latitude;
//    }
//
//    public double getLongitude() {
//        return this.longitude;
//    }
//
//    public Date getDate() {
//        return this.date;
//    }
//
//    public Provider getProvider() {
//        return this.provider;
//    }
//
//    public double getLatitudeDegree() {
//        return this.latitude;
//    }
//
//    public double getLongitudeDegree() {
//        return this.longitude;
//    }
//
//    public boolean IsActualCoordinates() {
//        return System.currentTimeMillis() - getDate().getTime() <= 3000;
//    }
//
//    public int[] getArgsDate() {
//        return new int[]{getDate().getYear() + 1900, getDate().getMonth() + 1, getDate().getDate(), getDate().getDay(), getDate().getHours(), getDate().getMinutes(), getDate().getSeconds()};
//    }
//}
