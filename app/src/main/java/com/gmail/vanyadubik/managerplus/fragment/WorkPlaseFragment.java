package com.gmail.vanyadubik.managerplus.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.AddedPhotosActivity;
import com.gmail.vanyadubik.managerplus.activity.MapTrackerActivity;
import com.gmail.vanyadubik.managerplus.adapter.VisitListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.db.document.Visit_Document;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;
import com.gmail.vanyadubik.managerplus.model.documents.VisitList;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import org.joda.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.activity.AddedPhotosActivity.GALLERY_EXTERNALID_OBJECT;
import static com.gmail.vanyadubik.managerplus.activity.AddedPhotosActivity.GALLERY_NAME_OBJECT;
import static com.gmail.vanyadubik.managerplus.common.Consts.CLEAR_DATE;

public class WorkPlaseFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_workspace;

    @Inject
    DataRepository dataRepository;


    private View view;

    private Button inputStartOdometer, inputEndOdometer, usingCarButton;
    private EditText startDateEditText, endDateEditText, startOdometerEditText, endOdometerEditText;
    private SimpleDateFormat dateFormatter;
    private Waybill_Document waybill;
    private List<VisitList> listVisits;
    private ListView listVisitsView;
    private Boolean inCar;

    public static WorkPlaseFragment getInstance() {

        Bundle args = new Bundle();
        WorkPlaseFragment fragment = new WorkPlaseFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ((ManagerPlusAplication) getActivity().getApplication()).getComponent().inject(this);

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        inputStartOdometer = (Button) view.findViewById(R.id.wb_startday_bt);
        inputStartOdometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDataOdometer(true);
            }
        });

        inputEndOdometer = (Button) view.findViewById(R.id.wb_endday_bt);
        inputEndOdometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDataOdometer(false);
            }
        });

        startDateEditText = (EditText) view.findViewById(R.id.wb_startday_date);
        endDateEditText = (EditText) view.findViewById(R.id.wb_endday_date);
        startOdometerEditText = (EditText) view.findViewById(R.id.wb_startday_odometer);
        endOdometerEditText = (EditText) view.findViewById(R.id.wb_endday_odometer);

        usingCarButton = (Button) view.findViewById(R.id.waybill_incar_button);
        usingCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inCar = inCar == null || inCar ? false : true;
                dataRepository.insertInCar(LocalDateTime.now().toDate(), inCar);
                setDrawableInCar();
            }
        });

        Button photoButton = (Button) view.findViewById(R.id.waybill_foto_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddedPhotosActivity.class);
                intent.putExtra(GALLERY_NAME_OBJECT, MobileManagerContract.WaybillContract.TABLE_NAME);
                intent.putExtra(GALLERY_EXTERNALID_OBJECT, waybill.getExternalId());
                startActivity(intent);
            }
        });

        Button showMap = (Button) view.findViewById(R.id.waybill_show_map);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MapTrackerActivity.class));
            }
        });

        listVisitsView = (ListView) view.findViewById(R.id.visits_today_listview);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    private void initDataInCap(){
        if(waybill!=null) {
            Date date = waybill.getDateStart();
            if (date.getTime()< 1000) {
                startDateEditText.setText(CLEAR_DATE);
            }else {
                startDateEditText.setText(dateFormatter.format(date));
            }

            date = waybill.getDateEnd();
            if (date.getTime()< 1000) {
                endDateEditText.setText(CLEAR_DATE);
            }else {
                endDateEditText.setText(dateFormatter.format(date));
            }
            startOdometerEditText.setText(String.valueOf(waybill.getStartOdometer()));
            endOdometerEditText.setText(String.valueOf(waybill.getEndOdometer()));
        }
    }

    private void initData(){

        waybill = dataRepository.getLastWaybill();

        setBackgroundButtonOdometer();

        if(waybill!=null) {
            initDataInCap();
        }else{
            return;
        }

        listVisits = new ArrayList<>();

        Date dateEnd = waybill.getDateEnd();
        if(waybill.getDateEnd().getTime() <1000){
            dateEnd = LocalDateTime.now().toDate();
            dateEnd.setHours(23);
            dateEnd.setMinutes(59);
            dateEnd.setSeconds(59);
        }
        List<Visit_Document> visits = dataRepository.getVisitByPeriod(waybill.getDateStart(), dateEnd);

        if(visits!=null) {
            for (Visit_Document visit : visits) {
                VisitList visitList = new VisitList(visit.getExternalId(), visit.getDate(), visit.getTypeVisit());
                Client_Element client = dataRepository.getClient(visit.getClientExternalId());
                if (client != null) {
                    visitList.setClient(client.getName());
                }
                listVisits.add(visitList);
            }

            VisitListAdapter adapter = new VisitListAdapter(getActivity(), listVisits);
            listVisitsView.setAdapter(adapter);
        }

        inCar = dataRepository.isInCar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showInputDataOdometer(Boolean start){
        final boolean inputStart = start;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(start ? getResources().getString(R.string.input_start_odometer) :
                getResources().getString(R.string.input_end_odometer));

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input_odometer, (ViewGroup) getView(), false);

        final EditText input_date = (EditText) viewInflated.findViewById(R.id.input_date);
        final Date dateInput = LocalDateTime.now().toDate();
        input_date.setText(dateFormatter.format(dateInput));
        input_date.setEnabled(false);
        input_date.setFocusableInTouchMode(false);
        input_date.setFocusable(false);
        final EditText input_odometer = (EditText) viewInflated.findViewById(R.id.input_odometer);
        input_odometer.setInputType(InputType.TYPE_CLASS_NUMBER);
        String textInput = "0";
        if(waybill == null){
            textInput = "0";
        }else{
            if(start){
                textInput = String.valueOf(waybill.getEndOdometer());
            }else{
                textInput = String.valueOf(waybill.getStartOdometer());
            }
        }
           input_odometer.setText(textInput);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                int odometer = Integer.valueOf(input_odometer.getText().toString());
                if(odometer == 0){
                    return;
                }
                if(waybill == null || inputStart){
                    String externalId = "app-" + UUID.randomUUID().toString();
                    waybill = Waybill_Document.builder()
                            .externalId(externalId)
                            .date(LocalDateTime.now().toDate())
                            .build();
                }

                if(inputStart) {
                    waybill.setStartOdometer(odometer);
                    waybill.setDateStart(dateInput);
                }else{
                    waybill.setEndOdometer(odometer);
                    waybill.setDateEnd(dateInput);
                }

                dataRepository.insertWaybill(waybill);

                initData();

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void setBackgroundButtonOdometer(){

        if(waybill == null || waybill.getEndOdometer() > 0){
            inputStartOdometer.setBackground(getResources().getDrawable(R.drawable.button_waybill_active));
            inputStartOdometer.setEnabled(false);
            inputEndOdometer.setBackground(getResources().getDrawable(R.drawable.button_waybill_inactive));
            inputStartOdometer.setEnabled(true);
        }else{
            inputStartOdometer.setBackground(getResources().getDrawable(R.drawable.button_waybill_inactive));
            inputStartOdometer.setEnabled(true);
            inputEndOdometer.setBackground(getResources().getDrawable(R.drawable.button_waybill_active));
            inputStartOdometer.setEnabled(false);
        }

    }

    @Override
    public void onBecameVisible() {

    }

    @Override
    public void onBecameUnVisible() {

    }

    private void setDrawableInCar(){
       if(inCar){
           usingCarButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_noincar, 0);
           usingCarButton.setText(getResources().getString(R.string.waybill_no_incar));
       }else {
           usingCarButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_incar, 0);
           usingCarButton.setText(getResources().getString(R.string.waybill_incar));
       }
    }
}
