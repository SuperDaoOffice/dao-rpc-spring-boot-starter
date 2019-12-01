package com.dao.rpc.common.exception;

/**
 * @author jay
 * @date 2018/5/15
 */
public class JsonOperationException extends RuntimeException {
    private String message;

    public JsonOperationException(String message) {
        this.message = message;
    }

    public JsonOperationException() {
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
