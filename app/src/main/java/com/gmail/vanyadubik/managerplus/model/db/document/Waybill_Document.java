package com.gmail.vanyadubik.managerplus.model.db.document;

import android.content.Context;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.repository.DataRepositoryImpl;

import java.util.Date;

public class Waybill_Document extends Document {

    private Date dateStart;
    private Date dateEnd;
    private int startLP;
    private int endLP;
    private int startOdometer;
    private int endOdometer;

    public Waybill_Document(int id, String externalId, boolean deleted, boolean inDB,
                            Date date, Date dateStart, Date dateEnd, int startLP, int endLP,
                           int startOdometer, int endOdometer) {
        super(id, externalId, deleted, inDB, date);
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

    public void saveInDB(Context context){

        DataRepository dataRepository = new DataRepositoryImpl(context.getContentResolver());
        dataRepository.insertWaybill(this);
    }

    public String deleteInDB(Context context){

        DataRepository dataRepository = new DataRepositoryImpl(context.getContentResolver());

        if(this.isInDB()) {
            dataRepository.setDocumentByExternalId(MobileManagerContract.WaybillContract.TABLE_NAME, this);
            return context.getResources().getString(R.string.error_delete_element_indb);
        }else{
            dataRepository.deletedElement(MobileManagerContract.WaybillContract.TABLE_NAME, this.getExternalId());
            return context.getResources().getString(R.string.deleted_element);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Document.Builder{
        private int id;
        private String externalId;
        private boolean deleted;
        private boolean inDB;
        private Date date;
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

        public Waybill_Document build() {
            return new Waybill_Document(id, externalId, deleted, inDB, date,
                    dateStart, dateEnd, startLP, endLP, startOdometer, endOdometer);
        }
    }
}