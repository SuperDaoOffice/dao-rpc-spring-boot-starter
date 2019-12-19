package com.dao.rpc.common.exception;

public enum ExceptionCode {

    UNKNOWN_ERROR(0),

    CONN_ERROR(100),

    INVOKE_ERROR(101);

    private Integer code;

    ExceptionCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
