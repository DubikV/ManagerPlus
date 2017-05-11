package com.gmail.vanyadubik.managerplus.model;

import java.io.File;

public class PhotoItem {
    private String title;
    private String absolutePath;
    private File file;

    public PhotoItem(String title, String absolutePath, File file) {
        this.title = title;
        this.absolutePath = absolutePath;
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public File getFile() {
        return file;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
