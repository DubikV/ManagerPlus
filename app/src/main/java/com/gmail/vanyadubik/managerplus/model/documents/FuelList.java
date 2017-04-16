package com.gmail.vanyadubik.managerplus.model.documents;
import java.util.Date;

public class FuelList {

    private String externalId;
    private Date date;
    private String typeFuel;
    private Double litres;

    public FuelList(String externalId, Date date, String typeFuel, Double litres) {
        this.externalId = externalId;
        this.date = date;
        this.typeFuel = typeFuel;
        this.litres = litres;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTypeFuel() {
        return typeFuel;
    }

    public void setTypeFuel(String typeFuel) {
        this.typeFuel = typeFuel;
    }

    public Double getLitres() {
        return litres;
    }

    public void setLitres(Double litres) {
        this.litres = litres;
    }
}