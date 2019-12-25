package com.dao.rpc.register.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
public class ServiceProxyConfig {


    @Bean
    public ServiceBeanDefinitionRegistry serviceBeanDefinitionRegistry(Environment env) {
        String packageName = env.getProperty("dao.rpc.client.packageName");
        DaoConnectionPoolProperties properties = new DaoConnectionPoolProperties();
        String maxCountStr = env.getProperty("dao.rpc.pool.maxCount");
        if (!StringUtils.isEmpty(maxCountStr)) {
            properties.setMaxCount(Integer.valueOf(maxCountStr));
        }
        String coreCountStr = env.getProperty("dao.rpc.pool.coreCount");
        if (!StringUtils.isEmpty(coreCountStr)) {
            properties.setCoreCount(Integer.valueOf(coreCountStr));
        }
        String initCountStr = env.getProperty("dao.rpc.pool.initCount");
        if (!StringUtils.isEmpty(initCountStr)) {
            properties.setInitCount(Integer.valueOf(initCountStr));
        }
        String maxWaitTimeStr = env.getProperty("dao.rpc.conn.maxWaitTime");
        if (!StringUtils.isEmpty(maxWaitTimeStr)) {
            properties.setMaxWaitTime(Long.valueOf(maxWaitTimeStr));
        }
        String maxIdleTimeStr = env.getProperty("dao.rpc.conn.maxIdleTime");
        if (!StringUtils.isEmpty(maxIdleTimeStr)) {
            properties.setMaxIdleTime(Long.valueOf(maxIdleTimeStr));
        }
        DaoConnectionProperties connectionProperties = new DaoConnectionProperties();
        String connTimeoutStr = env.getProperty("dao.rpc.conn.connTimeout");
        if (!StringUtils.isEmpty(connTimeoutStr)) {
            connectionProperties.setConnTimeout(Integer.valueOf(connTimeoutStr));
        }
        String invokeTimeoutStr = env.getProperty("dao.rpc.conn.invokeTimeout");
        if (!StringUtils.isEmpty(invokeTimeoutStr)) {
            connectionProperties.setInvokeTimeout(Integer.valueOf(invokeTimeoutStr));
        }
        String reconnectionCountStr = env.getProperty("dao.rpc.conn.reconnectCount");
        if (!StringUtils.isEmpty(reconnectionCountStr)) {
            connectionProperties.setReconnectCount(Integer.valueOf(reconnectionCountStr));
        }
        properties.setConnectionProperties(connectionProperties);
        return new ServiceBeanDefinitionRegistry(packageName, properties);
    }
}
