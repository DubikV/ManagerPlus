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
import com.gmail.vanyadubik.managerplus.model.documents.SelectionItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectionListAdapter extends BaseAdapter implements Filterable {

    private List<SelectionItem> modelValues;
    private List<SelectionItem> mOriginalValues;
    private Context context;
    private LayoutInflater layoutInflater;

    public SelectionListAdapter(Context context, List<SelectionItem> modelValues) {
        this.modelValues = modelValues;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public
    @NonNull
    View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        final SelectionItem selectionItem = getSelectionItem(position);

        TextView infoNameTextView = (TextView) view.findViewById(android.R.id.text1);
        infoNameTextView.setText(selectionItem.getPresentation());
        infoNameTextView.setTextSize(context.getResources().getDimension(R.dimen.text_size_medium));

        return view;
    }

    private SelectionItem getSelectionItem(int position) {
        return (SelectionItem) getItem(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                modelValues = (ArrayList<SelectionItem>) results.values; // has

                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the

                List<SelectionItem> FilteredArrList = new ArrayList<SelectionItem>();

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
                        SelectionItem selectionItem = mOriginalValues.get(i);

                        String data = selectionItem.getPresentation();
                        if (data.toLowerCase(locale).contains(constraint.toString())) {

                            FilteredArrList.add(selectionItem);
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