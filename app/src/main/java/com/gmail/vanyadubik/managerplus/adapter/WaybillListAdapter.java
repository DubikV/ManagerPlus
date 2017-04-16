package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.MapActivity;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_TRACK_DATE_END;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_SHOW_TRACK_DATE_START;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE;
import static com.gmail.vanyadubik.managerplus.activity.MapActivity.MAP_TYPE_SHOW_TRACK;

public class WaybillListAdapter extends BaseAdapter {

    private Context context;
    private List<Waybill_Document> list;
    private LayoutInflater layoutInflater;

    public WaybillListAdapter(Context context, List<Waybill_Document> list) {
        this.context = context;
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
        final Waybill_Document waybill = getDataTable(position);

        TextView waybilllistDate = (TextView) view.findViewById(R.id.waybilllist_date);
        waybilllistDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(waybill.getDateStart()));

        TextView waybilllistOdStart = (TextView) view.findViewById(R.id.waybilllist_odometer_start);
        waybilllistOdStart.setText(String.valueOf(waybill.getStartOdometer()));

        TextView waybilllistOdEnd = (TextView) view.findViewById(R.id.waybilllist_odometer_end);
        waybilllistOdEnd.setText(String.valueOf(waybill.getEndOdometer()));

        TextView waybilllistKm = (TextView) view.findViewById(R.id.waybilllist_km);
        waybilllistKm.setText(String.valueOf(waybill.getEndOdometer() == 0 ? 0 :
                waybill.getEndOdometer()-waybill.getStartOdometer()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra(MAP_TYPE, MAP_TYPE_SHOW_TRACK);
                intent.putExtra(MAP_SHOW_TRACK_DATE_START, String.valueOf(waybill.getDateStart().getTime()));
                Date dateEnd = waybill.getDateEnd();
                if (dateEnd.getTime() < 1000) {
                    dateEnd = waybill.getDateStart();
                    dateEnd.setHours(23);
                    dateEnd.setMinutes(59);
                    dateEnd.setSeconds(59);
                }
                intent.putExtra(MAP_SHOW_TRACK_DATE_END, String.valueOf(dateEnd.getTime()));
                v.getContext().startActivity(intent);
            }
        });

        return view;
    }

    private Waybill_Document getDataTable(int position) {
        return (Waybill_Document) getItem(position);
    }
}