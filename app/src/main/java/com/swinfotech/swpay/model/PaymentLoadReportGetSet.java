package com.swinfotech.swpay.model;

public class PaymentLoadReportGetSet {
    private String oldBal;
    private String netAmt;
    private String newBal;
    private String trDate;
    private String credit;
    private String debit;
    private String orId;
    private String remarks;
    private String debit1;

    public PaymentLoadReportGetSet() {
    }

    public PaymentLoadReportGetSet(String oldBal, String netAmt, String newBal, String trDate, String credit, String debit, String orId, String remarks, String debit1) {
        this.oldBal = oldBal;
        this.netAmt = netAmt;
        this.newBal = newBal;
        this.trDate = trDate;
        this.credit = credit;
        this.debit = debit;
        this.orId = orId;
        this.remarks = remarks;
        this.debit1 = debit1;

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

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
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

    public String getDebit1() {
        return debit1;
    }

    public void setDebit1(String debit1) {
        this.debit1 = debit1;
    }
}
