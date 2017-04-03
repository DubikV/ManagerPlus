package com.gmail.vanyadubik.managerplus.model.db;

import java.io.Serializable;

public class Element implements Serializable {

    private int id;
    private String externalId;
    private boolean deleted;
    private boolean inDB;

    public Element(int id, String externalId, boolean deleted, boolean inDB) {
        this.id = id;
        this.externalId = externalId;
        this.deleted = deleted;
        this.inDB = inDB;
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

    public static Builder builder() {
        return new Builder();
    }



    public static class Builder {
        private int id;
        private String externalId;
        private boolean deleted;
        private boolean inDB;

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

        public Element build() {
            return new Element(id, externalId, deleted, inDB);
        }
    }
}