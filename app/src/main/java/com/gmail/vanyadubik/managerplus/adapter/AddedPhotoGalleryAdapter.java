package com.gmail.vanyadubik.managerplus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.model.PhotoItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddedPhotoGalleryAdapter extends BaseAdapter {

	private List<PhotoItem> mData = new ArrayList<>();
	private Context mContext;

	public AddedPhotoGalleryAdapter(Context context, List<PhotoItem> data) {
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

        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new Gallery.LayoutParams(
                (int) mContext.getResources().getDimension(R.dimen.gallery_image_marging_bottom),
                (int) mContext.getResources().getDimension(R.dimen.gallery_image_marging_bottom)));


        final PhotoItem item = mData.get(position);
        Picasso.with(mContext)
                .load(item.getFile())
                .placeholder(mContext.getResources().getDrawable(R.drawable.ic_image))
                .fit()
                .into(imageView);

        return imageView;
	}
}
