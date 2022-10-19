package com.swinfotech.swpay.model;

import java.io.Serializable;

public class NewOPListGetSet implements Serializable {
    private String opName;
    private String opId;
    private String image;

    public NewOPListGetSet() {
    }

    public NewOPListGetSet(String opName, String opId, String image) {
        this.opName = opName;
        this.opId = opId;
        this.image = image;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
