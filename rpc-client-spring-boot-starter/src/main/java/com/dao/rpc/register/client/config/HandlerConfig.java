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
    public DaoHeartbeatClient daoHeartbeatClient( RegisterProperties registerProperties) {
        return new DaoHeartbeatClient( registerProperties);
    }


}
