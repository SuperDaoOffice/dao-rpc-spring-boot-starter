package com.dao.rpc.common.rpc;

import java.io.Serializable;
import java.util.Objects;

public class ServerAddress implements Serializable {

    private String serverName;

    private RemoteAddress address;

    public ServerAddress() {
    }

    public ServerAddress(String serverName, RemoteAddress address) {
        this.serverName = serverName;
        this.address = address;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public RemoteAddress getAddress() {
        return address;
    }

    public void setAddress(RemoteAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ServerAddress{" +
                "serverName='" + serverName + '\'' +
                ", address=" + address +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerAddress that = (ServerAddress) o;
        return Objects.equals(getServerName(), that.getServerName()) &&
                Objects.equals(getAddress(), that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerName(), getAddress());
    }
}
