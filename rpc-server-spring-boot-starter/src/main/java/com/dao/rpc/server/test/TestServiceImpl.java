package com.dao.rpc.server.test;

import com.dao.rpc.server.anno.ServiceExport;

/**
 * @author HuChiHui
 * @date 2019/12/07 下午 18:13
 * @description
 */
@ServiceExport
public class TestServiceImpl implements TestService , TestService1 {

    @Override
    public void say(String str) {
        System.out.println("say : " + str);
    }

    @Override
    public void say1(String str) {

    }
}
