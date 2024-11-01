package com.ppx.example.consumer;

import com.ppx.ppxrpc.config.ConfigUtils;
import com.ppx.ppxrpc.config.RpcConfig;

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
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
