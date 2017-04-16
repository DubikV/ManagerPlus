package com.gmail.vanyadubik.managerplus.model.db.document;

import java.util.Date;

public class Fuel_Document extends Document {

    private String typeFuel;
    private String typePayment;
    private double litres;
    private double money;
    private int createLP;

    public Fuel_Document(int id, String externalId, boolean deleted, boolean inDB,
                         Date date, String typeFuel, String typePayment, double litres,
                         double money, int createLP) {
        super(id, externalId, deleted, inDB, date);
        this.typeFuel = typeFuel;
        this.typePayment = typePayment;
        this.litres = litres;
        this.money = money;
        this.createLP = createLP;
    }

    public String getTypeFuel() {
        return typeFuel;
    }

    public void setTypeFuel(String typeFuel) {
        this.typeFuel = typeFuel;
    }

    public String getTypePayment() {
        return typePayment;
    }

    public void setTypePayment(String typePayment) {
        this.typePayment = typePayment;
    }

    public double getLitres() {
        return litres;
    }

    public void setLitres(double litres) {
        this.litres = litres;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getCreateLP() {
        return createLP;
    }

    public void setCreateLP(int createLP) {
        this.createLP = createLP;
    }

    public static  Builder builder(){
        return new Builder();
    }

    public static class Builder extends Document.Builder{
        private int id;
        private String externalId;
        private boolean deleted;
        private boolean inDB;
        private Date date;
        private String typeFuel;
        private String typePayment;
        private double litres;
        private double money;
        private int createLP;


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

        public Builder typeFuel(String typeFuel) {
            this.typeFuel = typeFuel;
            return this;
        }

        public Builder typePayment(String typePayment) {
            this.typePayment = typePayment;
            return this;
        }

        public Builder litres(double litres) {
            this.litres = litres;
            return this;
        }

        public Builder money(double money) {
            this.money = money;
            return this;
        }

        public Builder createLP(int createLP) {
            this.createLP = createLP;
            return this;
        }

        public Fuel_Document build() {
            return new Fuel_Document(id, externalId, deleted, inDB, date,
                    typeFuel, typePayment, litres, money, createLP);
        }
    }
}