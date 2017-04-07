package com.gmail.vanyadubik.managerplus.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.db.Client_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class ClientDetailActivity extends FragmentActivity {

    @Inject
    DataRepository dataRepository;

    private Client_Element client;
    private EditText mDetailNameView, mDetailAdressView, mDetailPhoneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            client = (Client_Element) extras.get("client");
        }else{
            client = Client_Element.builder().build();
        }

        mDetailNameView = (EditText) findViewById(R.id.client_detail_name);
        mDetailNameView.setFocusableInTouchMode(false);
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
                textView.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
                Button button1 = (Button) alert.findViewById(android.R.id.button1);
                button1.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
                Button button2 = (Button) alert.findViewById(android.R.id.button2);
                button2.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
                // TODO: (end stub) ------------------
            }
        });

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

            dataRepository.insertClient(Client_Element.builder()
                    .id(client.getId())
                    .name(mDetailNameView.getText().toString())
                    .externalId(client.getExternalId())
                    .address(mDetailAdressView.getText().toString())
                    .phone(mDetailPhoneView.getText().toString()).build());

            finish();
        }
    }

}
