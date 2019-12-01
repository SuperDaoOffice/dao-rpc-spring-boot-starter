package com.dao.rpc.common.entity;

public enum MessageType {

    REQUEST((byte) 1),

    RESPONSE((byte) 2),

    HEART_BEAT_REQ((byte) 3),

    HEART_BEAT_RES((byte) 4),

    EXCEPTION((byte) 5),

    CONN_REQ((byte) 6),

    CONN_RES((byte) 7);

    private byte value;

    MessageType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
