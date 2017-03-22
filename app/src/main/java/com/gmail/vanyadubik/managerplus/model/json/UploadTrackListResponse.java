package com.gmail.vanyadubik.managerplus.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadTrackListResponse {
    @Expose
    private String info;
    @Expose
    @SerializedName("track_list")
    private List<LocationPointDTO> locationPoints;

    public UploadTrackListResponse() {
    }

    public List<LocationPointDTO> getUploadedDocuments() {
        return locationPoints;
    }

}
