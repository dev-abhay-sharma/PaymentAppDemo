package com.swinfotech.swpay.model;

public class FULLTTFragmentGetSet {
    private String price;
    private String details;

    public FULLTTFragmentGetSet() {
    }

    public FULLTTFragmentGetSet(String price, String details) {
        this.price = price;
        this.details = details;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
