package com.dao.rpc.register.client;

import com.dao.rpc.register.client.handler.DaoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DaoRegisterClientApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(DaoRegisterClientApplication.class, args);
        DaoClient daoClient = new DaoClient();
        daoClient.start();
    }
}
