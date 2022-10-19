package com.swinfotech.swpay.model;

public class DebitReportGetSet {
    private String orId;
    private String oldBal;
    private String netAmt;
    private String newBal;
    private String trDate;
    private String mobileNo;
    private String nameTrBy;
    private String operatorName;
    private String remarks;

    public DebitReportGetSet() {
    }

    public DebitReportGetSet(String orId, String oldBal, String netAmt, String newBal, String trDate, String mobileNo, String nameTrBy, String operatorName, String remarks) {
        this.orId = orId;
        this.oldBal = oldBal;
        this.netAmt = netAmt;
        this.newBal = newBal;
        this.trDate = trDate;
        this.mobileNo = mobileNo;
        this.nameTrBy = nameTrBy;
        this.operatorName = operatorName;
        this.remarks = remarks;
    }

    public String getOrId() {
        return orId;
    }

    public void setOrId(String orId) {
        this.orId = orId;
    }

    public String getOldBal() {
        return oldBal;
    }

    public void setOldBal(String oldBal) {
        this.oldBal = oldBal;
    }

    public String getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(String netAmt) {
        this.netAmt = netAmt;
    }

    public String getNewBal() {
        return newBal;
    }

    public void setNewBal(String newBal) {
        this.newBal = newBal;
    }

    public String getTrDate() {
        return trDate;
    }

    public void setTrDate(String trDate) {
        this.trDate = trDate;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getNameTrBy() {
        return nameTrBy;
    }

    public void setNameTrBy(String nameTrBy) {
        this.nameTrBy = nameTrBy;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
