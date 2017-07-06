package com.gmail.vanyadubik.managerplus.model.db.element;

import android.content.Context;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.repository.DataRepositoryImpl;

import java.util.Date;

public class Photo_Element extends Element {

    private String holdername;
    private String holderId;
    private Date createDate;
    private String info;

    public Photo_Element(int id, String externalId, boolean deleted, boolean inDB,
                         String name, String holdername, String holderId, Date createDate, String info) {
        super(id, externalId, deleted, inDB, name);
        this.holdername = holdername;
        this.holderId = holderId;
        this.createDate = createDate;
        this.info = info;
    }

    public String getHoldername() {
        return holdername;
    }

    public void setHoldername(String holdername) {
        this.holdername = holdername;
    }

    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String holderId) {
        this.holderId = holderId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void saveInDB(Context context){

        DataRepository dataRepository = new DataRepositoryImpl(context.getContentResolver());
        dataRepository.insertPhoto(this);
    }

    public String deleteInDB(Context context){

        DataRepository dataRepository = new DataRepositoryImpl(context.getContentResolver());

        if(this.isInDB()) {
            dataRepository.setElementByExternalId(MobileManagerContract.ClientContract.TABLE_NAME, this);
            return context.getResources().getString(R.string.error_delete_element_indb);
        }else{
            dataRepository.deletedElement(MobileManagerContract.PhotoContract.TABLE_NAME, this.getExternalId());
            return context.getResources().getString(R.string.deleted_element);
        }

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
        private String holdername;
        private String holderId;
        private Date createDate;
        private String info;


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

        public Builder holdername(String holdername) {
            this.holdername = holdername;
            return this;
        }

        public Builder holderId(String holderId) {
            this.holderId = holderId;
            return this;
        }

        public Builder createDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }

        public Builder info(String info) {
            this.info = info;
            return this;
        }

        public Photo_Element build() {
            return new Photo_Element(id, externalId, deleted, inDB,
                    name, holdername, holderId, createDate, info);
        }
    }
}