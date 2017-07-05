package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;

import java.text.SimpleDateFormat;
import java.util.List;

public class WaybillListAdapter extends BaseAdapter {

    private List<Waybill_Document> list;
    private LayoutInflater layoutInflater;
    private Context context;
    private int mSelectedItem;

    public WaybillListAdapter(Context context, List<Waybill_Document> list) {
        this.list = list;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getmSelectedItem() {
        return mSelectedItem;
    }

    public void setmSelectedItem(int mSelectedItem) {
        this.mSelectedItem = mSelectedItem;
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
            waybilllistImage.setBackground(context.getResources().getDrawable(R.drawable.shape_body_left_selected));
        } else {
            waybilllistImage.setBackground(context.getResources().getDrawable(R.drawable.shape_body_left));
        }

        TextView waybilllistDate = (TextView) view.findViewById(R.id.waybilllist_date);
        waybilllistDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(waybill.getDateStart()));
        setBacgrounView(waybilllistDate, (int) getItemId(position));

        TextView waybilllistOdStart = (TextView) view.findViewById(R.id.waybilllist_odometer_start);
        waybilllistOdStart.setText(String.valueOf(waybill.getStartOdometer()));
        setBacgrounView(waybilllistOdStart, (int) getItemId(position));

        TextView waybilllistOdEnd = (TextView) view.findViewById(R.id.waybilllist_odometer_end);
        waybilllistOdEnd.setText(String.valueOf(waybill.getEndOdometer()));
        setBacgrounView(waybilllistOdEnd, (int) getItemId(position));

        TextView waybilllistKm = (TextView) view.findViewById(R.id.waybilllist_km);
        waybilllistKm.setText(String.valueOf(waybill.getEndOdometer() == 0 ? 0 :
                waybill.getEndOdometer()-waybill.getStartOdometer()));
        setBacgrounView(waybilllistKm, (int) getItemId(position));

        return view;
    }

    private void setBacgrounView(TextView view, int position) {
        if (getItemId(position) == mSelectedItem) {
            view.setBackground(context.getResources().getDrawable(R.drawable.shape_body_left_selected));
            view.setTextColor(context.getResources().getColor(R.color.colorWhite));
        } else {
            view.setBackground(context.getResources().getDrawable(R.drawable.shape_body_left));
            view.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

}