package com.dao.rpc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class RpcServerApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(RpcServerApplication.class, args);

    }


}
