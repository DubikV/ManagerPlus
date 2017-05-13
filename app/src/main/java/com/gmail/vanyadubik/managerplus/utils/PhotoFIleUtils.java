package com.gmail.vanyadubik.managerplus.utils;

import android.util.Log;

import java.io.File;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.gmail.vanyadubik.managerplus.common.Consts.DIR_PICTURES;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class PhotoFIleUtils {

    public File getPhotoFile(String nameFile) {
        File directory = getRootDir();
        if (!directory.exists()) {
            Log.e(TAGLOG, "Root directory not exist.");
            return null;
        }

        File dir = new File(directory.getPath() + File.separator + DIR_PICTURES);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, File.separator + nameFile + ".jpg");

    }

    private File getRootDir() {
        File directory = getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAGLOG, "Failed to create storage directory.");
                return null;
            }
        }

        return directory;
    }

}
