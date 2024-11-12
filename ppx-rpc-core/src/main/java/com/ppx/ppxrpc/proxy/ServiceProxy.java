package com.ppx.ppxrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ppx.ppxrpc.RpcApplication;
import com.ppx.ppxrpc.config.RpcConfig;
import com.ppx.ppxrpc.constant.RpcConstant;
import com.ppx.ppxrpc.model.RpcRequest;
import com.ppx.ppxrpc.model.RpcResponse;
import com.ppx.ppxrpc.model.ServiceMetaInfo;
import com.ppx.ppxrpc.registry.Registry;
import com.ppx.ppxrpc.registry.RegistryFactory;
import com.ppx.ppxrpc.serializer.JdkSerializer;
import com.ppx.ppxrpc.serializer.Serializer;
import com.ppx.ppxrpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler{
    /**
     * 调用代理
     * 当用户调用某个接口的方法时，会改为调用 invoke 方法。
     * 在 invoke 方法中，我们可以获取到要调用的方法信息、传入的参数列表等，这就是服务提供者需要的参数
     * 用这些参数来构造请求对象就可以完成调用了。
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
        // 指定序列化器
        // Serializer serializer = new JdkSerializer();

        // 指定序列化器
        /*Serializer serializer = null;
        ServiceLoader<Serializer> serviceLoader = ServiceLoader.load(Serializer.class);
        for (Serializer service : serviceLoader) {
            serializer = service;
        }*/

        // 指定序列化器，使用工厂 + 读取配置” 来获取实现类
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());


        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName()) // UserService
                .methodName(method.getName()) // getUser
                .parameterTypes(method.getParameterTypes()) // User
                .args(args) // user
                .build();
        try {
            // 序列化
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);

// 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
// 暂时先取第一个
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

// 发送请求
            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
