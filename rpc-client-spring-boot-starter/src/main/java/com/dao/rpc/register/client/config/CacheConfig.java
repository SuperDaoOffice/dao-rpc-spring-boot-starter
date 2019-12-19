package com.dao.rpc.register.client.config;

import com.dao.rpc.register.client.entity.DaoResponseFuture;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, DaoResponseFuture> responseCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(8, TimeUnit.SECONDS).build();
    }

}
