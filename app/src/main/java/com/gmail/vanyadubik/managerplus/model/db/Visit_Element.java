package com.gmail.vanyadubik.managerplus.model.db;

import java.util.Date;

public class Visit_Element extends Element {

    private Date date;
    private Date dateVisit;
    private String clientExternalId;
    private int createLP;
    private int visitLP;
    private String typeVisit;
    private String information;

    public Visit_Element(int id, String externalId, boolean deleted, boolean inDB,
                Date date, Date dateVisit, String clientExternalId, int createLP, int visitLP,
                         String typeVisit, String information) {
        super(id, externalId, deleted, inDB);
        this.date = date;
        this.dateVisit = dateVisit;
        this.clientExternalId = clientExternalId;
        this.createLP = createLP;
        this.visitLP = visitLP;
        this.typeVisit = typeVisit;
        this.information = information;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getTypeVisit() {
        return typeVisit;
    }

    public void setTypeVisit(String typeVisit) {
        this.typeVisit = typeVisit;
    }

    public int getVisitLP() {
        return visitLP;
    }

    public void setVisitLP(int visitLP) {
        this.visitLP = visitLP;
    }

    public int getCreateLP() {
        return createLP;
    }

    public void setCreateLP(int createLP) {
        this.createLP = createLP;
    }

    public String getClientExternalId() {
        return clientExternalId;
    }

    public void setClientExternalId(String clientExternalId) {
        this.clientExternalId = clientExternalId;
    }

    public Date getDateVisit() {
        return dateVisit;
    }

    public void setDateVisit(Date dateVisit) {
        this.dateVisit = dateVisit;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        private int createLP;
        private int visitLP;
        private String typeVisit;
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

        public Builder createLP(int createLP) {
            this.createLP = createLP;
            return this;
        }

        public Builder visitLP(int visitLP) {
            this.visitLP = visitLP;
            return this;
        }

        public Builder typeVisit(String typeVisit) {
            this.typeVisit = typeVisit;
            return this;
        }

        public Builder information(String information) {
            this.information = information;
            return this;
        }

        public Visit_Element build() {
            return new Visit_Element(id, externalId, deleted, inDB,
                    date, dateVisit, clientExternalId, createLP, visitLP, typeVisit, information);
        }
    }
}