package com.swinfotech.swpay.model;

public class BeneficiaryListGetSet {
    private String name;
    private String mobileNo;
    private String remitterID;
    private String rPTid;
    private String accountNum;
    private String ifsc;
    private String bankName;
    private String status;
    private String isValidate;

    public BeneficiaryListGetSet() {
    }

    public BeneficiaryListGetSet(String name, String mobileNo, String remitterID, String rPTid, String accountNum, String ifsc, String bankName, String status, String isValidate) {
        this.name = name;
        this.mobileNo = mobileNo;
        this.remitterID = remitterID;
        this.rPTid = rPTid;
        this.accountNum = accountNum;
        this.ifsc = ifsc;
        this.bankName = bankName;
        this.status = status;
        this.isValidate = isValidate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getRemitterID() {
        return remitterID;
    }

    public void setRemitterID(String remitterID) {
        this.remitterID = remitterID;
    }

    public String getrPTid() {
        return rPTid;
    }

    public void setrPTid(String rPTid) {
        this.rPTid = rPTid;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsValidate() {
        return isValidate;
    }

    public void setIsValidate(String isValidate) {
        this.isValidate = isValidate;
    }
}
