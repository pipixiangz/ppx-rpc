package com.ppx.ppxrpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * ******************************
 * author：      赵翔
 * createTime:   2024-10-22 11:33
 * description:  本地注册中心, key 为服务名称、value 为服务的实现类
 * version:      V1.0
 * ******************************
 */
public class LocalRegistry {
    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }
    /**
     * 获取服务
     *
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }
    /**
     * 删除服务
     *
     * @param serviceName
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }

}