package com.dao.rpc.common.entity;

import java.io.Serializable;

public class ConnRequest implements Serializable {

    private String serverName;

    private String ip;

    private Integer port;

    public ConnRequest() {
    }

    public ConnRequest(String serverName, String ip, Integer port) {
        this.serverName = serverName;
        this.ip = ip;
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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
}
