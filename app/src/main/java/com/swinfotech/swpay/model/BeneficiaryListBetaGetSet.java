package com.swinfotech.swpay.model;


public class BeneficiaryListBetaGetSet {
    private String code;
    private String name;
    private String accNo;
    private String accType;
    private String ifsc;
    private String bankName;
    private String mobileNo;
    private String beneId1;
    private String verify;
    private String aadharNo;
    private String panNo;



    public BeneficiaryListBetaGetSet() {
    }

    public BeneficiaryListBetaGetSet(String code, String name, String accNo, String accType, String ifsc, String bankName, String mobileNo, String beneId1, String verify, String aadharNo, String panNo) {
        this.code = code;
        this.name = name;
        this.accNo = accNo;
        this.accType = accType;
        this.ifsc = ifsc;
        this.bankName = bankName;
        this.mobileNo = mobileNo;
        this.beneId1 = beneId1;
        this.verify = verify;
        this.aadharNo = aadharNo;
        this.panNo = panNo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getBeneId1() {
        return beneId1;
    }

    public void setBeneId1(String beneId1) {
        this.beneId1 = beneId1;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }
}
