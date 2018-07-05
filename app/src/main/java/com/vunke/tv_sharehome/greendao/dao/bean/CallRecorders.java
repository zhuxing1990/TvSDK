package com.vunke.tv_sharehome.greendao.dao.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "CALL_RECORDERS".
 */
public class CallRecorders {

    private Long callId;
    private String contactName;
    private java.util.Date createTime;
    private String callTime;
    private String callType;
    private String callRecordersPhone;

    public CallRecorders() {
    }

    public CallRecorders(Long callId) {
        this.callId = callId;
    }

    public CallRecorders(Long callId, String contactName, java.util.Date createTime, String callTime, String callType, String callRecordersPhone) {
        this.callId = callId;
        this.contactName = contactName;
        this.createTime = createTime;
        this.callTime = callTime;
        this.callType = callType;
        this.callRecordersPhone = callRecordersPhone;
    }

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallRecordersPhone() {
        return callRecordersPhone;
    }

    public void setCallRecordersPhone(String callRecordersPhone) {
        this.callRecordersPhone = callRecordersPhone;
    }

}
