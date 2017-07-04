package com.gmail.vanyadubik.managerplus.model;

import java.io.File;

public class PhotoItem {
    private String title;
    private String absolutePath;
    private String externalId;
    private File file;

    public PhotoItem(String title, String absolutePath, String externalId, File file) {
        this.title = title;
        this.absolutePath = absolutePath;
        this.externalId = externalId;
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

    public String getExternalId() {
        return externalId;
    }
}
