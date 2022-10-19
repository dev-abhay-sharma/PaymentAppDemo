package com.swinfotech.swpay.model;

public class RaiseComplaintGetSet {
    private String subject;

    public RaiseComplaintGetSet() {
    }

    public RaiseComplaintGetSet(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return subject;
    }
}
