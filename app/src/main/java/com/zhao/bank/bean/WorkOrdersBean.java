package com.zhao.bank.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkOrdersBean {
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
        private int total;
        private int size;
        private int totalPage;
        private int page;
        private List<ContentBean> content;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<ContentBean> getContent() {
            return content;
        }

        public void setContent(List<ContentBean> content) {
            this.content = content;
        }

        public static class ContentBean {
            private String id;
            private String createdDate;
            private String lastModifiedDate;
            private String workId;
            private String boxId;
            private String userId;
            private String approveStatus;
            private String operationType;
            private String effectTime;
            private String invalidTime;
            private int expireMinute;
            private boolean effective;
            private LockBean lock;
            @SerializedName("new")
            private boolean newX;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCreatedDate() {
                return createdDate;
            }

            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
            }

            public String getLastModifiedDate() {
                return lastModifiedDate;
            }

            public void setLastModifiedDate(String lastModifiedDate) {
                this.lastModifiedDate = lastModifiedDate;
            }

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

            public boolean isEffective() {
                return effective;
            }

            public void setEffective(boolean effective) {
                this.effective = effective;
            }

            public LockBean getLock() {
                return lock;
            }

            public void setLock(LockBean lock) {
                this.lock = lock;
            }

            public boolean isNewX() {
                return newX;
            }

            public void setNewX(boolean newX) {
                this.newX = newX;
            }

            public static class LockBean {
                private String id;
                private String createdBy;
                private String createdDate;
                private String lastModifiedBy;
                private String lastModifiedDate;
                private String bleMac;
                private String imei;
                private String iccId;
                private String archStatus;
                private String power;
                private String uid;
                private String hexUid;
                @SerializedName("new")
                private boolean newX;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getCreatedBy() {
                    return createdBy;
                }

                public void setCreatedBy(String createdBy) {
                    this.createdBy = createdBy;
                }

                public String getCreatedDate() {
                    return createdDate;
                }

                public void setCreatedDate(String createdDate) {
                    this.createdDate = createdDate;
                }

                public String getLastModifiedBy() {
                    return lastModifiedBy;
                }

                public void setLastModifiedBy(String lastModifiedBy) {
                    this.lastModifiedBy = lastModifiedBy;
                }

                public String getLastModifiedDate() {
                    return lastModifiedDate;
                }

                public void setLastModifiedDate(String lastModifiedDate) {
                    this.lastModifiedDate = lastModifiedDate;
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

                public String getUid() {
                    return uid;
                }

                public void setUid(String uid) {
                    this.uid = uid;
                }

                public String getHexUid() {
                    return hexUid;
                }

                public void setHexUid(String hexUid) {
                    this.hexUid = hexUid;
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
}
