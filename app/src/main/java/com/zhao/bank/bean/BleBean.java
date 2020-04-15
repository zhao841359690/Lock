package com.zhao.bank.bean;

import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class BleBean {
    private int rssi;
    private BleDevice bleDevice;

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }
}
