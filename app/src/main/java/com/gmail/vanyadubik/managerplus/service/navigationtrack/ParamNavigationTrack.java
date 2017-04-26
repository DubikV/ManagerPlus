package com.gmail.vanyadubik.managerplus.service.navigationtrack;

import com.google.android.gms.maps.model.LatLng;

public class ParamNavigationTrack {
    private int idTrack;
    private float widthPolyline;
    private LatLng latLngStart;
    private LatLng latLngEnd;

    public ParamNavigationTrack(int idTrack, float widthPolyline, LatLng latLngStart, LatLng latLngEnd) {
        this.idTrack = idTrack;
        this.widthPolyline = widthPolyline;
        this.latLngStart = latLngStart;
        this.latLngEnd = latLngEnd;
    }

    public int getIdTrack() {
        return idTrack;
    }

    public void setIdTrack(int idTrack) {
        this.idTrack = idTrack;
    }

    public float getWidthPolyline() {
        return widthPolyline;
    }

    public void setWidthPolyline(float widthPolyline) {
        this.widthPolyline = widthPolyline;
    }

    public LatLng getLatLngStart() {
        return latLngStart;
    }

    public void setLatLngStart(LatLng latLngStart) {
        this.latLngStart = latLngStart;
    }

    public LatLng getLatLngEnd() {
        return latLngEnd;
    }

    public void setLatLngEnd(LatLng latLngEnd) {
        this.latLngEnd = latLngEnd;
    }
}