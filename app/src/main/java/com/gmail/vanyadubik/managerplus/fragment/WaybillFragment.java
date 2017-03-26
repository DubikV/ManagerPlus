package com.gmail.vanyadubik.managerplus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.gps.GPSTracker;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.task.SyncIntentService;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

public class WaybillFragment extends Fragment {
    private static  final int LAYOUT = R.layout.fragment_waybill;

    @Inject
    GPSTracker gpsTracker;

    private View view;

    public static WaybillFragment getInstance() {

        Bundle args = new Bundle();
        WaybillFragment fragment = new WaybillFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        Button btnShowLocation = (Button) view.findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(gpsTracker.canGetLocation()){
                    LocationPoint locationPoint = gpsTracker.getLocationPoint();
                    Toast.makeText(getActivity().getApplicationContext(),
                            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                    .format(locationPoint.getDate().getTime())
                                    + " location is - \nLat: " + locationPoint.getLatitude()
                                    + "\nLong: " + locationPoint.getLongitude(),
                            Toast.LENGTH_LONG).show();
                }else{
                    gpsTracker.showSettingsAlert();
                }

            }
        });

        Button btnSync = (Button) view.findViewById(R.id.btnSync);
        btnSync.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), SyncIntentService.class);
                getActivity().getApplicationContext().startService(intent);
            }
        });

        return view;
    }

}
