package com.gmail.vanyadubik.managerplus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.GPSTracker;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

public class FuelListFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_waybill;

    @Inject
    GPSTracker gpsTracker;

    private View view;

    private EditText dateStartEdinText, dateEndEdinText;
    private SimpleDateFormat dateFormatter;

    public static FuelListFragment getInstance() {

        Bundle args = new Bundle();
        FuelListFragment fragment = new FuelListFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ((ManagerPlusAplication) getActivity().getApplication()).getComponent().inject(this);

//
//        Button btnSync = (Button) view.findViewById(R.id.btnSync);
//        btnSync.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                Intent intent = new Intent(getActivity(), SyncIntentTrackService.class);
//                intent.putExtra(DATE_TRACK_START, dateFormatter.parse(dateStartEdinText.getText().toString(), new ParsePosition(0)).getTime());
//                intent.putExtra(DATE_TRACK_END, dateFormatter.parse(dateEndEdinText.getText().toString(), new ParsePosition(0)).getTime());
//                getActivity().startService(intent);
//            }
//        });
//        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
//
//        dateStartEdinText = (EditText) view.findViewById(R.id.dateStart);
//        dateStartEdinText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar newCalendar = Calendar.getInstance();
//                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        Calendar newDate = Calendar.getInstance();
//                        newDate.set(year, monthOfYear, dayOfMonth);
//                        dateStartEdinText.setText(dateFormatter.format(newDate.getTime()));
//                    }
//
//                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.show();
//            }
//        });
//
//        dateEndEdinText = (EditText) view.findViewById(R.id.dateEnd);
//        dateEndEdinText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar newCalendar = Calendar.getInstance();
//                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        Calendar newDate = Calendar.getInstance();
//                        newDate.set(year, monthOfYear, dayOfMonth);
//                        dateEndEdinText.setText(dateFormatter.format(newDate.getTime()));
//                    }
//
//                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.show();
//            }
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBecameVisible() {
    }
}
