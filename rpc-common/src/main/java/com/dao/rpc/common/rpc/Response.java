package com.dao.rpc.common.rpc;

import java.io.Serializable;

public class Response<T> implements Serializable {

    private String requestId;

    private T response;

    public Response() {
    }

    public Response(String requestId, T response) {
        this.requestId = requestId;
        this.response = response;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
