package com.gmail.vanyadubik.managerplus.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.FuelContract;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.db.document.Fuel_Document;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import org.joda.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_POSITION_LAT;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_POSITION_LON;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE_GET_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class FuelDetailActivity extends AppCompatActivity {

    @Inject
    DataRepository dataRepository;

    private Fuel_Document fuelDoc;
    private EditText mDetailDateView, mDetailTypeFuelView,
            mDetailTypePaymentView, mDetailLitresView,
            mDetailMoneyView, mLatView, mLongView;
    private LocationPoint position;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_doc);
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String externalId = extras.getString(FuelContract.EXTERNAL_ID);
            fuelDoc = dataRepository.getFuel(externalId);
        }else{
            fuelDoc = Fuel_Document.builder()
                    .externalId("app-" + UUID.randomUUID().toString())
                    .date(LocalDateTime.now().toDate())
                    .build();
        }

        position = dataRepository.getLocationPoint(fuelDoc.getCreateLP());

        mDetailDateView = (EditText) findViewById(R.id.fuel_detail_date);
        mDetailTypeFuelView = (EditText) findViewById(R.id.fuel_detail_typefuel);
        mDetailTypePaymentView = (EditText) findViewById(R.id.fuel_detail_typepayment);
        mDetailLitresView = (EditText) findViewById(R.id.fuel_detail_litres);
        mDetailMoneyView = (EditText) findViewById(R.id.fuel_detail_money);


        mLatView = (EditText) findViewById(R.id.fuel_lat_edit);
        mLongView = (EditText) findViewById(R.id.fuel_long_edit);

        Button saveButton = (Button) findViewById(R.id.save_fuel_detail_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'save member detail'");
                saveData();
            }
        });

        Button retMemberDet = (Button) findViewById(R.id.close_fuel_detail_button);
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
            case R.id.action_show_map_location:

                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra(MAP_TYPE, MAP_TYPE_GET_LOCATION);
                if(position!=null) {
                    intent.putExtra(MAP_SHOW_POSITION_LAT, String.valueOf(position.getLatitude()));
                    intent.putExtra(MAP_SHOW_POSITION_LON, String.valueOf(position.getLongitude()));
                }
                startActivityForResult(intent, 1);
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


        mDetailDateView.setText(dateFormatter.format(fuelDoc.getDate()));
        mDetailTypeFuelView.setText(fuelDoc.getTypeFuel());
        mDetailTypePaymentView.setText(fuelDoc.getTypePayment());
        mDetailLitresView.setText(String.valueOf(fuelDoc.getLitres()));
        mDetailMoneyView.setText(String.valueOf(fuelDoc.getMoney()));

        if(position != null) {
            mLatView.setText(String.valueOf(position.getLatitude()));
            mLongView.setText(String.valueOf(position.getLongitude()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}
        String lat = data.getStringExtra(MAP_SHOW_POSITION_LAT);
        String lon = data.getStringExtra(MAP_SHOW_POSITION_LON);
        if(lat != null && lon != null){
            if(position!=null) {
                position.setDate(LocalDateTime.now().toDate());
                position.setLatitude(Double.valueOf(lat));
                position.setLongitude(Double.valueOf(lon));
            }else{
                position = LocationPoint.builder()
                        .date(LocalDateTime.now().toDate())
                        .latitude(Double.valueOf(lat))
                        .longitude(Double.valueOf(lon))
                        .build();
            }
        }

    }

    private void closeView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FuelDetailActivity.this);
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
        mDetailTypeFuelView.setError(null);
        mDetailTypePaymentView.setError(null);
        mDetailLitresView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mDetailDateView.getText().toString())) {
            mDetailDateView.setError(getString(R.string.error_field_required));
            focusView = mDetailDateView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mDetailTypeFuelView.getText().toString())) {
            mDetailTypeFuelView.setError(getString(R.string.error_field_required));
            focusView = mDetailTypeFuelView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mDetailTypePaymentView.getText().toString())) {
            mDetailTypePaymentView.setError(getString(R.string.error_field_required));
            focusView = mDetailTypePaymentView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mDetailLitresView.getText().toString())) {
            mDetailLitresView.setError(getString(R.string.error_field_required));
            focusView = mDetailLitresView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();

        } else {
            int idPosition = 0;
            if (!TextUtils.isEmpty(mLatView.getText().toString())&&
                    !TextUtils.isEmpty(mLongView.getText().toString())) {

                if(position!=null) {
                    position.setDate(LocalDateTime.now().toDate());
                    position.setLatitude(Double.valueOf(mLatView.getText().toString()));
                    position.setLongitude(Double.valueOf(mLongView.getText().toString()));
                }else{
                    position = LocationPoint.builder()
                            .date(LocalDateTime.now().toDate())
                            .latitude(Double.valueOf(mLatView.getText().toString()))
                            .longitude(Double.valueOf(mLongView.getText().toString()))
                            .build();
                }
                idPosition = dataRepository.insertLocationPoint(position);
            }

            dataRepository.insertFuel(Fuel_Document.builder()
                    .id(fuelDoc.getId())
                    .externalId(fuelDoc.getExternalId())
                    .deleted(fuelDoc.isDeleted())
                    .inDB(fuelDoc.isInDB())
                    .date(fuelDoc.getDate())
                    .typeFuel(mDetailTypeFuelView.getText().toString())
                    .typePayment(mDetailTypePaymentView.getText().toString())
                    .typePayment(mDetailTypePaymentView.getText().toString())
                    .litres(Double.valueOf(mDetailLitresView.getText().toString()))
                    .money(Double.valueOf(mDetailMoneyView.getText().toString()))
                    .createLP(position != null ? position.getId() : 0)
                    .build());

            finish();
        }
    }

}
