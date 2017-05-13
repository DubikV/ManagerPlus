package com.gmail.vanyadubik.managerplus.utils;

import android.util.Log;

import java.io.File;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.gmail.vanyadubik.managerplus.common.Consts.DIRECTORY_IMAGE;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class PhotoFIleUtils {

    private File getPhotoFile(String nameFile) {
        File directory = getExternalStoragePublicDirectory(DIRECTORY_IMAGE);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {

                Log.e(TAGLOG, "Failed to create storage directory.");
                return null;
            }
        }


        File dir = new File(directory.getPath() + File.separator
                + ROOT_DIR + File.separator
                + memberId);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, File.separator + documentId + ".jpg");
    }

}
