package com.dao.rpc.common.rpc;

import java.io.Serializable;

public class Request implements Serializable {

    private String requestId;

    private String serviceName;

    private String method;

    private Object[] args;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String retrieveCompletedMethodUrl() {
        return serviceName + ":" + method;
    }
}
