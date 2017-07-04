package com.gmail.vanyadubik.managerplus.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.gmail.vanyadubik.managerplus.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchActivity extends FragmentActivity {


    public static final String PARAM_SEARCH = "param_search";
    public static final int SEARCH_BY_PERIOD = 1;
    public static final int SEARCH_BY_NAME = 2;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private EditText startdate, enddate;
    private Date dateStart, dateEnd;
    private int touchDate;

    private SlideDateTimeListener dateTimeListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            if(touchDate == 1) {
                startdate.setText(dateFormatter.format(date));
            }else {
                enddate.setText(dateFormatter.format(date));
            }
        }

        @Override
        public void onDateTimeCancel()
        {

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_tracking_notification);
        dateStart = dateEnd = new Date();
    }

    public void onResume() {
        super.onResume();
        int paramSearch = getIntent().getIntExtra(PARAM_SEARCH, 0);
        if (paramSearch > 0) {
            getIntent().removeExtra(PARAM_SEARCH);

            if (paramSearch == SEARCH_BY_PERIOD) {
                LayoutInflater factory = LayoutInflater.from(SearchActivity.this);

                final View textEntryView = factory.inflate(R.layout.activity_search_period, null);

                startdate = (EditText) textEntryView.findViewById(R.id.startdate);
                startdate.setText(dateFormatter.format(dateStart), TextView.BufferType.EDITABLE);
                startdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        touchDate = 1;
                    }
                });
                enddate = (EditText) textEntryView.findViewById(R.id.enddate);
                enddate.setText(dateFormatter.format(dateEnd), TextView.BufferType.EDITABLE);
                enddate.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Date date = new Date();
                        touchDate = 2;

                        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                .setListener(dateTimeListener)
                                .setInitialDate(date)
                                .setIs24HourTime(true)
                                .setIndicatorColor(getResources().getColor(R.color.colorPrimary))
                                .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                                .build()
                                .show();
                        return true;
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setTitle(getString(R.string.action_foto));
                builder.setMessage(getString(R.string.input_info_about_photo));
                builder.setView(textEntryView);

                builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String startdateText = startdate.getText().toString();
                        String enddatetext = enddate.getText().toString();

//                if(info==null || info.isEmpty()){
//                   return "";
//                }
                    }
                });

                builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                // TODO (start stub): to set size text in AlertDialog
                TextView textView = (TextView) alert.findViewById(android.R.id.message);
                textView.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
                Button button1 = (Button) alert.findViewById(android.R.id.button1);
                button1.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
                Button button2 = (Button) alert.findViewById(android.R.id.button2);
                button2.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
                // TODO: (end stub) ------------------
            }
        }else {
        }

//            Locale locale = Locale.US;
//            Builder builder = new Builder(this);
//            String notifyText;
//            if (typeMessage == 2) {
//                GpsTracking gpsTracking = new GpsTracking(this);
//                GpsTracking.GpsData gpsData = gpsTracking.getLastGpsData();
//                notifyText = getResources().getString(R.string.service_tracking_last_gps);
//                if(gpsData == null){
//                    notifyText = notifyText + ": " + getResources().getString(R.string.not_found);
//                }else {
//                    notifyText = notifyText + ": "
//                            + "\n" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(gpsData._date)
//                            + "\n" + new DecimalFormat("#.####").format(gpsData._latitude) +
//                            " : " + new DecimalFormat("#.####").format(gpsData._longitude);
//                }
//                builder.setTitle(R.string.service_tracking_error_location).
//                        setMessage(notifyText).setCancelable(false).
//                        setNegativeButton(R.string.questions_answer_ok, new DissmisButton());
//            }else {
//                boolean isGPSEnabled = ((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS);
//                String str = "%s%s%s";
//                Object[] objArr = new Object[4];
//                objArr[0] = SharedStorage.getInteger(this, GpsTracking.PREF_INTERVAL, 0) == 0 ? getResources().getString(R.string.service_tracking_null_interval) : "";
//                objArr[1] = !isGPSEnabled ? getResources().getString(R.string.gps_is_disabled) : "";
//                objArr[2] = SharedStorage.getInteger(getApplicationContext(), PREF_TYPE_SERVICE, 0) > 2 ? getResources().getString(R.string.service_tracking_null_type) : "";
//                notifyText = String.format(locale, str, objArr);
//                builder.setTitle(R.string.service_tracking_error_message).
//                        setMessage(notifyText).setCancelable(false).
//                        setNegativeButton(R.string.questions_answer_ok, new DissmisButton());
//
//                if (!isGPSEnabled) {
//                    builder.setNeutralButton(R.string.action_settings, new SettingsButton());
//                } else {
//                    builder.setNeutralButton(R.string.action_settings, new SettingsAppButton());
//                }
//            }
//            builder.create().show();

   }

}