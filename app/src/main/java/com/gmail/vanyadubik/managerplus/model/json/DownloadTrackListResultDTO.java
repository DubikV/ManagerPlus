package com.gmail.vanyadubik.managerplus.model.json;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

public class DownloadTrackListResultDTO {
    @Expose
    private DateTime dateTimeStart;
    @Expose
    private DateTime dateTimeEnd;

    public DownloadTrackListResultDTO(DateTime dateTimeStart, DateTime dateTimeEnd) {
        this.dateTimeStart = dateTimeStart;
        this.dateTimeEnd = dateTimeEnd;
    }

    public DateTime getDateTimeStart() {
        return dateTimeStart;
    }

    public DateTime getDateTimeEnd() {
        return dateTimeEnd;
    }
}
