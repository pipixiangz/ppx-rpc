package com.ppx.ppxrpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ******************************
 * author：      赵翔
 * createTime:   2024-10-22 13:22
 * description:  RPC 响应 (封装调用方法得到的返回值、以及调用的信息（比如异常情况）等)
 * version:      V1.0
 * ******************************
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型（预留）
     */
    private Class<?> dataType;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 异常信息
     */
    private Exception exception;

}
