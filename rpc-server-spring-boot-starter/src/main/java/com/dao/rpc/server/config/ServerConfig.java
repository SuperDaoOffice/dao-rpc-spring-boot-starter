package com.dao.rpc.server.config;

import com.dao.rpc.common.rpc.RegisterProperties;
import com.dao.rpc.server.handler.HeartbeatResHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RegisterProperties.class)
public class ServerConfig {


    @Bean
    public HeartbeatResHandler heartbeatResHandler() {
        return new HeartbeatResHandler();
    }

    @Bean
    public InitService initService(RegisterProperties registerProperties,HeartbeatResHandler heartbeatResHandler,
                                   @Value("${server.port}") Integer port) {
        return new InitService(registerProperties, port, heartbeatResHandler);
    }
}
