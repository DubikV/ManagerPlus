package com.gmail.vanyadubik.managerplus.model.json;

import com.google.gson.annotations.Expose;

import java.util.List;

public class UploadTrackListRequest {
    @Expose
    private List<LocationPointDTO> trackList;

    public UploadTrackListRequest(List<LocationPointDTO> trackList) {
        this.trackList = trackList;
    }

    public List<LocationPointDTO> getTrackList() {
        return trackList;
    }
}
