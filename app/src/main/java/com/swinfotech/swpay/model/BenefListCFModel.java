package com.swinfotech.swpay.model;

public class BenefListCFModel {
    String benef_id,benef_name,benef_mobile,bank_ac,ifsc,address1,pincode,bank_name;
    boolean isVerified;

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getBenef_id() {
        return benef_id;
    }

    public void setBenef_id(String benef_id) {
        this.benef_id = benef_id;
    }

    public String getBenef_name() {
        return benef_name;
    }

    public void setBenef_name(String benef_name) {
        this.benef_name = benef_name;
    }

    public String getBenef_mobile() {
        return benef_mobile;
    }

    public void setBenef_mobile(String benef_mobile) {
        this.benef_mobile = benef_mobile;
    }

    public String getBank_ac() {
        return bank_ac;
    }

    public void setBank_ac(String bank_ac) {
        this.bank_ac = bank_ac;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }
}
