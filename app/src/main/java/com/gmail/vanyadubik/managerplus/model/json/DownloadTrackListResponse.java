package com.gmail.vanyadubik.managerplus.model.json;

import com.google.gson.annotations.Expose;

public class DownloadTrackListResponse {
    @Expose
    private DownloadTrackListResultDTO trackListResult;

    public DownloadTrackListResponse(DownloadTrackListResultDTO trackListResult) {
        this.trackListResult = trackListResult;
    }

    public DownloadTrackListResultDTO getTrack() {
        return trackListResult;
    }
}
