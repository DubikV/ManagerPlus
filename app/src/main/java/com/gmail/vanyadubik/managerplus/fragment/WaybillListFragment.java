package com.gmail.vanyadubik.managerplus.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.MapActivity;
import com.gmail.vanyadubik.managerplus.activity.SearchActivity;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_TRACK_DATE_END;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_TRACK_DATE_START;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE_SHOW_TRACK;
import static com.gmail.vanyadubik.managerplus.activity.SearchActivity.SEARCH_BY_PERIOD;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_IMAGE;

public class WaybillListFragment extends Fragment implements FragmentBecameVisibleInterface {

    private static  final int LAYOUT = R.layout.fragment_waybill_list;
    private static final int SEARCH_ACTIVITY_WAYBILL_LIST = 997;

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
    private int mSelectedItem;

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
                setSelected(position);

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
                setSelected(list.size());
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
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra(SearchActivity.PARAM_SEARCH, SEARCH_BY_PERIOD);
                startActivityForResult(intent, SEARCH_ACTIVITY_WAYBILL_LIST);
                setSelected(list.size());
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_WAYBILL_LIST) {
            if (resultCode == RESULT_OK) {
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAGLOG_IMAGE, "Camera Cancelled");
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showButtons(false);
    }

    private void initData(){

        list = dataRepository.getAllWaybill();

        mSelectedItem = list.size();

        adapter = new WaybillListAdapter();
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBecameVisible() {
        initData();
    }

    @Override
    public void onBecameUnVisible() {
      showButtons(false);
    }

    private void setSelected(int position){
        mSelectedItem = position;
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

    private class WaybillListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public WaybillListAdapter() {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.waybill_list_item, parent, false);
            }

            Waybill_Document waybill = (Waybill_Document) getItem(position);

            ImageView waybilllistImage = (ImageView) view.findViewById(R.id.waybilllist_image);
            if (getItemId(position) == mSelectedItem) {
                waybilllistImage.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left_selected));
            } else {
                waybilllistImage.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left));
            }

            TextView waybilllistDate = (TextView) view.findViewById(R.id.waybilllist_date);
            waybilllistDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(waybill.getDateStart()));
            if (getItemId(position) == mSelectedItem) {
                waybilllistDate.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left_selected));
                waybilllistDate.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            } else {
                waybilllistDate.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left));
                waybilllistDate.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            }

            TextView waybilllistOdStart = (TextView) view.findViewById(R.id.waybilllist_odometer_start);
            waybilllistOdStart.setText(String.valueOf(waybill.getStartOdometer()));
            if (getItemId(position) == mSelectedItem) {
                waybilllistOdStart.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left_selected));
                waybilllistOdStart.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            } else {
                waybilllistOdStart.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left));
                waybilllistOdStart.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            }

            TextView waybilllistOdEnd = (TextView) view.findViewById(R.id.waybilllist_odometer_end);
            waybilllistOdEnd.setText(String.valueOf(waybill.getEndOdometer()));
            if (getItemId(position) == mSelectedItem) {
                waybilllistOdEnd.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left_selected));
                waybilllistOdEnd.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            } else {
                waybilllistOdEnd.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left));
                waybilllistOdEnd.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            }

            TextView waybilllistKm = (TextView) view.findViewById(R.id.waybilllist_km);
            waybilllistKm.setText(String.valueOf(waybill.getEndOdometer() == 0 ? 0 :
                    waybill.getEndOdometer()-waybill.getStartOdometer()));
            if (getItemId(position) == mSelectedItem) {
                waybilllistKm.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_right_selected));
                waybilllistKm.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            } else {
                waybilllistKm.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_right));
                waybilllistKm.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            }

            return view;
        }
    }
}
