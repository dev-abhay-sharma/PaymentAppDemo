package com.swinfotech.swpay.model;

public class StatementReportGetSet {
    private String oldBal;
    private String netAmt;
    private String newBal;
    private String trDate;
    private String txnType;
    private String orId, crdr_type, pay_ref_id, operator_name, service_name;
    private String remarks;
    private String status;
    private String retailerId;
    private String retailerName;
    private String retailerStatus;
    private String retailerRechargeNo;
    private String retailerAmount;
    private String dtCommAmt;
    private String dtChargeAmt;
    private String mdtCommAmt;
    private String mdtChargeAmt;
    private String dtMDTOp;


    public StatementReportGetSet() {
    }

    public StatementReportGetSet(String oldBal, String netAmt, String newBal, String trDate, String txnType, String orId, String remarks, String status, String retailerId, String retailerName, String retailerStatus, String retailerRechargeNo, String retailerAmount, String dtCommAmt, String dtChargeAmt, String mdtCommAmt, String mdtChargeAmt, String dtMDTOp) {
        this.oldBal = oldBal;
        this.netAmt = netAmt;
        this.newBal = newBal;
        this.trDate = trDate;
        this.txnType = txnType;
        this.orId = orId;
        this.remarks = remarks;
        this.status = status;
        this.retailerId = retailerId;
        this.retailerName = retailerName;
        this.retailerStatus = retailerStatus;
        this.retailerRechargeNo = retailerRechargeNo;
        this.retailerAmount = retailerAmount;
        this.dtCommAmt = dtCommAmt;
        this.dtChargeAmt = dtChargeAmt;
        this.mdtCommAmt = mdtCommAmt;
        this.mdtChargeAmt = mdtChargeAmt;
        this.dtMDTOp = dtMDTOp;
    }

    public String getOperator_name() {
        return operator_name;
    }

    public void setOperator_name(String operator_name) {
        this.operator_name = operator_name;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getPay_ref_id() {
        return pay_ref_id;
    }

    public void setPay_ref_id(String pay_ref_id) {
        this.pay_ref_id = pay_ref_id;
    }

    public String getCrdr_type() {
        return crdr_type;
    }

    public void setCrdr_type(String crdr_type) {
        this.crdr_type = crdr_type;
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

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getOrId() {
        return orId;
    }

    public void setOrId(String orId) {
        this.orId = orId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getRetailerStatus() {
        return retailerStatus;
    }

    public void setRetailerStatus(String retailerStatus) {
        this.retailerStatus = retailerStatus;
    }

    public String getRetailerRechargeNo() {
        return retailerRechargeNo;
    }

    public void setRetailerRechargeNo(String retailerRechargeNo) {
        this.retailerRechargeNo = retailerRechargeNo;
    }

    public String getRetailerAmount() {
        return retailerAmount;
    }

    public void setRetailerAmount(String retailerAmount) {
        this.retailerAmount = retailerAmount;
    }

    public String getDtCommAmt() {
        return dtCommAmt;
    }

    public void setDtCommAmt(String dtCommAmt) {
        this.dtCommAmt = dtCommAmt;
    }

    public String getDtChargeAmt() {
        return dtChargeAmt;
    }

    public void setDtChargeAmt(String dtChargeAmt) {
        this.dtChargeAmt = dtChargeAmt;
    }

    public String getMdtCommAmt() {
        return mdtCommAmt;
    }

    public void setMdtCommAmt(String mdtCommAmt) {
        this.mdtCommAmt = mdtCommAmt;
    }

    public String getMdtChargeAmt() {
        return mdtChargeAmt;
    }

    public void setMdtChargeAmt(String mdtChargeAmt) {
        this.mdtChargeAmt = mdtChargeAmt;
    }

    public String getDtMDTOp() {
        return dtMDTOp;
    }

    public void setDtMDTOp(String dtMDTOp) {
        this.dtMDTOp = dtMDTOp;
    }
}
