package com.swinfotech.swpay.model;

public class AEPSReportGetSet {
    private String orderId;
    private String date;
    private String oldBal;
    private String newBal;
    private String txnType;
    private String netAmount;
    private String amount;
    private String operator;
    private String status;
    private String merchantNo;
    private String trName;


    public AEPSReportGetSet() {
    }

    public AEPSReportGetSet(String orderId, String date, String oldBal, String newBal, String txnType, String netAmount, String amount, String operator, String status, String merchantNo, String trName) {
        this.orderId = orderId;
        this.date = date;
        this.oldBal = oldBal;
        this.newBal = newBal;
        this.txnType = txnType;
        this.netAmount = netAmount;
        this.amount = amount;
        this.operator = operator;
        this.status = status;
        this.merchantNo = merchantNo;
        this.trName = trName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOldBal() {
        return oldBal;
    }

    public void setOldBal(String oldBal) {
        this.oldBal = oldBal;
    }

    public String getNewBal() {
        return newBal;
    }

    public void setNewBal(String newBal) {
        this.newBal = newBal;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getTrName() {
        return trName;
    }

    public void setTrName(String trName) {
        this.trName = trName;
    }
}
