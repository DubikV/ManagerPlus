package com.gmail.vanyadubik.managerplus.model.db.element;

import java.io.Serializable;

public class Element implements Serializable {

    private int id;
    private String externalId;
    private boolean deleted;
    private boolean inDB;
    private String name;

    public Element(int id, String externalId, boolean deleted, boolean inDB, String name) {
        this.id = id;
        this.externalId = externalId;
        this.deleted = deleted;
        this.inDB = inDB;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        private String name;

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

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Element build() {
            return new Element(id, externalId, deleted, inDB, name);
        }
    }
}