package com.swinfotech.swpay.model;

public class OPCommissionGETSET {
    private String opName;
    private String commPer;
    private String commVal;
    private String serviceChargePer;
    private String serviceChargeVal;
    private String image;

    public OPCommissionGETSET() {

    }

    public OPCommissionGETSET(String opName, String commPer, String commVal, String serviceChargePer, String serviceChargeVal, String image) {
        this.image = image;
        this.opName = opName;
        this.commPer = commPer;
        this.commVal = commVal;
        this.serviceChargePer = serviceChargePer;
        this.serviceChargeVal = serviceChargeVal;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getCommPer() {
        return commPer;
    }

    public void setCommPer(String commPer) {
        this.commPer = commPer;
    }

    public String getCommVal() {
        return commVal;
    }

    public void setCommVal(String commVal) {
        this.commVal = commVal;
    }

    public String getServiceChargePer() {
        return serviceChargePer;
    }

    public void setServiceChargePer(String serviceChargePer) {
        this.serviceChargePer = serviceChargePer;
    }

    public String getServiceChargeVal() {
        return serviceChargeVal;
    }

    public void setServiceChargeVal(String serviceChargeVal) {
        this.serviceChargeVal = serviceChargeVal;
    }

}
