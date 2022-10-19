package com.swinfotech.swpay.model;

public class ManagePaymentRequestGetSet {
    private String id;
    private String amount;
    private String tr_date;
    private String tr_time;
    private String tr_no;
    private String bankName;
    private String branchName;
    private String chequeNo;
    private String utrNo;
    private String paymentMode;
    private String cashType;
    private String remarks;
    private String status;
    private String requestTime;
    private String responseTime;
    private String userId;
    private String userName;
    private String userBal;
    private String userMobile;
    private String u_Id;
    private String userType;

    public ManagePaymentRequestGetSet() {
    }

    public ManagePaymentRequestGetSet(String id, String amount, String tr_date, String tr_time, String tr_no, String bankName, String branchName, String chequeNo, String utrNo, String paymentMode, String cashType, String remarks, String status, String requestTime, String responseTime, String userId, String userName, String userBal, String userMobile, String u_Id, String userType) {
        this.id = id;
        this.amount = amount;
        this.tr_date = tr_date;
        this.tr_time = tr_time;
        this.tr_no = tr_no;
        this.bankName = bankName;
        this.branchName = branchName;
        this.chequeNo = chequeNo;
        this.utrNo = utrNo;
        this.paymentMode = paymentMode;
        this.cashType = cashType;
        this.remarks = remarks;
        this.status = status;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
        this.userId = userId;
        this.userName = userName;
        this.userBal = userBal;
        this.userMobile = userMobile;
        this.u_Id = u_Id;
        this.userType = userType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTr_date() {
        return tr_date;
    }

    public void setTr_date(String tr_date) {
        this.tr_date = tr_date;
    }

    public String getTr_time() {
        return tr_time;
    }

    public void setTr_time(String tr_time) {
        this.tr_time = tr_time;
    }

    public String getTr_no() {
        return tr_no;
    }

    public void setTr_no(String tr_no) {
        this.tr_no = tr_no;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public String getUtrNo() {
        return utrNo;
    }

    public void setUtrNo(String utrNo) {
        this.utrNo = utrNo;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getCashType() {
        return cashType;
    }

    public void setCashType(String cashType) {
        this.cashType = cashType;
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

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBal() {
        return userBal;
    }

    public void setUserBal(String userBal) {
        this.userBal = userBal;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getU_Id() {
        return u_Id;
    }

    public void setU_Id(String u_Id) {
        this.u_Id = u_Id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
