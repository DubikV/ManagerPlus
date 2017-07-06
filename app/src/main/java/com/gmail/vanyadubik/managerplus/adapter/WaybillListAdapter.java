package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.model.db.document.Waybill_Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WaybillListAdapter extends BaseAdapter  implements Filterable {

    public final static String filterDivider = "/";

    private List<Waybill_Document> modelValues;
    private List<Waybill_Document> mOriginalValues;
    private LayoutInflater layoutInflater;
    private Context context;
    private int mSelectedItem;

    public WaybillListAdapter(Context context, List<Waybill_Document> list) {
        this.modelValues = list;
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
        return modelValues.size();
    }

    @Override
    public Object getItem(int position) {
        return modelValues.get(position);
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

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                modelValues = (ArrayList<Waybill_Document>) results.values; // has

                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults(); // Holds the

                List<Waybill_Document> FilteredArrList = new ArrayList<Waybill_Document>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(modelValues); // saves

                }
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    Locale locale = Locale.getDefault();
                    constraint = constraint.toString().toLowerCase(locale);

                    String[] period = String.valueOf(constraint).split(filterDivider);
                    Date dateStart = new Date(Long.valueOf(period[0]));
                    Date dateEnd = new Date(Long.valueOf(period[1]));

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        Waybill_Document waybill = mOriginalValues.get(i);

                        Date date = waybill.getDateStart();
                        if (date.getTime() >= dateStart.getTime() && date.getTime() <= dateEnd.getTime()) {

                            FilteredArrList.add(waybill);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;

                }
                return results;
            }
        };
        return filter;
    }

}