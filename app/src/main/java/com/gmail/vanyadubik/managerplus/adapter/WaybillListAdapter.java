package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.model.db.Waybill_Element;

import java.text.SimpleDateFormat;
import java.util.List;

public class WaybillListAdapter extends BaseAdapter {

    private List<Waybill_Element> list;
    private LayoutInflater layoutInflater;

    public WaybillListAdapter(Context context, List<Waybill_Element> list) {
        this.list = list;
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
            view = layoutInflater.inflate(R.layout.waybill_list_item, parent, false);
        }
        Waybill_Element waybill = getDataTable(position);

        TextView waybilllistDate = (TextView) view.findViewById(R.id.waybilllist_date);
        waybilllistDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(waybill.getDateStart()));

        TextView waybilllistOdStart = (TextView) view.findViewById(R.id.waybilllist_odometer_start);
        waybilllistOdStart.setText(String.valueOf(waybill.getStartOdometer()));

        TextView waybilllistOdEnd = (TextView) view.findViewById(R.id.waybilllist_odometer_end);
        waybilllistOdEnd.setText(String.valueOf(waybill.getEndOdometer()));

        TextView waybilllistKm = (TextView) view.findViewById(R.id.waybilllist_km);
        waybilllistKm.setText(String.valueOf(waybill.getEndOdometer()-waybill.getStartOdometer()));

        return view;
    }

    private Waybill_Element getDataTable(int position) {
        return (Waybill_Element) getItem(position);
    }
}