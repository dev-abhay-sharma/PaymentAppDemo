package com.swinfotech.swpay.model;

public class CreditBalUsersListGetSet {
    private String userMobile, id;
    private String userName;
    private String amount;

    public CreditBalUsersListGetSet() {
    }

    public CreditBalUsersListGetSet(String userMobile, String userName, String amount) {
        this.userMobile = userMobile;
        this.userName = userName;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
