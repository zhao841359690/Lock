package com.zhao.lock.bean;

public class MyTicketBean {
    private String ticNb;
    private String locNb;
    private boolean isP;

    public String getTicNb() {
        return ticNb;
    }

    public void setTicNb(String ticNb) {
        this.ticNb = ticNb;
    }

    public String getLocNb() {
        return locNb;
    }

    public void setLocNb(String locNb) {
        this.locNb = locNb;
    }

    public boolean isP() {
        return isP;
    }

    public void setP(boolean p) {
        isP = p;
    }
}
