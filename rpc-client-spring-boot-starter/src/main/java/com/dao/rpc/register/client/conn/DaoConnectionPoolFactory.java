package com.dao.rpc.register.client.conn;

import com.dao.rpc.common.exception.DaoException;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.register.client.config.DaoConnectionPoolProperties;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DaoConnectionPoolFactory {

    private DaoConnectionPoolProperties poolProperties;

    private static final Cache<RemoteAddress, DaoConnectionPool> connectionCache =
            CacheBuilder.newBuilder()
                    .maximumSize(100)
                    .expireAfterAccess(1, TimeUnit.HOURS).build();

    public DaoConnectionPoolFactory(DaoConnectionPoolProperties poolProperties) {
        this.poolProperties = poolProperties;
    }

    public DaoConnectionPool getConnectionPool(RemoteAddress remoteAddress) {
        try {
            return connectionCache.get(remoteAddress, () -> {
                return new DaoConnectionPool(poolProperties, remoteAddress);
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new DaoException("创建连接失败...");
        }
    }
}
