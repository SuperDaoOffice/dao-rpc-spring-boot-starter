package com.dao.rpc.register.client.config;

import com.dao.rpc.common.anno.ServiceProvider;
import com.dao.rpc.common.util.AnnotationScannerUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;
import java.util.List;

public class LoadServiceProxy implements ApplicationContextAware {

    private String packageName;

    public LoadServiceProxy(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        List<Class<?>> classList = AnnotationScannerUtil
                .scanClassesByAnnotation(packageName, ServiceProvider.class);
        for (Class<?> clazz : classList) {
            //生成代理类，并注入到spring容器中
            Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                    new DaoServiceProxy(clazz));
            applicationContext.getAutowireCapableBeanFactory().autowireBean(proxyInstance);
        }
    }
}
