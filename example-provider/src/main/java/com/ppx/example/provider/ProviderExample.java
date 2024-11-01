package com.ppx.example.provider;

import com.ppx.example.common.service.UserService;
import com.ppx.ppxrpc.RpcApplication;
import com.ppx.ppxrpc.registry.LocalRegistry;
import com.ppx.ppxrpc.server.HttpServer;
import com.ppx.ppxrpc.server.VertxHttpServer;

/**
 * ******************************
 * author：      赵翔
 * createTime:   2024-11-01 11:41
 * description:  简易服务提供者示例
 * version:      V1.0
 * ******************************
 */
public class ProviderExample {

    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
