package com.swinfotech.swpay.model;

import java.io.Serializable;

public class SearchByNumberGetSet implements Serializable {
    private String PhNum, id;
    private String amount;
    private String status;
    private String date;
    private String trId;
    private String OpName;
    private String image;

    public SearchByNumberGetSet() {

    }

    public SearchByNumberGetSet(String phNum, String amount, String status, String date, String trId, String opName, String id, String image) {
        this.PhNum = phNum;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.trId = trId;
        this.OpName = opName;
        this.id = id;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhNum() {
        return PhNum;
    }

    public void setPhNum(String phNum) {
        PhNum = phNum;
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
        return OpName;
    }

    public void setOpName(String opName) {
        OpName = opName;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
