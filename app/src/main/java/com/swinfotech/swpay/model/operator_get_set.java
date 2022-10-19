package com.swinfotech.swpay.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class operator_get_set {
    private String opName;
    private String opId;
    private String image;

    public operator_get_set() {
    }

    public operator_get_set(String opName, String opId, String image) {
        this.opName = opName;
        this.opId = opId;
        this.image = image;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @NonNull
    @Override
    public String toString() {
        return opName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
//        return super.equals(obj);
        if (obj instanceof operator_get_set) {
            operator_get_set c = (operator_get_set) obj;
            if (c.getOpName().equals(opName) && c.getOpId().equals(opId)) return true;
        }
        return false;
    }
}
