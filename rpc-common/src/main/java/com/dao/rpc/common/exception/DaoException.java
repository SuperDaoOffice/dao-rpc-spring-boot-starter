package com.dao.rpc.common.exception;

/**
 * @author HuChiHui
 * @date 2019/11/25 下午 17:37
 * @description
 */
public class DaoException extends RuntimeException {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
