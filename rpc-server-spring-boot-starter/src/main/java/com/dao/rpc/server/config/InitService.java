package com.dao.rpc.server.config;

import com.dao.rpc.server.anno.ServiceExport;
import com.dao.rpc.server.anno.ServiceProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InitService implements ApplicationContextAware {

    private Map<String, ActionMethod> exportedMethodMap = new HashMap<>(16);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loadExportService(applicationContext);

    }

    private void loadExportService(ApplicationContext applicationContext) {
        for (String beanName : applicationContext.getBeanNamesForAnnotation(ServiceExport.class)) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = bean.getClass();
            for (Method method : beanClass.getDeclaredMethods()) {
                for (Class<?> parentInterface : beanClass.getInterfaces()) {
                    boolean isProviderInterface = parentInterface.isAnnotationPresent(ServiceProvider.class);
                    if (!isProviderInterface) {
                        continue;
                    }
                    String serviceName = parentInterface.getName();
                    for (Method interfaceMethod : parentInterface.getDeclaredMethods()) {
                        if (methodEquals(interfaceMethod, method)) {
                            ActionMethod actionMethod = new ActionMethod(bean, method);
                            List<String> parameterList = new ArrayList<>();
                            for (Class<?> parameterType : interfaceMethod.getParameterTypes()) {
                                parameterList.add(parameterType.getName());
                            }
                            String parameterName = String.join(";", parameterList);
                            String url = serviceName + ":" + interfaceMethod.getName() + "(" + parameterName + ")";
                            exportedMethodMap.put(url,actionMethod);
                        }
                    }
                }
            }

        }

    }

    public boolean methodEquals(Method interfaceMethod, Method method) {
        if (stringEquals(method.getName(), interfaceMethod.getName())) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?>[] interfaceMethodParameterTypes = interfaceMethod.getParameterTypes();
            if (parameterTypes.length != interfaceMethodParameterTypes.length) {
                return false;
            }
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!stringEquals(parameterTypes[i].getName(), interfaceMethodParameterTypes[i].getName())) {
                    break;
                }
                return true;
            }
        }
        return false;
    }

    private boolean stringEquals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }
}
