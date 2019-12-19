package com.dao.rpc.common.rpc;

import java.util.List;

public class RegisterServer {

    private RemoteAddress address;

    private List<String> serviceNameList;

    public RegisterServer() {
    }

    public RegisterServer(RemoteAddress address, List<String> serviceNameList) {
        this.address = address;
        this.serviceNameList = serviceNameList;
    }

    public RemoteAddress getAddress() {
        return address;
    }

    public void setAddress(RemoteAddress address) {
        this.address = address;
    }

    public List<String> getServiceNameList() {
        return serviceNameList;
    }

    public void setServiceNameList(List<String> serviceNameList) {
        this.serviceNameList = serviceNameList;
    }
}
