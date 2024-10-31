package com.ppx.ppxrpc.server;

import com.ppx.ppxrpc.model.RpcRequest;
import com.ppx.ppxrpc.model.RpcResponse;
import com.ppx.ppxrpc.registry.LocalRegistry;
import com.ppx.ppxrpc.serializer.JdkSerializer;
import com.ppx.ppxrpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;
/**
 * ******************************
 * description:  HTTP 请求处理
 * ******************************
 */
/*业务流程如下：
            1. 反序列化请求为对象，并从请求对象中获取参数。
            2. 根据服务名称从本地注册器中获取到对应的服务实现类。
            3. 通过反射机制调用方法，得到返回结果。
            4. 对返回结果进行封装和序列化，并写入到响应中。*/
public class HttpServerHandler implements Handler<HttpServerRequest>  {
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        final Serializer serializer = new JdkSerializer();
        // 记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        // 异步处理 HTTP 请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                // 反序列化请求
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcResponse == null){
                rpcResponse.setMessage("rpcResponse is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try{
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs()); // 动态代理invoke调用方法
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("success");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 响应
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        // 发送 HTTP 响应
        HttpServerResponse response = request.response();
        response.putHeader("content-type", "application/json");

        try {
            // 序列化响应
            byte[] responseBytes = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(responseBytes));
        } catch (IOException e) {
            e.printStackTrace();
            response.end(Buffer.buffer());

        }
    }
}

