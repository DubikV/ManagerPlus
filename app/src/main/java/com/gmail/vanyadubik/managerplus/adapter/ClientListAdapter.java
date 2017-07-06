package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientListAdapter extends BaseAdapter implements Filterable {

    private List<Client_Element> modelValues;
    private List<Client_Element> mOriginalValues;
    private LayoutInflater layoutInflater;
    private Context context;
    private int mSelectedItem;

    public ClientListAdapter(Context context, List<Client_Element> list) {
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
            view = layoutInflater.inflate(R.layout.client_list_item, parent, false);
        }
        final Client_Element client = getDataTable(position);

        TextView name = (TextView) view.findViewById(R.id.client_item_name);
        name.setText(client.getName());
        setBacgrounView(name, (int) getItemId(position));

        TextView phone = (TextView) view.findViewById(R.id.client_item_phone);
        phone.setText(client.getPhone());
        setBacgrounView(phone, (int) getItemId(position));

        return view;
    }

    private Client_Element getDataTable(int position) {
        return (Client_Element) getItem(position);
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

                modelValues = (ArrayList<Client_Element>) results.values; // has

                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the

                List<Client_Element> FilteredArrList = new ArrayList<Client_Element>();

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
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        Client_Element client = mOriginalValues.get(i);

                        String data = client.getName();
                        if (data.toLowerCase(locale).contains(constraint.toString())) {

                            FilteredArrList.add(client);
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