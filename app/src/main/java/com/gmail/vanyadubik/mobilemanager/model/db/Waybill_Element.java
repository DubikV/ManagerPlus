package com.gmail.vanyadubik.mobilemanager.model.db;

import java.util.Date;

public class Waybill_Element extends Element {

    private Date date;
    private Date dateStart;
    private Date dateEnd;
    private LocationPoint startLP;
    private LocationPoint endLP;

    public Waybill_Element(int id, String externalId, boolean deleted, boolean inDB,
            Date date, Date dateStart, Date dateEnd, LocationPoint startLP, LocationPoint endLP) {
        super(id, externalId, deleted, inDB);
        this.startLP = startLP;
        this.endLP = endLP;
        this.date = date;
        this.dateStart = dateEnd;
        this.dateEnd = dateEnd;
    }


    public Date getDate() {
        return date;
    }

    public LocationPoint getStartLP() {
        return startLP;
    }

    public LocationPoint getEndLP() {
        return endLP;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
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
        private Date dateStart;
        private Date dateEnd;
        private LocationPoint startLP;
        private LocationPoint endLP;


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

        public Builder dateStart(Date dateStart) {
            this.dateStart = dateStart;
            return this;
        }

        public Builder dateEnd(Date dateEnd) {
            this.dateEnd = dateEnd;
            return this;
        }

        public Builder startLP(LocationPoint startLP) {
            this.startLP = startLP;
            return this;
        }

        public Builder endLP(LocationPoint endLP) {
            this.endLP = endLP;
            return this;
        }

        public Waybill_Element build() {
            return new Waybill_Element(id, externalId, deleted, inDB,
                    date, dateStart, dateEnd, startLP, endLP);
        }
    }
}