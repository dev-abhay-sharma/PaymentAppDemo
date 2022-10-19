package com.swinfotech.swpay.model;

public class Scheme {
    private String scheme_names, scheme_id;

    public Scheme(String scheme_names, String scheme_id) {
        this.scheme_names = scheme_names;
        this.scheme_id = scheme_id;
    }

    public String getScheme_names() {
        return scheme_names;
    }

    public void setScheme_names(String scheme_names) {
        this.scheme_names = scheme_names;
    }

    public String getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(String scheme_id) {
        this.scheme_id = scheme_id;
    }

    @Override
    public String toString() {
        return scheme_names;
    }
}
