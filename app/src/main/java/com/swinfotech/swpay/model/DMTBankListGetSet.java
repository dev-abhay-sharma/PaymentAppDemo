package com.swinfotech.swpay.model;

public class DMTBankListGetSet {
    private String bankName;
    private String ifscCode;

    public DMTBankListGetSet() {
    }

    public DMTBankListGetSet(String bankName, String ifscCode) {
        this.bankName = bankName;
        this.ifscCode = ifscCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }
}
