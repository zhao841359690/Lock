package com.zhao.bank.bean;

import com.google.gson.annotations.SerializedName;

public class LockInfoBean {
    private boolean success;
    private int code;
    private String msg;
    private DataBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String bleMac;
        private String imei;
        private String iccId;
        private String registerDate;
        private String destroyStatus;
        private String archStatus;
        private String power;
        private String rootNbLot;
        private String tempNbLot;
        private String gujianbanbenhao;
        private String banbenhao;
        private String rootNbLotNew;
        private String lastConnectTime;
        private String hexUid;
        private int uid;
        @SerializedName("new")
        private boolean newX;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBleMac() {
            return bleMac;
        }

        public void setBleMac(String bleMac) {
            this.bleMac = bleMac;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getIccId() {
            return iccId;
        }

        public void setIccId(String iccId) {
            this.iccId = iccId;
        }

        public String getRegisterDate() {
            return registerDate;
        }

        public void setRegisterDate(String registerDate) {
            this.registerDate = registerDate;
        }

        public String getDestroyStatus() {
            return destroyStatus;
        }

        public void setDestroyStatus(String destroyStatus) {
            this.destroyStatus = destroyStatus;
        }

        public String getArchStatus() {
            return archStatus;
        }

        public void setArchStatus(String archStatus) {
            this.archStatus = archStatus;
        }

        public String getPower() {
            return power;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public String getRootNbLot() {
            return rootNbLot;
        }

        public void setRootNbLot(String rootNbLot) {
            this.rootNbLot = rootNbLot;
        }

        public String getTempNbLot() {
            return tempNbLot;
        }

        public void setTempNbLot(String tempNbLot) {
            this.tempNbLot = tempNbLot;
        }

        public String getGujianbanbenhao() {
            return gujianbanbenhao;
        }

        public void setGujianbanbenhao(String gujianbanbenhao) {
            this.gujianbanbenhao = gujianbanbenhao;
        }

        public String getBanbenhao() {
            return banbenhao;
        }

        public void setBanbenhao(String banbenhao) {
            this.banbenhao = banbenhao;
        }

        public String getRootNbLotNew() {
            return rootNbLotNew;
        }

        public void setRootNbLotNew(String rootNbLotNew) {
            this.rootNbLotNew = rootNbLotNew;
        }

        public String getLastConnectTime() {
            return lastConnectTime;
        }

        public void setLastConnectTime(String lastConnectTime) {
            this.lastConnectTime = lastConnectTime;
        }

        public String getHexUid() {
            return hexUid;
        }

        public void setHexUid(String hexUid) {
            this.hexUid = hexUid;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public boolean isNewX() {
            return newX;
        }

        public void setNewX(boolean newX) {
            this.newX = newX;
        }
    }
}
