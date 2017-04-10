package com.gmail.vanyadubik.managerplus.model.map;

import com.google.android.gms.maps.model.LatLng;

public class MarkerMap {
    private String name;
    private LatLng latLng;

    public MarkerMap(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
