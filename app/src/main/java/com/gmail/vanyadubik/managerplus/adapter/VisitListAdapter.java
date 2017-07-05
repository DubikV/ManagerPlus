package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.model.documents.VisitList;

import java.text.SimpleDateFormat;
import java.util.List;

public class VisitListAdapter extends BaseAdapter {

    private List<VisitList> list;
    private LayoutInflater layoutInflater;
    private Context context;
    private int mSelectedItem;

    public VisitListAdapter(Context context, List<VisitList> list) {
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


}