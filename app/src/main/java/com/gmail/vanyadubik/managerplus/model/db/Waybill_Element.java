package com.gmail.vanyadubik.managerplus.model.db;

import java.util.Date;

public class Waybill_Element extends Element {

    private Date dateStart;
    private Date dateEnd;
    private int startLP;
    private int endLP;
    private int startOdometer;
    private int endOdometer;

    public Waybill_Element(int id, String externalId, boolean deleted, boolean inDB,
             Date dateStart, Date dateEnd, int startLP, int endLP,
                           int startOdometer, int endOdometer) {
        super(id, externalId, deleted, inDB);
        this.startLP = startLP;
        this.endLP = endLP;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.startOdometer = startOdometer;
        this.endOdometer = endOdometer;
    }

    public int getStartLP() {
        return startLP;
    }

    public int getEndLP() {
        return endLP;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public int getStartOdometer() {
        return startOdometer;
    }

    public int getEndOdometer() {
        return endOdometer;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setStartLP(int startLP) {
        this.startLP = startLP;
    }

    public void setEndLP(int endLP) {
        this.endLP = endLP;
    }

    public void setStartOdometer(int startOdometer) {
        this.startOdometer = startOdometer;
    }

    public void setEndOdometer(int endOdometer) {
        this.endOdometer = endOdometer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Element.Builder{
        private int id;
        private String externalId;
        private boolean deleted;
        private boolean inDB;
        private Date dateStart;
        private Date dateEnd;
        private int startLP;
        private int endLP;
        private int startOdometer;
        private int endOdometer;


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

        public Builder dateStart(Date dateStart) {
            this.dateStart = dateStart;
            return this;
        }

        public Builder dateEnd(Date dateEnd) {
            this.dateEnd = dateEnd;
            return this;
        }

        public Builder startOdometer(int startOdometer) {
            this.startOdometer = startOdometer;
            return this;
        }

        public Builder endOdometer(int endOdometer) {
            this.endOdometer = endOdometer;
            return this;
        }

        public Builder startLP(int startLP) {
            this.startLP = startLP;
            return this;
        }

        public Builder endLP(int endLP) {
            this.endLP = endLP;
            return this;
        }

        public Waybill_Element build() {
            return new Waybill_Element(id, externalId, deleted, inDB,
                    dateStart, dateEnd, startLP, endLP, startOdometer, endOdometer);
        }
    }
}