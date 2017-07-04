//package com.gmail.vanyadubik.managerplus.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.gmail.vanyadubik.managerplus.R;
//import com.gmail.vanyadubik.managerplus.activity.FuelDetailActivity;
//import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
//import com.gmail.vanyadubik.managerplus.model.documents.FuelList;
//
//import java.text.SimpleDateFormat;
//import java.util.List;
//
//public class FuelListAdapter extends BaseAdapter {
//
//    private List<FuelList> list;
//    private LayoutInflater layoutInflater;
//    private Context context;
//
//    public FuelListAdapter(Context context, List<FuelList> list) {
//        this.list = list;
//        this.context = context;
//        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return list.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        if (view == null) {
//            view = layoutInflater.inflate(R.layout.fuel_list_item, parent, false);
//        }
//        final FuelList fuelDoc = (FuelList) getItem(position);
//
//        TextView date = (TextView) view.findViewById(R.id.fuel_item_data);
//        date.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(fuelDoc.getDate().getTime()));
//
//        TextView typeFuel = (TextView) view.findViewById(R.id.fuel_item_type);
//        typeFuel.setText(fuelDoc.getTypeFuel());
//
//        TextView litres = (TextView) view.findViewById(R.id.fuel_item_litres);
//        litres.setText(String.valueOf(fuelDoc.getLitres()));
//
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.getContext().startActivity(
//                        new Intent(context, FuelDetailActivity.class)
//                                .putExtra(MobileManagerContract.FuelContract.EXTERNAL_ID, fuelDoc.getExternalId()));
//            }
//        });
//
//        return view;
//    }
//
//}