package com.dao.rpc.common.exception;

import java.io.Serializable;

public class InvokeExceptionEntity extends ExceptionEntity implements Serializable {

    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
