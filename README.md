# dao-rpc-spring-boot-starter

#### 介绍
使用netty实现的自定义协议的rpc框架

#### 协议

    |  1byte        |   1byte           | 1byte    | 4byte                    | ...       |  ...   |
    |  magic(固定值) |   version(版本号) | 消息类型  | 消息长度(除开前三个header) | 其他header | 内容体 |
    
#### 整体架构

    register-server-spring-boot-starter: 注册中心
    rpc-server-spring-boot-starter: 服务暴露者
    rpc-server-spring-boot-client: 服务调用者
    rpc-common: 通用sdk

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 待做项

1.参数细化，可配置
2.性能优化，性能测试

