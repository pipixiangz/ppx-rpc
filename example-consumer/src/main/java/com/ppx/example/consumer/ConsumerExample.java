package com.ppx.example.consumer;

import com.ppx.example.common.model.User;
import com.ppx.example.common.service.UserService;
import com.ppx.ppxrpc.config.ConfigUtils;
import com.ppx.ppxrpc.config.RpcConfig;
import com.ppx.ppxrpc.proxy.ServiceProxyFactory;

/**
 * ******************************
 * author：      赵翔
 * createTime:   2024-11-01 11:40
 * description:  简易服务消费者示例
 * version:      V1.0
 * ******************************
 */
public class ConsumerExample {

    public static void main(String[] args) {
        //RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        //System.out.println(rpc);
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("ppx");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }

        long number = userService.getNumber();
        System.out.println(number);
    }
}
