package com.gmail.vanyadubik.managerplus.model.json;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class UploadTrackListResultDTO {
    @Expose
    private Date dateStart;
    @Expose
    private Date dateEnd;

    public UploadTrackListResultDTO(Date dateStart, Date dateEnd) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }
}
