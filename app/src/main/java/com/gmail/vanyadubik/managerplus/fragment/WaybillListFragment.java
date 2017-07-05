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
import com.gmail.vanyadubik.managerplus.activity.MapActivity;
import com.gmail.vanyadubik.managerplus.adapter.WaybillListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.ElementUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.R.id.enddate;
import static com.gmail.vanyadubik.managerplus.R.id.startdate;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_TRACK_DATE_END;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_TRACK_DATE_START;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE_SHOW_TRACK;

public class WaybillListFragment extends Fragment implements FragmentBecameVisibleInterface {

    private static  final int LAYOUT = R.layout.fragment_waybill_list;

    @Inject
    DataRepository dataRepository;
    @Inject
    ElementUtils elementUtils;

    private View view;
    private List<Waybill_Document> list;
    private ListView listView;
    private WaybillListAdapter adapter;
    private FloatingActionButton waybillDelBtn, waybillSearchBtn, waybillAddBtn;
    private Waybill_Document selectedwaybill;
    private int touchDate;
    private Date selectPeriodStart, selectPeriodEnd;
    private Boolean selectionOn;
    private BottomSheetDialog bottomSheetDialog ;
    private View textEntryView;

    public static WaybillListFragment getInstance() {

        Bundle args = new Bundle();
        WaybillListFragment fragment = new WaybillListFragment();
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

        listView = (ListView) view.findViewById(R.id.waybill_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedwaybill = (Waybill_Document) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra(MAP_TYPE, MAP_TYPE_SHOW_TRACK);
                intent.putExtra(MAP_SHOW_TRACK_DATE_START, String.valueOf(selectedwaybill.getDateStart().getTime()));
                Date dateEnd = selectedwaybill.getDateEnd();
                if (dateEnd.getTime() < 1000) {
                    dateEnd = selectedwaybill.getDateStart();
                    dateEnd.setHours(23);
                    dateEnd.setMinutes(59);
                    dateEnd.setSeconds(59);
                }
                intent.putExtra(MAP_SHOW_TRACK_DATE_END, String.valueOf(dateEnd.getTime()));
                startActivity(intent);
                showButtons(false);
                setSelected(list.size());

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedwaybill = (Waybill_Document) adapter.getItem(position);
                showButtons(true);
                setSelected(position);
                return true;
            }
        });
        listView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setSelected(list.size());
            }
        });

        waybillAddBtn = (FloatingActionButton) view.findViewById(R.id.waybill_add_bt);
        waybillAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        waybillDelBtn = (FloatingActionButton) view.findViewById(R.id.waybill_del_bt);
        waybillDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedwaybill == null){
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
                                elementUtils.deleteDocument(selectedwaybill, MobileManagerContract.WaybillContract.TABLE_NAME), Toast.LENGTH_SHORT)
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


        waybillSearchBtn = (FloatingActionButton) view.findViewById(R.id.waybill_search_bt);
        waybillSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showButtons(false);
                setSelected(list.size());

                if (selectionOn) {

                    selectionOn = false;

                    initData();

                    waybillSearchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_open));
                    waybillSearchBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

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
        showButtons(false);
    }

    private void initData(){

        if(selectionOn == true){
            list = dataRepository.getWaybillByPeriod(selectPeriodStart, selectPeriodEnd);
        }else {
            list = dataRepository.getAllWaybill();
        }

        adapter = new WaybillListAdapter(getActivity(), list);
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

                waybillSearchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_open_color));
                waybillSearchBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));

                bottomSheetDialog.hide();
            }
        });

        Button cancelButton = (Button) textEntryView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionOn = false;

                initData();

                waybillSearchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_open));
                waybillSearchBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                bottomSheetDialog.hide();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBecameVisible() {
        initData();
        showButtons(false);
    }

    @Override
    public void onBecameUnVisible() {
      showButtons(false);
    }

    private void setSelected(int position){
        adapter.setmSelectedItem(position);
        adapter.notifyDataSetChanged();
    }

    private void showButtons(boolean show){
        if(show){
            waybillDelBtn.animate().translationX(0).setInterpolator(new LinearInterpolator()).start();
            waybillDelBtn.setVisibility(View.VISIBLE);
        }else{
            waybillDelBtn.animate().translationX(waybillDelBtn.getWidth() + getResources().getDimension(R.dimen.marging_button_map)).setInterpolator(new LinearInterpolator()).start();
            waybillDelBtn.setVisibility(View.GONE);
        }
    }

}
