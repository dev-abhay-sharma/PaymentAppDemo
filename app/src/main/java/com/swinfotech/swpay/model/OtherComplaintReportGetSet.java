package com.swinfotech.swpay.model;

public class OtherComplaintReportGetSet {
    private String id;
    private String supportType;
    private String message;
    private String status;
    private String addDate;
    private String response;
    private String replyDate;

    public OtherComplaintReportGetSet() {
    }

    public OtherComplaintReportGetSet(String id, String supportType, String message, String status, String addDate, String response, String replyDate) {
        this.id = id;
        this.supportType = supportType;
        this.message = message;
        this.status = status;
        this.addDate = addDate;
        this.response = response;
        this.replyDate = replyDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSupportType() {
        return supportType;
    }

    public void setSupportType(String supportType) {
        this.supportType = supportType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }
}
