package com.gmail.vanyadubik.managerplus.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.FuelDetailActivity;
import com.gmail.vanyadubik.managerplus.adapter.FuelListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.db.document.Document;
import com.gmail.vanyadubik.managerplus.model.db.document.Fuel_Document;
import com.gmail.vanyadubik.managerplus.model.documents.FuelList;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.ElementUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.R.id.enddate;
import static com.gmail.vanyadubik.managerplus.R.id.startdate;

public class FuelListFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_fuel_list;

    @Inject
    DataRepository dataRepository;
    @Inject
    ElementUtils elementUtils;

    private View view;
    private List<FuelList> list;
    private ListView listView;
    private FuelListAdapter adapter;
    private FloatingActionButton fuelDelBtn, fuelSearchBtn, fuelAddBtn;
    private Fuel_Document selectedfuel;
    private int touchDate;
    private Date selectPeriodStart, selectPeriodEnd;
    private Boolean selectionOn;
    private BottomSheetDialog bottomSheetDialog ;
    private View textEntryView;

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

        selectPeriodStart = new Date();
        selectPeriodStart.setHours(0);
        selectPeriodStart.setMinutes(0);
        selectPeriodStart.setSeconds(0);

        selectPeriodEnd = new Date();
        selectPeriodEnd.setHours(23);
        selectPeriodEnd.setMinutes(59);
        selectPeriodEnd.setSeconds(59);

        selectionOn = false;

        listView = (ListView) view.findViewById(R.id.fuellist_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FuelList fuelList = (FuelList)adapter.getItem(position);
                selectedfuel = (Fuel_Document) dataRepository.
                        getDocumentByExternaID(MobileManagerContract.FuelContract.TABLE_NAME,
                                fuelList.getExternalId());
                startActivity(
                        new Intent(getActivity(), FuelDetailActivity.class)
                                .putExtra(MobileManagerContract.FuelContract.EXTERNAL_ID, selectedfuel.getExternalId()));
                showButtons(false);
                setSelected(list.size());

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FuelList fuelList = (FuelList)adapter.getItem(position);
                selectedfuel = (Fuel_Document) dataRepository.
                        getDocumentByExternaID(MobileManagerContract.FuelContract.TABLE_NAME,
                                fuelList.getExternalId());
                showButtons(true);
                setSelected(position);
                return true;
            }
        });

        fuelAddBtn = (FloatingActionButton) view.findViewById(R.id.fuel_add_bt);
        fuelAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FuelDetailActivity.class));
                setSelected(list.size());
                showButtons(false);
            }
        });

        fuelDelBtn = (FloatingActionButton) view.findViewById(R.id.fuel_del_bt);
        fuelDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedfuel == null){
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.not_selected_document), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.action_foto));
                builder.setMessage(getString(R.string.deleted_selected_document));

                builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getActivity(),
                                elementUtils.deleteDocument(selectedfuel, MobileManagerContract.FuelContract.TABLE_NAME), Toast.LENGTH_SHORT)
                                .show();

                        initData();

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


                showButtons(false);
                setSelected(list.size());
            }
        });

        fuelSearchBtn = (FloatingActionButton) view.findViewById(R.id.fuel_search_bt);
        fuelSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showButtons(false);
                setSelected(list.size());

                if (selectionOn) {

                    selectionOn = false;

                    initData();

                    fuelSearchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_open));
                    fuelSearchBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                }else {

                    initSearch();

                    bottomSheetDialog.setContentView(textEntryView);

                    bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    bottomSheetDialog.show();

                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();

        showButtons(false);

        setSelected(list.size());

    }

    private void initData(){

        list = new ArrayList<>();

        List<Fuel_Document> fuellist = new ArrayList<>();

        if(selectionOn == true){
            List<Document> listDocuments = dataRepository.
                    getDocumentsByPeriod(MobileManagerContract.FuelContract.TABLE_NAME,
                            selectPeriodStart, selectPeriodEnd);

            for (Document document : listDocuments) {
                fuellist.add((Fuel_Document) document);
            }

        }else {
            fuellist = dataRepository.getAllFuel();
        }

        for (Fuel_Document fuel_document : fuellist) {
            list.add(
                    new FuelList(
                            fuel_document.getExternalId(),
                            fuel_document.getDate(),
                            fuel_document.getTypeFuel(),
                            fuel_document.getLitres()));
        }

        adapter = new FuelListAdapter(getActivity(), list);
        adapter.setmSelectedItem(list.size());
        listView.setAdapter(adapter);
    }

    private void initSearch(){

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);

        textEntryView = getActivity().getLayoutInflater().inflate(R.layout.dialog_search_period, null);

        final EditText startdateEditText = (EditText) textEntryView.findViewById(startdate);
        startdateEditText.setText(dateFormatter.format(selectPeriodStart), TextView.BufferType.EDITABLE);
        final EditText enddateEditText = (EditText) textEntryView.findViewById(enddate);
        enddateEditText.setText(dateFormatter.format(selectPeriodEnd), TextView.BufferType.EDITABLE);

        final SlideDateTimeListener dateTimeListener = new SlideDateTimeListener() {

            @Override
            public void onDateTimeSet(Date date) {
                if (touchDate == 1) {
                    selectPeriodStart = date;
                    startdateEditText.setText(dateFormatter.format(date));
                } else {
                    selectPeriodEnd = date;
                    enddateEditText.setText(dateFormatter.format(date));
                }
            }

            @Override
            public void onDateTimeCancel() {

            }
        };

        startdateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchDate = 1;

                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                        .setListener(dateTimeListener)
                        .setInitialDate(selectPeriodStart)
                        .setIs24HourTime(true)
                        .setIndicatorColor(getResources().getColor(R.color.colorPrimary))
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .build()
                        .show();
                return true;
            }
        });
        enddateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchDate = 2;

                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                        .setListener(dateTimeListener)
                        .setInitialDate(selectPeriodEnd)
                        .setIs24HourTime(true)
                        .setIndicatorColor(getResources().getColor(R.color.colorPrimary))
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .build()
                        .show();
                return true;
            }
        });

        Button okButton = (Button) textEntryView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionOn = true;

                initData();

                fuelSearchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_open_color));
                fuelSearchBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));

                bottomSheetDialog.hide();
            }
        });

        Button cancelButton = (Button) textEntryView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionOn = false;

                initData();

                fuelSearchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_open));
                fuelSearchBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                bottomSheetDialog.hide();
            }
        });
    }


    private void setSelected(int position){
        adapter.setmSelectedItem(position);
        adapter.notifyDataSetChanged();
    }

    private void showButtons(boolean show){
        if(show){
            fuelDelBtn.animate().translationX(0).setInterpolator(new LinearInterpolator()).start();
            fuelDelBtn.setVisibility(View.VISIBLE);
        }else{
            fuelDelBtn.animate().translationX(fuelDelBtn.getWidth() + getResources().getDimension(R.dimen.marging_button_map)).setInterpolator(new LinearInterpolator()).start();
            fuelDelBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBecameVisible() {
        showButtons(false);
        setSelected(list.size());

    }

    @Override
    public void onBecameUnVisible() {

    }

}
