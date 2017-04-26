package com.gmail.vanyadubik.managerplus.service.navigationtrack;

import com.google.android.gms.maps.model.PolylineOptions;

public interface NavigationUpdateListener {

    void updateNavogationTrack(PolylineOptions lineOptions, int idTrack);

}