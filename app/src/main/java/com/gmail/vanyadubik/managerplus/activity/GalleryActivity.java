package com.gmail.vanyadubik.managerplus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.GalleryAdapter;
import com.gmail.vanyadubik.managerplus.model.PhotoItem;

import java.io.File;
import java.util.ArrayList;

import static com.gmail.vanyadubik.managerplus.activity.AddedPhotosActivity.PATH_SELECTED_PHOTO;

public class GalleryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().setTitle(getResources().getString(R.string.all_foto_in_device));

        GridView gallery = (GridView) findViewById(R.id.all_photo_gridview);

        final ArrayList<PhotoItem> allImages = getAllShownImagesPath();


        gallery.setAdapter(new GalleryAdapter(this, allImages));//new ImageAdapter(this, allImages));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (null != allImages && !allImages.isEmpty())
                   showAskAlert(allImages.get(position).getTitle());
                ;

            }
        });

    }

    private ArrayList<PhotoItem> getAllShownImagesPath() {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<PhotoItem> listOfAllImages = new ArrayList<PhotoItem>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(new PhotoItem(absolutePathOfImage, new File(absolutePathOfImage)));
        }
        return listOfAllImages;
    }

    public void showAskAlert(final String imageFullName){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.action_foto));
        builder.setMessage(getString(R.string.action_get_photo));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra(PATH_SELECTED_PHOTO, imageFullName);
                setResult(RESULT_OK, intent);
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.selected_photo)
                                + " " + imageFullName,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        // TODO: (end stub) ------------------

    }

}