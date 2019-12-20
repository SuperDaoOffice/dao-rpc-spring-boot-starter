package com.dao.rpc.register.config;

import com.dao.rpc.register.handler.ConnResHandler;
import com.dao.rpc.register.handler.RegisterExceptionHandler;
import com.dao.rpc.register.handler.RegisterServer;
import com.dao.rpc.register.handler.RegisterServerHandler;
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
    public RegisterServerHandler registerServerHandler() {
        return new RegisterServerHandler();
    }

    @Bean
    public RegisterServer registerServer(ConnResHandler daoConnResHandler, RegisterExceptionHandler exceptionHandler,
                                         RegisterServerHandler registerServerHandler,
                                         ServerProperties serverProperties, @Value("${server.port}") Integer port) {
        return new RegisterServer(daoConnResHandler, exceptionHandler , registerServerHandler, serverProperties, port);
    }
}
