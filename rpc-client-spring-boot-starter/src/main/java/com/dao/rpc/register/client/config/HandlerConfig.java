package com.dao.rpc.register.client.config;

import com.dao.rpc.common.rpc.RegisterProperties;
import com.dao.rpc.register.client.handler.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RegisterProperties.class})
public class HandlerConfig {


    @Bean
    public DaoConnReqHandler daoConnReqHandler() {
        return new DaoConnReqHandler();
    }

    @Bean
    public DaoConnResHandler daoConnResHandler() {
        return new DaoConnResHandler();
    }

    @Bean
    public DaoHeartbeatResHandler daoHeartbeatResHandler() {
        return new DaoHeartbeatResHandler();
    }

    @Bean
    public DaoAddServerHandler daoAddServerHandler() {
        return new DaoAddServerHandler();
    }

    @Bean
    public DaoDeleteServerHandler daoDeleteServerHandler() {
        return new DaoDeleteServerHandler();
    }

    @Bean
    public DaoClientExceptionHandler daoClientExceptionHandler() {
        return new DaoClientExceptionHandler();
    }

    @Bean
    public DaoHeartbeatClient daoHeartbeatClient(DaoConnReqHandler reqHandler,
                                                 DaoConnResHandler resHandler, DaoHeartbeatResHandler heartbeatResHandler,
                                                 DaoAddServerHandler addServerHandler, DaoDeleteServerHandler deleteServerHandler,
                                                 DaoClientExceptionHandler exceptionHandler, RegisterProperties registerProperties
    ) {
        return new DaoHeartbeatClient(reqHandler, resHandler, heartbeatResHandler, addServerHandler,
                deleteServerHandler, exceptionHandler, registerProperties);
    }
}
