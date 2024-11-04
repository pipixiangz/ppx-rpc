package com.ppx.ppxrpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppx.ppxrpc.model.RpcRequest;
import com.ppx.ppxrpc.model.RpcResponse;

import java.io.IOException;

/**
 * Json 序列化器
 * 实现相对复杂，要考虑对象转换的兼容性问题，比如 Object 数组在序列化后会丢失类型。
 *
 */
public class JsonSerializer implements Serializer {
    // ObjectMapper 是 Jackson 库的主要类，它提供一些功能将 Java 对象转换成 JSON 格式的数据
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 序列化
     * @param obj
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    /**
     * 反序列化
     * @param bytes
     * @param classType
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, classType);
        if (obj instanceof RpcRequest) {
            return handleRequest((RpcRequest) obj, classType);
        }
        if (obj instanceof RpcResponse) {
            return handleResponse((RpcResponse) obj, classType);
        }
        return obj;
    }

    /**
     * 由于 Object 的原始对象会被擦除，导致反序列化时会被作为 LinkedHashMap 无法转换成原始对象，因此这里做了特殊处理
     *
     * @param rpcRequest rpc 请求
     * @param type       类型
     * @return {@link T}
     * @throws IOException IO异常
     */
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        // 获取 rpc 请求中的参数类型数组
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        // 获取 rpc 请求中的参数数组
        Object[] args = rpcRequest.getArgs();

        // 循环处理每个参数的类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            // 如果类型不同，则重新处理一下类型
            if (!clazz.isAssignableFrom(args[i].getClass())) {
                // 将参数 args[i]（一个对象）序列化为字节数组
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                // 从字节数组中反序列化出符合预期类型的参数值
                args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
            }
        }
        return type.cast(rpcRequest);
    }

    /**
     * 由于 Object 的原始对象会被擦除，导致反序列化时会被作为 LinkedHashMap 无法转换成原始对象，因此这里做了特殊处理
     *
     * @param rpcResponse rpc 响应
     * @param type        类型
     * @return {@link T}
     * @throws IOException IO异常
     */
    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
        // 处理响应数据
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes, rpcResponse.getDataType()));
        return type.cast(rpcResponse);
    }
}
