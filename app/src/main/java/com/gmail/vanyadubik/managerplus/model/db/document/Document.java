package com.gmail.vanyadubik.managerplus.model.db.document;

import java.io.Serializable;
import java.util.Date;

public class Document implements Serializable {

    private int id;
    private String externalId;
    private boolean deleted;
    private boolean inDB;
    private Date date;

    public Document(int id, String externalId, boolean deleted, boolean inDB, Date date) {
        this.id = id;
        this.externalId = externalId;
        this.deleted = deleted;
        this.inDB = inDB;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isInDB() {
        return inDB;
    }

    public void setInDB(boolean inDB) {
        this.inDB = inDB;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String externalId;
        private boolean deleted;
        private boolean inDB;
        private Date date;

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

        public Document build() {
            return new Document(id, externalId, deleted, inDB, date);
        }
    }
}