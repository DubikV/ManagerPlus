package com.gmail.vanyadubik.managerplus.activity;


import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.ClientSmalListAdapter;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.calendarapi.GoogleCalendarApi;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.db.document.Visit_Document;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;
import com.gmail.vanyadubik.managerplus.model.documents.SelectionItem;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.PhoneUtils;

import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.activity.SelectionActivity.SELECTION_ID_SELECTED_ITEM;
import static com.gmail.vanyadubik.managerplus.activity.SelectionActivity.SELECTION_LIST_PARAM;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class VisitDetailActivity extends AppCompatActivity {

    private static final int CAPTURE_SELECTION_CLIENT_ACTIVITY_REQ = 995;

    @Inject
    DataRepository dataRepository;
    @Inject
    PhoneUtils phoneUtils;
    @Inject
    GoogleCalendarApi calendarApi;

    private Visit_Document visit;
    private Client_Element client;
    private EditText mDetailDateView, mDetailTypeView, mDetailInfoView, mLatView, mLongView;
    private AutoCompleteTextView mDetailClientView;
    private LocationPoint visitPosition;
    private SimpleDateFormat dateFormatter;
    private ClientSmalListAdapter clientAdapter;
    private List<Client_Element> allClientsList;

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
                        List<SelectionItem> selectionItemList = new ArrayList<SelectionItem>();
                        for(Client_Element clientElement : allClientsList){
                            selectionItemList.add(
                                    new SelectionItem(clientElement.getExternalId(),
                                            clientElement.getName()));
                        }

                        Intent intent = new Intent(VisitDetailActivity.this, SelectionActivity.class);
                        intent.putExtra(SELECTION_LIST_PARAM, (Serializable) selectionItemList);
                        startActivityForResult(intent, CAPTURE_SELECTION_CLIENT_ACTIVITY_REQ);

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

        mLatView = (EditText) findViewById(R.id.visit_detail_lat_edit);
        mLongView = (EditText) findViewById(R.id.visit_detail_long_edit);

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

        initData();
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
                phoneUtils.call(this, client.getPhone());
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
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initData(){
        Bundle extras = getIntent().getExtras();
        Date date = LocalDateTime.now().toDate();
        if (extras != null) {
            String externalId = extras.getString(MobileManagerContract.VisitContract.EXTERNAL_ID);
            visit = dataRepository.getVisit(externalId);

            if (visit != null) {
                client = dataRepository.getClient(visit.getClientExternalId());
                visitPosition = dataRepository.getLocationPoint(visit.getVisitLP());

                mDetailDateView.setText(dateFormatter.format(visit.getDate()));
                mDetailTypeView.setText(visit.getTypeVisit());

                mDetailClientView.setText(client != null ? client.getName() : "");
                mDetailInfoView.setText(visit.getInformation());
            }else{
                visit = Visit_Document.builder()
                        .externalId("app-" + UUID.randomUUID().toString())
                        .build();
                date = new Date(extras.getLong(MobileManagerContract.VisitContract.DATE));
                mDetailDateView.setText(dateFormatter.format(date));
            }

        }else{
            visit = Visit_Document.builder()
                    .externalId("app-" + UUID.randomUUID().toString())
                    .build();
            mDetailDateView.setText(dateFormatter.format(date));
        }

        allClientsList = dataRepository.getAllClients();

        clientAdapter = new ClientSmalListAdapter(this, allClientsList);

        mDetailClientView.setThreshold(1);
        mDetailClientView.setAdapter(clientAdapter);

        if(visitPosition != null) {
            mLatView.setText(String.valueOf(visitPosition.getLatitude()));
            mLongView.setText(String.valueOf(visitPosition.getLongitude()));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_SELECTION_CLIENT_ACTIVITY_REQ) {
            if (resultCode == RESULT_OK) {

                String externalIdClient = data.getStringExtra(SELECTION_ID_SELECTED_ITEM);

                if(externalIdClient == null || externalIdClient.isEmpty()){
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.not_selected_element), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                client = (Client_Element) dataRepository
                        .getElementByExternaID(MobileManagerContract.ClientContract.TABLE_NAME,
                                externalIdClient);
                mDetailClientView.setText(client.getName());

            }
        }

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
        textView.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
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

        if (client == null) {
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

            Visit_Document visit_document = Visit_Document.builder()
                    .id(visit.getId())
                    .externalId(visit.getExternalId())
                    .deleted(visit.isDeleted())
                    .inDB(visit.isInDB())
                    .date(visit.getDate())
                    .dateVisit(visit.getDate())
                    .clientExternalId( client != null ? client.getExternalId() : "")
                    .createLP(visitPosition != null ? visitPosition.getId() : 0)
                    .visitLP(idPosition)
                    .typeVisit(mDetailTypeView.getText().toString())
                    .information(mDetailInfoView.getText().toString())
                    .build();

            dataRepository.insertVisit(visit_document);

            calendarApi.upgrateEventByVisit(visit_document);
            finish();
        }
    }

}
