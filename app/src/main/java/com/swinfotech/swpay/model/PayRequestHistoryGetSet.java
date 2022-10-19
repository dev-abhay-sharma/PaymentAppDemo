package com.swinfotech.swpay.model;

public class PayRequestHistoryGetSet {
    private String id;
    private String requestTime;
    private String status;
    private String amount;
    private String bankName;
    private String parentName;
    private String parentMobile;
    private String trDate;
    private String responseTime;
    private String trNo;
    private String paymentMode;
    private String remarks;

    public PayRequestHistoryGetSet() {
    }

    public PayRequestHistoryGetSet(String id, String requestTime, String status, String amount, String bankName, String parentName, String parentMobile, String trDate, String responseTime, String trNo, String paymentMode, String remarks) {
        this.id = id;
        this.requestTime = requestTime;
        this.status = status;
        this.amount = amount;
        this.bankName = bankName;
        this.parentName = parentName;
        this.parentMobile = parentMobile;
        this.trDate = trDate;
        this.responseTime = responseTime;
        this.trNo = trNo;
        this.paymentMode = paymentMode;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentMobile() {
        return parentMobile;
    }

    public void setParentMobile(String parentMobile) {
        this.parentMobile = parentMobile;
    }

    public String getTrDate() {
        return trDate;
    }

    public void setTrDate(String trDate) {
        this.trDate = trDate;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getTrNo() {
        return trNo;
    }

    public void setTrNo(String trNo) {
        this.trNo = trNo;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
