package com.dao.rpc.common.exception;

public class ExceptionEntity {

    private Integer code;

    private String msg;

    public ExceptionEntity() {
    }

    public ExceptionEntity(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
