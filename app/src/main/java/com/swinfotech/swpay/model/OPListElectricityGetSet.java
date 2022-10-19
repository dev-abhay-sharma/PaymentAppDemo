package com.swinfotech.swpay.model;

import java.io.Serializable;

public class OPListElectricityGetSet implements Serializable {
    private String id;
    private String opName;
    private String opCode;
    private String image;

    public OPListElectricityGetSet() {
    }

    public OPListElectricityGetSet(String id, String opName, String opCode, String image) {
        this.id = id;
        this.opName = opName;
        this.opCode = opCode;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
