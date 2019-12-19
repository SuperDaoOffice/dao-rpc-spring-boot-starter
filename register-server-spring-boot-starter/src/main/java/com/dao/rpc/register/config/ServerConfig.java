package com.dao.rpc.register.config;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
import com.dao.rpc.register.handler.ConnResHandler;
import com.dao.rpc.register.handler.RegisterExceptionHandler;
import com.dao.rpc.register.handler.RegisterServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ServerProperties.class)
public class ServerConfig {



    @Bean
    public ConnResHandler daoConnResHandler() {
        return new ConnResHandler();
    }

    @Bean
    public RegisterExceptionHandler daoServerExceptionHandler() {
        return new RegisterExceptionHandler();
    }

    @Bean
    public RegisterServer registerServer(ConnResHandler daoConnResHandler, RegisterExceptionHandler exceptionHandler,
                                         ServerProperties serverProperties, @Value("${server.port}") Integer port) {
        return new RegisterServer( daoConnResHandler, exceptionHandler, serverProperties, port);
    }
}
