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
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.ClientDetailActivity;
import com.gmail.vanyadubik.managerplus.adapter.ClientListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.ElementUtils;

import java.util.List;

import javax.inject.Inject;

public class ClientListFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_client_list;

    @Inject
    DataRepository dataRepository;
    @Inject
    ElementUtils elementUtils;

    private View view;
    private List<Client_Element> list;
    private ListView listView;
    private ClientListAdapter adapter;
    private FloatingActionButton clientDelBtn, clientSearchBtn, clientAddBtn;
    private Client_Element selectedClient;
    private String selectedName;
    private Boolean selectionOn;
    private BottomSheetDialog bottomSheetDialog ;
    private View textEntryView;

    public static ClientListFragment getInstance() {

        Bundle args = new Bundle();
        ClientListFragment fragment = new ClientListFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ((ManagerPlusAplication) getActivity().getApplication()).getComponent().inject(this);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.clients);

        selectedName = "";

        selectionOn = false;

        listView = (ListView) view.findViewById(R.id.client_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedClient = (Client_Element) adapter.getItem(position);
                startActivity(
                        new Intent(getActivity(), ClientDetailActivity.class)
                                .putExtra(MobileManagerContract.ClientContract.EXTERNAL_ID, selectedClient.getExternalId()));
                showButtons(false);
                setSelected(list.size());

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedClient = (Client_Element) adapter.getItem(position);
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

        clientAddBtn = (FloatingActionButton) view.findViewById(R.id.client_add_bt);
        clientAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ClientDetailActivity.class));
                showButtons(false);
                setSelected(list.size());
            }
        });

        clientDelBtn = (FloatingActionButton) view.findViewById(R.id.client_del_bt);
        clientDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedClient == null){
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
                                elementUtils.deleteElement(selectedClient, MobileManagerContract.WaybillContract.TABLE_NAME), Toast.LENGTH_SHORT)
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


        clientSearchBtn = (FloatingActionButton) view.findViewById(R.id.client_search_bt);
        clientSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showButtons(false);
                setSelected(list.size());

                if (selectionOn) {

                    selectionOn = false;

                    selectedName = "";

                    adapter.getFilter().filter(selectedName);

                    clientSearchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_open));
                    clientSearchBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                }else {

                    initSearch();

                    bottomSheetDialog.setContentView(textEntryView);

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
        initData();
    }

    private void initData(){

        list = dataRepository.getAllClients();

        adapter = new ClientListAdapter(getActivity(), list);
        adapter.setmSelectedItem(list.size());
        listView.setAdapter(adapter);

        if(selectedName != null && ! selectedName.isEmpty()){
            adapter.getFilter().filter(selectedName);
        }
    }


    private void initSearch(){

        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);

        textEntryView = getActivity().getLayoutInflater().inflate(R.layout.dialog_search_name, null);

        final EditText searchEditText = (EditText) textEntryView.findViewById(R.id.search_textView);
        searchEditText.setText(selectedName, TextView.BufferType.EDITABLE);
        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = searchEditText.getRight()
                            - searchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        searchEditText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button okButton = (Button) textEntryView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionOn = true;

                selectedName = String.valueOf(searchEditText.getText());
                adapter.getFilter().filter(selectedName);

                clientSearchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_open_color));
                clientSearchBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));

                bottomSheetDialog.hide();
            }
        });

        Button cancelButton = (Button) textEntryView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionOn = false;

                adapter.getFilter().filter(selectedName);

                clientSearchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_open));
                clientSearchBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

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

    }

    private void setSelected(int position){
        adapter.setmSelectedItem(position);
        adapter.notifyDataSetChanged();
    }

    private void showButtons(boolean show){
        if(show){
            clientDelBtn.animate().translationX(0).setInterpolator(new LinearInterpolator()).start();
            clientDelBtn.setVisibility(View.VISIBLE);
        }else{
            clientDelBtn.animate().translationX(clientDelBtn.getWidth() + getResources().getDimension(R.dimen.marging_button_map)).setInterpolator(new LinearInterpolator()).start();
            clientDelBtn.setVisibility(View.GONE);
        }
    }
}
