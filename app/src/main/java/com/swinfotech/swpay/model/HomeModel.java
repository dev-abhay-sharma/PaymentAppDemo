package com.swinfotech.swpay.model;


public class HomeModel {
    private String name;
    private int drawableId;
    private Class aclass;
    private boolean isAllReports;

    public HomeModel(String name, int drawableId, Class aclass, boolean isAllReports) {
        this.name = name;
        this.drawableId = drawableId;
        this.aclass = aclass;
        this.isAllReports = isAllReports;
    }

    public boolean isAllReports() {
        return isAllReports;
    }

    public void setAllReports(boolean allReports) {
        isAllReports = allReports;
    }

    public String getName() {
        return name;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public Class getAclass() {
        return aclass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public void setAclass(Class aclass) {
        this.aclass = aclass;
    }

}
