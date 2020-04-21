package com.zhao.bank.bean;

import com.google.gson.annotations.SerializedName;

public class WorkOrderBean {
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
        private String workId;
        private String boxId;
        private String userId;
        private String approveUserId;
        private String approveStatus;
        private String operationType;
        private String effectTime;
        private String invalidTime;
        private int expireMinute;
        private LockBean lock;

        public String getWorkId() {
            return workId;
        }

        public void setWorkId(String workId) {
            this.workId = workId;
        }

        public String getBoxId() {
            return boxId;
        }

        public void setBoxId(String boxId) {
            this.boxId = boxId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getApproveUserId() {
            return approveUserId;
        }

        public void setApproveUserId(String approveUserId) {
            this.approveUserId = approveUserId;
        }

        public String getApproveStatus() {
            return approveStatus;
        }

        public void setApproveStatus(String approveStatus) {
            this.approveStatus = approveStatus;
        }

        public String getOperationType() {
            return operationType;
        }

        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }

        public String getEffectTime() {
            return effectTime;
        }

        public void setEffectTime(String effectTime) {
            this.effectTime = effectTime;
        }

        public String getInvalidTime() {
            return invalidTime;
        }

        public void setInvalidTime(String invalidTime) {
            this.invalidTime = invalidTime;
        }

        public int getExpireMinute() {
            return expireMinute;
        }

        public void setExpireMinute(int expireMinute) {
            this.expireMinute = expireMinute;
        }

        public LockBean getLock() {
            return lock;
        }

        public void setLock(LockBean lock) {
            this.lock = lock;
        }

        public static class LockBean {
            private String id;
            private String bleMac;
            private String imei;
            private String iccId;
            private String rootNbLot;
            private String uid;
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

            public String getRootNbLot() {
                return rootNbLot;
            }

            public void setRootNbLot(String rootNbLot) {
                this.rootNbLot = rootNbLot;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
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
}
