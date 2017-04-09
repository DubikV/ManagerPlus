package com.gmail.vanyadubik.managerplus.model.documents;
import java.util.Date;

public class VisitList {

    private String externalId;
    private Date date;
    private String client;
    private String typeVisit;

    public VisitList(String externalId, Date date, String client, String typeVisit) {
        this.externalId = externalId;
        this.date = date;
        this.client = client;
        this.typeVisit = typeVisit;
    }

    public VisitList(String externalId, Date date, String typeVisit) {
        this.externalId = externalId;
        this.date = date;
        this.typeVisit = typeVisit;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getTypeVisit() {
        return typeVisit;
    }

    public void setTypeVisit(String typeVisit) {
        this.typeVisit = typeVisit;
    }
}