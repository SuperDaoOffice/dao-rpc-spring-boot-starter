package com.dao.rpc.register.client.config;

import com.dao.rpc.register.client.conn.DaoConnectionPoolFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class DaoServiceFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    private DaoConnectionPoolFactory poolFactory;

    public DaoServiceFactory(Class<T> interfaceType , DaoConnectionPoolFactory poolFactory) {
        this.interfaceType = interfaceType;
        this.poolFactory = poolFactory;
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaceType},
                new DaoServiceProxy(interfaceType , poolFactory));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
