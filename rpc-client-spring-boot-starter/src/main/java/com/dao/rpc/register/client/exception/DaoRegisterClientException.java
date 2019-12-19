package com.dao.rpc.register.client.exception;

public class DaoRegisterClientException extends RuntimeException {

    public DaoRegisterClientException(String message) {
        super(message);
    }

    public DaoRegisterClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
