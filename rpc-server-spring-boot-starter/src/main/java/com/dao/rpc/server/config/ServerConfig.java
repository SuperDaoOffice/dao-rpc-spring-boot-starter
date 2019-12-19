package com.dao.rpc.server.config;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
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
    public DaoEncoder daoEncoder() {
        return new DaoEncoder();
    }

    @Bean
    public DaoDecoder daoDecoder() {
        return new DaoDecoder();
    }

    @Bean
    public HeartbeatResHandler heartbeatResHandler() {
        return new HeartbeatResHandler();
    }

    @Bean
    public InitService initService(RegisterProperties registerProperties, DaoEncoder daoEncoder, DaoDecoder daoDecoder,
                                   HeartbeatResHandler heartbeatResHandler,
                                   @Value("server.port") Integer port) {
        return new InitService(registerProperties, port, daoEncoder, daoDecoder, heartbeatResHandler);
    }
}
