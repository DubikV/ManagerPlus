package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.VisitDetailActivity;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.documents.VisitList;

import java.text.SimpleDateFormat;
import java.util.List;

public class VisitPlaneListAdapter extends BaseAdapter {

    private List<VisitList> list;
    private LayoutInflater layoutInflater;
    private Context context;

    public VisitPlaneListAdapter(Context context, List<VisitList> list) {
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
            view = layoutInflater.inflate(R.layout.visitplane_list_item, parent, false);
        }
        final VisitList visit = getDataTable(position);

        TextView date = (TextView) view.findViewById(R.id.visit_item_date);
        date.setText(new SimpleDateFormat("HH:mm").format(visit.getDate().getTime()));

        TextView client = (TextView) view.findViewById(R.id.visit_item_client);
        client.setText(visit.getClient());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(
                        new Intent(context, VisitDetailActivity.class)
                                .putExtra(MobileManagerContract.VisitContract.EXTERNAL_ID, visit.getExternalId()));
            }
        });

        return view;
    }

    private VisitList getDataTable(int position) {
        return (VisitList) getItem(position);
    }
}