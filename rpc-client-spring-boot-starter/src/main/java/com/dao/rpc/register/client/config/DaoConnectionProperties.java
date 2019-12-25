package com.dao.rpc.register.client.config;

/**
 * @author HuChiHui
 * @date 2019/12/24 上午 10:33
 * @description
 */
public class DaoConnectionProperties {

    private Integer connTimeout = 10000;

    private Integer invokeTimeout = 10000;

    private Integer reconnectCount = 3;

    public Integer getConnTimeout() {
        return connTimeout;
    }

    public void setConnTimeout(Integer connTimeout) {
        this.connTimeout = connTimeout;
    }

    public Integer getInvokeTimeout() {
        return invokeTimeout;
    }

    public void setInvokeTimeout(Integer invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
    }

    public Integer getReconnectCount() {
        return reconnectCount;
    }

    public void setReconnectCount(Integer reconnectCount) {
        this.reconnectCount = reconnectCount;
    }
}
