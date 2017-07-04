package com.gmail.vanyadubik.managerplus.utils;

import android.content.Context;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.model.db.document.Document;
import com.gmail.vanyadubik.managerplus.model.db.element.Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.repository.DataRepositoryImpl;

public class ElementUtils {

    DataRepository dataRepository;

    private Context _Context;

    public ElementUtils(Context mContext) {
        _Context = mContext;
        dataRepository = new DataRepositoryImpl(mContext.getContentResolver());
    }

    public String deleteElement(Element element, String nameElement){
        if(element==null){
            return _Context.getResources().getString(R.string.error_delete_element);
        }
        if(element.isInDB()) {
            dataRepository.setElementByExternalId(nameElement, element);
            return _Context.getResources().getString(R.string.error_delete_element_indb);
        }else{
            dataRepository.deletedElement(nameElement, element.getExternalId());
            return _Context.getResources().getString(R.string.deleted_element);
        }
    }

    public String deleteDocument(Document document, String nameDocument){
        if(document==null){
            return _Context.getResources().getString(R.string.error_delete_element);
        }
        if(document.isInDB()) {
            dataRepository.setDocumentByExternalId(nameDocument, document);
            return _Context.getResources().getString(R.string.error_delete_document_indb);
        }else{
            dataRepository.deletedElement(nameDocument, document.getExternalId());
            return _Context.getResources().getString(R.string.deleted_element);
        }
    }



}
