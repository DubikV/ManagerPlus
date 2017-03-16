package com.gmail.vanyadubik.mobilemanager.model.db;

import java.util.Date;

public class Visit_Element extends Element {

    private Date date;
    private Date dateVisit;
    private String clientExternalId;
    private String createLP;
    private String visitLP;
    private String information;

    public Visit_Element(int id, String externalId, boolean deleted, boolean inDB,
                Date date, Date dateVisit, String clientExternalId, String createLP, String visitLP, String information) {
        super(id, externalId, deleted, inDB);
        this.date = date;
        this.dateVisit = dateVisit;
        this.clientExternalId = clientExternalId;
        this.createLP = createLP;
        this.visitLP = visitLP;
        this.information = information;
    }

    public Date getDate() {
        return date;
    }

    public Date getDateVisit() {
        return dateVisit;
    }

    public String getClientExternalId() {
        return clientExternalId;
    }

    public String getCreateLP() {
        return createLP;
    }

    public String getVisitLP() {
        return visitLP;
    }

    public String getInformation() {
        return information;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Element.Builder{
        private int id;
        private String externalId;
        private boolean deleted;
        private boolean inDB;
        private Date date;
        private Date dateVisit;
        private String clientExternalId;
        private String createLP;
        private String visitLP;
        private String information;


        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder deleted(Boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder inDB(Boolean inDB) {
            this.inDB = inDB;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder dateVisit(Date dateVisit) {
            this.dateVisit = dateVisit;
            return this;
        }

        public Builder clientExternalId(String clientExternalId) {
            this.clientExternalId = clientExternalId;
            return this;
        }

        public Builder createLP(String createLP) {
            this.createLP = createLP;
            return this;
        }

        public Builder visitLP(String visitLP) {
            this.visitLP = visitLP;
            return this;
        }

        public Builder information(String information) {
            this.information = information;
            return this;
        }

        public Visit_Element build() {
            return new Visit_Element(id, externalId, deleted, inDB,
                    date, dateVisit, clientExternalId, createLP, visitLP, information);
        }
    }
}