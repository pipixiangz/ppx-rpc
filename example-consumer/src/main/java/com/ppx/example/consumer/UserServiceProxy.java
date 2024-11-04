package com.ppx.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ppx.example.common.model.User;
import com.ppx.example.common.service.UserService;
import com.ppx.ppxrpc.model.RpcRequest;
import com.ppx.ppxrpc.model.RpcResponse;
import com.ppx.ppxrpc.serializer.JdkSerializer;
import com.ppx.ppxrpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 */
public class UserServiceProxy {

    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        // 发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            // 序列化请求
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            // 构造 HTTP 请求去调用服务提供者
            try (HttpResponse response = HttpRequest.post("http://localhost:8080/") //  try(): try-with-resources
                    .body(bodyBytes)
                    .execute()) {
                // 获取响应
                result = response.bodyBytes();
            }
            // 反序列化响应
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
