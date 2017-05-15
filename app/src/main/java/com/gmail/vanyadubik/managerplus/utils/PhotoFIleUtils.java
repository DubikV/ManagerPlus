package com.gmail.vanyadubik.managerplus.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.model.PhotoItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.gmail.vanyadubik.managerplus.common.Consts.DIRECTORY_APP;
import static com.gmail.vanyadubik.managerplus.common.Consts.DIR_PICTURES;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_IMAGE;

public class PhotoFIleUtils {
    private Context mContext;

    public PhotoFIleUtils(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<PhotoItem> getAllShownImagesPath() {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<PhotoItem> listOfAllImages = new ArrayList<PhotoItem>();
        String absolutePathOfImage = null;
        String nameImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = mContext.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(
                    new PhotoItem(
                            getNameFileFromPath(absolutePathOfImage),
                            absolutePathOfImage,
                            new File(absolutePathOfImage)));
        }
        return listOfAllImages;
    }

    public File getPhotoFile(String nameFile) {
        File directory = getPictureDir();
        if (!directory.exists()) {
            Log.e(TAGLOG_IMAGE, "Picture directory not exist.");
            return null;
        }

        return new File(directory, File.separator + nameFile + ".jpg");

    }

    public File getRootDir() {
        File directory = getExternalStoragePublicDirectory(DIRECTORY_APP);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAGLOG_IMAGE, "Failed to create storage directory.");
                return null;
            }
        }

        return directory;
    }

    public File getPictureDir() {
        File rootDir = getRootDir();
        if (!rootDir.exists()) {
            Log.e(TAGLOG_IMAGE, "Root directory not exist.");
            return null;
        }

        File directory = new File(rootDir.getPath() + File.separator + DIR_PICTURES);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAGLOG_IMAGE, "Failed to create picture directory.");
                return null;
            }
        }

        return directory;
    }

    public String getNameFileFromPath(String path){
        return path.substring(path.lastIndexOf("/")+1);
    }

    public boolean copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return false;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
        return true;
    }

}
