package com.dao.rpc.register.exception;


import static com.dao.rpc.common.exception.ExceptionCode.UNKNOWN_ERROR;

/**
 * @author HuChiHui
 * @date 2019/11/25 下午 17:37
 * @description
 */
public class RegisterServerException extends RuntimeException {

    private Integer code;

    public RegisterServerException(String message) {
        super(message);
        this.code = UNKNOWN_ERROR.getCode();
    }

    public RegisterServerException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public RegisterServerException(String message, Throwable throwable) {
        super(message, throwable);
        this.code = UNKNOWN_ERROR.getCode();
    }

    public RegisterServerException(Integer code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
