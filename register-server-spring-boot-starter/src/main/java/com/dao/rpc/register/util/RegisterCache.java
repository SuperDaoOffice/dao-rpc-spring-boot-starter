package com.dao.rpc.register.util;

import com.dao.rpc.common.exception.DaoException;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.common.rpc.ServerAddress;
import io.netty.channel.Channel;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterCache {

    private static ConcurrentHashMap<String, List<RemoteAddress>> registeredServerMap = new ConcurrentHashMap<>(32);
    private static ConcurrentHashMap<RemoteAddress, Channel> clientChannelMap = new ConcurrentHashMap<>(32);

    public static void notifyAllClientWithAddedServer(String serverName, RemoteAddress address) {
        for (Channel channel : clientChannelMap.values()) {
            Message<ServerAddress> message = Message.buildAddedServerMessage(new ServerAddress(serverName, address));
            channel.writeAndFlush(message);
        }
    }

    public static void notifyAllClientWithDeletedServer(RemoteAddress address) {
        String serverName = null;
        for (Map.Entry<String, List<RemoteAddress>> entry : registeredServerMap.entrySet()) {
            if (entry.getValue().contains(address)) {
                serverName = entry.getKey();
                break;
            }
        }
        if (!StringUtils.isEmpty(serverName)) {
            for (Channel channel : clientChannelMap.values()) {
                Message<ServerAddress> message = Message.buildDeletedServerMessage(new ServerAddress(serverName, address));
                channel.writeAndFlush(message);
            }
        }
    }

    public static ConcurrentHashMap<String, List<RemoteAddress>> getRegisteredServerMap() {
        return registeredServerMap;
    }

    public static Channel getClientChannel(RemoteAddress address) {
        return clientChannelMap.get(address);
    }

    public static void addClientChannel(RemoteAddress address, Channel channel) {
        clientChannelMap.put(address, channel);
    }

    public static void removeClientChannel(RemoteAddress address) {
        clientChannelMap.remove(address);
    }

    public static List<RemoteAddress> getServerNodes(String serverName) throws IOException, ClassNotFoundException {
        if (StringUtils.isEmpty(serverName)) {
            throw new DaoException("服务名为空");
        }
        List<RemoteAddress> addressList = registeredServerMap.get(serverName);
        if (addressList == null || addressList.isEmpty()) {
            return Collections.emptyList();
        }
        return addressList;
    }

    public static void addServerNode(String serverName, RemoteAddress remoteAddress) {
        List<RemoteAddress> RemoteAddressList = registeredServerMap.get(serverName);
        if (RemoteAddressList == null) {
            RemoteAddressList = new ArrayList<>();
            registeredServerMap.put(serverName, RemoteAddressList);
        }
        RemoteAddressList.add(remoteAddress);
    }


    /**
     * 移除节点
     *
     * @param remoteAddress
     */
    public static void removeServerNode(RemoteAddress remoteAddress) {
        for (List<RemoteAddress> RemoteAddressList : registeredServerMap.values()) {
            RemoteAddressList.remove(remoteAddress);
        }
    }


    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
}
