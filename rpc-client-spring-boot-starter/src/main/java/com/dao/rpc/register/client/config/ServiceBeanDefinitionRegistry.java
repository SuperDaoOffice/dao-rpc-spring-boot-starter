package com.dao.rpc.register.client.config;

import com.dao.rpc.common.anno.ServiceProvider;
import com.dao.rpc.common.util.AnnotationScannerUtil;
import com.dao.rpc.register.client.conn.DaoConnectionPoolFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.List;

public class ServiceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {

    private String packageName;

    private DaoConnectionPoolFactory poolFactory;


    public ServiceBeanDefinitionRegistry(String packageName, DaoConnectionPoolProperties poolProperties) {
        this.packageName = packageName;
        this.poolFactory = poolProperties.retrievePoolFactory();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<Class<?>> classList = AnnotationScannerUtil
                .scanClassesByAnnotation(packageName, ServiceProvider.class);
        for (Class<?> beanClazz : classList) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            ConstructorArgumentValues argumentValues = definition.getConstructorArgumentValues();
            argumentValues.addGenericArgumentValue(beanClazz);
            argumentValues.addGenericArgumentValue(poolFactory);
            definition.setBeanClass(DaoServiceFactory.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(beanClazz.getSimpleName(), definition);

        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

}
