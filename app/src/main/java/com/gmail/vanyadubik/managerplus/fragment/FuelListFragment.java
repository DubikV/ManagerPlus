package com.gmail.vanyadubik.managerplus.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.gmail.vanyadubik.managerplus.activity.FuelDetailActivity;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.db.document.Fuel_Document;
import com.gmail.vanyadubik.managerplus.model.documents.FuelList;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.ElementUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
    private int mSelectedItem;

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
                setSelected(position);

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
        listView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setSelected(list.size());
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
                setSelected(list.size());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();

        showButtons(false);
    }

    private void initData(){

        list = new ArrayList<>();

        List<Fuel_Document> fuellist = dataRepository.getAllFuel();


        for (Fuel_Document fuel_document : fuellist) {
            list.add(
                    new FuelList(
                            fuel_document.getExternalId(),
                            fuel_document.getDate(),
                            fuel_document.getTypeFuel(),
                            fuel_document.getLitres()));
        }

        adapter = new FuelListAdapter();
        listView.setAdapter(adapter);
    }

    private void setSelected(int position){
        mSelectedItem = position;
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

    private class FuelListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public FuelListAdapter() {
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
                view = layoutInflater.inflate(R.layout.fuel_list_item, parent, false);
            }
            final FuelList fuelDoc = (FuelList) getItem(position);

            ImageView fuellistImage = (ImageView) view.findViewById(R.id.imageView_fuellist);
            if (getItemId(position) == mSelectedItem) {
                fuellistImage.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left_selected));
            } else {
                fuellistImage.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left));
            }

            TextView date = (TextView) view.findViewById(R.id.fuel_item_data);
            date.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(fuelDoc.getDate().getTime()));
            if (getItemId(position) == mSelectedItem) {
                date.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left_selected));
                date.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            } else {
                date.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left));
                date.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            }

            TextView typeFuel = (TextView) view.findViewById(R.id.fuel_item_type);
            typeFuel.setText(fuelDoc.getTypeFuel());
            if (getItemId(position) == mSelectedItem) {
                typeFuel.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left_selected));
                typeFuel.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            } else {
                typeFuel.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_left));
                typeFuel.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            }

            TextView litres = (TextView) view.findViewById(R.id.fuel_item_litres);
            litres.setText(String.valueOf(fuelDoc.getLitres()));
            if (getItemId(position) == mSelectedItem) {
                litres.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_right_selected));
                litres.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            } else {
                litres.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_body_right));
                litres.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            }

            return view;
        }

    }
}
