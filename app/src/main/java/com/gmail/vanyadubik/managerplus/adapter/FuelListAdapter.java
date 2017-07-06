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
import com.gmail.vanyadubik.managerplus.model.documents.FuelList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FuelListAdapter extends BaseAdapter implements Filterable {

    public final static String filterDivider = "/";

    private List<FuelList> modelValues;
    private List<FuelList> mOriginalValues;
    private LayoutInflater layoutInflater;
    private Context context;
    private int mSelectedItem;

    public FuelListAdapter(Context context, List<FuelList> list) {
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
            view = layoutInflater.inflate(R.layout.fuel_list_item, parent, false);
        }
        final FuelList fuelDoc = (FuelList) getItem(position);

        ImageView fuellistImage = (ImageView) view.findViewById(R.id.imageView_fuellist);
        if (getItemId(position) == mSelectedItem) {
            fuellistImage.setBackground(context.getResources().getDrawable(R.drawable.shape_body_left_selected));
        } else {
            fuellistImage.setBackground(context.getResources().getDrawable(R.drawable.shape_body_left));
        }

        TextView date = (TextView) view.findViewById(R.id.fuel_item_data);
        date.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(fuelDoc.getDate().getTime()));
        setBacgrounView(date, (int) getItemId(position));

        TextView typeFuel = (TextView) view.findViewById(R.id.fuel_item_type);
        typeFuel.setText(fuelDoc.getTypeFuel());
        setBacgrounView(typeFuel, (int) getItemId(position));

        TextView litres = (TextView) view.findViewById(R.id.fuel_item_litres);
        litres.setText(String.valueOf(fuelDoc.getLitres()));
        setBacgrounView(litres, (int) getItemId(position));

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

                modelValues = (ArrayList<FuelList>) results.values; // has

                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults(); // Holds the

                List<FuelList> FilteredArrList = new ArrayList<FuelList>();

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
                        FuelList fuelList = mOriginalValues.get(i);

                        Date date = fuelList.getDate();
                        if (date.getTime() >= dateStart.getTime() && date.getTime() <= dateEnd.getTime()) {

                            FilteredArrList.add(fuelList);
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