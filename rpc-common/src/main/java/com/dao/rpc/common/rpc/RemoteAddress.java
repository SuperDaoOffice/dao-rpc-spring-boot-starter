package com.dao.rpc.common.rpc;

import java.io.Serializable;
import java.util.Objects;

public class RemoteAddress implements Serializable {

    private String ip;

    private Integer port;

    public RemoteAddress() {
    }

    public RemoteAddress(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ServerAddress{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteAddress that = (RemoteAddress) o;
        return Objects.equals(getIp(), that.getIp()) &&
                Objects.equals(getPort(), that.getPort());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIp(), getPort());
    }
}
