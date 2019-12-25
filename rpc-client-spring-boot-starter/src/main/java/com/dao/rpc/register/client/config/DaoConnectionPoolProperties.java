package com.dao.rpc.register.client.config;

import com.dao.rpc.register.client.conn.DaoConnectionPoolFactory;

/**
 * @author HuChiHui
 * @date 2019/12/24 上午 11:34
 * @description
 */
public class DaoConnectionPoolProperties {

    private DaoConnectionProperties connectionProperties;

    private Integer maxCount = 10;

    private Integer coreCount = 0;

    private Integer initCount = 0;

    private Long maxWaitTime = 6000L;

    private Long maxIdleTime = 6000L;

    public DaoConnectionPoolProperties() {
    }

    public Long getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(Long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public DaoConnectionProperties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(DaoConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public Long getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(Long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public Integer getCoreCount() {
        return coreCount;
    }

    public void setCoreCount(Integer coreCount) {
        this.coreCount = coreCount;
    }

    public Integer getInitCount() {
        return initCount;
    }

    public void setInitCount(Integer initCount) {
        this.initCount = initCount;
    }

    public DaoConnectionPoolFactory retrievePoolFactory() {
        return new DaoConnectionPoolFactory(this);
    }
}
