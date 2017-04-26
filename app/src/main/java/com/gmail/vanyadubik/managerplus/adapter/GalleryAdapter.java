package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gmail.vanyadubik.managerplus.model.PhotoItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends BaseAdapter {

	private List<PhotoItem> mData = new ArrayList<>();
	private Context mContext;

	public GalleryAdapter(Context context, List<PhotoItem> data) {
		mContext = context;
        mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int pos) {
		return mData.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        ImageView i = new ImageView(mContext);

        Picasso.with(mContext)
                .load(mData.get(position).getFile())
                //.placeholder(R.drawable.shape_camera)
                .into(i);

        return i;
	}
}
