package com.dao.rpc.register.client.entity;

import com.dao.rpc.common.rpc.RemoteAddress;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RemoteServices {

    private static final ConcurrentHashMap<String, List<RemoteAddress>> remoteAddressMap
            = new ConcurrentHashMap<>(16);

    private static final Cache<String, DaoResponseFuture> responseCache =
            CacheBuilder.newBuilder()
                    .maximumSize(100)
                    .expireAfterWrite(8, TimeUnit.SECONDS).build();

    public static RemoteAddress getRemoteAddress(String serviceName) {
        List<RemoteAddress> remoteAddressList = remoteAddressMap.get(serviceName);
        int index = Double.valueOf(Math.random()).intValue() % remoteAddressList.size();
        return remoteAddressList.get(index);
    }

    public static void addRemoteAddressMap(String serviceName, List<RemoteAddress> remoteAddressList) {
        remoteAddressMap.put(serviceName, remoteAddressList);
    }

    public static void addRemoteAddressMap(String serviceName, RemoteAddress remoteAddress) {
        List<RemoteAddress> remoteAddressList = remoteAddressMap.computeIfAbsent(serviceName, k -> new ArrayList<>());
        remoteAddressList.add(remoteAddress);
    }

    public static void removeRemoteAddress(String serviceName , RemoteAddress remoteAddress){
        List<RemoteAddress> remoteAddresses = remoteAddressMap.get(serviceName);
        if (remoteAddress != null){
            remoteAddresses.remove(remoteAddress);
        }
    }

    public static void addFutureCache(String requestId, DaoResponseFuture future) {
        responseCache.put(requestId, future);
    }

    public static DaoResponseFuture getFuture(String requestId) {
        return responseCache.getIfPresent(requestId);
    }
}
