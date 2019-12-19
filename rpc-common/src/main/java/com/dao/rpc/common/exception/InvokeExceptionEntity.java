package com.dao.rpc.common.exception;

public class InvokeExceptionEntity extends ExceptionEntity {

    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
