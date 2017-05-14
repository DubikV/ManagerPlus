package com.gmail.vanyadubik.managerplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.Toast;
import android.net.Uri;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.AddedPhotoAdapter;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.PhotoItem;
import com.gmail.vanyadubik.managerplus.model.db.element.Photo_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.PhotoFIleUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import static android.R.attr.path;
import static com.gmail.vanyadubik.managerplus.activity.ImageActivity.IMAGE_FULL_NAME;
import static com.gmail.vanyadubik.managerplus.activity.ImageActivity.IMAGE_NAME;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_IMAGE;

public class AddedPhotosActivity extends AppCompatActivity {

    private static final int CAPTURE_CAMERA_ACTIVITY_REQ = 999;
    private static final int CAPTURE_ALL_IMAGE_ACTIVITY_REQ = 998;

    private static final String GALLERY_NAME_OBJECT = "gallery_name_object";
    private static final String GALLERY_EXTERNALID_OBJECT = "gallery_externatlid_object";
    public static final String PATH_SELECTED_PHOTO = "path_selected_photo";

    @Inject
    DataRepository dataRepository;
    @Inject
    PhotoFIleUtils photoFileUtils;

    private ImageView selectedImage;
    private Gallery gallery;
    private AddedPhotoAdapter mAdapter;
    private TextSwitcher mTitle;
    private List<PhotoItem> mData;
    private PhotoItem selectedPhoto;
    private List<Photo_Element> addedPhotos;
    private Uri path = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_foto);
        getSupportActionBar().setTitle(getResources().getString(R.string.added_foto));
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        selectedImage = (ImageView)findViewById(R.id.gallery_imageView);
        selectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPhoto==null){
                    return;
                }
                Intent intent = new Intent(AddedPhotosActivity.this, ImageActivity.class);
                intent.putExtra(IMAGE_NAME, selectedPhoto.getTitle());
                intent.putExtra(IMAGE_FULL_NAME, selectedPhoto.getAbsolutePath());
                startActivity(intent);
            }
        });

        gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setSpacing(1);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                PhotoItem photoItem = mData.get(position);

                Picasso.with(AddedPhotosActivity.this)
                        .load(photoItem.getFile())
                        .placeholder(getResources().getDrawable(android.R.drawable.ic_menu_gallery))
                        .fit()
                        .into(selectedImage);

                mTitle.setText(photoItem.getTitle());
            }
        });
        mTitle = (TextSwitcher) findViewById(R.id.title);
        mData = new ArrayList<>();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.added_photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_foto) {
            File pathDir = photoFileUtils.getPictureDir();
            path = Uri.fromFile(new File(pathDir, File.separator + String.valueOf(UUID.randomUUID().toString()) + ".jpg"));
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, path);
            startActivityForResult(cameraIntent, CAPTURE_CAMERA_ACTIVITY_REQ);
            return true;
        }

        if (id == R.id.action_add_image) {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivityForResult(intent, CAPTURE_ALL_IMAGE_ACTIVITY_REQ);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle extras = getIntent().getExtras();

        String objectName = null;
        String objectId = null;
        if (extras != null) {
            objectName = extras.getString(GALLERY_NAME_OBJECT);
            objectId = extras.getString(GALLERY_EXTERNALID_OBJECT);
        }else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.photo_not_found), Toast.LENGTH_SHORT)
                    .show();

            selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_image));
           // mTitle.setText(getResources().getString(R.string.photo_not_found));
            return;
        }

        addedPhotos = dataRepository.getPhotoByElement(objectName, objectId);

        if (addedPhotos.size() == 0){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.photo_not_found), Toast.LENGTH_LONG)
                    .show();
        }

        initData();

    }

    private void initData(){
        for(Photo_Element photo_element : addedPhotos){
            PhotoItem photoItem = initPhotoFile(photo_element);
            if(photoItem!=null) {
                mData.add(photoItem);
            }else{
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.photo_not_found)+": "+photo_element.getExternalId(), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        mAdapter = new AddedPhotoAdapter(AddedPhotosActivity.this, mData);
        gallery.setAdapter(mAdapter);
    }

    private PhotoItem initPhotoFile(Photo_Element photo_element) {
        File filePhoto = photoFileUtils.getPhotoFile(photo_element.getExternalId());
        if (filePhoto==null){
            return null;
        }
        return  new PhotoItem(
                photo_element.getName() + photo_element.getCreateDate(),
                filePhoto.getAbsolutePath(),
                filePhoto);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String pathPhoto = null;

        if (requestCode == CAPTURE_CAMERA_ACTIVITY_REQ) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data == null ? path : data.getData();
                pathPhoto = photoUri.getPath();
                Log.d(TAGLOG_IMAGE, "Image saved successfully to " + pathPhoto);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAGLOG_IMAGE, "Camera Cancelled");
                return;
            }
        }
        if (requestCode == CAPTURE_ALL_IMAGE_ACTIVITY_REQ) {
            if (resultCode == RESULT_OK) {

                String pathSelectedPhoto = data.getStringExtra(PATH_SELECTED_PHOTO);

//                Uri photoUri = data == null ? path : data.getData();
//                Log.d("DOCUMENT_CAPTURE", "Image saved successfully to " + photoUri.getPath());
//                Picasso.with(this).load(photoUri).placeholder(R.drawable.shape_camera).into(imageView);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAGLOG_IMAGE, "All photos cancelled");
            }
        }

        if(pathPhoto==null || pathPhoto.isEmpty()){
            return;
        }

//        dataRepository.insertPhoto(Photo_Element.builder()
//                .externalId(cursor.getString(cursor.getColumnIndex(MobileManagerContract.PhotoContract.EXTERNAL_ID)))
//                .name(cursor.getString(cursor.getColumnIndex(MobileManagerContract.PhotoContract.NAME)))
//                .holdername(cursor.getString(cursor.getColumnIndex(MobileManagerContract.PhotoContract.HOLDERNAME)))
//                .holderId(cursor.getString(cursor.getColumnIndex(MobileManagerContract.PhotoContract.HOLDERID)))
//                .createDate(convertDate(cursor, MobileManagerContract.PhotoContract.DATE))
//                .info("")
//                .build());

        initData();

//        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ) {
//            if (resultCode == RESULT_OK) {
//                Uri photoUri = data == null ? path : data.getData();
//                Log.d("DOCUMENT_CAPTURE", "Image saved successfully to " + photoUri.getPath());
//                Picasso.with(this).load(photoUri).placeholder(R.drawable.shape_camera).into(imageView);
//            } else if (resultCode == RESULT_CANCELED) {
//                Log.d("DOCUMENT_CAPTURE", "Cancelled");
//            }
//        }
    }
}