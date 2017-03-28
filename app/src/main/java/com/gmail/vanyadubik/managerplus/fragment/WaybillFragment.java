package com.gmail.vanyadubik.managerplus.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.GPSTracker;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService.DATE_TRACK_END;
import static com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService.DATE_TRACK_START;

public class WaybillFragment extends Fragment {
    private static  final int LAYOUT = R.layout.fragment_waybill;

    @Inject
    GPSTracker gpsTracker;

    private View view;

    private Date dateStart, dateEnd;
    private EditText dateStartEdinText, dateEndEdinText;
    private SimpleDateFormat dateFormatter;

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
        ((ManagerPlusAplication) getActivity().getApplication()).getComponent().inject(this);

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
                Intent intent = new Intent(getActivity(), SyncIntentTrackService.class);
                intent.putExtra(DATE_TRACK_START, dateFormatter.parse(dateStartEdinText.getText().toString(), new ParsePosition(0)).getTime());
                intent.putExtra(DATE_TRACK_END, dateFormatter.parse(dateEndEdinText.getText().toString(), new ParsePosition(0)).getTime());
                getActivity().startService(intent);
            }
        });
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        dateStartEdinText = (EditText) view.findViewById(R.id.dateStart);
        dateStartEdinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        dateStartEdinText.setText(dateFormatter.format(newDate.getTime()));
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        dateEndEdinText = (EditText) view.findViewById(R.id.dateEnd);
        dateEndEdinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        dateEndEdinText.setText(dateFormatter.format(newDate.getTime()));
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        return view;
    }

}
