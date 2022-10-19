package com.swinfotech.swpay.model;

import java.io.Serializable;

public class TransactionReportGetSet implements Serializable {
    private String trId;
    private String trDate;
    private String oldBal;
    private String amount;
    private String netAmount;
    private String newBal;
    private String opRef;
    private String apiRef;
    private String rechargeNum;
    private String opName;
    private String serviceName;
    private String status;
    private String opImage;
    private String trType;
    private String remark;

    public TransactionReportGetSet() {

    }

    public TransactionReportGetSet(String trId, String trDate, String oldBal, String amount, String netAmount, String newBal, String opRef, String apiRef, String rechargeNum, String opName, String serviceName, String status, String opImage, String trType, String remark) {
        this.trId = trId;
        this.trDate = trDate;
        this.oldBal = oldBal;
        this.amount = amount;
        this.netAmount = netAmount;
        this.newBal = newBal;
        this.opRef = opRef;
        this.apiRef = apiRef;
        this.rechargeNum = rechargeNum;
        this.opName = opName;
        this.serviceName = serviceName;
        this.status = status;
        this.opImage = opImage;
        this.trType = trType;
        this.remark = remark;
    }

    public String getTrId() {
        return trId;
    }

    public void setTrId(String trId) {
        this.trId = trId;
    }

    public String getTrDate() {
        return trDate;
    }

    public void setTrDate(String trDate) {
        this.trDate = trDate;
    }

    public String getOldBal() {
        return oldBal;
    }

    public void setOldBal(String oldBal) {
        this.oldBal = oldBal;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getNewBal() {
        return newBal;
    }

    public void setNewBal(String newBal) {
        this.newBal = newBal;
    }

    public String getOpRef() {
        return opRef;
    }

    public void setOpRef(String opRef) {
        this.opRef = opRef;
    }

    public String getApiRef() {
        return apiRef;
    }

    public void setApiRef(String apiRef) {
        this.apiRef = apiRef;
    }

    public String getRechargeNum() {
        return rechargeNum;
    }

    public void setRechargeNum(String rechargeNum) {
        this.rechargeNum = rechargeNum;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpImage() {
        return opImage;
    }

    public void setOpImage(String opImage) {
        this.opImage = opImage;
    }

    public String getTrType() {
        return trType;
    }

    public void setTrType(String trType) {
        this.trType = trType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
