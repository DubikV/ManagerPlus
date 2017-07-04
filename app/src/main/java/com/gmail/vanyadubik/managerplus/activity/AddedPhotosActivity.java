package com.gmail.vanyadubik.managerplus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.AddedPhotoGalleryAdapter;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.PhotoItem;
import com.gmail.vanyadubik.managerplus.model.db.element.Photo_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.utils.ElementUtils;
import com.gmail.vanyadubik.managerplus.utils.PhotoFIleUtils;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.activity.ImageActivity.IMAGE_FULL_NAME;
import static com.gmail.vanyadubik.managerplus.activity.ImageActivity.IMAGE_NAME;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_IMAGE;

public class AddedPhotosActivity extends AppCompatActivity {

    private static final int CAPTURE_CAMERA_ACTIVITY_REQ = 999;
    private static final int CAPTURE_ALL_IMAGE_ACTIVITY_REQ = 998;

    public static final String GALLERY_NAME_OBJECT = "gallery_name_object";
    public static final String GALLERY_EXTERNALID_OBJECT = "gallery_externatlid_object";
    public static final String PATH_SELECTED_PHOTO = "path_selected_photo";
    public static final String NAME_TYPEFILE_PHOTO = ".jpg";

    @Inject
    DataRepository dataRepository;
    @Inject
    PhotoFIleUtils photoFileUtils;
    @Inject
    ElementUtils elementUtils;

    private ImageView selectedImage;
    private Gallery gallery;
    private AddedPhotoGalleryAdapter mAdapter;
    private TextView mTitle;
    private List<PhotoItem> mData;
    private PhotoItem selectedPhoto;
    private List<Photo_Element> addedPhotos;
    private Uri path = null;
    private String holderName, holderId;
    private SimpleDateFormat dateFormat;
    private File pathPictureDir;
    private Photo_Element selected_photo;


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
                selectedPhoto = mData.get(position);
                initSelectedPhoto();
            }
        });


        mTitle = (TextView) findViewById(R.id.title);
        mData = new ArrayList<>();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        pathPictureDir = photoFileUtils.getPictureDir();

        FloatingActionButton deletePhoto = (FloatingActionButton)
                findViewById(R.id.delete_added_photo);
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'delete photo'");

                if(selectedPhoto == null){
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.not_selected_photo), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AddedPhotosActivity.this);
                builder.setTitle(getString(R.string.action_foto));
                builder.setMessage(getString(R.string.deleted_selected_element));

                builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getApplicationContext(),
                                elementUtils.deleteElement(selected_photo, MobileManagerContract.PhotoContract.TABLE_NAME), Toast.LENGTH_SHORT)
                                .show();

                        initData();
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
        });

        FloatingActionButton insertInfo = (FloatingActionButton)
                findViewById(R.id.insert_info);
        insertInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'insert info'");

                if(selectedPhoto == null){
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.not_selected_photo), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AddedPhotosActivity.this);
                builder.setTitle(getString(R.string.action_foto));
                builder.setMessage(getString(R.string.input_info_about_photo));
                final EditText input = new EditText(AddedPhotosActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                input.setText(selected_photo.getInfo());

                builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String info = input.getText().toString();

                        if(info==null || info.isEmpty()){
                            return;
                        }

                        if(selected_photo== null){
                            return;
                        }
                        selected_photo.setInfo(info);

                        dataRepository.setElementByExternalId(MobileManagerContract.PhotoContract.TABLE_NAME, selected_photo);

                        initData();
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
        });

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

            path = Uri.fromFile(new File(pathPictureDir, File.separator + String.valueOf(UUID.randomUUID().toString()) + NAME_TYPEFILE_PHOTO));
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

        if (id == R.id.action_return) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            holderName = extras.getString(GALLERY_NAME_OBJECT);
            holderId = extras.getString(GALLERY_EXTERNALID_OBJECT);
        }else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.photo_not_found), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }
        if(holderName == null || holderName.isEmpty() || holderId == null || holderId.isEmpty()){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.holder_photo_not_specified), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        initData();

    }

    private void initData(){

        addedPhotos = dataRepository.getPhotoByElement(holderName, holderId);

        if(addedPhotos==null){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.photo_not_found), Toast.LENGTH_LONG)
                    .show();
            selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_image));
            mTitle.setText(getResources().getString(R.string.photo_not_found));
            return;
        }

        mData.clear();
        if (addedPhotos.size() == 0){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.photo_not_found), Toast.LENGTH_LONG)
                    .show();
            selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_image));
            mTitle.setText(getResources().getString(R.string.photo_not_found));
        }else {
            for (Photo_Element photo_element : addedPhotos) {
                PhotoItem photoItem = initPhotoFile(photo_element);
                if (photoItem != null) {
                    mData.add(photoItem);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.photo_not_found)
                                    + ": " + photo_element.getExternalId(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
            if (mData.size()!=0){
                int selected = 0;
                gallery.setSelected(true);
                gallery.setSelection(selected);
                selectedPhoto = mData.get(selected);
                initSelectedPhoto();
            }
        }
        mAdapter = new AddedPhotoGalleryAdapter(AddedPhotosActivity.this, mData);
        gallery.setAdapter(mAdapter);
    }

    private void initSelectedPhoto(){

        selected_photo = (Photo_Element) dataRepository.
                getElementByExternaID(MobileManagerContract.PhotoContract.TABLE_NAME,
                        selectedPhoto.getExternalId());

        Picasso.with(AddedPhotosActivity.this)
                .load(selectedPhoto.getFile())
                .placeholder(getResources().getDrawable(android.R.drawable.ic_menu_gallery))
                .fit()
                .into(selectedImage);

        mTitle.setText(selectedPhoto.getTitle());
    }

    private PhotoItem initPhotoFile(Photo_Element photo_element) {
        File filePhoto = photoFileUtils.getPhotoFile(photo_element.getExternalId());
        if (filePhoto==null){
            return null;
        }
        return  new PhotoItem(
                photo_element.getName() + "\n" +
                        getResources().getString(R.string.added_foto_date) + ": " +
                        dateFormat.format(photo_element.getCreateDate().getTime()),
                filePhoto.getAbsolutePath(),
                photo_element.getExternalId(),
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


                File newFile = new File(pathPictureDir, File.separator + String.valueOf(UUID.randomUUID().toString()) + NAME_TYPEFILE_PHOTO);

                try {
                    if(photoFileUtils.copyFile(new File(pathSelectedPhoto), newFile)){
                        pathPhoto = newFile.getPath();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAGLOG_IMAGE, "Image saved successfully to " + pathPhoto);

            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAGLOG_IMAGE, "All photos cancelled");
            }
        }

        if(pathPhoto==null || pathPhoto.isEmpty()){
            return;
        }

        String nameFile = photoFileUtils.getNameFileFromPath(pathPhoto);

        dataRepository.insertPhoto(Photo_Element.builder()
                .externalId(nameFile.replace(NAME_TYPEFILE_PHOTO,""))
                .name(nameFile)
                .holdername(holderName)
                .holderId(holderId)
                .createDate(LocalDateTime.now().toDate())
                .info("")
                .build());

        initData();
    }
}