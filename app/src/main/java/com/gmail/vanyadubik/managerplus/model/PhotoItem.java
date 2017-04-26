package com.gmail.vanyadubik.managerplus.model;

import java.io.File;

public class PhotoItem {
    private String title;
    private File file;

    public PhotoItem(String title, File file) {
        this.title = title;
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public File getFile() {
        return file;
    }
}
