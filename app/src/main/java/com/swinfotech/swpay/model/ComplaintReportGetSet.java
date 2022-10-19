package com.swinfotech.swpay.model;

public class ComplaintReportGetSet {
    private String orID;
    private String date;
    private String mobileNo;
    private String amount;
    private String details;
    private String operator;
    private String accountNo;
    private String remitter;
    private String supportType;
    private String status;
    private String message;
    private String statusSuccess;

    public ComplaintReportGetSet() {
    }

    public ComplaintReportGetSet(String orID, String date, String mobileNo, String amount, String details, String operator, String accountNo, String remitter, String supportType, String status, String message, String statusSuccess) {
        this.orID = orID;
        this.date = date;
        this.mobileNo = mobileNo;
        this.amount = amount;
        this.details = details;
        this.operator = operator;
        this.accountNo = accountNo;
        this.remitter = remitter;
        this.supportType = supportType;
        this.status = status;
        this.message = message;
        this.statusSuccess = statusSuccess;
    }

    public String getOrID() {
        return orID;
    }

    public void setOrID(String orID) {
        this.orID = orID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getRemitter() {
        return remitter;
    }

    public void setRemitter(String remitter) {
        this.remitter = remitter;
    }

    public String getSupportType() {
        return supportType;
    }

    public void setSupportType(String supportType) {
        this.supportType = supportType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatusSuccess() {
        return statusSuccess;
    }

    public void setStatusSuccess(String statusSuccess) {
        this.statusSuccess = statusSuccess;
    }
}
