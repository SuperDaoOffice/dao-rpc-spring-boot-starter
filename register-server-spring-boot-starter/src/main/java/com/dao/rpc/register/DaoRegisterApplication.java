package com.dao.rpc.register;

import com.dao.rpc.register.handler.DaoServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DaoRegisterApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(DaoRegisterApplication.class, args);
        DaoServer daoServer = new DaoServer();
        daoServer.start();
    }
}
