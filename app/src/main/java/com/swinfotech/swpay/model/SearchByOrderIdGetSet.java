package com.swinfotech.swpay.model;

import java.io.Serializable;

public class SearchByOrderIdGetSet implements Serializable {
    private String id;
    private String phNum;
    private String amount;
    private String status;
    private String date;
    private String trId;
    private String opName;
    private String image;

    public SearchByOrderIdGetSet() {

    }

    public SearchByOrderIdGetSet(String id, String phNum, String amount, String status, String date, String trId, String opName, String image) {
        this.id = id;
        this.phNum = phNum;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.trId = trId;
        this.opName = opName;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhNum() {
        return phNum;
    }

    public void setPhNum(String phNum) {
        this.phNum = phNum;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrId() {
        return trId;
    }

    public void setTrId(String trId) {
        this.trId = trId;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
