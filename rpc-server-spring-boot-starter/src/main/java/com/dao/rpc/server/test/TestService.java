package com.dao.rpc.server.test;

import com.dao.rpc.server.anno.ServiceProvider;

/**
 * @author HuChiHui
 * @date 2019/12/07 下午 18:13
 * @description
 */
@ServiceProvider
public interface TestService {

    void say(String str);
}
