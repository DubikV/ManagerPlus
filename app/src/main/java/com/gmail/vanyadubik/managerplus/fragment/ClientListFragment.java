package com.gmail.vanyadubik.managerplus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.ClientDetailActivity;
import com.gmail.vanyadubik.managerplus.adapter.ClientListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.db.Client_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import java.util.List;

import javax.inject.Inject;

public class ClientListFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_client_list;

    @Inject
    DataRepository dataRepository;

    private View view;
    private List<Client_Element> list;
    private ListView listView;

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

        listView = (ListView) view.findViewById(R.id.client_listview);

        FloatingActionButton clientAddBtn = (FloatingActionButton) view.findViewById(R.id.client_add_bt);
        clientAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ClientDetailActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list = dataRepository.getAllClients();

        ClientListAdapter adapter = new ClientListAdapter(getActivity(), list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBecameVisible() {

    }

    @Override
    public void onBecameUnVisible() {

    }
}
