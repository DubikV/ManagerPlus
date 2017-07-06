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
import com.gmail.vanyadubik.managerplus.model.documents.VisitList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VisitListAdapter extends BaseAdapter  implements Filterable {

    public final static String filterDivider = "/";

    private List<VisitList> modelValues;
    private List<VisitList> mOriginalValues;
    private LayoutInflater layoutInflater;
    private Context context;
    private int mSelectedItem;

    public VisitListAdapter(Context context, List<VisitList> list) {
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
            view = layoutInflater.inflate(R.layout.visit_list_item, parent, false);
        }
        final VisitList visit = getDataTable(position);

        ImageView fuellistImage = (ImageView) view.findViewById(R.id.visit_item_image);
        if (getItemId(position) == mSelectedItem) {
            fuellistImage.setBackground(context.getResources().getDrawable(R.drawable.shape_body_left_selected));
        } else {
            fuellistImage.setBackground(context.getResources().getDrawable(R.drawable.shape_body_left));
        }

        TextView date = (TextView) view.findViewById(R.id.visit_item_date);
        date.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(visit.getDate().getTime()));
        setBacgrounView(date, (int) getItemId(position));

        TextView client = (TextView) view.findViewById(R.id.visit_item_client);
        client.setText(visit.getClient());
        setBacgrounView(client, (int) getItemId(position));

        TextView typevisit = (TextView) view.findViewById(R.id.visit_item_typevisit);
        typevisit.setText(visit.getTypeVisit());
        setBacgrounView(typevisit, (int) getItemId(position));

        return view;
    }

    private VisitList getDataTable(int position) {
        return (VisitList) getItem(position);
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

                modelValues = (ArrayList<VisitList>) results.values; // has

                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults(); // Holds the

                List<VisitList> FilteredArrList = new ArrayList<VisitList>();

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
                        VisitList visitList = mOriginalValues.get(i);

                        Date date = visitList.getDate();
                        if (date.getTime() >= dateStart.getTime() && date.getTime() <= dateEnd.getTime()) {

                            FilteredArrList.add(visitList);
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