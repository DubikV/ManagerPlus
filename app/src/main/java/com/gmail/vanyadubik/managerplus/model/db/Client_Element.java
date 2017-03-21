package com.gmail.vanyadubik.managerplus.model.db;

public class Client_Element extends Element {

    private String name;
    private String address;
    private String phone;
    private String positionLP;

    public Client_Element(int id, String externalId, boolean deleted, boolean inDB,
                          String name, String address, String phone, String positionLP) {
        super(id, externalId, deleted, inDB);
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.positionLP = positionLP;
    }

    public String getName() {
        return name;
    }

    public String getPositionLP() {
        return positionLP;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Element.Builder{
        private int id;
        private String externalId;
        private boolean deleted;
        private boolean inDB;
        private String name;
        private String address;
        private String phone;
        private String positionLP;


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

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder positionLP(String positionLP) {
            this.positionLP = positionLP;
            return this;
        }

        public Client_Element build() {
            return new Client_Element(id, externalId, deleted, inDB,
                    name, address, phone, positionLP);
        }
    }
}