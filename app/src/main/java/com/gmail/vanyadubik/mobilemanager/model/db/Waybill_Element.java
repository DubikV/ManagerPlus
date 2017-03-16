package com.gmail.vanyadubik.mobilemanager.model.db;

import java.util.Date;

public class Waybill_Element extends Element {

    private Date date;
    private Date dateStart;
    private Date dateEnd;
    private String startLP;
    private String endLP;

    public Waybill_Element(int id, String externalId, boolean deleted, boolean inDB,
            Date date, Date dateStart, Date dateEnd, String startLP, String endLP) {
        super(id, externalId, deleted, inDB);
        this.startLP = startLP;
        this.endLP = endLP;
        this.date = date;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }


    public Date getDate() {
        return date;
    }

    public String getStartLP() {
        return startLP;
    }

    public String getEndLP() {
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
        private String startLP;
        private String endLP;


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

        public Builder startLP(String startLP) {
            this.startLP = startLP;
            return this;
        }

        public Builder endLP(String endLP) {
            this.endLP = endLP;
            return this;
        }

        public Waybill_Element build() {
            return new Waybill_Element(id, externalId, deleted, inDB,
                    date, dateStart, dateEnd, startLP, endLP);
        }
    }
}