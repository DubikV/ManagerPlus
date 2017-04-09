package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.ClientDetailActivity;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.db.Client_Element;

import java.util.List;

public class ClientListAdapter extends BaseAdapter {

    private List<Client_Element> list;
    private LayoutInflater layoutInflater;
    private Context context;

    public ClientListAdapter(Context context, List<Client_Element> list) {
        this.list = list;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = layoutInflater.inflate(R.layout.client_list_item, parent, false);
        }
        final Client_Element client = getDataTable(position);

        TextView name = (TextView) view.findViewById(R.id.client_item_name);
        name.setText(client.getName());

        TextView phone = (TextView) view.findViewById(R.id.client_item_phone);
        phone.setText(client.getPhone());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(
                        new Intent(context, ClientDetailActivity.class)
                                .putExtra(MobileManagerContract.ClientContract.CLIENT_ID, client.getExternalId()));
            }
        });

        return view;
    }

    private Client_Element getDataTable(int position) {
        return (Client_Element) getItem(position);
    }
}