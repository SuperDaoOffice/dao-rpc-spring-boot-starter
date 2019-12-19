package com.dao.rpc.register.config;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
import com.dao.rpc.register.handler.ConnResHandler;
import com.dao.rpc.register.handler.RegisterServer;
import com.dao.rpc.register.handler.RegisterExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ServerProperties.class)
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
    public ConnResHandler daoConnResHandler() {
        return new ConnResHandler();
    }

    @Bean
    public RegisterExceptionHandler daoServerExceptionHandler() {
        return new RegisterExceptionHandler();
    }

    @Bean
    public RegisterServer daoServer(DaoEncoder daoEncoder, DaoDecoder daoDecoder, ServerProperties serverProperties,
                                    ConnResHandler daoConnResHandler, RegisterExceptionHandler exceptionHandler) {
        return new RegisterServer(daoEncoder, daoDecoder, serverProperties, daoConnResHandler, exceptionHandler);
    }
}
