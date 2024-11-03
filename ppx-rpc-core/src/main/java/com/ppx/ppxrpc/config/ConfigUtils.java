package com.ppx.ppxrpc.config;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
/**
 * ******************************
 * author：      赵翔
 * createTime:   2024-11-01 10:44
 * description:  配置工具类
 * version:      V1.0
 * ******************************
 */
public class ConfigUtils {
    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置文件并将属性映射到指定的类类型。加载配置对象，支持区分环境
     *
     * @param tClass       需要映射属性的类类型
     * @param prefix       属性前缀，用于筛选特定的属性
     * @param environment  环境标识（如 "dev", "prod"），用于加载相应的配置文件
     * @return 返回映射了配置属性的指定类类型实例
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        // 初始化一个 StringBuilder 用于构造配置文件名称
        StringBuilder configFileBuilder = new StringBuilder("application");

        // 如果提供了环境标识，则追加到文件名后缀（如 "application-dev.properties"）
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }

        // 完成文件名，添加 ".properties" 扩展名
        configFileBuilder.append(".properties");

        // 从构造的文件名中加载配置属性
        Props props = new Props(configFileBuilder.toString());

        // 将属性映射到指定的类类型，并根据前缀筛选相关属性
        return props.toBean(tClass, prefix);
    }
}

