package com.dao.rpc.register.client.config;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class DaoServiceFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    public DaoServiceFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaceType},
                new DaoServiceProxy(interfaceType));
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
