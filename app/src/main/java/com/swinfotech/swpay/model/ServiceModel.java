package com.swinfotech.swpay.model;

public class ServiceModel {
    private String id;
    private String status;

    public ServiceModel() {
    }

    public ServiceModel(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
