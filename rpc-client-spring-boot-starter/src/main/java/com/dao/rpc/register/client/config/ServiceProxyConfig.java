package com.dao.rpc.register.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ServiceProxyConfig {

    @Bean
    public ServiceBeanDefinitionRegistry serviceBeanDefinitionRegistry(Environment env) {
        String packageName = env.getProperty("dao.rpc.client.packageName");
        return new ServiceBeanDefinitionRegistry(packageName);
    }
}
