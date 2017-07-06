package com.gmail.vanyadubik.managerplus.model.db.element;

import android.content.Context;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.repository.DataRepositoryImpl;

public class Client_Element extends Element {

    private String address;
    private String phone;
    private int positionLP;

    public Client_Element(int id, String externalId, boolean deleted, boolean inDB,
                          String name, String address, String phone, int positionLP) {
        super(id, externalId, deleted, inDB, name);
        this.address = address;
        this.phone = phone;
        this.positionLP = positionLP;
    }

    public int getPositionLP() {
        return positionLP;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }


    public void saveInDB(Context context){

        DataRepository dataRepository = new DataRepositoryImpl(context.getContentResolver());
        dataRepository.insertClient(this);
    }

    public String deleteInDB(Context context){

        DataRepository dataRepository = new DataRepositoryImpl(context.getContentResolver());

        if(this.isInDB()) {
            dataRepository.setElementByExternalId(MobileManagerContract.ClientContract.TABLE_NAME, this);
            return context.getResources().getString(R.string.error_delete_element_indb);
        }else{
            dataRepository.deletedElement(MobileManagerContract.ClientContract.TABLE_NAME, this.getExternalId());
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
        private String address;
        private String phone;
        private int positionLP;


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

        public Builder positionLP(int positionLP) {
            this.positionLP = positionLP;
            return this;
        }

        public Client_Element build() {
            return new Client_Element(id, externalId, deleted, inDB,
                    name, address, phone, positionLP);
        }
    }
}