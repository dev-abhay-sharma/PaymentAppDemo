package com.swinfotech.swpay.model;

public class AEPSViewBeneficiaryGetSet {
    private String id, benef_name, bank_name, bank_ifsc, account_no, contacts, created_on, benef_id, AEPSBalance, AEPSLimit;

    public AEPSViewBeneficiaryGetSet(String id, String benef_name, String bank_name, String bank_ifsc, String account_no, String contacts, String created_on, String benef_id, String AEPSBalance, String AEPSLimit) {
        this.id = id;
        this.benef_name = benef_name;
        this.bank_name = bank_name;
        this.bank_ifsc = bank_ifsc;
        this.account_no = account_no;
        this.contacts = contacts;
        this.created_on = created_on;
        this.benef_id = benef_id;
        this.AEPSBalance = AEPSBalance;
        this.AEPSLimit = AEPSLimit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBenef_name() {
        return benef_name;
    }

    public void setBenef_name(String benef_name) {
        this.benef_name = benef_name;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_ifsc() {
        return bank_ifsc;
    }

    public void setBank_ifsc(String bank_ifsc) {
        this.bank_ifsc = bank_ifsc;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getBenef_id() {
        return benef_id;
    }

    public void setBenef_id(String benef_id) {
        this.benef_id = benef_id;
    }

    public String getAEPSBalance() {
        return AEPSBalance;
    }

    public void setAEPSBalance(String AEPSBalance) {
        this.AEPSBalance = AEPSBalance;
    }

    public String getAEPSLimit() {
        return AEPSLimit;
    }

    public void setAEPSLimit(String AEPSLimit) {
        this.AEPSLimit = AEPSLimit;
    }

    @Override
    public String toString() {
        return benef_name + " " + bank_name + " " + account_no;
    }
}
