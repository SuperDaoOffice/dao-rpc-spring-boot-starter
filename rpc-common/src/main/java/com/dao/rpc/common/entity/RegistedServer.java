package com.dao.rpc.common.entity;

import com.dao.rpc.common.exception.DaoException;
import com.dao.rpc.common.util.Validator;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RegistedServer {

    private static ConcurrentHashMap<String, List<ServerAddress>> registedServerMap = new ConcurrentHashMap<>(16);


    public static List<ServerAddress> getServerNodes(String serverName) throws IOException, ClassNotFoundException {
        if (StringUtils.isEmpty(serverName)) {
            throw new DaoException("服务名为空");
        }
        List<ServerAddress> addressList = registedServerMap.get(serverName);
        if (addressList == null || addressList.isEmpty()) {
            return Collections.emptyList();
        }
        return deepCopy(addressList);
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

    /**
     * 注册服务
     *
     * @param connRequest
     */
    public static void addServerNode(ConnRequest connRequest) {
        Validator validator = Validator.create()
                .notBlank(connRequest.getIp(), "ip")
                .notNull(connRequest.getPort(), "port")
                .notBlank(connRequest.getServerName(), "服务名");
        if (!validator.isValid()) {
            throw new DaoException(validator.getMessage());
        }
        ServerAddress serverAddress = new ServerAddress(connRequest.getIp(), connRequest.getPort());
        List<ServerAddress> serverAddressList = registedServerMap.get(connRequest.getServerName());
        if (serverAddressList == null) {
            serverAddressList = new ArrayList<>();
            registedServerMap.put(connRequest.getServerName(), serverAddressList);
        }
        serverAddressList.add(serverAddress);
    }

    /**
     * 移除节点
     *
     * @param ip
     * @param port
     */
    public static void removeServerNode(String ip, Integer port) {
        ServerAddress serverAddress = new ServerAddress(ip, port);
        for (List<ServerAddress> serverAddressList : registedServerMap.values()) {
            serverAddressList.remove(serverAddress);
        }
    }


    private static class ServerAddress implements Serializable {

        private String ip;

        private Integer port;

        public ServerAddress(String ip, Integer port) {
            this.ip = ip;
            this.port = port;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return "ServerAddress{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ServerAddress that = (ServerAddress) o;
            return Objects.equals(getIp(), that.getIp()) &&
                    Objects.equals(getPort(), that.getPort());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getIp(), getPort());
        }
    }


}
