package com.gmail.vanyadubik.managerplus.activity;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.photo.CoverFlow;

import java.io.FileInputStream;

public class AddedPhotosActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CoverFlow coverFlow;
        coverFlow = new CoverFlow(this);
        coverFlow.setAdapter(new ImageAdapter(this));
        ImageAdapter coverImageAdapter =  new ImageAdapter(this);
        coverImageAdapter.createReflectedImages();
        coverFlow.setAdapter(coverImageAdapter);
        coverFlow.setSpacing(-25);
        coverFlow.setSelection(4, true);
        coverFlow.setAnimationDuration(1000);
        setContentView(coverFlow);
    }
    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;
        private FileInputStream fis;

        private Integer[] mImageIds = {
                R.drawable.ic_map,
                R.drawable.ic_arrow_back,
                R.drawable.ic_calendar_grey,
                R.drawable.ic_close_dark,
                R.drawable.ic_calendar_grey,
                R.drawable.ic_calendar,
                R.drawable.ic_arrow_back,
                R.drawable.ic_calendar
        };

        private ImageView[] mImages;

        public ImageAdapter(Context c) {
            mContext = c;
            mImages = new ImageView[mImageIds.length];
        }
        public boolean createReflectedImages() {
            final int reflectionGap = 4;
            int index = 0;
            for (int imageId : mImageIds) {
                Bitmap originalImage = BitmapFactory.decodeResource(getResources(),
                        imageId);
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();
                Matrix matrix = new Matrix();
                matrix.preScale(1, -1);
                Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);
                Bitmap bitmapWithReflection = Bitmap.createBitmap(width
                        , (height + height/2), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapWithReflection);
                canvas.drawBitmap(originalImage, 0, 0, null);
                Paint deafaultPaint = new Paint();
                canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
                canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);

                Paint paint = new Paint();
                LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                        bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                        Shader.TileMode.CLAMP);
                paint.setShader(shader);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawRect(0, height, width,
                        bitmapWithReflection.getHeight() + reflectionGap, paint);
                ImageView imageView = new ImageView(mContext);
                imageView.setImageBitmap(bitmapWithReflection);
                imageView.setLayoutParams(new CoverFlow.LayoutParams(120, 180));
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                mImages[index++] = imageView;
            }
            return true;
        }

        public int getCount() {
            return mImageIds.length;
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
            i.setImageResource(mImageIds[position]);
            i.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
            i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
            drawable.setAntiAlias(true);
            return i;
        }

        public float getScale(boolean focused, int offset) {
            return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));
        }
    }
}