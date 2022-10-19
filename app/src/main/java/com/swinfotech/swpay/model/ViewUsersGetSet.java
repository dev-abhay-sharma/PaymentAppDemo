package com.swinfotech.swpay.model;

public class ViewUsersGetSet {
    private String userId;
    private String userName;
    private String userMobile;
    private String userEmail;
    private String userStatus;
    private String userFirmName;
    private String balanceAmt;


    public ViewUsersGetSet() {
    }


    public ViewUsersGetSet(String userId, String userName, String userMobile, String userEmail, String userStatus, String userFirmName, String balanceAmt) {
        this.userId = userId;
        this.userName = userName;
        this.userMobile = userMobile;
        this.userEmail = userEmail;
        this.userStatus = userStatus;
        this.userFirmName = userFirmName;
        this.balanceAmt = balanceAmt;
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

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserFirmName() {
        return userFirmName;
    }

    public void setUserFirmName(String userFirmName) {
        this.userFirmName = userFirmName;
    }

    public String getBalanceAmt() {
        return balanceAmt;
    }

    public void setBalanceAmt(String balanceAmt) {
        this.balanceAmt = balanceAmt;
    }
}
