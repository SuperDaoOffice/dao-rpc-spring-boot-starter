package com.dao.rpc.server.config;

import com.dao.rpc.common.anno.ServiceExport;
import com.dao.rpc.common.anno.ServiceProvider;
import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
import com.dao.rpc.common.rpc.RegisterProperties;
import com.dao.rpc.server.handler.HeartbeatResHandler;
import com.dao.rpc.server.handler.InvokeMethodHandler;
import com.dao.rpc.server.handler.RegisterServerHandler;
import com.dao.rpc.server.handler.ServerExceptionHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class InitService implements ApplicationContextAware {

    private static Map<String, ActionMethod> exportedMethodMap = new HashMap<>(16);

    private RegisterProperties registerProperties;

    private HeartbeatResHandler heartbeatResHandler;

    private Integer port;

    public InitService(RegisterProperties registerProperties, Integer port,
                       HeartbeatResHandler heartbeatResHandler) {
        this.registerProperties = registerProperties;
        this.port = port;
        this.heartbeatResHandler = heartbeatResHandler;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        List<String> exportServiceList = loadExportService(applicationContext);
        register(exportServiceList, port);
        listenConn(port);

    }

    /**
     * 注册暴露的服务
     *
     * @param exportServiceList
     */
    private void register(List<String> exportServiceList, Integer port) {
        RegisterServerHandler registerServerHandler = new RegisterServerHandler(exportServiceList, port);
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2,
                                    4, 0, 0))
                                    .addLast(new DaoDecoder())
                                    .addLast(new DaoEncoder())
                                    .addLast(registerServerHandler)
                                    .addLast(heartbeatResHandler);
                        }
                    });
            ChannelFuture future = b.connect(registerProperties.getHost(),
                    registerProperties.getPort()).sync();
            future.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("连接关闭......");
                    group.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取ActionMethod
     *
     * @param url
     * @return
     */
    public static ActionMethod retrieveMethod(String url) {
        return exportedMethodMap.get(url);
    }

    /**
     * 监听连接
     */
    private void listenConn(Integer port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2,
                                            4, 0, 0))
                                    .addLast(new DaoDecoder())
                                    .addLast(new DaoEncoder())
                                    .addLast(new InvokeMethodHandler())
                                    .addLast(new ServerExceptionHandler());

                        }
                    });
            ChannelFuture future = b.bind(port).sync();
            future.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("连接关闭......");
                    bossGroup.shutdownGracefully();
                    workGroup.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载暴露的服务
     *
     * @param applicationContext
     * @return
     */
    private List<String> loadExportService(ApplicationContext applicationContext) {
        Set<String> serverNameSet = new HashSet<>();
        for (String beanName : applicationContext.getBeanNamesForAnnotation(ServiceExport.class)) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = bean.getClass();
            Map<Method, String> methodMap = new HashMap<>(32);
            for (Class<?> parentInterface : beanClass.getInterfaces()) {
                boolean isProviderInterface = parentInterface.isAnnotationPresent(ServiceProvider.class);
                if (!isProviderInterface) {
                    continue;
                }
                String serviceName = parentInterface.getName();
                serverNameSet.add(serviceName);
                for (Method method : parentInterface.getDeclaredMethods()) {
                    methodMap.put(method, serviceName);
                }
            }
            Set<Method> methodSet = methodMap.keySet();
            for (Method method : beanClass.getDeclaredMethods()) {
                List<Method> interfaceMethods = methodSet.stream().filter(m -> methodEquals(m, method))
                        .collect(Collectors.toList());
                if (interfaceMethods.isEmpty()) {
                    continue;
                }
                for (Method interfaceMethod : interfaceMethods) {
                    String serviceName = methodMap.get(interfaceMethod);
                    ActionMethod actionMethod = new ActionMethod(bean, method);
                    List<String> parameterList = new ArrayList<>();
                    for (Class<?> parameterType : interfaceMethod.getParameterTypes()) {
                        parameterList.add(parameterType.getName());
                    }
                    String parameterName = String.join(";", parameterList);
                    String url = serviceName + ":" + interfaceMethod.getName() + "(" + parameterName + ")";
                    exportedMethodMap.put(url, actionMethod);
                }
            }
        }
        return new ArrayList<>(serverNameSet);
    }

    /**
     * 判断method是否相等
     *
     * @param interfaceMethod
     * @param method
     * @return
     */
    private boolean methodEquals(Method interfaceMethod, Method method) {
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
