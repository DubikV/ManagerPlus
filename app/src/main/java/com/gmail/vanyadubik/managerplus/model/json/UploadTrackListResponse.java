package com.gmail.vanyadubik.managerplus.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadTrackListResponse {
    @Expose
    private String info;
    @Expose
    @SerializedName("new_tracks")
    private UploadTrackListResultDTO resultDTO;

    public UploadTrackListResponse() {
    }

    public UploadTrackListResultDTO getResultDTO() {
        return resultDTO;
    }

    public String getInfo() {
        return info;
    }
}
