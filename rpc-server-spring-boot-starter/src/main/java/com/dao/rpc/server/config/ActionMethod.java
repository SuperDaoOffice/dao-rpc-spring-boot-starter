package com.dao.rpc.server.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActionMethod {

    private Object target;

    private Method method;

    public ActionMethod(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public Object call(Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, args);
    }
}
