package com.dao.rpc.register.client.config;

import com.dao.rpc.common.exception.DaoException;
import com.dao.rpc.common.protocol.Header;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.common.rpc.Request;
import com.dao.rpc.common.util.IDUtils;
import com.dao.rpc.register.client.conn.DaoConnection;
import com.dao.rpc.register.client.conn.DaoConnectionPool;
import com.dao.rpc.register.client.conn.DaoConnectionPoolFactory;
import com.dao.rpc.register.client.entity.RemoteServices;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DaoServiceProxy implements InvocationHandler {

    private Class interfaceClass;

    private DaoConnectionPoolFactory poolFactory;

    public DaoServiceProxy(Class interfaceClass, DaoConnectionPoolFactory poolFactory) {
        this.interfaceClass = interfaceClass;
        this.poolFactory = poolFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<String> parameterList = new ArrayList<>();
        for (Class<?> parameterType : method.getParameterTypes()) {
            parameterList.add(parameterType.getName());
        }
        String parameterName = String.join(";", parameterList);
        String methodName = method.getName() + "(" + parameterName + ")";
        Request request = new Request();
        String serviceName = interfaceClass.getName();
        request.setServiceName(serviceName);
        request.setMethod(methodName);
        request.setArgs(args);
        request.setRequestId(IDUtils.nextId());
        Message<Request> message = new Message<>();
        Header header = new Header();
        header.setMessageType(MessageType.REQUEST.getValue());
        message.setHeader(header);
        message.setContent(request);

        RemoteAddress remoteAddress = RemoteServices.getRemoteAddress(serviceName);
        if (remoteAddress == null) {
            throw new DaoException("没有找到远程服务: " + serviceName);
        }
        DaoConnectionPool connectionPool = poolFactory.getConnectionPool(remoteAddress);
        DaoConnection connection = connectionPool.getConnection();
        return connection.remoteInvoke(message);
    }
}
