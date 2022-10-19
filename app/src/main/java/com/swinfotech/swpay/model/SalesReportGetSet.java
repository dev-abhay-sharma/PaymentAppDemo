package com.swinfotech.swpay.model;

public class SalesReportGetSet {
    private String amtSuccess;
    private String uName;
    private String id;
    private String mobile;
    private String balAmt;


    public SalesReportGetSet() {
    }

    public SalesReportGetSet(String amtSuccess, String uName, String id, String mobile, String balAmt) {
        this.amtSuccess = amtSuccess;
        this.uName = uName;
        this.id = id;
        this.mobile = mobile;
        this.balAmt = balAmt;
    }

    public String getAmtSuccess() {
        return amtSuccess;
    }

    public void setAmtSuccess(String amtSuccess) {
        this.amtSuccess = amtSuccess;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBalAmt() {
        return balAmt;
    }

    public void setBalAmt(String balAmt) {
        this.balAmt = balAmt;
    }
}
