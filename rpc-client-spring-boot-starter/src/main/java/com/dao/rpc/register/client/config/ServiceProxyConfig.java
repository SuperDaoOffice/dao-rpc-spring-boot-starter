package com.dao.rpc.register.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class ServiceProxyConfig {

    @Bean
    public LoadServiceProxy loadServiceProxy(ClientProperties clientProperties) {
        return new LoadServiceProxy(clientProperties.getPackageName());
    }
}
