package com.gmail.vanyadubik.managerplus.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.ClientSmalListAdapter;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.db.Client_Element;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.db.Visit_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import org.joda.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class VisitDetailActivity extends AppCompatActivity {

    @Inject
    DataRepository dataRepository;

    private Visit_Element visit;
    private Client_Element client;
    private EditText mDetailDateView, mDetailTypeView, mDetailInfoView, mLatView, mLongView;
    private AutoCompleteTextView mDetailClientView;
    private LocationPoint visitPosition;
    private SimpleDateFormat dateFormatter;
    private ClientSmalListAdapter clientAdapter;

    private SlideDateTimeListener dateTimeListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            if(visit!=null){
                visit.setDate(date);
            }

            mDetailDateView.setText(dateFormatter.format(date));

        }

        @Override
        public void onDateTimeCancel()
        {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        mDetailDateView = (EditText) findViewById(R.id.visit_detail_date);
        mDetailDateView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Date date = new Date();
                if(visit!=null){
                    if(visit.getDate()!=null){
                        if(visit.getDate().getTime() > 1000) {
                            date = visit.getDate();
                        }
                    }
                }

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

        mDetailTypeView = (EditText) findViewById(R.id.visit_detail_typevisit);

        mDetailClientView = (AutoCompleteTextView) findViewById(R.id.visit_detail_client);
        mDetailClientView.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = mDetailClientView.getRight()
                            - mDetailClientView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        mDetailClientView.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        mDetailClientView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                client = clientAdapter.getSuggestions().get(position);
            }
        });
        mDetailInfoView = (EditText) findViewById(R.id.visit_detail_info);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.visit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_return:
                closeView();
                return true;
            case R.id.action_save:
                saveData();
                return true;
            case R.id.action_call:
                return true;
            case R.id.action_foto:
                return true;
            case R.id.action_show_location:
                LocationPoint lastPoint = dataRepository.getLastTrackPoint();
                if(lastPoint != null) {
                    mLatView.setText(String.valueOf(lastPoint.getLatitude()));
                    mLongView.setText(String.valueOf(lastPoint.getLongitude()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String externalId = extras.getString(MobileManagerContract.VisitContract.VISIT_ID);
            visit = dataRepository.getVisit(externalId);

            client = dataRepository.getClient(visit.getClientExternalId());
            visitPosition = dataRepository.getLocationPoint(visit.getVisitLP());

            mDetailDateView.setText(dateFormatter.format(visit.getDate()));
            mDetailTypeView.setText(visit.getTypeVisit());

            mDetailClientView.setText(client!=null? client.getName(): "");
            mDetailInfoView.setText(visit.getInformation());

        }else{
            visit = Visit_Element.builder()
                    .externalId("app-" + UUID.randomUUID().toString())
                    .build();
        }



        mLatView = (EditText) findViewById(R.id.visit_detail_lat_edit);
        mLongView = (EditText) findViewById(R.id.visit_detail_long_edit);

        if(visitPosition != null) {
            mLatView.setText(String.valueOf(visitPosition.getLatitude()));
            mLongView.setText(String.valueOf(visitPosition.getLongitude()));
        }

        Button saveButton = (Button) findViewById(R.id.save_client_detail_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'save member detail'");
                saveData();
            }
        });

        Button retMemberDet = (Button) findViewById(R.id.close_client_detail_button);
        retMemberDet.setFocusable(true);
        retMemberDet.setFocusableInTouchMode(true);
        retMemberDet.requestFocus();
        retMemberDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               closeView();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Client_Element> clientsList = dataRepository.getAllClients();

        clientAdapter = new ClientSmalListAdapter(this, clientsList);

        mDetailClientView.setThreshold(1);
        mDetailClientView.setAdapter(clientAdapter);

    }

    private void closeView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VisitDetailActivity.this);
        builder.setMessage(getString(R.string.questions_data_save));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveData();
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        // TODO: (end stub) ------------------
    }

    private void saveData() {

        mDetailDateView.setError(null);
        mDetailTypeView.setError(null);
        mDetailClientView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mDetailDateView.getText().toString())) {
            mDetailDateView.setError(getString(R.string.error_field_required));
            focusView = mDetailDateView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mDetailTypeView.getText().toString())) {
            mDetailTypeView.setError(getString(R.string.error_field_required));
            focusView = mDetailTypeView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mDetailClientView.getText().toString())) {
            mDetailClientView.setError(getString(R.string.error_field_required));
            focusView = mDetailClientView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();

        } else {
            int idPosition = 0;
            if (!TextUtils.isEmpty(mLatView.getText().toString())&&
                    !TextUtils.isEmpty(mLongView.getText().toString())) {

                if(visitPosition!=null) {
                    visitPosition.setDate(LocalDateTime.now().toDate());
                    visitPosition.setLatitude(Double.valueOf(mLatView.getText().toString()));
                    visitPosition.setLongitude(Double.valueOf(mLongView.getText().toString()));
                }else{
                    visitPosition = LocationPoint.builder()
                            .date(LocalDateTime.now().toDate())
                            .latitude(Double.valueOf(mLatView.getText().toString()))
                            .longitude(Double.valueOf(mLongView.getText().toString()))
                            .build();
                }
                idPosition = dataRepository.insertLocationPoint(visitPosition);
            }
            dataRepository.insertVisit(Visit_Element.builder()
                    .id(visit.getId())
                    .externalId(visit.getExternalId())
                    .deleted(visit.isDeleted())
                    .inDB(visit.isInDB())
                    .date(visit.getDate())
                    .dateVisit(visit.getDateVisit())
                    .clientExternalId( client != null ? client.getExternalId() : "")
                    .createLP(visitPosition != null ? visitPosition.getId() : 0)
                    .visitLP(idPosition)
                    .typeVisit(mDetailTypeView.getText().toString())
                    .information(mDetailInfoView.getText().toString())
                    .build());

            finish();
        }
    }

}
