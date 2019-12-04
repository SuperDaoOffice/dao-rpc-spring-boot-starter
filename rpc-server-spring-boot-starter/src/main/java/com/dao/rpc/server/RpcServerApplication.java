package com.dao.rpc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.lang.reflect.Method;

@SpringBootApplication
public class RpcServerApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(RpcServerApplication.class, args);
        Person person = new Person();
        for (Method method : person.getClass().getDeclaredMethods()) {
            System.out.println("...............");
            System.out.println(method.getName());
            for (Class<?> parameterType : method.getParameterTypes()) {
                System.out.print(parameterType.getName() + "   ");
            }
            System.out.println("\n................");
        }
    }

    static class Person {

        String getName() {
            return "dao";
        }

        void setName(String name){

        }
    }
}
