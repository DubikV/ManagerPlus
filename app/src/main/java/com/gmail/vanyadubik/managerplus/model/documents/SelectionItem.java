package com.gmail.vanyadubik.managerplus.model.documents;

import java.io.Serializable;

public class SelectionItem  implements Serializable {

    private String externalId;
    private String presentation;

    public SelectionItem(String externalId, String presentation) {
        this.externalId = externalId;
        this.presentation = presentation;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPresentation() {
        return presentation;
    }

}
