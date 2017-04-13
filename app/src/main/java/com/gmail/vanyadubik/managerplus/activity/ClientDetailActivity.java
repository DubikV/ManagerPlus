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
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ClientContract;
import com.gmail.vanyadubik.managerplus.model.db.Client_Element;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import org.joda.time.LocalDateTime;

import java.util.UUID;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_POSITION_LAT;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_POSITION_LON;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE_GET_LOCATION;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class ClientDetailActivity extends AppCompatActivity {

    @Inject
    DataRepository dataRepository;

    private Client_Element client;
    private EditText mDetailNameView, mDetailAdressView, mDetailPhoneView, mLatView, mLongView;
    private LocationPoint position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);
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
                LocationPoint locationPoint = dataRepository.getLastTrackPoint();
                if(locationPoint!=null){
                    break;
                }
                intent.putExtra(MAP_TYPE, MAP_TYPE_GET_LOCATION);
                intent.putExtra(MAP_SHOW_POSITION_LAT, String.valueOf(locationPoint.getLatitude()));
                intent.putExtra(MAP_SHOW_POSITION_LON, String.valueOf(locationPoint.getLongitude()));
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

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String externalId = extras.getString(ClientContract.CLIENT_ID);
            client = dataRepository.getClient(externalId);
        }else{
            client = Client_Element.builder()
                    .externalId("app-" + UUID.randomUUID().toString())
                    .build();
        }

        position = dataRepository.getLocationPoint(client.getPositionLP());

        mDetailNameView = (EditText) findViewById(R.id.client_detail_name);
        mDetailNameView.setText(client.getName());


        mDetailAdressView = (EditText) findViewById(R.id.client_detail_adress);
        mDetailAdressView.setText(client.getAddress());

        mDetailPhoneView = (EditText) findViewById(R.id.client_detail_phone);
        mDetailPhoneView.setText(client.getPhone());
        mDetailPhoneView.addTextChangedListener(new TextWatcher() {
            int length_before = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length_before = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (length_before < s.length()) {
                    if (s.length() == 1) {
                        if (Character.isDigit(s.charAt(0)))
                            s.insert(0, "(");
                    }
                    if (s.length() == 4) {
                        s.append(")");
                        if (s.length() > 4) {
                            if (Character.isDigit(s.charAt(4)))
                                s.insert(4, ")");
                        }
                    }
                    if (s.length() == 8 || s.length() == 11) {
                        s.append("-");
                        if (s.length() > 8) {
                            if (Character.isDigit(s.charAt(8)))
                                s.insert(8, "-");
                        }
                        if (s.length() > 11) {
                            if (Character.isDigit(s.charAt(11)))
                                s.insert(11, "-");
                        }
                    }
                }
            }
        });

        mLatView = (EditText) findViewById(R.id.client_lat_edit);
        mLongView = (EditText) findViewById(R.id.client_long_edit);

        if(position != null) {
            mLatView.setText(String.valueOf(position.getLatitude()));
            mLongView.setText(String.valueOf(position.getLongitude()));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}
        String lat = data.getStringExtra(MAP_SHOW_POSITION_LAT);
        String lon = data.getStringExtra(MAP_SHOW_POSITION_LON);
        if(lat != null && lon != null){
            mLatView.setText(lat);
            mLongView.setText(lon);
        }

    }

    private void closeView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClientDetailActivity.this);
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

        mDetailNameView.setError(null);
        mDetailPhoneView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mDetailNameView.getText().toString())) {
            mDetailNameView.setError(getString(R.string.error_field_required));
            focusView = mDetailNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mDetailPhoneView.getText().toString())) {
            mDetailPhoneView.setError(getString(R.string.error_field_required));
            focusView = mDetailPhoneView;
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
            dataRepository.insertClient(Client_Element.builder()
                    .id(client.getId())
                    .name(mDetailNameView.getText().toString())
                    .externalId(client.getExternalId())
                    .address(mDetailAdressView.getText().toString())
                    .phone(mDetailPhoneView.getText().toString())
                    .positionLP(idPosition).build());

            finish();
        }
    }

}
