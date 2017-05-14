package com.gmail.vanyadubik.managerplus.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.ImageActivity;
import com.gmail.vanyadubik.managerplus.model.PhotoItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.vanyadubik.managerplus.activity.ImageActivity.IMAGE_FULL_NAME;
import static com.gmail.vanyadubik.managerplus.activity.ImageActivity.IMAGE_NAME;

public class AddedPhotoAdapter extends BaseAdapter {

	private List<PhotoItem> mData = new ArrayList<>();
	private Context mContext;

	public AddedPhotoAdapter(Context context, List<PhotoItem> data) {
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

        View row = convertView;

        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.gallery_item, parent, false);
            row.setLayoutParams(new GridView.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 350));
            holder = new ViewHolder();
            holder.textView = (TextSwitcher) row.findViewById(R.id.title);
            holder.imageView = (ImageView) row.findViewById(R.id.gallery_imageView);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final PhotoItem item = mData.get(position);
        Picasso.with(mContext)
                .load(item.getFile())
                .placeholder(mContext.getResources().getDrawable(android.R.drawable.ic_menu_gallery))
                .fit()
                .into(holder.imageView);

        holder.textView.setText(item.getTitle());

        return row;
	}

    static class ViewHolder {
        TextSwitcher textView;
        ImageView imageView;
    }
}
